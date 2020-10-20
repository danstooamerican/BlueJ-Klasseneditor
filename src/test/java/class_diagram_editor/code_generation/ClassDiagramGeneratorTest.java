package class_diagram_editor.code_generation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ClassDiagramGeneratorTest {

    private static final String CLASS_1_NAME = "Class1";
    private static final String CLASS_METHOD_1_NAME = "classMethod";
    private static final String ATTRIBUTE_1_NAME = "testVar";
    private static final String ATTRIBUTE_1_TYPE = "String";

    private static final String INTERFACE_1_NAME = "Interface1";
    private static final String INTERFACE_METHOD_1_NAME = "interfaceMethod";
    private static final String INTERFACE_RETURN_TYPE_1 = "int";

    private static final String CLASS_1_SOURCE =
            "public class " + CLASS_1_NAME + " {" +
                "private final " + ATTRIBUTE_1_TYPE + " " + ATTRIBUTE_1_NAME + ";" +

                "public " + CLASS_1_NAME + "(" + ATTRIBUTE_1_TYPE + " test) {" +
                    "this." + ATTRIBUTE_1_NAME + " = test;" +
                "}" +

                "public " + ATTRIBUTE_1_TYPE + " " + CLASS_METHOD_1_NAME + "(int number) {" +
                    "return " + ATTRIBUTE_1_NAME + ";" +
                "}" +
            "}";

    private static final String INTERFACE_1_SOURCE =
            "public interface " + INTERFACE_1_NAME + " {" +
                "public " + INTERFACE_RETURN_TYPE_1 + " " + INTERFACE_METHOD_1_NAME + "(int number);" +
            "}";

    private ClassDiagramGenerator classDiagramGenerator;

    @BeforeEach
    void setUp() {
        this.classDiagramGenerator = new ClassDiagramGenerator();
    }

    @Test
    void addSource_parsesWithoutException() {
        assertDoesNotThrow(() -> {
            this.classDiagramGenerator.addSource(CLASS_1_NAME, CLASS_1_SOURCE);
        });

        assertDoesNotThrow(() -> {
            this.classDiagramGenerator.addSource(INTERFACE_1_NAME, INTERFACE_1_SOURCE);
        });
    }
}
