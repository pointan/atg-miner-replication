package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATSyntaxTree;
import at.fhooe.se.atgminer.atg.semantics.*;
import at.fhooe.se.atgminer.util.ListUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Analysis the data flow across the syntax tree.</p>
 */
public class DataFlowAnalysis {
    public void processTree(ATSyntaxTree root) {
        Scope rootScope = new Scope(Scope.Type.ROOT);
        handleAnys(root);
        processTreeRecursive(root, rootScope);
    }

    private void handleAnys(ATSyntaxTree root) {
        root.stream()
                .filter(x -> x.getSymbol() instanceof ATNonTerminal && x.getSymbol().getKey().equals("Any"))
                .forEach(this::handleSingleAnyNode);
    }

    private void handleSingleAnyNode(ATSyntaxTree any) {
        List<BaseSemantic> preSemantics = new ArrayList<>();
        List<BaseSemantic> postSemantics = new ArrayList<>();
        collectSemanticsRecursiveAndClear(any, preSemantics, postSemantics);
        final var anyOutAttr = any.getOutAttributes().isEmpty() ? new InOutAttribute(null, null, "Any_out", InOutAttribute.Type.OUT) : any.getOutAttributes().get(0);
        any.getOutAttributes().add(anyOutAttr);
        postSemantics = postSemantics.stream().map(sem -> {
            // find all TerminalHeuristicPlaceHolders, and create a map to replace them with our anyOutAttr:
            var replacement = new HashMap<BaseSemantic, BaseSemantic>();
            sem.stream().filter(TerminalHeuristicPlaceHolder.class::isInstance).forEach(thph -> replacement.put(thph, anyOutAttr));
            return sem.replace(replacement);
        }).collect(Collectors.toList());
        any.getPreSemantics().addAll(preSemantics);
        any.getSemantics().addAll(postSemantics);
    }

    private void collectSemanticsRecursiveAndClear(ATSyntaxTree node, List<BaseSemantic> preSemantics, List<BaseSemantic> postSemantics) {
        preSemantics.addAll(node.getPreSemantics());
        node.getPreSemantics().clear();
        node.getChildren().forEach(child -> collectSemanticsRecursiveAndClear(child, preSemantics, postSemantics));
        postSemantics.addAll(node.getSemantics());
        node.getSemantics().clear();
    }

