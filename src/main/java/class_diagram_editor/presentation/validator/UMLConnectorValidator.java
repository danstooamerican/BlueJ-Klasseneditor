package class_diagram_editor.presentation.validator;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.skins.AssociationConnectionSkin;
import class_diagram_editor.presentation.skins.ClassSkin;
import class_diagram_editor.presentation.skins.ExtendsConnectionSkin;
import class_diagram_editor.presentation.skins.ImplementsConnectionSkin;
import class_diagram_editor.presentation.skins.InterfaceSkin;
import de.tesis.dynaware.grapheditor.GConnectorValidator;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.beans.property.BooleanProperty;

public class UMLConnectorValidator implements GConnectorValidator {

    private boolean drawAssociation;
    private ClassDiagram classDiagram;

    public UMLConnectorValidator(BooleanProperty drawAssociation, ClassDiagram classDiagram) {
        this.classDiagram = classDiagram;

        drawAssociation.addListener((observable, oldValue, newValue) -> {
            this.drawAssociation = newValue;
        });
    }

    @Override
    public boolean prevalidate(GConnector source, GConnector target) {
        if (source == null || target == null) {
            return false;
        }

        return !source.equals(target);
    }

    @Override
    public boolean validate(GConnector source, GConnector target) {
        GNode sourceNode = source.getParent();
        GNode targetNode = target.getParent();

        if (sourceNode.equals(targetNode)) {
            return false;
        }

        Connectable sourceConnectable = classDiagram.findElement(sourceNode.getId());
        Connectable targetConnectable = classDiagram.findElement(targetNode.getId());

        if (sourceConnectable == null || targetConnectable == null) {
            return false;
        }

        // two extends to the same node
        if (sourceConnectable.getExtendsRelations().contains(targetConnectable)) {
            return false;
        }

        // cyclic extension
        if (targetConnectable.isExtending(sourceConnectable)) {
            return false;
        }

        if (sourceConnectable instanceof ClassModel && targetConnectable instanceof InterfaceModel) {
            ClassModel sourceClass = (ClassModel) sourceConnectable;

            // two implements to the same node
            if (sourceClass.getImplementsInterfaces().contains(targetConnectable)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String createConnectionType(GConnector source, GConnector target) {
        if (drawAssociation) {
            return AssociationConnectionSkin.TYPE;
        }

        final String sourceType = source.getParent().getType();
        final String targetType = target.getParent().getType();

        if (sourceType.equals(ClassSkin.TYPE) && targetType.equals(ClassSkin.TYPE)) {
            return ExtendsConnectionSkin.TYPE;
        } else if (sourceType.equals(InterfaceSkin.TYPE) && targetType.equals(InterfaceSkin.TYPE)) {
            return ExtendsConnectionSkin.TYPE;
        } else if (sourceType.equals(InterfaceSkin.TYPE) && targetType.equals(ClassSkin.TYPE)) {
            return AssociationConnectionSkin.TYPE;
        } else if (targetType.equals(InterfaceSkin.TYPE)) {
            return ImplementsConnectionSkin.TYPE;
        }

        return "default-connection";
    }

    @Override
    public String createJointType(GConnector source, GConnector target) {
        return null;
    }
}
