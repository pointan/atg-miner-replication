package at.fhooe.se.atgminer.atg.semantics;

import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

public abstract class DefSemantic extends BaseSemantic {
    public DefSemantic(InterpreterNode basedOn) {
        super(basedOn);
    }
}
