package at.fhooe.se.atgminer;

import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.LL1GrammarInterpreter;
import at.fhooe.se.atgminer.concept.mimid.Program;
import at.fhooe.se.atgminer.interpreterextensions.RuntimeObjectComparer;
import at.fhooe.se.atgminer.semantic.v2.ATGMinerV2;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ThrowException;
import at.scch.codeanalytics.Gastm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Main class for Attribute Grammar Evaluation</p>
 */
public class ATGMinerEvaluation {
    private static final String DEFAULT_BASE_PATH = "examples/";
    private static final String PROGRAM_FILE = "program.py";
    private static final List<String> EXAMPLES = List.of(
            "ABCParse",
            "Calc",
            "CgiDecode",
            "Mathexpr",
            "MicroJson",
            "NumberParser",
            "SimpleExprCalc",
            "Xml",
            "ZeroOne"
    );

    private final String programPath;
    private ATGrammar grammar;

    public ATGMinerEvaluation(String programPath) {
        this.programPath = programPath;
    }

    /**
     * @param args optional: args[0] is the examples directory (default {@value DEFAULT_BASE_PATH}),
     *             further arguments restrict the run to the given example names (e.g. {@code Calc Xml})
     */
    public static void main(String[] args) {
        String basePath = args.length > 0 ? args[0] : DEFAULT_BASE_PATH;
        String base = basePath.endsWith("/") ? basePath : basePath + "/";
        List<String> selected = args.length > 1 ? List.of(args).subList(1, args.length) : List.of();
        EXAMPLES.stream()
                .filter(example -> selected.isEmpty() || selected.contains(example))
                .map(example -> base + example + "/" + PROGRAM_FILE)
                .peek(p -> System.out.println("### " + p))
                .map(ATGMinerEvaluation::new)
                .forEach(ATGMinerEvaluation::run);
    }

    public void run() {
        try {
            runInternal();
        } catch (Exception e) {
            System.err.println("Failed to successfully evaluate grammar for " + programPath + " with error " + e.getMessage());
        }
    }

    public void runInternal() {
        extractATG();
        List<String> inputs = loadInputs();
        Map<String, String> errors = new HashMap<>();
        int valid = inputs.stream().mapToInt(input -> {
            RuntimeObject ro1 = runProgram(input);
            RuntimeObject ro2;
            try {
                ro2 = interpretGrammar(input);
            } catch (LL1GrammarInterpreter.InterpreterError e) {
                errors.put(input, e.getMessage());
                System.err.println("Input " + input + " lead to interpreter error: " + e.getMessage());
                return 0;
            } catch (LL1GrammarInterpreter.ParserError e) {
                errors.put(input, e.getMessage());
                System.err.println("Input " + input + " lead to parsing error: " + e.getMessage());
                return 0;
            } catch (ThrowException e) {
                System.out.println("Comparing ThrowException with " + ro1);
                return ro1.getType().isClass() && ro1.getType().getTypeName().contains("Exception") ? 1 : 0;
            }
            if (compareResults(ro1, ro2)) {
                return 1;
            }
            errors.put(input, "Results not equal");
            System.out.println("Input: " + input + " resulted in not equal result: " + ro1 + " <> " + ro2);
            return 0;
        }).sum();
        logResult(valid, inputs.size(), errors);
    }

    private void extractATG() {
        String grammarPath = new File(programPath).getParentFile().getAbsolutePath() + "/grammar.atg";
        grammar = new ATGMinerV2().processWithGeneratingInputs(grammarPath, programPath);
        try {
            Files.writeString(Path.of(new File(programPath).getParentFile().getAbsolutePath() + "/result.atg"), grammar.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RuntimeObject runProgram(String input) {
        return Program
                .fromFile(new File(programPath))
                .withStdOut(false)
                .runWithInput(input, x -> Gastm.findFunctionDefinition("result", x.getInteralAST()))
                .getValue();
    }

    private RuntimeObject interpretGrammar(String input) {
        return new LL1GrammarInterpreter(grammar, input).parse().getFinalResult().values().stream().findFirst().orElseThrow();
    }

    private boolean compareResults(RuntimeObject ro1, RuntimeObject ro2) {
        return RuntimeObjectComparer.compareRuntimeObjects(ro1, ro2);
    }

    private List<String> loadInputs() {
        try {
            return Files.readAllLines(Path.of(new File(programPath).getParentFile().getAbsolutePath() + "/manual_inputs.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void logResult(int valid, int size, Map<String, String> errors) {
        String log = valid + "/" + size + " (" + (valid / (double) size) + ")";
        System.out.println(" >>> " + log + " <<< ");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(log);

            if (!errors.isEmpty()) {
                sb.append("\n\nErrors:");
                errors.entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue() + "\n").forEach(sb::append);
            }

            Files.writeString(Path.of(new File(programPath).getParentFile().getAbsolutePath() + "/result.txt"), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
