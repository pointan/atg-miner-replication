package at.fhooe.se.atgminer.atg;

import at.fhooe.se.atgminer.atg.semantics.*;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.*;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.expr.IExpressionEval;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.expr.JavaEval;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Interprets an {@link ATGrammar} with all {@link at.fhooe.se.atgminer.atg.semantics.BaseSemantic}</p>
 */
public class LL1GrammarInterpreter extends AbstractThrowingSemanticVisitor<RuntimeObject> implements ATElementVisitor<Void> {

    public static final class ParserError extends RuntimeException {
        public ParserError(String message) {
            super(message);
        }
    }

    public static final class InterpreterError extends RuntimeException {
        public InterpreterError(String message) {
            super(message);
        }

        public InterpreterError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class ObjectRuntimeObject extends RuntimeObject {

        private final Frame objectFrame;
        private final String className;

        public ObjectRuntimeObject(Frame objectFrame, String className) {
            super(null, typeSystem.findType("object"));
            this.objectFrame = objectFrame;
            this.className = className;
        }

        public Frame getObjectFrame() {
            return objectFrame;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public String toString() {
            return className + "{" + objectFrame + "}";
        }
    }

    public static final class Frame {
        private final Frame parent;
        private final Map<InOutAttribute, RuntimeObject> results = new HashMap<>();
        private final Map<AbstractDataContainerSemantic, RuntimeObject> variables = new HashMap<>();
        private final String debuggingName;

        public Frame(Frame parent, String debuggingName) {
            this.parent = parent;
            this.debuggingName = debuggingName;
        }

        public RuntimeObject getVariable(AbstractDataContainerSemantic variable) {
            if (variable.getName().equals("line") || variable.getName().equals("i")) {
                Frame f = this;
                while(f.getParent() != null) {
                    f = f.getParent();
                }
                return f.variables.get(variable);
            }
            if (variables.containsKey(variable)) {
                return variables.get(variable);
            }
            if (variable instanceof InOutAttribute) {
                // try to fuzzy search for the variable - compare without subscripts:
                String nameWithoutSubscript = variable.getName().replaceAll("_(\\d+)_", "_");
                for (Map.Entry<AbstractDataContainerSemantic, RuntimeObject> abstractDataContainerSemanticRuntimeObjectEntry : variables.entrySet()) {
                    String s = abstractDataContainerSemanticRuntimeObjectEntry.getKey().getName().replaceAll("_(\\d+)_", "_");
                    if (s.equals(nameWithoutSubscript)) {
                        return abstractDataContainerSemanticRuntimeObjectEntry.getValue();
                    }
                }
                System.out.println("No in out variable found, not even with fuzzy search.");
            }
            throw new IllegalStateException("Variable " + variable.getName() + " not present!");
        }

        public void setVariable(AbstractDataContainerSemantic variable, RuntimeObject value) {
            if (variable.getName().equals("line") || variable.getName().equals("i")) {
                Frame f = this;
                while(f.getParent() != null) {
                    f = f.getParent();
                }
                f.variables.put(variable, value);
            } else {
                variables.put(variable, value);
            }
        }

        public void setResult(InOutAttribute attribute, RuntimeObject value) {
            results.put(attribute, value);
        }

        public RuntimeObject getResult(InOutAttribute attribute) {
            var res = results.get(attribute);
            if (res == null) {
                throw new InterpreterError("No result for " + attribute + " in frame " + debuggingName);
            }
            return res;
        }

        public Frame getParent() {
            return parent;
        }

        @Override
        public String toString() {
            return variables.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining(", "));
        }
    }

    public static final class Result {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private Map<String, RuntimeObject> finalResult;

        public ByteArrayOutputStream getOutputStream() {
            return outputStream;
        }

        public Map<String, RuntimeObject> getFinalResult() {
            return finalResult;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "outputStream=" + outputStream +
                    ", finalResult=" + finalResult +
                    '}';
        }
    }

    private final Frame global;
    private final TypeSystem typeSystem;
    private final IExpressionEval eval;
    private final ATGrammar grammar;
    private final String input;
    private final Deque<Frame> frameStack;
    private final Result result = new Result();
    private final PrintStream out = new PrintStream(result.outputStream);
    private Deque<ATTerminal> terminalInput;
    private int inputIdx = 0; // Just for debugging


    public LL1GrammarInterpreter(ATGrammar grammar, String input) {
        this.grammar = grammar;
        this.input = input;
        typeSystem = TypeSystem.getDefault();
        eval = new JavaEval(typeSystem);
        frameStack = new ArrayDeque<>();
        global = new Frame(null, "global");
    }

    public Result parse() {
        try {
            terminalInput = new ArrayDeque<>(Objects.requireNonNull(tryFindBestMatchingTerminals(input)));
            visitGrammar(grammar);
            return result;
        } catch (InterpreterError | ThrowException e) {
            throw e;
        } catch(Throwable t) {
            throw new InterpreterError("Failed to interpret grammar with given input with: " + t.getMessage(), t);
        }
    }

