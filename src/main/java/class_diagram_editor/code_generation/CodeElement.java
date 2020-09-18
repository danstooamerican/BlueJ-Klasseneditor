package class_diagram_editor.code_generation;

public interface CodeElement {

    String getName();

    void accept(CodeGenerator codeGenerator);

}
