package class_diagram_editor.code_generation;

import class_diagram_editor.code_generation.generators.InterfaceGenerator;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.code_generation.ClassGenerator;
import lombok.Getter;

/**
 * Generates Java code from UML models.
 */
public class JavaCodeGenerator {
    private static final InterfaceGenerator interfaceGenerator = new InterfaceGenerator();
    private static final ClassGenerator classGenerator = new ClassGenerator();

    private final CodeRepository codeRepository;

    @Getter
    private String lastGeneratedCode;

    /**
     * Creates a new {@link JavaCodeGenerator}.
     */
    public JavaCodeGenerator() {
        this.codeRepository = new CodeRepository();
    }

    /**
     * Creates a new {@link JavaCodeGenerator} which preserves preexisting method bodies of a Java class.
     *
     * @param elementName the name of the class.
     * @param sourceCode the source code of the class.
     */
    public JavaCodeGenerator(String elementName, String sourceCode) {
        this.codeRepository = new CodeRepository(elementName, sourceCode);
    }

    private void generate(String code) {
        lastGeneratedCode = code;
    }

    /**
     * Generates the Java source code for the given {@link ClassModel class}. The source code can be retrieved by
     * calling {@code getLastGeneratedCode()}.
     *
     * @param classModel the {@link ClassModel class} which is generated.
     */
    public void visitClass(ClassModel classModel) {
        final String sourceCode = classGenerator.generate(classModel, codeRepository);

        generate(sourceCode);
    }

    /**
     * Generates the Java source code for the given {@link InterfaceModel interface}. The source code can be retrieved by
     * calling {@code getLastGeneratedCode()}.
     *
     * @param interfaceModel the {@link InterfaceModel class} which is generated.
     */
    public void visitInterface(InterfaceModel interfaceModel) {
        final String sourceCode = interfaceGenerator.generate(interfaceModel);

        generate(sourceCode);
    }

}