    private void processTreeRecursive(ATSyntaxTree node, Scope scope) {
        // Symbol index is not required here, as the object is uniquely used, so we can identify which element is used via the object id
        InOutAttribute resultAttribute = new InOutAttribute(null, null, node.getSymbol().getKey() + "_out", InOutAttribute.Type.OUT);
        if (node.getParent() == null) {
            node.getOutAttributes().add(resultAttribute);
        }
        Scope childScope = new Scope(Scope.nodeToType(node), scope, resultAttribute);

        // ***  process presemantics ***
        updateSemantic(node.getPreSemantics(), scope);
        // Replace InAttributes
        for (BaseSemantic preSemantic : new ArrayList<>(node.getPreSemantics())) {
            if (preSemantic instanceof InAttributePlaceholderSemantic) {
                InAttributePlaceholderSemantic inAttributePlaceholderSemantic = (InAttributePlaceholderSemantic) preSemantic;
                String name = node.getSymbol().getKey() + "_" + node.getSymbolIndex() + "_in_" + inAttributePlaceholderSemantic.getFormalParam().getName();
                String nameWithoutSubscript = node.getSymbol().getKey() + "_in_" + inAttributePlaceholderSemantic.getFormalParam().getName();
                InOutAttribute attribute = new InOutAttribute(null, inAttributePlaceholderSemantic, name, InOutAttribute.Type.IN);
                InOutAttribute attributeWithoutSubscript = new InOutAttribute(null, inAttributePlaceholderSemantic, nameWithoutSubscript, InOutAttribute.Type.IN);
                AssignmentSemantic assignInNode = new AssignmentSemantic(null, attribute, inAttributePlaceholderSemantic.getActualParam());
                ListUtil.replace(node.getPreSemantics(), preSemantic, assignInNode);
                childScope.addReplacement(inAttributePlaceholderSemantic.getFormalParam(), attributeWithoutSubscript);
                node.getInAttributes().add(attribute);
            }
        }

        // *** process children ***
        for (ATSyntaxTree child : node.getChildren()) {
            processTreeRecursive(child, childScope);
        }

        // *** process postsemantics ***
        updateSemantic(node.getSemantics(), scope);
        // Replace result placeholders
        for (BaseSemantic semantic : new ArrayList<>(node.getSemantics())) {
            // for the moment we assume that the result placeholder is always attached to the node, which returns the result (this should be always the case, or?)
            List<BaseSemantic> resultPlaceholders = semantic.stream().filter(ResultPlaceholderSemantic.class::isInstance).collect(Collectors.toList());
            if (!resultPlaceholders.isEmpty()) {
                if (resultPlaceholders.size() > 1) {
                    throw new IllegalStateException("ERROR: Cannot deal with more then one result placeholder");
                } else {
                    ResultPlaceholderSemantic resultPlaceholder = (ResultPlaceholderSemantic)resultPlaceholders.get(0);

                    resultAttribute.setOrigin(resultPlaceholder);
                    BaseSemantic newNode;
                    if (resultPlaceholders == semantic) {
                        newNode = resultAttribute;
                    } else {
                        newNode = semantic.replace(Map.of(resultPlaceholder, resultAttribute));
                    }
                    ListUtil.replace(node.getSemantics(), semantic, newNode);
                    if (node.getSymbol() instanceof ATNonTerminal) {
                        node.getOutAttributes().add(resultAttribute);
                    } else {
//                        throw new IllegalStateException("Adding Out attribute to anything other than NonTerminal is not supported yet.");
                    }
                }
            }
        }
        // replace return semantics
        for (BaseSemantic baseSemantic : new ArrayList<>(node.getSemantics())) {
            if (baseSemantic instanceof ReturnSemantic) {
                ReturnSemantic returnSemantic = (ReturnSemantic) baseSemantic;
                Optional<InOutAttribute> resultAttributeOnFunctionScopeOpt = scope.getResultAttributeOnFunctionScope();
                if (resultAttributeOnFunctionScopeOpt.isPresent()) {
                    AssignmentSemantic assignmentSemantic = new AssignmentSemantic(null,resultAttributeOnFunctionScopeOpt.get(), returnSemantic.getResult());
                    ListUtil.replace(node.getSemantics(), baseSemantic, assignmentSemantic);
                } // else {otherwise we just leave the result call, as this is probably the top result}
            }
        }
    }

    private void updateSemantic(List<BaseSemantic> semantics, Scope scope) {
        for (BaseSemantic baseSemantic : new ArrayList<>(semantics)) {
            BaseSemantic newSem = baseSemantic.replace(scope.getReplacementsOnFunctionScope());
            ListUtil.replace(semantics, baseSemantic, newSem);
        }
    }

    private static class Scope {
        enum Type {FUNCTION, BLOCK, ROOT}

        private final Scope parent;
        private final Type type;
        private final Map<BaseSemantic, BaseSemantic> replacements = new HashMap<>();
        private final InOutAttribute resultAttribute;

        public Scope(Type type) {
            this(type, null, null);
        }

        public Scope(Type type, Scope parent, InOutAttribute resultAttribute) {
            this.parent = parent;
            this.type = type;
            this.resultAttribute = resultAttribute;
        }

        public static Type nodeToType(ATSyntaxTree node) {
            return node.isPseudoNode() ? Type.BLOCK : Type.FUNCTION;
        }

        public void addReplacement(BaseSemantic semantic, BaseSemantic replacement) {
            replacements.put(semantic, replacement);
        }

        public Map<BaseSemantic, BaseSemantic> getReplacementsOnFunctionScope() {
            List<Map<BaseSemantic, BaseSemantic>> relevantReplacements = new ArrayList<>();
            Scope currentScope = this;
            while(currentScope != null && currentScope.type != Type.FUNCTION) {
                relevantReplacements.add(currentScope.replacements);
                currentScope = currentScope.parent;
            }
            if (currentScope != null) {
                relevantReplacements.add(currentScope.replacements);
            }

            return relevantReplacements.stream()
                    .map(Map::entrySet)
                    .flatMap(Set::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public Optional<InOutAttribute> getResultAttributeOnFunctionScope() {
            Scope currentScope = this;
            while(currentScope != null && currentScope.type != Type.FUNCTION) {
                currentScope = currentScope.parent;
            }
            if (currentScope == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(currentScope.resultAttribute);
        }
    }
}
