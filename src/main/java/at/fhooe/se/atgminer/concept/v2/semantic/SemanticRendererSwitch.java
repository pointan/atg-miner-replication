package at.fhooe.se.atgminer.concept.v2.semantic;

import java.util.StringJoiner;

import at.fhooe.se.atgminer.atg.semantics.AddSemantic;
import at.fhooe.se.atgminer.atg.semantics.AndSemantic;
import at.fhooe.se.atgminer.atg.semantics.AssignmentSemantic;
import at.fhooe.se.atgminer.atg.semantics.BlockSemantic;
import at.fhooe.se.atgminer.atg.semantics.ConstSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideIntSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideRealSemantic;
import at.fhooe.se.atgminer.atg.semantics.EqualSemantic;
import at.fhooe.se.atgminer.atg.semantics.ExpressionNodeSemantic;
import at.fhooe.se.atgminer.atg.semantics.FunctionSemantic;
import at.fhooe.se.atgminer.atg.semantics.InOutAttribute;
import at.fhooe.se.atgminer.atg.semantics.InSetSemantic;
import at.fhooe.se.atgminer.atg.semantics.MultiplySemantic;
import at.fhooe.se.atgminer.atg.semantics.NotEqualSemantic;
import at.fhooe.se.atgminer.atg.semantics.NotGreaterSemantic;
import at.fhooe.se.atgminer.atg.semantics.ResultPlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.ReturnPlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.SubtractSemantic;
import at.fhooe.se.atgminer.atg.semantics.SynthesizedAttributePlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.VariableSemantic;
import at.fhooe.se.atgminer.atg.semantics.WriteSemantic;

public class SemanticRendererSwitch extends SemanticSwitch<String> {

	@Override
	public String caseSynthesizedAttributePlaceholderSemantic(SynthesizedAttributePlaceholderSemantic object) {
		return String.format("out(%s -> %s)", doSwitch(object.getFormalParam()), doSwitch(object.getActualParam()));
	}

	@Override
	public String caseReturnPlaceholderSemantic(ReturnPlaceholderSemantic object) {
		return "return " + doSwitch(object.getResultValue());
	}

	@Override
	public String caseResultPlaceholderSemantic(ResultPlaceholderSemantic object) {
		return object.toString();
	}

	@Override
	public String caseAddSemantic(AddSemantic object) {
		return String.format("(%s + %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseAndSemantic(AndSemantic object) {
		return String.format("(%s and %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseAssignmentSemantic(AssignmentSemantic object) {
		return String.format("%s = %s", doSwitch(object.getLeftHandSide()), doSwitch(object.getRightHandSide()));
	}

	@Override
	public String caseBlockSemantic(BlockSemantic object) {
		StringJoiner s = new StringJoiner("; ");
		object.getSemantics().forEach((e) -> s.add(doSwitch(e)));
		return s.toString();
	}

	@Override
	public String caseDivideIntSemantic(DivideIntSemantic object) {
		return String.format("(%s div %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseDivideRealSemantic(DivideRealSemantic object) {
		return String.format("(%s / %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseVariableSemantic(VariableSemantic object) {
		return object.toString();
	}

	@Override
	public String caseInOutAttribute(InOutAttribute object) {
		return object.toString();
	}

	@Override
	public String caseEqualSemantic(EqualSemantic object) {
		return String.format("(%s == %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseFunctionSemantic(FunctionSemantic object) {
		return object.toString();
	}

	@Override
	public String caseMultiplySemantic(MultiplySemantic object) {
		return String.format("(%s * %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseNotEqualSemantic(NotEqualSemantic object) {
		return String.format("(%s != %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseNotGreaterSemantic(NotGreaterSemantic object) {
		return String.format("(%s <= %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseConstSemantic(ConstSemantic object) {
		return object.toString();
	}

	@Override
	public String caseSubtractSemantic(SubtractSemantic object) {
		return String.format("(%s - %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}

	@Override
	public String caseWriteSemantic(WriteSemantic object) {
		StringJoiner str = new StringJoiner(",", "Write(", ")");
		object.getChildren().forEach((e) -> str.add(doSwitch(e)));
		return str.toString();
	}

	@Override
	public String caseInSetSemantic(InSetSemantic object) {
		return String.format("(%s in %s)", doSwitch(object.getLeftOperand()), doSwitch(object.getRightOperand()));
	}
	
	@Override
	public String caseExpressionNodeSemantic(ExpressionNodeSemantic object) {
		return object.toString();
	}

}
