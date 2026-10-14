package at.fhooe.se.atgminer.atg;

import java.util.*;

public class ATChoice extends ATElement {

    private final List<ATElement> choices;

    ATChoice(ATElement[] choices) {
        // create a mutable list here, in order to be able to postprocess this
        // afterwards.
        this.choices = new ArrayList<>(Arrays.asList(choices));
    }

    @Override
    public List<ATElement> getElements() {
        return choices;
    }

    @Override
    public <T> T accept(ATElementVisitor<T> v) {
        return v.visitChoice(this);
    }

    @Override
    public String getKey() {
        return "(Choice-" +getID()+ ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ATChoice))
            return false;
        ATChoice atChoice = (ATChoice) o;
        return choices.equals(atChoice.choices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(choices);
    }

    public void add(ATElement e) {
        choices.add(e);
    }

    public void add(int index, ATElement e) {
        choices.add(index, e);
    }

    public void remove(ATElement e) {
        choices.remove(e);
    }

}
