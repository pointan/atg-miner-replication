package at.fhooe.se.atgminer.atg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ATTerminalGroup extends ATTerminal {

    public static final ATTerminalGroup NUMBER = new ATTerminalGroup("number", "0123456789".chars().mapToObj(x -> String.valueOf((char) x)).collect(Collectors.toList()), List.of());
    public static final ATTerminalGroup ALPHA = new ATTerminalGroup("alpha", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(x -> String.valueOf((char) x)).collect(Collectors.toList()), List.of());
    public static final ATTerminalGroup ALPHA_NUM = new ATTerminalGroup("alphanum", List.of(), List.of(NUMBER, ALPHA));
    public static final ATTerminalGroup HEX = new ATTerminalGroup("hexnumber", "abcdefABCDEF".chars().mapToObj(x -> String.valueOf((char) x)).collect(Collectors.toList()), List.of(NUMBER));

    public static final ATTerminalGroup ANY = new ATTerminalGroup("any", "!#$&'()*+,-./:;<=>?@[]^_`{|}~".chars().mapToObj(x -> String.valueOf((char) x)).collect(Collectors.toList()), List.of(ALPHA_NUM));

    private final List<String> tokens;
    private final List<ATTerminalGroup> basedOn;

    private ATTerminalGroup(String groupName, List<String> tokens, List<ATTerminalGroup> basedOn) {
        super(groupName);
        this.tokens = Objects.requireNonNull(tokens);
        this.basedOn = Objects.requireNonNull(basedOn);
    }

    public List<String> getTokens() {
        List<String> res = new ArrayList<>(tokens);
        basedOn.forEach(x -> res.addAll(x.getTokens()));
        return res;
    }

    public List<String> getGroupSpecificToken() {
        return tokens;
    }

    public List<ATTerminalGroup> getBasedOn() {
        return basedOn;
    }

    public boolean isBasedOn(ATTerminalGroup group) {
        return getBasedOn().contains(group) || getBasedOn().stream().anyMatch(x -> x.isBasedOn(group));
    }

    @Override
    public <T> T accept(ATElementVisitor<T> v) {
        return v.visitTerminalGroup(this);
    }
}
