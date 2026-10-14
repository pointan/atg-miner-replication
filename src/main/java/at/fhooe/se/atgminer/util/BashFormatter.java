package at.fhooe.se.atgminer.util;

/**
 * <p>Text coloring in bash using ANSI escape codes.</p>
 *
 * @see https://en.wikipedia.org/wiki/ANSI_escape_code#Select_Graphic_Rendition_parameters
 */
public class BashFormatter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_DEFAULT_COLOR = "\u001B[39m";
    private static final String ANSI_RAPID_BLINK = "\u001B[6m";
    private static final String ANSI_BLINK_OFF = "\u001B[25m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_BOLD_OFF = "\u001B[22m";

    private final boolean format;

    public BashFormatter(boolean format) {
        this.format = format;
    }

    public String black(String text) {
        return format ? color(text, ANSI_BLACK) : text;
    }

    public String red(String text) {
        return format ? color(text, ANSI_RED) : text;
    }

    public String green(String text) {
        return format ? color(text, ANSI_GREEN) : text;
    }

    public String yellow(String text) {
        return format ? color(text, ANSI_YELLOW) : text;
    }

    public String blue(String text) {
        return format ? color(text, ANSI_BLUE) : text;
    }

    public String purple(String text) {
        return format ? color(text, ANSI_PURPLE) : text;
    }
    public String cyan(String text) {
        return format ? color(text, ANSI_CYAN) : text;
    }

    public String white(String text) {
        return format ? color(text, ANSI_WHITE) : text;
    }

    public String blink(String text) {
        return format ? ANSI_RAPID_BLINK + text + ANSI_BLINK_OFF : text;
    }

    public String bold(String text) {
        return format ? ANSI_BOLD + text + ANSI_BOLD_OFF : text;
    }

    private String color(String text, String color) {
        return format ? color + text + ANSI_DEFAULT_COLOR : text;
    }

    public String indent(int indentation) {
        return format ? StringUtil.ident("  ", indentation) : "";
    }

    public String nl() {
        return format ? "\n" : "";
    }
}
