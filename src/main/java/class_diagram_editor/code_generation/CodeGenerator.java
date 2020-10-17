package class_diagram_editor.code_generation;

import class_diagram_editor.code_generation.generators.InterfaceGenerator;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.code_generation.ClassGenerator;
import lombok.Getter;


public class CodeGenerator {
    private static final InterfaceGenerator interfaceGenerator = new InterfaceGenerator();
    private static final ClassGenerator classGenerator = new ClassGenerator();

    private final CodeRepository codeRepository;

    @Getter
    private String lastGeneratedCode;

    public CodeGenerator() {
        this.codeRepository = new CodeRepository();
    }

    public CodeGenerator(String elementName, String sourceCode) {
        this.codeRepository = new CodeRepository(elementName, sourceCode);
    }

    private void generate(String code) {
        lastGeneratedCode = code;
    }

    public void visitClass(ClassModel classModel) {
        final String sourceCode = classGenerator.generate(classModel, codeRepository);

        generate(sourceCode);
    }

    public void visitInterface(InterfaceModel interfaceModel) {
        final String sourceCode = interfaceGenerator.generate(interfaceModel);

        generate(sourceCode);
    }

}