    private List<ATTerminal> tryFindBestMatchingTerminals(String input) {
        List<ATTerminal> existingGrammarTerminals = grammar.getTerminalList();
        List<ATTerminal> terminals = new ArrayList<>();
        int start = 0, end = 1;
        while (start < input.length()) {
            while (true) {
                if (end > input.length()) {
                    end--;
                    break;
                }
                String currentSubString = input.substring(start, end);
                if (existingGrammarTerminals.stream().anyMatch(t -> t.getToken().startsWith(currentSubString))) {
                    end++;
                } else {
                    end--;
                    break;
                }
            }
            // if start and end are both the same, no terminal has been found, that starts with that character --> this terminal might not exist in the grammar
            if (start == end) {
                System.out.println("Find best matching terminal returns null with start=" + start + " and end=" + end);
                return null;
            }
            String longestMatchingTermString = input.substring(start, end);

            Optional<ATTerminal> first = existingGrammarTerminals.stream().filter(x -> x.getToken().equals(longestMatchingTermString)).findFirst();
            while (first.isEmpty() && start != end) {
                end--;
                String longestMatchingTermStringInner = input.substring(start, end);
                first = existingGrammarTerminals.stream().filter(x -> x.getToken().equals(longestMatchingTermStringInner)).findFirst();
            }
            if (first.isPresent()) {
                terminals.add(first.get());
            } else {
                throw new IllegalStateException("At least one character in the input cannot be matched to a terminal symbol. Start position: " + start + " in input: " + input);
            }
            start = end;
            end++;
        }
        return terminals;
    }

    @Override
    public Void visitGrammar(ATGrammar grammar) {
        global.setVariable(new VariableSemantic(null, "line"), typeSystem.newStringValue(terminalInput.stream().map(ATTerminal::getToken).collect(Collectors.joining())));
        global.setVariable(new VariableSemantic(null, "i"), typeSystem.newIntValue(0));
        frameStack.push(new Frame(global, grammar.getStartSymbol().getKey()));
        var x = grammar.findRuleOf(grammar.getStartSymbol()).accept(this);
        Frame pop = frameStack.pop();
        result.finalResult = grammar.getStartSymbol().getSynthesizedAttributes().stream()
                .collect(Collectors.toMap(AbstractDataContainerSemantic::getName, pop::getResult));
        return x;
    }

    @Override
    public Void visitRule(ATRule rule) {
        return rule.getBody().accept(this);
    }

    @Override
    public Void visitTerminal(ATTerminal symbol) {
        symbol.getPreSemantics().forEach(x -> x.accept(this));
        if (terminalInput.isEmpty()) {
            throw new ParserError("No more terminals left in input!");
        }
        ATTerminal current = terminalInput.pop();
        if (!current.equals(symbol)) {
            throw new ParserError("Expected " + symbol + " but found " + current);
        }
        inputIdx++;
        symbol.getPostSemantics().forEach(x -> x.accept(this));
        return null;
    }

    @Override
    public Void visitTerminalClass(ATTerminalClass symbol) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public Void visitTerminalGroup(ATTerminalGroup group) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public Void visitNonTerminal(ATNonTerminal symbol) {
        symbol.getPreSemantics().forEach(x -> x.accept(this));
        ATRule r = grammar.findRuleOf(symbol);
        if (r == null) {
            throw new IllegalStateException("No rule for NT symbol " + symbol);
        }

        var frame = new Frame(global, symbol.getKey());
        // pass in variables to calling frame
        for (InOutAttribute inheritedAttribute : symbol.getInheritedAttributes()) {
            frame.setVariable(inheritedAttribute, frameStack.element().getVariable(inheritedAttribute));
        }
        frameStack.push(frame);
        var res = r.accept(this);
        Frame resultFrame = frameStack.pop();
        // extract results:
        for (InOutAttribute synthesizedAttribute : symbol.getSynthesizedAttributes()) {
            frameStack.element().setVariable(synthesizedAttribute, resultFrame.getResult(synthesizedAttribute));
        }
        symbol.getPostSemantics().forEach(x -> x.accept(this));
        return res;
    }

    @Override
    public Void visitSequence(ATSequence element) {
        element.getPreSemantics().forEach(x -> x.accept(this));
        element.getSeq().forEach(x -> x.accept(this));
        element.getPostSemantics().forEach(x -> x.accept(this));
        return null;
    }

    @Override
    public Void visitOption(ATOption element) {
        element.getPreSemantics().forEach(x -> x.accept(this));
        Void res = null;
        Set<ATTerminal> first = grammar.firstSet(element.getBody());
        if (!terminalInput.isEmpty() && first.contains(terminalInput.element())) {
            res = element.getBody().accept(this);
        }
        element.getPostSemantics().forEach(x -> x.accept(this));
        return res;
    }

