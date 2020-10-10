package class_diagram_editor.diagram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodModelTest {

    private MethodModel methodModel;

    @BeforeEach
    void setUp() {
        this.methodModel = new MethodModel();
    }

    @Test
    void addParameter_addsParameter() {
        AttributeModel attributeModel = new AttributeModel();

        methodModel.addParameter(attributeModel);

        List<AttributeModel> attributes = methodModel.getParameters();

        assertNotNull(attributes);
        assertEquals(1, attributes.size());
        assertEquals(attributeModel, attributes.get(0));
    }

    @Test
    void addParameter_ignoresAddingTheSameObject() {
        AttributeModel attributeModel = new AttributeModel();

        methodModel.addParameter(attributeModel);
        methodModel.addParameter(attributeModel);

        List<AttributeModel> attributes = methodModel.getParameters();

        assertNotNull(attributes);
        assertEquals(1, attributes.size());
        assertEquals(attributeModel, attributes.get(0));
    }

    @Test
    void addParameter_throwsNullPointerExceptionForNull() {
        assertThrows(NullPointerException.class, () -> {
            methodModel.addParameter(null);
        });
    }

    @Test
    void hasReturnType_returnFalseForConstructor() {
        methodModel.setConstructor(true);

        assertFalse(methodModel.hasReturnType());
    }

    @Test
    void hasReturnType_returnFalseForReturnTypeNull() {
        methodModel.setReturnType(null);

        assertFalse(methodModel.hasReturnType());
    }

    @Test
    void hasReturnType_returnFalseForEmptyReturnType() {
        methodModel.setReturnType("");

        assertFalse(methodModel.hasReturnType());

        methodModel.setReturnType("   ");

        assertFalse(methodModel.hasReturnType());
    }

    @Test
    void hasReturnType_returnTrueForValidReturnType() {
        methodModel.setReturnType("String");

        assertTrue(methodModel.hasReturnType());
    }
}
