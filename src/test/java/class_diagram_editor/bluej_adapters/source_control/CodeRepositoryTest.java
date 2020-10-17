package class_diagram_editor.bluej_adapters.source_control;

import class_diagram_editor.code_generation.CodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeRepositoryTest {

    private static final String ELEMENT_NAME = "TestClass";
    private static final String SOURCE_CODE =
            "public class TestClass {" +
                "private String name;" +
                "private boolean test;" +

                "public void testMethod(String name, boolean test, AddClass addClass) {" +
                    "this.test = test;" +
                    "this.name = name;" +
                "}" +
            "}";

    private CodeRepository codeRepository;

    @BeforeEach
    void setUp() {
        this.codeRepository = new CodeRepository(ELEMENT_NAME, SOURCE_CODE);
    }

    @Test
    void retrieveMethodBody_noError() {
        String body = codeRepository.getMethodBody("public void testMethod(String name, boolean test, AddClass addClass)");

        assertEquals("this.test = test;\nthis.name = name;", body);
    }
}
