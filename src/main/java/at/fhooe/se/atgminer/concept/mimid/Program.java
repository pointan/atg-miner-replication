package at.fhooe.se.atgminer.concept.mimid;

import java.io.File;
import java.util.function.Function;

import at.fhooe.se.atgminer.concept.v2.pdf.ComparisonSet;
import at.fhooe.se.codeanalysis.AbstractSyntaxTree;
import at.fhooe.se.codeanalysis.PythonAbstractSyntaxTree;
import at.fhooe.se.codeanalysis.adele.interpreter.DefaultStandardOutput;
import at.fhooe.se.codeanalysis.adele.interpreter.Interpreter;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterFactory;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterListener;
import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterSystem;
import at.fhooe.se.codeanalysis.adele.interpreter.PythonInterpreterFactory;
import at.fhooe.se.codeanalysis.adele.interpreter.python.PythonLoader;
import at.scch.codeanalytics.gastm.FunctionDefinition;

public class Program {

	public static Program fromFile(File pythonFile) {
		var mLoader = PythonLoader.fromDirectory(pythonFile.getParentFile().getAbsolutePath());
		var ast = PythonAbstractSyntaxTree.parseFile(pythonFile);
		var factory = new PythonInterpreterFactory(mLoader, ast.getInteralAST());
		return new Program(ast, factory);
	}

	private final AbstractSyntaxTree ast;
	private final PythonInterpreterFactory factory;
	private Interpreter se;
	private boolean withStdOut = true;

	public Program(AbstractSyntaxTree ast, PythonInterpreterFactory factory) {
		this.ast = ast;
		this.factory = factory;
	}

	public ProgramResult runWithInput(String input) {
		return runWithInput(input, (InterpreterListener) null);
	}

	public ProgramResult runWithInput(String input, InterpreterListener l) {
		return runWithInput(input, l, ast.getInternalEntryPoint());
	}

	public ProgramResult runWithInput(String input, Function<AbstractSyntaxTree, FunctionDefinition> entryPointExtractor) {
		return runWithInput(input, null, entryPointExtractor.apply(ast));
	}

	public ProgramResult runWithInput(String input, InterpreterListener l, FunctionDefinition entryPoint) {
		se = factory.createInterpreterWithInput(input);
		if (withStdOut) InterpreterSystem.setStdOut(new DefaultStandardOutput());
		if (l != null)
			se.addListener(l);
		var result = se.call(entryPoint);
		if (l != null)
			se.removeListener(l);
		return new ProgramResult(result, se.getProblems());
	}


	public FunctionDefinition getInternalEntryPoint() {
		return ast.getInternalEntryPoint();
	}

	public InterpreterFactory getFactory() {
		return factory;
	}

	public ComparisonSet getComparisions() {
		var result = new ComparisonSet();
		if (se != null) {
			result.fillFromPathCondition(se.getGlobals().getSymbolicFrame().getPathConditionList());
		}
		return result;
	}

	public Program withStdOut(boolean withStdOut) {
		this.withStdOut = withStdOut;
		return this;
	}

}
