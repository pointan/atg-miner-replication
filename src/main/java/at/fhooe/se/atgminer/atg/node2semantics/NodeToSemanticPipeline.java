package at.fhooe.se.atgminer.atg.node2semantics;

import at.fhooe.se.atgminer.atg.node2semantics.impl.*;
import at.fhooe.se.atgminer.atg.semantics.BaseSemantic;
import at.fhooe.se.atgminer.util.CastUtil;
import at.fhooe.se.codeanalysis.adele.interpreter.node.InterpreterNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A pipeline that composes multiple {@link NodeToSemantic} processors, and uses the first processors where the
 * {@link NodeToSemantic#canProcess(InterpreterNode)} accepts the input.
 * </p>
 */
public class NodeToSemanticPipeline implements NodeToSemantic<InterpreterNode, BaseSemantic> {

    private final List<NodeToSemantic<? extends InterpreterNode, ? extends BaseSemantic>> processors = new ArrayList<>();

    public NodeToSemanticPipeline addProcessor(
            NodeToSemantic<? extends InterpreterNode, ? extends BaseSemantic> processor) {
        this.processors.add(processor);
        return this;
    }

    @Override
    public boolean canProcess(InterpreterNode node) {
        return true;
    }

    @Override
    public BaseSemantic process(InterpreterNode node) {
        for (NodeToSemantic<? extends InterpreterNode, ? extends BaseSemantic> processor : processors) {
            if (processor.canProcess(node)) {
                return processor.process(CastUtil.cast(node));
            }
        }
        throw new IllegalStateException(
                "No processor was able to process the node of type " + node.getClass().getSimpleName());
    }

    @Deprecated
    public static NodeToSemantic<InterpreterNode, BaseSemantic> createPipeline() {
        NodeToSemanticPipeline pipeline = new NodeToSemanticPipeline();
        return pipeline.addProcessor(new AddNodeToAddSemantic(pipeline))
                .addProcessor(new SubtractNodeToSubtractSemantic(pipeline))
                .addProcessor(new MultiplyNodeToMultiplySemantic(pipeline))
                .addProcessor(new DivideIntNodeToDivideIntSemantic(pipeline))
                .addProcessor(new DivideRealNodeToDivideRealSemantic(pipeline))
                .addProcessor(new TruncNodeToTruncateSemantic(pipeline))
                .addProcessor(new OrdNodeToOrdSemantic(pipeline))
                .addProcessor(new ActualParamInOutNodeToBaseSemantic(pipeline))
                .addProcessor(new OrNodeToOrSemantic(pipeline)).addProcessor(new AndNodeToAndSemantic(pipeline))
                .addProcessor(new NotEqualNodeToNotEqualSemantic(pipeline))
                .addProcessor(new EqualNodeToEqualSemantic(pipeline))
                .addProcessor(new UnaryMinusNodeToUnaryMinusSemantic(pipeline))
                .addProcessor(new IdentifierNodeToVariableSemantic())
                .addProcessor(new ValueLiteralNodeToConstSemantic())
                .addProcessor(new FuncCallNodeToNonTerminalAttribute());
    }

    /**
     * The concept implementation (version 2) uses the pipeline constructed by this method. Note that this pipeline is
     * complete in the sense that every interpreter node is transformed into a semantic node.
     *
     * @return the pipeline of transformation processors for the concept implementation
     */
    public static NodeToSemantic<InterpreterNode, BaseSemantic> createPipeline2() {
        NodeToSemanticPipeline pipeline = new NodeToSemanticPipeline();
        pipeline.addProcessor(new FuncCallNodeToResultPlaceholder(pipeline));
        return buildDefaultPipeline(pipeline);
    }

    public static NodeToSemantic<InterpreterNode, BaseSemantic> createPipelineWithoutFunctionPlaceholder() {
        NodeToSemanticPipeline pipeline = new NodeToSemanticPipeline();
        pipeline.addProcessor(new FuncCallNodeToFuncInvokeSemantic(pipeline));
        return buildDefaultPipeline(pipeline);
    }

    private static NodeToSemanticPipeline buildDefaultPipeline(NodeToSemanticPipeline pipeline) {
        return pipeline.addProcessor(new AddNodeToAddSemantic(pipeline))
                .addProcessor(new SubtractNodeToSubtractSemantic(pipeline))
                .addProcessor(new MultiplyNodeToMultiplySemantic(pipeline))
                .addProcessor(new DivideIntNodeToDivideIntSemantic(pipeline))
                .addProcessor(new DivideRealNodeToDivideRealSemantic(pipeline))
                .addProcessor(new TruncNodeToTruncateSemantic(pipeline))
                .addProcessor(new OrdNodeToOrdSemantic(pipeline))
                .addProcessor(new ActualParamInOutNodeToBaseSemantic(pipeline))
                .addProcessor(new OrNodeToOrSemantic(pipeline)).addProcessor(new AndNodeToAndSemantic(pipeline))
                .addProcessor(new NotEqualNodeToNotEqualSemantic(pipeline))
                .addProcessor(new EqualNodeToEqualSemantic(pipeline))
                .addProcessor(new NotGreaterNodeToNotGreaterSemantic(pipeline))
                .addProcessor(new UnaryMinusNodeToUnaryMinusSemantic(pipeline))
                .addProcessor(new IdentifierNodeToVariableSemantic())
                .addProcessor(new ValueLiteralNodeToConstSemantic())
                .addProcessor(new DecimalFieldWidthNodeToIdentiySemantic(pipeline))
                .addProcessor(new StringLengthNodeToStrLenSemantic(pipeline))
                .addProcessor(new SetConstructorNodeToSetConstructorSemantic(pipeline))
                .addProcessor(new InSetNodeToInSetSemantic(pipeline))
                .addProcessor(new MethodInvokeNodeToResultPlaceholder(pipeline))
                .addProcessor(new PythonListNodeToListSemantic(pipeline))
                .addProcessor(new ArrayAccessNodeToArrayAccessSemantic(pipeline))
                .addProcessor(new PythonDictNodeToDictNode(pipeline))
                .addProcessor(new ChrNodeToChrSemantic(pipeline))
                .addProcessor(new QualifiedIdentifierNodeToQualifiedIdentifierSemantic(pipeline))
                .addProcessor(new SliceNodeToSliceSemantic(pipeline))
                .addProcessor(new LessNodeToLessSemantic(pipeline))
                .addProcessor(new PythonAssignNodeToAssignSemantic(pipeline))
                .addProcessor(new BitOrNodeToBitOrSemantic(pipeline))
                .addProcessor(new BitShiftLeftNodeToBitShiftLeftSemantic(pipeline))
                .addProcessor(new DivideNodeToDivideRealSemantic(pipeline))
                .addProcessor(new GreaterNodeToGreaterSemantic(pipeline))
                .addProcessor(new InputNodeToInputSemantic())
                .addProcessor(new FloatNodeToFloatSemantic(pipeline))
                .addProcessor(new PrintNodeToPrintSemantic(pipeline))
                .addProcessor(new LenNodeToLenSemantic(pipeline))
                .addProcessor(new PythonIntNodeToIntSemantic(pipeline))
                .addProcessor(new NoneNodeToNullSemantic())
                .addProcessor(new WhileStmtNodeToWhileSemantic(pipeline))
                .addProcessor(new ExprStmtNodeToSemantic(pipeline))
                .addProcessor(new BlockStmtNodeToBlockSemantic(pipeline))
                .addProcessor(new IfStmtNodeToIfSemantic(pipeline))
                .addProcessor(new ThrowNodeToErrorSemantic())
                .addProcessor(new EmptyStmtNodeToEmptySemantic())
                .addProcessor(new ReturnStmtNodeToReturnSemantic(pipeline))
                .addProcessor(new MethodDefNodeToMethodDefSemantic(pipeline))
                .addProcessor(new FuncDefNodeToFuncDefSemantic(pipeline))
                .addProcessor(new StdFuncCallNodeToStdFuncSemantic(pipeline)) // should be down at the bottom as it accepts every stdFuncCall so every specific one has to be above
                .addProcessor(new ExpressionNodeToSemantic()) // must be the last processor because it accepts everything
        ;
    }

}
