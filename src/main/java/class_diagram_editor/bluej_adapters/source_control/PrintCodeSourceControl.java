package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.code_generation.ClassDiagramGenerator;
import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.JavaCodeGenerator;
import class_diagram_editor.diagram.ClassDiagram;

import java.util.Iterator;

/**
 * Test implementation which prints the generated source code to the console.
 */
public class PrintCodeSourceControl implements SourceCodeControl {

    /**
     * Generates the Java source code for all elements in the given {@link ClassDiagram class diagram}
     * and prints out the result.
     *
     * This implementation ignores the {@code performBackup} flag and never performs a backup.
     *
     * @param classDiagram the {@link ClassDiagram class diagram} which is generated.
     * @param generationType additional settings for the code generation (ignored).
     */
    @Override
    public void generateCode(ClassDiagram classDiagram, GenerationType generationType) {
        Iterator<CodeElement> iterator = classDiagram.iterator();

        while (iterator.hasNext()) {
            CodeElement codeElement = iterator.next();

            final JavaCodeGenerator codeGenerator = new JavaCodeGenerator();

            if (!codeElement.getName().equals(codeElement.getLastGeneratedName())) {
                System.out.println("This element was renamed from " + codeElement.getLastGeneratedName());
            }

            codeElement.accept(codeGenerator);

            System.out.println(codeGenerator.getLastGeneratedCode());
            System.out.println();
        }
    }

    @Override
    public ClassDiagram generateDiagram() {
        ClassDiagramGenerator classDiagramGenerator = new ClassDiagramGenerator();

        return classDiagramGenerator.generate();
    }

    @Override
    public String getGenerationDirPath() {
        return null;
    }
}
