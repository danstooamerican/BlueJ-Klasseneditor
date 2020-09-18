package class_diagram_editor.code_generation;

import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.code_generation.ClassGenerator;
import lombok.Getter;

@Getter
public class CodeGenerator {

    private String lastGeneratedCode;

    private void generate(String code) {
        lastGeneratedCode = code;
    }

    public void visitClass(ClassModel classModel) {
        ClassGenerator classGenerator = new ClassGenerator();

        generate(classGenerator.generate(classModel));
    }

    public void visitInterface(InterfaceModel interfaceModel) {

    }

}
