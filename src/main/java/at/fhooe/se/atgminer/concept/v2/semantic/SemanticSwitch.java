package at.fhooe.se.atgminer.concept.v2.semantic;

import java.util.Objects;

import at.fhooe.se.atgminer.atg.semantics.AddSemantic;
import at.fhooe.se.atgminer.atg.semantics.AndSemantic;
import at.fhooe.se.atgminer.atg.semantics.AssignmentSemantic;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.atg.semantics.BlockSemantic;
import at.fhooe.se.atgminer.atg.semantics.ConstSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideIntSemantic;
import at.fhooe.se.atgminer.atg.semantics.DivideRealSemantic;
import at.fhooe.se.atgminer.atg.semantics.EmptySemantic;
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
import at.fhooe.se.atgminer.atg.semantics.SetConstructorSemantic;
import at.fhooe.se.atgminer.atg.semantics.SubtractSemantic;
import at.fhooe.se.atgminer.atg.semantics.SynthesizedAttributePlaceholderSemantic;
import at.fhooe.se.atgminer.atg.semantics.VariableSemantic;
import at.fhooe.se.atgminer.atg.semantics.WriteSemantic;

public class SemanticSwitch<T> {

	public SemanticSwitch() {
	}

	public T doSwitchOptional(BaseSemantic object) {
		if (object != null) {
			return doSwitch(object);
		} else {
			return null;
		}
	}

	public T doSwitch(BaseSemantic object) {
		Objects.requireNonNull(object);

		if (object instanceof AddSemantic) {
			return caseAddSemantic((AddSemantic) object);
		} else if (object instanceof AndSemantic) {
			return caseAndSemantic((AndSemantic) object);
		} else if (object instanceof AssignmentSemantic) {
			return caseAssignmentSemantic((AssignmentSemantic) object);
		} else if (object instanceof BlockSemantic) {
			return caseBlockSemantic((BlockSemantic) object);
		} else if (object instanceof DivideIntSemantic) {
			return caseDivideIntSemantic((DivideIntSemantic) object);
		} else if (object instanceof DivideRealSemantic) {
			return caseDivideRealSemantic((DivideRealSemantic) object);
		} else if (object instanceof EqualSemantic) {
			return caseEqualSemantic((EqualSemantic) object);
		} else if (object instanceof FunctionSemantic) {
			return caseFunctionSemantic((FunctionSemantic) object);
		} else if (object instanceof MultiplySemantic) {
			return caseMultiplySemantic((MultiplySemantic) object);
		} else if (object instanceof NotEqualSemantic) {
			return caseNotEqualSemantic((NotEqualSemantic) object);
		} else if (object instanceof NotGreaterSemantic) {
			return caseNotGreaterSemantic((NotGreaterSemantic) object);
		} else if (object instanceof SubtractSemantic) {
			return caseSubtractSemantic((SubtractSemantic) object);
		} else if (object instanceof ResultPlaceholderSemantic) {
			return caseResultPlaceholderSemantic((ResultPlaceholderSemantic) object);
		} else if (object instanceof ReturnPlaceholderSemantic) {
			return caseReturnPlaceholderSemantic((ReturnPlaceholderSemantic) object);
		} else if (object instanceof SynthesizedAttributePlaceholderSemantic) {
			return caseSynthesizedAttributePlaceholderSemantic((SynthesizedAttributePlaceholderSemantic) object);
		} else if (object instanceof EmptySemantic) {
			return caseEmptySemantic((EmptySemantic) object);
		} else if (object instanceof InOutAttribute) {
			return caseInOutAttribute((InOutAttribute) object);
		} else if (object instanceof VariableSemantic) {
			return caseVariableSemantic((VariableSemantic) object);
		} else if (object instanceof ConstSemantic) {
			return caseConstSemantic((ConstSemantic) object);
		} else if (object instanceof WriteSemantic) {
			return caseWriteSemantic((WriteSemantic) object);
		} else if (object instanceof SetConstructorSemantic) {
			return caseSetConstructorSemantic((SetConstructorSemantic) object);
		} else if (object instanceof InSetSemantic) {
			return caseInSetSemantic((InSetSemantic) object);
		} else if (object instanceof ExpressionNodeSemantic) {
			return caseExpressionNodeSemantic((ExpressionNodeSemantic) object);
		} else {
			System.out.println("## Unhandled semantic class " + object.getClass().getSimpleName());
			return null;
		}
	}

	public T caseExpressionNodeSemantic(ExpressionNodeSemantic object) {
		return null;
	}

	public T caseConstSemantic(ConstSemantic object) {
		return null;
	}

	public T caseSynthesizedAttributePlaceholderSemantic(SynthesizedAttributePlaceholderSemantic object) {
		return null;
	}

	public T caseVariableSemantic(VariableSemantic object) {
		return null;
	}

	public T caseInOutAttribute(InOutAttribute object) {
		return null;
	}

	public T caseEmptySemantic(EmptySemantic object) {
		return null;
	}

	public T caseReturnPlaceholderSemantic(ReturnPlaceholderSemantic object) {
		return null;
	}

	public T caseResultPlaceholderSemantic(ResultPlaceholderSemantic object) {
		return null;
	}

	public T caseAddSemantic(AddSemantic object) {
		return null;
	}

	public T caseAndSemantic(AndSemantic object) {
		return null;
	}

	public T caseAssignmentSemantic(AssignmentSemantic object) {
		return null;
	}

	public T caseBlockSemantic(BlockSemantic object) {
		return null;
	}

	public T caseDivideIntSemantic(DivideIntSemantic object) {
		return null;
	}

	public T caseDivideRealSemantic(DivideRealSemantic object) {
		return null;
	}

	public T caseEqualSemantic(EqualSemantic object) {
		return null;
	}

	public T caseFunctionSemantic(FunctionSemantic object) {
		return null;
	}

	public T caseMultiplySemantic(MultiplySemantic object) {
		return null;
	}

	public T caseNotEqualSemantic(NotEqualSemantic object) {
		return null;
	}

	public T caseNotGreaterSemantic(NotGreaterSemantic object) {
		return null;
	}

	public T caseSubtractSemantic(SubtractSemantic object) {
		return null;
	}

	public T caseWriteSemantic(WriteSemantic object) {
		return null;
	}

	public T caseInSetSemantic(InSetSemantic object) {
		return null;
	}

	public T caseSetConstructorSemantic(SetConstructorSemantic object) {
		return null;
	}

}
