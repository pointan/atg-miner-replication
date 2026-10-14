package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemantic;
import at.fhooe.se.atgminer.atg.node2semantics.NodeToSemanticPipeline;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.DefSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.concept.mimid.Program;
import at.fhooe.se.atgminer.fuzzer.ConfiguredEfficientGrammarFuzzer;
import at.fhooe.se.atgminer.parsetree.*;
import at.fhooe.se.atgminer.semantic.SemanticGrammarEnrichment;
import at.fhooe.se.atgminer.util.CastUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterProblem;
import at.fhooe.se.codeanalysis.adele.interpreter.node.DefNode;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Base class for the second concept version of the ATGMiner process</p>
 */
public class ATGMinerV2 {

    private int globalCnt = 1;

    public ATGrammar processWithGeneratingInputs(String grammarPath, String programPath) {
        Set<String> usedInputs = new HashSet<>();
        ATGrammar grammar = loadGrammar(grammarPath);
        ConfiguredEfficientGrammarFuzzer egf = new ConfiguredEfficientGrammarFuzzer(grammar);
        ConfiguredEfficientGrammarFuzzer.GeneratedInputs generatedInputs = egf.generateInputsToCoverFullGrammarWithGenerator(50);
        int numberOfValidInputsNeededForGeneration = 0;
        while (generatedInputs.hasMore()) {
            String input = generatedInputs.nextInput();
            if (!input.isBlank()) {
                try {
                    if (usedInputs.contains(input)) {
                        System.out.println("Skipping " + input + " because it was already used!");
                        continue;
                    }
                    run(grammar, input, grammarPath, programPath);
                    generatedInputs.commitLastInput();
                    numberOfValidInputsNeededForGeneration++;
                    usedInputs.add(input);
                    System.out.println("INFO: Successfully ran program with input: " + input);
                } catch (ParseError e) {
                    System.out.println("ERROR: Could not run program for input: " + input + " with error: " + e.getMessage());
                } catch (GrammarError e) {
                    System.out.println("ERROR: Could not construct syntax tree for input: " + input + " with error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("ERROR: Something is not working properly for input: " + input + " with error: " + e.getMessage());
                }
            } else {
                generatedInputs.commitLastInput();
            }
        }

        printGrammar(grammar);
        System.out.println("Needed " + numberOfValidInputsNeededForGeneration + " valid inputs to generated the result.");
        System.out.println("Use the following inputs, to generate the grammar: \n" + String.join("\n", usedInputs));
        return grammar;
    }

    public ATGrammar processForSingleInput(String grammarPath, String input, String programPath) {
        return processForMultipleInputs(grammarPath, List.of(input), programPath);
    }

    public ATGrammar processForMultipleInputs(String grammarPath, List<String> inputs, String programPath) {
        ATGrammar grammar = loadGrammar(grammarPath);

        for (String input : inputs) {
            try {
                run(grammar, input, grammarPath, programPath);
            } catch (ParseError e) {
                System.out.println("ERROR: Could not run program for input: " + input + " with error: " + e.getMessage());
            } catch (GrammarError e) {
                System.out.println("ERROR: Could not construct syntax tree for input: " + input + " with error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("ERROR: Something is not working properly for input: " + input + " with error: " + e.getMessage());
                throw e;
            }
        }
        printGrammar(grammar);
        return grammar;
    }

    private void printGrammar(ATGrammar grammar) {
        beautifyGrammar(grammar);
        System.out.println(grammar.toStringForBash());
//        System.out.println(grammar.accept(new CocoRVisitor(new CocoRSemVisitor())));
    }

    private void run(ATGrammar grammar, String input, String grammarPath, String programPath) {
        System.out.printf("INFO: Running programm (%s) with input: %s for grammar %s\n", programPath, input, grammarPath);
        ATSyntaxTree syntaxTree = createSyntaxTree(grammar, input);
        ParseTree parseTree = createParseTree(programPath, input);
        List<DefSemantic> globalFuncs = CastUtil.cast(new ArrayList<>(parseTree.getPreSemantics()));
        parseTree.getPreSemantics().clear();
        grammar.addGlobalFunctions(globalFuncs);
        PTSTMapping mappings = new PTSTMapping();
        mappings.findMappings(syntaxTree, parseTree);
        mappings.moveMapping();
        DataFlowAnalysis dfa = new DataFlowAnalysis();
        dfa.processTree(syntaxTree);
        SemanticGrammarEnrichment sge = new SemanticGrammarEnrichment(grammar);
        sge.enrichGrammar(syntaxTree);
    }

    private void beautifyGrammar(ATGrammar grammar) {
        for (ATRule atRule : grammar.getRuleList()) {
            int i = 1;
            List<InOutAttribute> synthesizedAttributes = new ArrayList<>(atRule.getKey().getSynthesizedAttributes());
            if (synthesizedAttributes.size() == 1) {
                synthesizedAttributes.get(0).setDisplayName(atRule.getKey().getKey());
            } else {
                for (InOutAttribute synthesizedAttribute : synthesizedAttributes) {
                    synthesizedAttribute.setDisplayName(atRule.getKey().getKey() + "_" + i++);
                }
            }

            Map<ATNonTerminal, AtomicInteger> mapping = new HashMap<>();
            atRule.getBody().stream().forEach(elem -> elem.accept(new AbstractATElementVisitor<Void>() {
                @Override
                public Void visitNonTerminal(ATNonTerminal symbol) {
                    for (InOutAttribute synthesizedAttribute : symbol.getSynthesizedAttributes()) {
                        int x = mapping.computeIfAbsent(symbol, k -> new AtomicInteger(1)).getAndIncrement();
                        if (!synthesizedAttribute.hasDisplayName()) {
                            String displayName = elem.getKey();
                            if (x > 1) {
                                displayName += "_" + x;
                            }
                            synthesizedAttribute.setDisplayName(displayName);
                        }
                    }
                    return null;
                }
            }));
        }
    }

    private Optional<ParseTree> tryFindSuitableNonPseudoNode(ATSyntaxTree syntaxTree, ParseTree parseTree) {
        while (parseTree != null && parseTree.isPseudoNode() && parseTree.getReachableTerms().equals(syntaxTree.getReachableTerms())) {
            parseTree = parseTree.getParent();
        }
        if (parseTree == null || parseTree.isPseudoNode()) {
            return Optional.empty();
        }
        if (parseTree.getReachableTerms().equals(syntaxTree.getReachableTerms())) {
            return Optional.of(parseTree);
        } else {
            return Optional.empty();
        }
    }


    private ATGrammar loadGrammar(String path) {
        return ATGrammar.fromCocoFile(path);
    }

    private ATSyntaxTree createSyntaxTree(ATGrammar grammar, String input) {
        try {
            LL1GrammarParser p = new LL1GrammarParser(grammar, true);
            return p.parse(input);
        } catch (Exception e) {
            throw new GrammarError(e.getMessage(), e);
        }
    }

    private ParseTree createParseTree(String path, String input) {
        Program program = Program.fromFile(new File(path)).withStdOut(false);
        var parseTreeBuilder = new SemanticParseTreeEnrichment();
        var result = program.runWithInput(input, parseTreeBuilder);
        if (!result.isValid()) {
            String problems = result.getProblems().stream().map(InterpreterProblem::getProblemText).collect(Collectors.joining("; "));
            throw new ParseError("Invalid result: " + result.getResultDebugString() + (!problems.isEmpty() ? " with problems: " + problems : ""));
        }
        ParseTree pt = parseTreeBuilder.getRoot();


        SemanticFunctionExtractionPTVisitor semanticFunctionExtractionPTVisitor = new SemanticFunctionExtractionPTVisitor();
        pt.apply(semanticFunctionExtractionPTVisitor);
        semanticFunctionExtractionPTVisitor.getGlobalMethods().forEach(pt::addPreSemantic);
        pt.apply(new RemoveMarkedNodesVisitor());


        pt.apply(new MarkAllButLastTerminalSymbol());
        pt.apply(new MarkNonTerminalLeafVisitor());
        pt.apply(new MarkParentVisitor());

        pt.apply(new ParseTreeVisitor() { // remove semantics from SEM nodes
            @Override
            public void visit(ParseTree parseTree) {
                parseTree.getChildren().forEach(this::visit);
                if (parseTree.getLabel().equals("SEM") && !parseTree.getPostSemantics().isEmpty()) {
                    parseTree.tryFindNextSibling().ifPresentOrElse(sibling -> {
                                sibling.getPreSemantics().addAll(0, parseTree.getPostSemantics());
                                parseTree.getPostSemantics().clear();
                            },
                            () -> System.out.println("WARNING: SEM node with semantic without a sibling found!"));
                }
            }
        });
        pt.apply(new ParseTreeVisitor() { // unmark non terminal nodes with semantics on it
            @Override
            public void visit(ParseTree parseTree) {
                parseTree.getChildren().forEach(this::visit);
                if (!parseTree.isTerminalSymbol() && parseTree.containsSemantics()) {
                    parseTree.setAlive(true);
                }
            }
        });
        pt.apply(new RemoveSemanticFromMarkedNodesVisitor());
        pt.apply(new RemoveMarkedNodesVisitor());
        return pt;
    }

    static class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }
    }

    static class GrammarError extends RuntimeException {
        public GrammarError(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
