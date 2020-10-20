package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.diagram.ClassDiagram;

/**
 * Generates source code from a given {@link ClassDiagram class diagram}.
 */
public interface SourceCodeControl {

    /**
     * Generates source code for all elements in the given {@link ClassDiagram class diagram}.
     *
     * @param classDiagram the {@link ClassDiagram class diagram} which is generated.
     */
    void generateCode(ClassDiagram classDiagram);

    /**
     * Creates a new {@link ClassDiagram class diagram} which is based on the source code.
     */
    ClassDiagram generateDiagram();

}
