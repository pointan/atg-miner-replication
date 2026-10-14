package at.fhooe.se.atgminer.fuzzer;

import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATTerminal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>A representation of the derivation tree used in the fuzzing process</p>
 */
public class DerivationTree implements Iterable<DerivationTree> {

    private static int ID_CNT = 0;

    private final int id = ID_CNT++;
    private final ATElement element;
    private final List<DerivationTree> children;

    private boolean wasExpanded = false;

    public DerivationTree(ATElement element) {
        this.element = element;
        children = new ArrayList<>();
    }

    public boolean canExpand() {
        return !(element instanceof ATTerminal) && !wasExpanded;
    }

    public List<DerivationTree> getChildren() {
        return children;
    }

    public void addChildByElement(ATElement element) {
        this.getChildren().add(new DerivationTree(element));
    }

    public ATElement getElement() {
        return element;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(id + "[label=\"" + getElement().getKey() + "\"];\n");
        for (DerivationTree child : getChildren()) {
            res.append(id).append("->").append(child.id).append("\n");
            res.append(child);
        }
        return res.toString();
    }

    @Override
    public Iterator<DerivationTree> iterator() {
        List<DerivationTree> list = new ArrayList<>();
        new Consumer<DerivationTree>() {
            public void accept(DerivationTree tree) {
                list.add(tree);
                tree.getChildren().forEach(this);
            }
        }.accept(this);
        return list.iterator();
    }

    public Stream<DerivationTree> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public void setWasExpanded(boolean wasExpanded) {
        this.wasExpanded = wasExpanded;
    }

    public DerivationTree copy() {
        DerivationTree copy = new DerivationTree(this.element);
        copy.wasExpanded = this.wasExpanded;
        this.getChildren().stream().map(DerivationTree::copy).forEach(copy.getChildren()::add);
        return copy;
    }
}
