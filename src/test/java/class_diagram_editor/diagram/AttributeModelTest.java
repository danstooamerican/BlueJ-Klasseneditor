package class_diagram_editor.diagram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeModelTest {

    private final String name = "name";
    private final String type = "String";

    private AttributeModel attributeModel;

    @BeforeEach
    void setUp() {
        this.attributeModel = new AttributeModel();
        this.attributeModel.setType(type);
        this.attributeModel.setName(name);
    }

    @Test
    void getSetter_createsCorrectMethod() {
        attributeModel.setHasSetter(true);

        MethodModel setter = attributeModel.getSetter();

        assertEquals("setName", setter.getName());
        assertFalse(setter.hasReturnType());
        assertEquals(Visibility.PUBLIC, setter.getVisibility());

        List<AttributeModel> parameters = setter.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        AttributeModel parameter = parameters.get(0);
        assertEquals(name, parameter.getName());
        assertEquals(type, parameter.getType());
    }

    @Test
    void getSetter_returnsNullIfHasSetterFalse() {
        assertNull(attributeModel.getSetter());
    }

    @Test
    void getGetter_createsCorrectMethod() {
        attributeModel.setHasGetter(true);

        MethodModel getter = attributeModel.getGetter();

        assertEquals("getName", getter.getName());
        assertEquals(Visibility.PUBLIC, getter.getVisibility());
        assertEquals(type, getter.getReturnType());
        assertTrue(getter.getParameters().isEmpty());
    }

    @Test
    void getGetter_returnsNullIfHasGetterFalse() {
        assertNull(attributeModel.getGetter());
    }
}
