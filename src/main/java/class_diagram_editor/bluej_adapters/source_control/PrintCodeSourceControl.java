package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import class_diagram_editor.code_generation.SourceCodeControl;
import class_diagram_editor.diagram.ClassDiagram;

import java.util.Iterator;

public class PrintCodeSourceControl implements SourceCodeControl {
    @Override
    public void generate(ClassDiagram classDiagram) {
        Iterator<CodeElement> iterator = classDiagram.iterator();

        while (iterator.hasNext()) {
            CodeElement codeElement = iterator.next();

            final CodeGenerator codeGenerator = new CodeGenerator();

            codeElement.accept(codeGenerator);

            System.out.println(codeGenerator.getLastGeneratedCode());
            System.out.println();
        }
    }
}
