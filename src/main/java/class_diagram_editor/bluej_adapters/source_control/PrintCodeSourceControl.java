package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.JavaCodeGenerator;
import class_diagram_editor.diagram.ClassDiagram;

import java.util.Iterator;

/**
 * Test implementation which prints the generated source code to the console.
 */
public class PrintCodeSourceControl implements SourceCodeControl {
    @Override
    public void generateCode(ClassDiagram classDiagram) {
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
        return ClassDiagram.getInstance();
    }
}
