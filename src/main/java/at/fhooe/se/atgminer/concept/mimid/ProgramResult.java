package at.fhooe.se.atgminer.concept.mimid;

import java.util.List;

import at.fhooe.se.codeanalysis.adele.interpreter.InterpreterProblem;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ClassObject;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.ClassType;
import at.fhooe.se.codeanalysis.adele.interpreter.runtime.RuntimeObject;

public class ProgramResult {

	private final RuntimeObject resultValue;
	private final List<InterpreterProblem> problemList;

	public ProgramResult(RuntimeObject resultValue, List<InterpreterProblem> problemList) {
		this.resultValue = resultValue;
		this.problemList = problemList;
	}

	public List<InterpreterProblem> getProblems() {
		return problemList;
	}

	public RuntimeObject getValue() {
		return resultValue;
	}

	public boolean isValid() {
		if (resultValue == RuntimeObject.VOID) {
			return false;
		} else if (resultValue.getType().isInteger()) {
			return resultValue.intValue() == 0;
		} else {
			// Python
			if (resultValue.getType().isClass()
					&& (resultValue.getType().getTypeName().equals("Exception") || ((ClassType) resultValue.getType())
							.getBaseTypes().stream().anyMatch(bt -> bt.getTypeName().equals("Exception")))) {
				return false;
			}
			return !resultValue.toString().contains("O[class Exception");
		}
	}
	
	public String getResultDebugString() {
		if (resultValue.getType().isClass()) {
			return ((ClassObject)resultValue).toDebugString();
		}
		else {
			return resultValue.toString();
		}
	}

}
