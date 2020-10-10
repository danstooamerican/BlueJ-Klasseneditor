package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterfaceModelTest {

    private final String interfaceName = "interfaceName";
    private InterfaceModel interfaceModel;

    @Mock
    private CodeGenerator codeGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.interfaceModel = new InterfaceModel();
        this.interfaceModel.setName(interfaceName);
    }

    @Test
    void accept_callsVisitInterface() {
        interfaceModel.accept(codeGenerator);

        Mockito.verify(codeGenerator).visitInterface(interfaceModel);
    }

    @Test
    void accept_throwsNullPointerWhenPassingNull() {
        assertThrows(NullPointerException.class, () -> {
            interfaceModel.accept(null);
        });
    }

    @Test
    void hasMethods_returnsFalseWithNoMethods() {
        assertFalse(interfaceModel.hasMethods());
    }

    @Test
    void hasMethods_returnsTrueWithMethods() {
        interfaceModel.addMethod(new MethodModel());

        assertTrue(interfaceModel.hasMethods());
    }

    @Test
    void addMethod_addsMethod() {
        MethodModel methodModel = new MethodModel();

        interfaceModel.addMethod(methodModel);

        List<MethodModel> methods = interfaceModel.getMethods();

        assertNotNull(methods);
        assertEquals(1, methods.size());
        assertEquals(methodModel, methods.get(0));
    }

    @Test
    void addMethod_throwsNullPointerExceptionWhenPassingNull() {
        assertThrows(NullPointerException.class, () -> {
            interfaceModel.addMethod(null);
        });
    }

    @Test
    void isExtending_returnsFalseWithNoInterfaces() {
        assertFalse(interfaceModel.isExtending());
    }

    @Test
    void isExtending_returnsTrueWithInterfaces() {
        Connectable connectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        assertTrue(interfaceModel.isExtending());
    }

    @Test
    void isExtending_returnsFalseForNull() {
        Connectable connectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        assertFalse(interfaceModel.isExtending(null));
    }

    @Test
    void isExtending_returnsFalseForWrongInterface() {
        Connectable connectable = new InterfaceModel();
        Connectable notExtendedConnectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        assertFalse(interfaceModel.isExtending(notExtendedConnectable));
    }

    @Test
    void isExtending_returnsTrueForAddedInterface() {
        Connectable connectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        assertTrue(interfaceModel.isExtending(connectable));
    }

    @Test
    void isExtending_returnsFalseIfNoInterfacesAdded() {
        Connectable connectable = new InterfaceModel();

        assertFalse(interfaceModel.isExtending(connectable));
    }

    @Test
    void addExtendsRelation_addsRelation() {
        Connectable connectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        Collection<Connectable> connectables = interfaceModel.getExtendsRelations();

        assertNotNull(connectables);
        assertEquals(1, connectables.size());

        assertEquals(connectable, connectables.stream().findFirst().orElse(null));
    }

    @Test
    void addExtendsRelation_throwsNullPointerExceptionForNull() {
        assertThrows(NullPointerException.class, () -> {
            interfaceModel.addExtendsRelation(null);
        });
    }

    @Test
    void removeExtendsRelation_removesRelation() {
        Connectable connectable = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable);

        interfaceModel.removeExtendsRelation(connectable);

        Collection<Connectable> connectables = interfaceModel.getExtendsRelations();

        assertNotNull(connectables);
        assertTrue(connectables.isEmpty());
    }

    @Test
    void removeExtendsRelation_throwsNullPointerExceptionForNull() {
        assertThrows(NullPointerException.class, () -> {
            interfaceModel.removeExtendsRelation(null);
        });
    }

    @Test
    void hasAssociations_returnsFalseWithNoAssociations() {
        assertFalse(interfaceModel.hasAssociations());
    }

    @Test
    void hasAssociations_returnsTrueWithAssociations() {
        Connectable connectable = new InterfaceModel();
        String identifier = "id";

        interfaceModel.addAssociation(identifier, connectable);

        assertTrue(interfaceModel.hasAssociations());
    }

    @Test
    void addAssociation_throwsNullPointerExceptionForNull() {
        Connectable connectable = new InterfaceModel();
        String identifier = "id";

        assertThrows(NullPointerException.class, () -> {
            interfaceModel.addAssociation(null, connectable);
        });

        assertThrows(NullPointerException.class, () -> {
            interfaceModel.addAssociation(identifier, null);
        });

        assertThrows(NullPointerException.class, () -> {
            interfaceModel.addAssociation(null, null);
        });
    }

    @Test
    void addAssociation_addsAssociation() {
        String identifier = "id";
        Connectable connectable = new InterfaceModel();

        assertTrue(interfaceModel.addAssociation(identifier, connectable));

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertEquals(1, associations.size());

        assertEquals(connectable, associations.get(identifier));
    }

    @Test
    void addAssociations_returnsFalseOnDuplicateIdentifier() {
        String identifier = "id";
        Connectable connectable1 = new InterfaceModel();
        Connectable connectable2 = new InterfaceModel();

        interfaceModel.addAssociation(identifier, connectable1);

        assertFalse(interfaceModel.addAssociation(identifier, connectable2));

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertEquals(1, associations.size());
        assertEquals(connectable1, associations.get(identifier));
    }

    @Test
    void addAssociations_returnsFalseOnDuplicateTransitiveIdentifier() {
        String identifier = "id";
        Connectable connectable1 = new InterfaceModel();
        Connectable connectable2 = new InterfaceModel();
        Connectable connectable3 = new InterfaceModel();

        interfaceModel.addExtendsRelation(connectable1);
        connectable1.addAssociation(identifier, connectable2);

        assertFalse(interfaceModel.addAssociation(identifier, connectable3));

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertEquals(1, associations.size());
        assertEquals(connectable2, associations.get(identifier));
    }

    @Test
    void removeAssociation_throwsNullPointerExceptionForNull() {
        assertThrows(NullPointerException.class, () -> {
           interfaceModel.removeAssociation(null);
        });
    }

    @Test
    void removeAssociation_removesAssociation() {
        Connectable connectable = new InterfaceModel();
        String identifier = "id";

        interfaceModel.addAssociation(identifier, connectable);

        interfaceModel.removeAssociation(identifier);

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertTrue(associations.isEmpty());
    }

    @Test
    void removeReferencesTo_noExceptionForNull() {
        assertDoesNotThrow(() -> {
            interfaceModel.removeReferencesTo(null);
        });
    }

    @Test
    void removeReferencesTo_removesAssociationsAndExtends() {
        String name = "name";
        InterfaceModel connectable = new InterfaceModel();
        connectable.setName(name);

        String identifier = "id";

        interfaceModel.addAssociation(identifier, connectable);
        interfaceModel.addExtendsRelation(connectable);

        interfaceModel.removeReferencesTo(connectable);

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertTrue(associations.isEmpty());

        Collection<Connectable> extendsRelations = interfaceModel.getExtendsRelations();

        assertNotNull(extendsRelations);
        assertTrue(extendsRelations.isEmpty());
    }

    @Test
    void edit_replacesAssociationsAndExtendsAndMethods() {
        String identifier = "id";
        Connectable association = new InterfaceModel();
        Connectable extendsRelation = new InterfaceModel();
        MethodModel methodModel = new MethodModel();

        InterfaceModel editModel = new InterfaceModel();
        editModel.setExtendsInterfaces(Set.of(extendsRelation));
        editModel.setAssociations(Map.of(identifier, association));
        editModel.setMethods(List.of(methodModel));

        interfaceModel.edit(editModel);

        assertEquals(association, interfaceModel.getAssociations().get(identifier));
        assertTrue(interfaceModel.getMethods().contains(methodModel));
        assertTrue(interfaceModel.getExtendsRelations().contains(extendsRelation));
    }

    @Test
    void getAssociations_includesTransitiveRelations() {
        String identifier = "id";

        Connectable extendsRealtion = new InterfaceModel();
        Connectable association = new InterfaceModel();

        interfaceModel.addExtendsRelation(extendsRealtion);
        extendsRealtion.addAssociation(identifier, association);

        Map<String, Connectable> associations = interfaceModel.getAssociations();

        assertNotNull(associations);
        assertEquals(1, associations.size());
        assertEquals(association, associations.get(identifier));
    }

    @Test
    void toString_returnsName() {
        String name = "Name";

        interfaceModel.setName(name);

        assertEquals(name, interfaceModel.toString());
    }
}