    @Override
    public Void visitRepetition(ATRepetition element) {
        element.getPreSemantics().forEach(x -> x.accept(this));
        Set<ATTerminal> first = grammar.firstSet(element.getBody());
        while (!terminalInput.isEmpty() && first.contains(terminalInput.element())) {
            element.getBody().accept(this);
        }
        element.getPostSemantics().forEach(x -> x.accept(this));
        return null;
    }

    @Override
    public Void visitChoice(ATChoice element) {
        element.getPreSemantics().forEach(x -> x.accept(this));
        Void res = null;
        int cnt = 0;
        ATElement sel = null;
        for (ATElement e : element.getElements()) {
            Set<ATTerminal> first = grammar.firstSet(e);
            if (first.contains(terminalInput.element())) {
                if (sel == null) {
                    sel = e;
                }
                cnt++;
            }
        }
        if (cnt > 1) {
            System.out.println("Warning: LL(1) conflict in choice " + element);
        }
        if (sel != null) {
            res = sel.accept(this);
        } else {
            throw new ParserError("No element in choice " + element + " could be matched!");
        }
        element.getPostSemantics().forEach(x -> x.accept(this));
        return res;
    }

    @Override
    public RuntimeObject visitAssignmentSemantic(AssignmentSemantic assignmentsemantic) {
        RuntimeObject accept = assignmentsemantic.getRightHandSide().accept(this);
        if (assignmentsemantic.getLeftHandSide() instanceof VariableSemantic) {
            VariableSemantic variablesemantic = (VariableSemantic) assignmentsemantic.getLeftHandSide();
            frameStack.element().setVariable(variablesemantic, accept);
        } else if (assignmentsemantic.getLeftHandSide() instanceof QualifiedIdentifierSemantic) {
            QualifiedIdentifierSemantic lhs = (QualifiedIdentifierSemantic) assignmentsemantic.getLeftHandSide();
            if (!(lhs.getQualifiers() instanceof VariableSemantic)) {
                throw new IllegalStateException("Currently only VariableSemantic is supported");
            }
            if (!(lhs.getMember() instanceof VariableSemantic)) {
                throw new IllegalStateException("Can only assign to Variable semantics");
            }
            if (((VariableSemantic) lhs.getQualifiers()).getName().equals("self")) {
                frameStack.element().getParent().setVariable((VariableSemantic) lhs.getMember(), accept);
            } else {
                ObjectRuntimeObject object = (ObjectRuntimeObject) frameStack.element().getVariable((AbstractDataContainerSemantic) lhs.getQualifiers());
                object.getObjectFrame().setVariable((VariableSemantic) lhs.getMember(), accept);
            }
        } else if (assignmentsemantic.getLeftHandSide() instanceof InOutAttribute) {
            InOutAttribute attribute = (InOutAttribute) assignmentsemantic.getLeftHandSide();
            if (attribute.getType() == InOutAttribute.Type.OUT) {
                frameStack.element().setResult(attribute, accept);
            } else if (attribute.getType() == InOutAttribute.Type.IN) {
                frameStack.element().setVariable(attribute, accept);
            } else {
                throw new UnsupportedOperationException("Unsupported type.");
            }
        } else if (assignmentsemantic.getLeftHandSide() instanceof ArrayAccessSemantic) {
            ArrayAccessSemantic arrayaccesssemantic = (ArrayAccessSemantic) assignmentsemantic.getLeftHandSide();

            if (arrayaccesssemantic.getArrayName() instanceof AbstractDataContainerSemantic && arrayaccesssemantic.getSubscripts().length == 1 && arrayaccesssemantic.getSubscripts()[0] instanceof AbstractDataContainerSemantic) {
                RuntimeObject variable = frameStack.element().getVariable((AbstractDataContainerSemantic) arrayaccesssemantic.getArrayName());
                // Dict node?
                if (variable instanceof DictObject) {
                    DictObject dictObject = (DictObject) variable;
                    dictObject.put(arrayaccesssemantic.getSubscripts()[0].accept(this), accept);
                }
            }
        } else {
            throw new ParserError("Cannot assign to " + assignmentsemantic.getLeftHandSide() + " of type " + assignmentsemantic.getLeftHandSide().getClass().getSimpleName() + "!");
        }

        return RuntimeObject.VOID;
    }

    @Override
    public RuntimeObject visitConstSemantic(ConstSemantic constsemantic) {
        return typeInference(constsemantic.getValue());
    }

