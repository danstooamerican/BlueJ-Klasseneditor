package class_diagram_editor.presentation.validator;

import class_diagram_editor.presentation.skins.AssociationConnectionSkin;
import class_diagram_editor.presentation.skins.ExtendsConnectionSkin;
import class_diagram_editor.presentation.skins.ImplementsConnectionSkin;
import de.tesis.dynaware.grapheditor.GConnectorValidator;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.beans.property.BooleanProperty;

public class UMLConnectorValidator implements GConnectorValidator {

    private boolean drawAssociation;

    public UMLConnectorValidator(BooleanProperty drawAssociation) {
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
        return true;
    }

    @Override
    public String createConnectionType(GConnector source, GConnector target) {
        if (drawAssociation) {
            return AssociationConnectionSkin.TYPE;
        }

        final String sourceType = source.getParent().getType();
        final String targetType = target.getParent().getType();

        if (sourceType.equals("class") && targetType.equals("class")) {
            return ExtendsConnectionSkin.TYPE;
        } else if (sourceType.equals("interface") && targetType.equals("interface")) {
            return ExtendsConnectionSkin.TYPE;
        } else if (sourceType.equals("interface") && targetType.equals("class")) {
            return AssociationConnectionSkin.TYPE;
        } else if (targetType.equals("interface")) {
            return ImplementsConnectionSkin.TYPE;
        }

        return "default-connection";
    }

    @Override
    public String createJointType(GConnector source, GConnector target) {
        return null;
    }
}
