package at.fhooe.se.atgminer.atg.semanticsmerge;

import at.fhooe.se.atgminer.atg.semantics.*;

/**
 * <p>Merges two semantics</p>
 */
public class SemanticMerge {
    public BaseSemantic merge(BaseSemantic sem1, BaseSemantic sem2) {
        if (sem1 instanceof NotVisitedPlaceholderSemantic) return sem2;
        if (sem2 instanceof NotVisitedPlaceholderSemantic) return sem1;
        if (!sem1.getClass().equals(sem2.getClass())) {
            throw new IllegalStateException("Cannot merge classes of incompatible types");
        }

        if (sem1 instanceof BlockSemantic) {
            return mergeBlocks((BlockSemantic) sem1, (BlockSemantic) sem2);
        }

        if (sem1 instanceof IfSemantic) {
            return mergeIf((IfSemantic) sem1, (IfSemantic) sem2);
        }

        if (sem1 instanceof WhileSemantic) {
            return mergeWhile((WhileSemantic) sem1, (WhileSemantic) sem2);
        }

        // default path:
        if (!sem1.equals(sem2)) {
            throw new IllegalStateException("Elements cannot be merged, and are not equal!");
        } else {
            return sem1;
        }
    }

    private BlockSemantic mergeBlocks (BlockSemantic sem1, BlockSemantic sem2) {
        // if only one contains semantics, we use that
        if (sem1.getChildren().isEmpty()) return sem2;
        if (sem2.getChildren().isEmpty()) return sem1;
        if (sem1.getChildren().size() != sem2.getChildren().size()) {
            throw new IllegalStateException("Cannot merge blocks of unequal size");
        }
        BlockSemantic merged = new BlockSemantic();
        for (int i = 0; i < sem1.getChildren().size(); i++) {
            BaseSemantic elem = merge(sem1.getChildren().get(i), sem2.getChildren().get(i));
            merged.addSemantic(elem);
        }
        return merged;
    }

    private IfSemantic mergeIf(IfSemantic sem1, IfSemantic sem2) {
        return new IfSemantic(
                merge(sem1.getCondition(), sem2.getCondition()),
                merge(sem1.getThenBlock(), sem2.getThenBlock()),
                merge(sem1.getElseBlock(), sem2.getElseBlock())
        );
    }

    private WhileSemantic mergeWhile(WhileSemantic sem1, WhileSemantic sem2) {
        return new WhileSemantic(
                merge(sem1.getCondition(), sem2.getCondition()),
                merge(sem1.getBody(), sem2.getBody())
        );
    }
}
