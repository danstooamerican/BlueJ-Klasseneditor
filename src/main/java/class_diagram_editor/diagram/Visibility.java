package class_diagram_editor.diagram;

public enum Visibility {
    PUBLIC("+"),
    PRIVATE("-"),
    PROTECTED("#"),
    PACKAGE_PRIVATE("~");

    private final String symbol;

    Visibility(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
