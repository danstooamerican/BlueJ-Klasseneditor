package class_diagram_editor.code_generation;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaCodeGeneratorTest {

    private static final String ELEMENT_NAME = "TestClass";
    private static final String METHOD_SIGNATURE = "public void testMethod(String name, boolean test, AddClass addClass)";
    private static final String METHOD_BODY = "this.test = test;" + System.lineSeparator() + "        this.name = name;" + System.lineSeparator();
    private static final String CLASS_SOURCE_CODE_WITH_BODY =
            "public class TestClass {" + System.lineSeparator() +
                "    private final String name;" + System.lineSeparator() +
                "    private boolean test;" + System.lineSeparator() + System.lineSeparator() +

                "    " + METHOD_SIGNATURE + " {" + System.lineSeparator() +
                "        " + METHOD_BODY +
                "    }" + System.lineSeparator() + "    " +  System.lineSeparator() + "    " + System.lineSeparator() + "    "  + System.lineSeparator() +
            "}" + System.lineSeparator();

    private static final String CLASS_SOURCE_CODE_NO_BODY =
            "public class TestClass {" + System.lineSeparator() +
            "    private final String name;" + System.lineSeparator() +
            "    private boolean test;" + System.lineSeparator() + System.lineSeparator() +

            "    " + METHOD_SIGNATURE + " {" + System.lineSeparator() +
            "    }" + System.lineSeparator() + "    " +  System.lineSeparator() + "    " + System.lineSeparator() + "    " + System.lineSeparator() +
            "}" + System.lineSeparator();

    private static final String INTERFACE_SOURCE_CODE =
            "public interface TestInterface {" + System.lineSeparator() +
            "    void testMethod(String name, boolean test, AddClass addClass);" + System.lineSeparator() +
            "}" + System.lineSeparator();

    private JavaCodeGenerator codeGenerator;
    private JavaCodeGenerator codeGeneratorWithCode;

    private ClassModel classModel;
    private InterfaceModel interfaceModel;

    @BeforeEach
    void setUp() {
        this.codeGenerator = new JavaCodeGenerator();
        this.codeGeneratorWithCode = new JavaCodeGenerator(ELEMENT_NAME, CLASS_SOURCE_CODE_WITH_BODY);

        initClassModel();
        initInterfaceModel();
    }

    private void initClassModel() {
        this.classModel = new ClassModel();
        classModel.setName("TestClass");

        classModel.addMethod(createTestMethod());

        AttributeModel attribute1 = new AttributeModel();
        attribute1.setName("name");
        attribute1.setType("String");
        attribute1.setVisibility(Visibility.PRIVATE);
        attribute1.setFinal(true);

        AttributeModel attribute2 = new AttributeModel();
        attribute2.setName("test");
        attribute2.setType("boolean");
        attribute2.setVisibility(Visibility.PRIVATE);

        classModel.addAttribute(attribute1);
        classModel.addAttribute(attribute2);
    }

    private void initInterfaceModel() {
        this.interfaceModel = new InterfaceModel();
        interfaceModel.setName("TestInterface");

        interfaceModel.addMethod(createTestMethod());
    }

    private MethodModel createTestMethod() {
        MethodModel methodModel = new MethodModel();
        methodModel.setName("testMethod");
        methodModel.setVisibility(Visibility.PUBLIC);

        AttributeModel parameter1 = new AttributeModel();
        parameter1.setName("name");
        parameter1.setType("String");

        AttributeModel parameter2 = new AttributeModel();
        parameter2.setName("test");
        parameter2.setType("boolean");

        AttributeModel parameter3 = new AttributeModel();
        parameter3.setName("addClass");
        parameter3.setType("AddClass");

        methodModel.addParameter(parameter1);
        methodModel.addParameter(parameter2);
        methodModel.addParameter(parameter3);

        return methodModel;
    }

    @Test
    void visitClass_noBody_generatesCorrectSourceCode() {
        codeGenerator.visitClass(classModel);

        assertEquals(CLASS_SOURCE_CODE_NO_BODY, codeGenerator.getLastGeneratedCode());
    }

    @Test
    void visitClass_withBody_generatesCorrectSourceCode() {
        codeGeneratorWithCode.visitClass(classModel);

        assertEquals(CLASS_SOURCE_CODE_WITH_BODY, codeGeneratorWithCode.getLastGeneratedCode());
    }

    @Test
    void visitInterface_generatesCorrectSourceCode() {
        codeGenerator.visitInterface(interfaceModel);

        assertEquals(INTERFACE_SOURCE_CODE, codeGenerator.getLastGeneratedCode());
    }
}
