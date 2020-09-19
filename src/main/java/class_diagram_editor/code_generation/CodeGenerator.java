package class_diagram_editor.code_generation;

import class_diagram_editor.code_generation.generators.InterfaceGenerator;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.code_generation.ClassGenerator;
import lombok.Getter;

@Getter
public class CodeGenerator {

    private static final InterfaceGenerator interfaceGenerator = new InterfaceGenerator();
    private static final ClassGenerator classGenerator = new ClassGenerator();

    private String lastGeneratedCode;

    private void generate(String code) {
        lastGeneratedCode = code;
    }

    public void visitClass(ClassModel classModel) {
        generate(classGenerator.generate(classModel));
    }

    public void visitInterface(InterfaceModel interfaceModel) {
        generate(interfaceGenerator.generate(interfaceModel));
    }

}
