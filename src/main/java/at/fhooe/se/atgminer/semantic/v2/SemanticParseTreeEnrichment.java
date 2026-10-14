package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemanticPipeline;
import at.fhooe.se.atgminer.atg.semantics.*;
import at.fhooe.se.atgminer.atg.semanticsmerge.SemanticMerge;
import at.fhooe.se.atgminer.parsetree.ParseTree;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterNodeVisitorSuperCallAdapter;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterNodeVisitorThrowingAdapter;
import at.fhooe.se.codeanalysis.adele.interpreter.node.*;
import at.fhooe.se.codeanalysis.adele.interpreter.node.func.StdFuncCallNode;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;
import at.scch.codeanalytics.commons.contracts.Contract;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>Bringing semantics into the parse tree.</p>
 */
public class SemanticParseTreeEnrichment extends MimidParseTreeBuilderV2 {

    private final NodeToSemantic<InterpreterNode, BaseSemantic> nodeToSemantic = NodeToSemanticPipeline.createPipeline2();
    private final Deque<ExpressionNode> lastFuncCallNodeStack = new ArrayDeque<>();
    private List<BaseSemantic> enterCallStmtSem = null;

    @Override
    public void leaveExprStmt(ExprStmtNode node) {
        super.enterExprStmt(node);
        if (isLexerFunction()) return;
        BaseSemantic sem = nodeToSemantic.process(node.getExpression());
        if (doesContainLexerResultPlaceholder(sem)) {
            return;
        }
        if (lastNode.isTerminalSymbol()) {
            sem = SemanticHeuristics.inputAccessReplacementSemantic(sem, lastNode.getLabel());
        }
        // We can ignore some element, e.g. if a result placeholder only is placed:
        if (sem instanceof ResultPlaceholderSemantic) return;

        lastNode.addPostSemantic(sem);
    }

    private boolean doesContainLexerResultPlaceholder(BaseSemantic semantic) {
        return semantic.stream()
                .filter(ResultPlaceholderSemantic.class::isInstance)
                .map(ResultPlaceholderSemantic.class::cast)
                .map(ResultPlaceholderSemantic::getFunctionName)
                .anyMatch(LEXER_FUNCTIONS::contains);
    }

    @Override
    public void leaveReturnStmt(ReturnStmtNode returnNode, RuntimeObject value, ExpressionNode returnExprNode) {
        super.leaveReturnStmt(value, returnExprNode);
        if (isLexerFunction()) return;
        BaseSemantic result = nodeToSemantic.process(returnExprNode);
        ReturnSemantic returnSemantic = new ReturnSemantic(returnNode, result);
        lastNode.addPostSemantic(returnSemantic);
    }

    @Override
    public void enterFuncCall(FuncCallNode node) {
        super.enterFuncCall(node);
        lastFuncCallNodeStack.push(node);
    }

    @Override
    public void leaveFuncCall(FuncCallNode node, RuntimeObject returnObject) {
        super.leaveFuncCall(node, returnObject);
        ExpressionNode pop = lastFuncCallNodeStack.pop();
        Contract.assertThat(node == pop, "Invalid lastFuncCallNodeStack");
    }

    @Override
    public void enterMethodInvoke(MethodInvokeNode interpreterNodes) {
        super.enterMethodInvoke(interpreterNodes);
        lastFuncCallNodeStack.push(interpreterNodes);
    }

    @Override
    public void leaveMethodInvoke(MethodInvokeNode interpreterNodes) {
        super.leaveMethodInvoke(interpreterNodes);
        ExpressionNode pop = lastFuncCallNodeStack.pop();
        Contract.assertThat(interpreterNodes == pop, "Invalid lastFuncCallNodeStack");
    }

    private String getLastFuncCallNodeName() {
        return lastFuncCallNodeStack.element().accept(new InterpreterNodeVisitorThrowingAdapter<>() {
            @Override
            public String visitFuncCallNode(FuncCallNode node) {
                return node.getFuncName();
            }

            @Override
            public String visitMethodInvokeNode(MethodInvokeNode node) {
                return node.getMethodName();
            }
        });
    }

    private ExpressionNode[] getLastFuncCallNodeActualParam() {
        return lastFuncCallNodeStack.element().accept(new InterpreterNodeVisitorThrowingAdapter<>() {
            @Override
            public ExpressionNode[] visitFuncCallNode(FuncCallNode node) {
                return node.getParams();
            }

            @Override
            public ExpressionNode[] visitMethodInvokeNode(MethodInvokeNode node) {
                return node.getParams();
            }
        });
    }

    public void enterMethodOrFuncDef(DefNode node, FormalParamDefNode[] formalParameters, String funcName) {
        if (formalParameters.length == 0) {
            return;
        }
        if (lastFuncCallNodeStack.isEmpty()) {
            System.out.println("WARNING: Found formal parameters but no associated FuncCallNode available");
            return;
        }
        Contract.assertThat(funcName.equals(getLastFuncCallNodeName()), "node.getFuncName().equals(lastFuncCallNode.getFuncName())");
        enterCallStmtSem = new ArrayList<>();
        for (int i = 0; i < formalParameters.length; i++) {
            var formalParam = formalParameters[i];
            var actualParam = getLastFuncCallNodeActualParam()[i];
            var formalParamSem = new VariableSemantic(formalParam, formalParam.getIdent(), true);
            var actualParamSem = nodeToSemantic.process(actualParam);
            BaseSemantic sem;
            if (formalParam.getKind() == FormalParamDefNode.ParamKind.IN) {
                sem = new InAttributePlaceholderSemantic(formalParam, formalParamSem, actualParamSem);
            } else { // can only be InOut parameter, because Out only parameter does not exist
                sem = new SynthesizedAttributePlaceholderSemantic(formalParam, formalParamSem, actualParamSem);
            }
            // short hack to ignore all call to input()
            if (sem.stream().anyMatch(InputSemantic.class::isInstance)) {
                continue;
            }
            enterCallStmtSem.add(sem);
        }
    }

