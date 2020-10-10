package class_diagram_editor.diagram;

/**
 * Represents the visibility of Java elements.
 */
public enum Visibility {
    PUBLIC("+", "public"),
    PRIVATE("-", "private"),
    PROTECTED("#", "protected"),
    PACKAGE_PRIVATE("~", "");

    private final String symbol;
    private final String code;

    /**
     * Creates a new {@link Visibility}.
     *
     * @param symbol the corresponding UML symbol.
     * @param code the corresponding Java source code.
     */
    Visibility(String symbol, String code) {
        this.symbol = symbol;
        this.code = code;
    }

    /**
     * @return the UML symbol representing this {@link Visibility visibility}.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the Java source code representing this {@link Visibility visibility}.
     */
    public String getCode() {
        return code;
    }
}
