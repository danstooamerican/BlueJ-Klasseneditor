package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.diagram.ClassDiagram;

/**
 * Generates source code from a given {@link ClassDiagram class diagram}.
 */
public interface SourceCodeControl {

    /**
     * Generates source code for all elements in the given {@link ClassDiagram class diagram}.
     * The backup is placed in the same folder as the folder which contains the generated code.
     * The name of the backup folder is the generated code folder's name with a -backup suffix.
     * If the backup folder already exists it is overwritten.
     *
     * @param classDiagram the {@link ClassDiagram class diagram} which is generated.
     * @param generationType additional settings for the code generation.
     */
    void generateCode(ClassDiagram classDiagram, GenerationType generationType);

    /**
     * Creates a new {@link ClassDiagram class diagram} which is based on the source code.
     */
    ClassDiagram generateDiagram();

}
