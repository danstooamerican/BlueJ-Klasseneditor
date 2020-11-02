package class_diagram_editor.code_generation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeRepositoryTest {

    private static final String ELEMENT_NAME = "TestClass";
    private static final String METHOD_SIGNATURE = "public void testMethod(String name, boolean test, AddClass addClass)";
    private static final String METHOD_BODY = "this.test = test;" + System.lineSeparator() + "this.name = name;";
    private static final String SOURCE_CODE =
            "public class TestClass {" +
                "private String name;" +
                "private boolean test;" +

                "public TestClass(String test, int hello) {" +
                    "this.name = test;" +
                "}" +

                METHOD_SIGNATURE + " {" +
                    METHOD_BODY +
                "}" +
            "}";

    private CodeRepository codeRepository;

    @BeforeEach
    void setUp() {
        this.codeRepository = new CodeRepository(ELEMENT_NAME, SOURCE_CODE);
    }

    @Test
    void retrieveMethodBody_getsCorrectMethodBody() {
        String body = codeRepository.getMethodBody(METHOD_SIGNATURE);

        assertEquals(METHOD_BODY, body);
    }

    @Test
    void CodeRepository_noArgsHasNoMethods() {
        CodeRepository codeRepository = new CodeRepository();

        String sourceCode = codeRepository.getMethodBody(METHOD_SIGNATURE);

        assertEquals("", sourceCode);
    }

    @Test
    void CodeRepository_emptySourceCodeHasNoMethods() {
        CodeRepository codeRepository = new CodeRepository(ELEMENT_NAME, "");

        String sourceCode = codeRepository.getMethodBody(METHOD_SIGNATURE);

        assertEquals("", sourceCode);
    }

    @Test
    void CodeRepository_emptyElementNameHasNoMethods() {
        CodeRepository codeRepository = new CodeRepository("", SOURCE_CODE);

        String sourceCode = codeRepository.getMethodBody(METHOD_SIGNATURE);

        assertEquals("", sourceCode);
    }
}