    @Override
    public RuntimeObject visitUnaryMinusSemantic(UnaryMinusSemantic unaryminussemantic) {
        RuntimeObject obj = unaryminussemantic.getOperand().accept(this);
        if (obj.getType().isInteger()) {
            return eval.iminus(obj);
        } else if (obj.getType().isReal()) {
            return eval.rminus(obj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(obj.getType(), "-");
        }
    }

    @Override
    public RuntimeObject visitAddSemantic(AddSemantic addsemantic) {
        RuntimeObject leftObj = addsemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = addsemantic.getRightOperand().accept(this);

        if (leftObj.getType().isInteger()) {
            return eval.iadd(leftObj, rightObj);
        } else if (leftObj.getType().isReal()) {
            return eval.radd(leftObj, rightObj);
        } else if (leftObj.getType().isString()) {
            return eval.sadd(leftObj, rightObj);
        } else if (leftObj.getType().isChar()) {
            return eval.cadd(leftObj, rightObj);
        } else if (leftObj.getType().isSet()) {
            return eval.setadd(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "+");
        }
    }

    @Override
    public RuntimeObject visitQualifiedIdentifierSemantic(QualifiedIdentifierSemantic qualifiedidentifiersemantic) {
        if (!(qualifiedidentifiersemantic.getQualifiers() instanceof AbstractDataContainerSemantic)) {
            throw new IllegalStateException("Only supports qualifiers for AbstractDataContainerSemantic");
        }
        if (!(qualifiedidentifiersemantic.getMember() instanceof AbstractDataContainerSemantic)) {
            throw new IllegalStateException("Can only assign to Variable semantics");
        }
        String name = ((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getQualifiers()).getName();
        if (name.equals("self")) {
            try {
                return frameStack.element().getParent().getVariable((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getMember());
            } catch (IllegalStateException e) {
                if (((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getMember()).getName().equals("idx")) {
                    var val = typeSystem.newIntValue(0);
                    frameStack.element().getParent().setVariable((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getMember(), val);
                    return val;
                } else {
                    throw e;
                }
            }
        } else if (name.equals("math")) {
            switch (((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getMember()).getName()) {
                case "pi":
                    return typeSystem.newRealValue(Math.PI);
                case "e":
                    return typeSystem.newRealValue(Math.E);
                default:
                    throw new IllegalStateException("Unsupported math qualifier name: " + name);
            }
        } else {
            ObjectRuntimeObject object = (ObjectRuntimeObject) qualifiedidentifiersemantic.getQualifiers().accept(this);
            return object.getObjectFrame().getVariable((AbstractDataContainerSemantic) qualifiedidentifiersemantic.getMember());
        }

    }

    @Override
    public RuntimeObject visitFloatSemantic(FloatSemantic floatsemantic) {
        var value = floatsemantic.getParameter().accept(this);
        var str = value.stringValue();
        double v = 0;

        if ("inf".equals(str)) {
            v = Double.POSITIVE_INFINITY;
        } else if ("-inf".equals(str)) {
            v = Double.NEGATIVE_INFINITY;
        } else {
            try {
                v = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                throw new InterpreterError("Invalid number format: " + str + " (expected float/double or inf or -inf)", e);
            }
        }
        return TypeSystem.getDefault().newRealValue(v);
    }

    @Override
    public RuntimeObject visitTerminalHeuristicPlaceHolder(TerminalHeuristicPlaceHolder terminalheuristicplaceholder) {
        return terminalheuristicplaceholder.getNode().accept(this);
    }

    @Override
    public RuntimeObject visitVariableSemantic(VariableSemantic variablesemantic) {
        return frameStack.element().getVariable(variablesemantic);
    }

    @Override
    public RuntimeObject visitInOutAttribute(InOutAttribute inoutattribute) {
        return frameStack.element().getVariable(inoutattribute);
    }

    @Override
    public RuntimeObject visitMultiplySemantic(MultiplySemantic multiplysemantic) {
        RuntimeObject leftObj = multiplysemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = multiplysemantic.getRightOperand().accept(this);

        IExpressionEval i = InterpreterRuntime.getDefault().getEval();
        if (leftObj.getType().isInteger()) {
            return i.imult(leftObj, rightObj);
        } else if (leftObj.getType().isReal()) {
            return i.rmult(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "*");
        }
    }

    @Override
    public RuntimeObject visitListSemantic(ListSemantic listsemantic) {
        return TypeSystem.getDefault().newListObject(listsemantic.getChildren().stream().map(x -> x.accept(this)).toArray(RuntimeObject[]::new));
    }

    @Override
    public RuntimeObject visitMethodInvokeSemantic(MethodInvokeSemantic methodinvokesemantic) {
//        if (!(methodinvokesemantic.getObj() instanceof AbstractDataContainerSemantic)) {
//            throw new IllegalStateException("Method invoke currently only supported for Variable semantics");
//        }
        if (methodinvokesemantic.getFuncName().equals("append")) {
            RuntimeObject variable = methodinvokesemantic.getObj().accept(this);
            return ((ListObject) variable).append(Stream.of(methodinvokesemantic.getActualParams()).map(x -> x.accept(this)).toArray(RuntimeObject[]::new));
        } else if (methodinvokesemantic.getObj() instanceof VariableSemantic && ((VariableSemantic) methodinvokesemantic.getObj()).getName().equals("math")) {
            RuntimeObject[] params;
            if (methodinvokesemantic.getActualParams()[0] instanceof UnpackingSemantic) {
                ListObject accept = (ListObject) ((UnpackingSemantic) methodinvokesemantic.getActualParams()[0]).getParameter().accept(this);
                params = accept.elements();
            } else {
                params = Stream.of(methodinvokesemantic.getActualParams()).map(x -> x.accept(this)).toArray(RuntimeObject[]::new);
            }
            switch (methodinvokesemantic.getFuncName()) {
                case "acos":
                    return visitForMathMethod(params, Math::acos);
                case "asin":
                    return visitForMathMethod(params, Math::asin);
                case "atan":
                    return visitForMathMethod(params, Math::atan);
                case "atan2":
                    return visitForMathMethod(params, Math::atan2);
                case "ceil":
                    return visitForMathMethod(params, Math::ceil);
                case "cos":
                    return visitForMathMethod(params, Math::cos);
                case "cosh":
                    return visitForMathMethod(params, Math::cosh);
                case "degrees":
                    return visitForMathMethod(params, Math::toDegrees);
                case "exp":
                    return visitForMathMethod(params, Math::exp);
                case "fabs":
                    return visitForMathMethod(params, Math::abs);
                case "floor":
                    return visitForMathMethod(params, Math::floor);
                case "fmod":
                    return visitForMathMethod(params, (a, b) -> a - (int) Math.floor(a / b) * b);
                case "hypot":
                    return visitForMathMethod(params, Math::hypot);
                case "ldexp":
                    return visitForMathMethod(params, (mantissa, exponent) -> mantissa * Math.pow(2, exponent));
                case "log":
                    return visitForMathMethod(params, Math::log);
                case "log10":
                    return visitForMathMethod(params, Math::log10);
                case "pow":
                    return visitForMathMethod(params, Math::pow);
                case "radians":
                    return visitForMathMethod(params, Math::toRadians);
                case "sin":
                    return visitForMathMethod(params, Math::sin);
                case "sinh":
                    return visitForMathMethod(params, Math::sinh);
                case "sqrt":
                    return visitForMathMethod(params, Math::sqrt);
                case "tan":
                    return visitForMathMethod(params, Math::tan);
                case "tanh":
                    return visitForMathMethod(params, Math::tanh);
                default:
                    throw new UnsupportedOperationException("Not yet implemented: " + methodinvokesemantic.getFuncName());
            }
        } else {
            RuntimeObject variable = methodinvokesemantic.getObj().accept(this);
            if (variable instanceof ObjectRuntimeObject) {
                ObjectRuntimeObject classObject = (ObjectRuntimeObject) variable;
                MethodDefSemantic methodDefSemantic = grammar.getGlobalFunctions().stream()
                        .filter(x -> x instanceof MethodDefSemantic)
                        .map(MethodDefSemantic.class::cast)
                        .filter(x -> x.getClassName().equals(classObject.getClassName()) && x.getMethodName().equals(methodinvokesemantic.getFuncName()))
                        .findFirst()
                        .orElseThrow();
                try {
                    Frame methodFrame = new Frame(classObject.getObjectFrame(), methodinvokesemantic.getFuncName());
                    VariableSemantic[] variables = methodDefSemantic.getFormalParamDefs().stream()
                            .map(x -> new VariableSemantic(x.getBasedOn(), x.getIdent()))
                            .toArray(VariableSemantic[]::new);
                    if (variables.length != methodinvokesemantic.getActualParams().length) {
                        throw new IllegalStateException("Parameter size does not match number of provided variables");
                    }
                    for (int i = 0; i < variables.length; i++) {
                        RuntimeObject value = methodinvokesemantic.getActualParams()[i].accept(this);
                        methodFrame.setVariable(variables[i], value);
                    }
                    frameStack.push(methodFrame);
                    methodDefSemantic.getStatements().forEach(stmt -> stmt.accept(this));
                    return RuntimeObject.VOID;
                } catch (ReturnException e) {
                    return e.getReturnValue();
                } finally {
                    frameStack.pop();
                }
            } else if (variable instanceof ValueObject) {
                Object value = variable.getValue();
                if (variable.getType().isString() && value.equals("") && methodinvokesemantic.getFuncName().equals("join")) {
                    RuntimeObject accept = methodinvokesemantic.getActualParams()[0].accept(this);
                    if (!(accept instanceof ListObject)) {
                        throw new IllegalStateException("Only support join on List objects");
                    }
                    return ((ListObject) accept).join();
                }
            }
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private RuntimeObject visitForMathMethod(RuntimeObject[] params, DoubleUnaryOperator function) {
        return typeSystem.newRealValue(function.applyAsDouble(params[0].realValue()));
    }

    private RuntimeObject visitForMathMethod(RuntimeObject[] params, DoubleBinaryOperator function) {
        return typeSystem.newRealValue(function.applyAsDouble(params[0].realValue(), params[1].realValue()));
    }

    @Override
    public RuntimeObject visitPrintSemantic(PrintSemantic printsemantic) {
        RuntimeObject accept = printsemantic.getParameter().accept(this);
        out.println(accept.stringValue());
        return RuntimeObject.VOID;
    }

    @Override
    public RuntimeObject visitChrSemantic(ChrSemantic chrsemantic) {
        RuntimeObject obj = chrsemantic.getParameter().accept(this);
        int value = obj.intValue();
        return TypeSystem.getDefault().newCharValue((char) value).setSymbolicObject(obj.getSymbolicObject());
    }

    @Override
    public RuntimeObject visitStdFuncSemantic(StdFuncSemantic stdfuncsemantic) {
        if (stdfuncsemantic.getFunctionName().equals("abs")) {
            RuntimeObject p0 = stdfuncsemantic.getParameter()[0].accept(this);
            if (p0.getType().isReal()) {
                return eval.rabs(p0);
            } else if (p0.getType().isInteger()) {
                return eval.iabs(p0);
            } else {
                throw new IllegalStateException("Abs(): wrong argument type " + p0.getType());
            }
        } else {
            throw new UnsupportedOperationException("Std func " + stdfuncsemantic.getFunctionName() + " not yet implemented!");
        }
    }

    @Override
    public RuntimeObject visitLenSemantic(LenSemantic lensemantic) {
        RuntimeObject seqObj = lensemantic.getParameter().accept(this);
        if (seqObj.getType().isString() || seqObj.getType().isChar()) {
            int len = seqObj.stringValue().length();
            return TypeSystem.getDefault().newIntValue(len);
        } else if (seqObj.getType().isList()) {
            int len = ((ListObject) seqObj).size();
            return TypeSystem.getDefault().newIntValue(len);
        } else if (seqObj.getType().isDict()) {
            int len = ((DictObject) seqObj).size();
            return TypeSystem.getDefault().newIntValue(len);
        } else {
            throw InterpreterRuntime.Error.runtimeError("wrong argument type for len(str): " + seqObj.getType());
        }
    }

    @Override
    public RuntimeObject visitIfSemantic(IfSemantic ifsemantic) {
        RuntimeObject condObj = ifsemantic.getCondition().accept(this);
        if (condObj.getType() != condObj.getType().getTypeSystem().boolType()) {
            throw new IllegalStateException(TypeSystem.Error.cannotConvertType(condObj.getType(), TypeSystem.getDefault().boolType()));
        }
        if (condObj.boolValue()) {
            ifsemantic.getThenBlock().accept(this);
        } else if (ifsemantic.getElseBlock() != null) {
            ifsemantic.getElseBlock().accept(this);
        }
        return RuntimeObject.VOID;
    }

    @Override
    public RuntimeObject visitEqualSemantic(EqualSemantic equalsemantic) {
        RuntimeObject a = equalsemantic.getLeftOperand().accept(this);
        RuntimeObject b = equalsemantic.getRightOperand().accept(this);

        final IExpressionEval eval = InterpreterRuntime.getDefault().getEval();
        Type t = getDynamicTypeEqual(a, b);
        if (t.isInteger()) {
            return eval.ieq(a, b);
        } else if (t.isEnum()) {
            return eval.ieq(a, b);
        } else if (t.isReal()) {
            return eval.req(a, b);
        } else if (t.isString()) {
            return eval.seq(a, b);
        } else if (t.isChar()) {
            return eval.ceq(a, b);
        } else if (t.isBool()) {
            return eval.beq(a, b);
        } else if (t.isSet()) {
            return eval.seteq(a, b);
        } else if (t instanceof NoneType) {
            return eval.seq(a, b);
        } else {
            throw new IllegalStateException("Unsupported type of t");
        }
    }

    @Override
    public RuntimeObject visitDivideRealSemantic(DivideRealSemantic dividerealsemantic) {
        RuntimeObject leftObj = dividerealsemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = dividerealsemantic.getRightOperand().accept(this);

        if (typeSystem.getDynamicType(leftObj, rightObj).isInteger()) {
            return InterpreterRuntime.getDefault().getEval().idiv(leftObj, rightObj);
        } else if (typeSystem.getDynamicType(leftObj, rightObj).isReal()) {
            return InterpreterRuntime.getDefault().getEval().rdiv(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "/");
        }
    }

    @Override
    public RuntimeObject visitSubtractSemantic(SubtractSemantic subtractsemantic) {
        RuntimeObject leftObj = subtractsemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = subtractsemantic.getRightOperand().accept(this);

        IExpressionEval i = InterpreterRuntime.getDefault().getEval();
        Type t = typeSystem.getDynamicType(leftObj, rightObj);
        if (t.isInteger()) {
            return i.isub(leftObj, rightObj);
        } else if (t.isReal()) {
            return i.rsub(leftObj, rightObj);
        } else if (t.isSet()) {
            return i.setsub(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "-");
        }
    }

    @Override
    public RuntimeObject visitBitOrSemantic(BitOrSemantic bitorsemantic) {
        RuntimeObject leftObj = bitorsemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = bitorsemantic.getRightOperand().accept(this);

        if (typeSystem.getDynamicType(leftObj, rightObj).isInteger()) {
            return InterpreterRuntime.getDefault().getEval().ior(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "|");
        }
    }

    @Override
    public RuntimeObject visitBitShiftLeftSemantic(BitShiftLeftSemantic bitshiftleftsemantic) {
        RuntimeObject leftObj = bitshiftleftsemantic.getLeftOperand().accept(this);
        RuntimeObject rightObj = bitshiftleftsemantic.getRightOperand().accept(this);

        if (typeSystem.getDynamicType(leftObj, rightObj).isInteger()) {
            return InterpreterRuntime.getDefault().getEval().ishl(leftObj, rightObj);
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "shl");
        }
    }

    @Override
    public RuntimeObject visitIntSemantic(IntSemantic intsemantic) {
        String str = intsemantic.getParameter().accept(this).stringValue();
        if (intsemantic.getBase() != null) {
            var base = intsemantic.getBase().accept(this).intValue();
            int v = Integer.parseInt(str, base);
            return typeSystem.newIntValue(v);
        }

        try {
            int v = Integer.decode(str);
            return typeSystem.newIntValue(v);
        } catch (NumberFormatException e) {
            int v = (int) Double.parseDouble(str);
            return typeSystem.newIntValue(v);
        }
    }

    @Override
    public RuntimeObject visitDictSemantic(DictSemantic dictsemantic) {
        DictObject dictObj = typeSystem.newDictObject();
        for (DictSemantic.Entry e : dictsemantic.getEntries()) {
            RuntimeObject keyObj = e.getKey().accept(this);
            RuntimeObject valueObj = e.getValue().accept(this);
            dictObj.put(keyObj, valueObj);
        }
        return dictObj;
    }

    @Override
    public RuntimeObject visitNullSemantic(NullSemantic nullsemantic) {
        Type noneType = typeSystem.findType(PythonTypeSystem.NONE_NAME);
        return noneType.newInstance();
    }

    @Override
    public RuntimeObject visitAndSemantic(AndSemantic andsemantic) {
        RuntimeObject leftObj = andsemantic.getLeftOperand().accept(this);
        if (leftObj.getType().isBool()) {
            if (leftObj.boolValue()) {
                RuntimeObject rightObj = andsemantic.getRightOperand().accept(this);
                if (rightObj.getType().isBool()) {
                    return eval.band(leftObj, rightObj);
                } else {
                    throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), rightObj.getType(), "and");
                }
            } else {
                return eval.id(leftObj);
            }
        } else {
            throw TypeSystem.Error.incompatibleTypeError(leftObj.getType(), "and");
        }
    }

    @Override
    public RuntimeObject visitNotVisitedPlaceholderSemantic(NotVisitedPlaceholderSemantic notvisitedplaceholdersemantic) {
        throw new InterpreterError("Cannot interpret NotVisitedPlaceholderSemantic!");
    }

    @Override
    public RuntimeObject visitErrorSemantic(ErrorSemantic errorSemantic) {
        throw new ThrowException(null);
    }

    @Override
    public RuntimeObject visitFuncInvokeSemantic(FuncInvokeSemantic funcinvokesemantic) {
        Optional<FuncDefSemantic> functionDefSemanticOpt = grammar.getGlobalFunctions().stream()
                .filter(x -> x instanceof FuncDefSemantic)
                .map(FuncDefSemantic.class::cast)
                .filter(x -> x.getFuncName().equals(funcinvokesemantic.getFuncName()))
                .findFirst();

        if (functionDefSemanticOpt.isPresent()) {
            FuncDefSemantic functionDefSemantic = functionDefSemanticOpt.get();
            try {
                Frame functionFrame = new Frame(global, funcinvokesemantic.getFuncName());
                VariableSemantic[] variables = functionDefSemantic.getParamDefSemantics().stream()
                        .map(x -> new VariableSemantic(x.getBasedOn(), x.getIdent()))
                        .toArray(VariableSemantic[]::new);
                if (variables.length != funcinvokesemantic.getActualParams().length) {
                    throw new IllegalStateException("Parameter size does not match number of provided variables");
                }
                for (int i = 0; i < variables.length; i++) {
                    RuntimeObject value = funcinvokesemantic.getActualParams()[i].accept(this);
                    functionFrame.setVariable(variables[i], value);
                }
                frameStack.push(functionFrame);
                functionDefSemantic.getStatements().forEach(stmt -> stmt.accept(this));
                return RuntimeObject.VOID;
            } catch (ReturnException e) {
                return e.getReturnValue();
            } finally {
                frameStack.pop();
            }
        } else {
            // if no FuncDefSemantic was found, it might be a MethodDefSemantic with the constructor:
            Optional<MethodDefSemantic> methodDefSemanticOpt = grammar.getGlobalFunctions().stream()
                    .filter(x -> x instanceof MethodDefSemantic)
                    .map(MethodDefSemantic.class::cast)
                    .filter(x -> x.getClassName().equals(funcinvokesemantic.getFuncName()) && x.getMethodName().equals("__init__"))
                    .findFirst();

            if (methodDefSemanticOpt.isPresent()) {
                MethodDefSemantic methodDefSemantic = methodDefSemanticOpt.get();
                Frame objectFrame = new Frame(global, methodDefSemantic.getClassName() + "-obj");
                ObjectRuntimeObject object = new ObjectRuntimeObject(objectFrame, funcinvokesemantic.getFuncName());
                Frame methodFrame = new Frame(objectFrame, funcinvokesemantic.getFuncName());
                VariableSemantic[] variables = methodDefSemantic.getFormalParamDefs().stream()
                        .map(x -> new VariableSemantic(x.getBasedOn(), x.getIdent()))
                        .toArray(VariableSemantic[]::new);
                if (variables.length != funcinvokesemantic.getActualParams().length) {
                    throw new IllegalStateException("Parameter size does not match number of provided variables");
                }
                for (int i = 0; i < variables.length; i++) {
                    RuntimeObject value = funcinvokesemantic.getActualParams()[i].accept(this);
                    methodFrame.setVariable(variables[i], value);
                }
                frameStack.push(methodFrame);
                methodDefSemantic.getStatements().forEach(stmt -> stmt.accept(this));
                frameStack.pop();
                return object;
            }
        }

        throw new RuntimeException("No function or method found with name: " + funcinvokesemantic.getFuncName());
    }

    @Override
    public RuntimeObject visitEmptySemantic(EmptySemantic emptysemantic) {
        return RuntimeObject.VOID;
    }

    @Override
    public RuntimeObject visitReturnSemantic(ReturnSemantic returnsemantic) {
        throw new ReturnException(returnsemantic.getResult().accept(this));
    }

    @Override
    public RuntimeObject visitSliceSemantic(SliceSemantic slicesemantic) {
        RuntimeObject seqObj = slicesemantic.getSequenceName().accept(this);

        ValueObject beginIndexObj = null;
        ValueObject endIndexObj = null;
        int beginIndex = 0, endIndex = 0;

        if (slicesemantic.getBeginIdx() != null) {
            beginIndexObj = (ValueObject) slicesemantic.getBeginIdx().accept(this);
            beginIndex = checkIndex(beginIndexObj.intValue(), seqObj.stringValue().length());
        }
        if (slicesemantic.getEndIdx() != null) {
            endIndexObj = (ValueObject) slicesemantic.getEndIdx().accept(this);
            endIndex = checkIndex(endIndexObj.intValue(), seqObj.stringValue().length());
        }
        if (seqObj.getType().isString()) {
            if (beginIndexObj != null && endIndexObj != null) {
                String sliceStr;
                if (beginIndex <= endIndex) {
                    sliceStr = seqObj.stringValue().substring(beginIndex, endIndex);
                } else {
                    sliceStr = "";
                }
                return TypeSystem.getDefault().newStringValue(sliceStr);
            } else if (beginIndexObj != null) {
                String sliceStr = seqObj.stringValue().substring(beginIndex);
                return TypeSystem.getDefault().newStringValue(sliceStr);
            } else if (endIndexObj != null) {
                String sliceStr = seqObj.stringValue().substring(0, endIndex);
                return TypeSystem.getDefault().newStringValue(sliceStr);
            } else {
                throw new UnsupportedOperationException("SliceNode not implemented.");
            }
        } else {
            throw new UnsupportedOperationException("SliceNode not implemented.");
        }
    }

    private int checkIndex(int index, int seqLen) {
        if (index < 0) {
            if (-seqLen < index) {
                return seqLen + index;
            } else {
                return 0;
            }
        } else {
            if (index < seqLen) {
                return index;
            } else {
                return seqLen;
            }
        }
    }

    private RuntimeObject typeInference(Object value) {
        if (value instanceof String) {
            return typeSystem.newStringValue((String) value);
        }
        if (value instanceof Integer) {
            return typeSystem.newIntValue((Integer) value);
        }
        if (value instanceof Boolean) {
            return typeSystem.newBoolValue((Boolean) value);
        }
        throw new IllegalStateException("Cannot infer type for " + value + " of type " + value.getClass().getSimpleName() + "!");
    }

    private Type getDynamicTypeEqual(RuntimeObject leftObj, RuntimeObject rightObj) {
        if (leftObj.getType().isReal() && rightObj.getType().isInteger()) {
            return leftObj.getType();
        } else if (leftObj.getType().isInteger() && rightObj.getType().isReal()) {
            return rightObj.getType();
        } else {
            return typeSystem.getDynamicType(leftObj, rightObj);
        }
    }
}