    @Override
    public void enterMethodDef(MethodDefNode node) {
        super.enterMethodDef(node);
        if (node.getMethodName().equals("__init__")) return; // ignore init nodes.
        enterMethodOrFuncDef(node, node.getFormalParameters(), node.getMethodName());
    }

    @Override
    public void enterFuncDef(FuncDefNode node) {
        super.enterFuncDef(node);
        enterMethodOrFuncDef(node, node.getFormalParameters(), node.getFuncName());
    }

    @Override
    public void enterCallStmt(String funcName) {
        super.enterCallStmt(funcName);
        if (isLexerFunction()) return;
        if (enterCallStmtSem != null) {
            enterCallStmtSem.forEach(nodeStack.peek()::addPreSemantic);
            enterCallStmtSem = null;
        }
    }

    @Override
    public void leaveFuncDef(FuncDefNode node) {
        super.leaveFuncDef(node);
        leaveFuncOrMethodDef(node);
    }

    @Override
    public void leaveMethodDef(MethodDefNode node) {
       leaveFuncOrMethodDef(node);
    }

    public void leaveFuncOrMethodDef(InterpreterNode node) {
        // check if last node, has terminal access, if not, we can remove that function call completely as it is a semantic function and not a parsing function
//        if (lastNode.stream().noneMatch(ParseTree::isTerminalSymbol)) {
//            ParseTree parent = lastNode.getParent();
//            parent.removeChild(lastNode);
//            ParseTree pt = new ParseTree("semnode"+idx++, "SEM");
//            parent.addChild(pt);
//            lastNode = pt;
//        }
    }

    @Override
    public void leaveWhile(WhileStmtNode node, int iterations) {
        SemanticMerge sm = new SemanticMerge();
        super.leaveWhile(node, iterations);
        if (!lastNode.containsTerminals() && iterations > 0) {
            var prevLastNode = lastNode.getParent().getChildren().get(lastNode.getParent().getChildren().size() - 2);
            lastNode.getParent().removeChild(lastNode);

            var whileSemantic = nodeToSemantic.process(node);

            lastNode = prevLastNode;
            lastNode.addPostSemantic(whileSemantic);
        } else if (!lastNode.containsTerminals() && !containsPossibleLineAccess(node.getCondition())) {
            var prevLastNode = lastNode.getParent().getChildren().get(lastNode.getParent().getChildren().size() - 2);
            lastNode.getParent().removeChild(lastNode);
            lastNode = prevLastNode;
            lastNode.addPostSemantic(new NotVisitedPlaceholderSemantic(node));
        }
    }

    private boolean containsPossibleLineAccess(ExpressionNode node) {
        return node.accept(new InterpreterNodeVisitorSuperCallAdapter<>() {
            @Override
            public Boolean visitBinaryExpressionNode(BinaryExpressionNode node) {
                return node.getLeft().accept(this) || node.getRight().accept(this);
            }

            @Override
            public Boolean visitQualifiedIdentifierNode(QualifiedIdentifierNode node) {
                if (node.getQualifiers() instanceof IdentifierNode) {
                    return ((IdentifierNode) node.getQualifiers()).getIdent().equals("self") && node.getMember().getIdent().equals("line");
                }
                return false;
            }

            @Override
            public Boolean visitFuncCallNode(FuncCallNode node) {
                return Stream.of(node.getParams()).anyMatch(n -> n.accept(this));
            }

            @Override
            public Boolean visitStdFuncCallNode(StdFuncCallNode node) {
                return Stream.of(node.getParams()).anyMatch(n -> n.accept(this));
            }

            @Override
            public Boolean visitIdentifierNode(IdentifierNode node) {
                return false;
            }

            @Override
            public Boolean visitMethodInvokeNode(MethodInvokeNode node) {
                return Stream.of(node.getParams()).anyMatch(n -> n.accept(this));
            }

            @Override
            public Boolean visitValueLiteralNode(ValueLiteralNode node) {
                return false;
            }
        });
    }

    @Override
    public void leaveIfStmt(IfStmtNode stmt, ExpressionNode conditionExpression, RuntimeObject condition) {
        super.leaveIfStmt(stmt, conditionExpression, condition);
        // check if there are no accesses to terminals. in addition, also check if the condition is true, if there is a then block, or if the condition is false, if there is an else block.
        // because otherwise it still could be a syntax block that just has not been visited. we can only add semantics, if they have been visited.
        if (!lastNode.containsTerminals() && ((condition.boolValue() && stmt.getThenBlock() != null) || (!condition.boolValue() && stmt.getElseBlock() != null)) && !containsPossibleLineAccess(conditionExpression)) {
            var prevLastNode = lastNode.getParent().getChildren().get(lastNode.getParent().getChildren().size() - 2);
            lastNode.getParent().removeChild(lastNode);

            var ifSemantic = nodeToSemantic.process(stmt);

            lastNode = prevLastNode;
            lastNode.addPostSemantic(ifSemantic);
        }
    }
}
