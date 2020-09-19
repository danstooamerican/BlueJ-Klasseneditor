package class_diagram_editor.diagram;

public enum Visibility {
    PUBLIC("+", "public"),
    PRIVATE("-", "private"),
    PROTECTED("#", "protected"),
    PACKAGE_PRIVATE("~", "");

    private final String symbol;
    private final String code;

    Visibility(String symbol, String code) {
        this.symbol = symbol;
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCode() {
        return code;
    }
}
