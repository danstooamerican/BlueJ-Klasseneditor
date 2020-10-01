package class_diagram_editor.presentation.graph_editor;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.presentation.skins.AssociationConnectionSkin;
import class_diagram_editor.presentation.skins.ClassSkin;
import class_diagram_editor.presentation.skins.ConnectorSkin;
import class_diagram_editor.presentation.skins.ExtendsConnectionSkin;
import class_diagram_editor.presentation.skins.ImplementsConnectionSkin;
import class_diagram_editor.presentation.skins.InterfaceSkin;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;

public class GraphSkinFactory {

    private final ClassDiagram classDiagram;

    public GraphSkinFactory(ClassDiagram classDiagram) {
        this.classDiagram = classDiagram;
    }

    public GNodeSkin createNodeSkin(final GNode node) {
        switch (node.getType()) {
            case "class":
                return new ClassSkin(node, classDiagram.getClassModel(node.getId()));
            case "interface":
                return new InterfaceSkin(node, classDiagram.getInterfaceModel(node.getId()));
            default:
                return new DefaultNodeSkin(node);
        }
    }

    public GConnectorSkin createConnectorSkin(final GConnector connector) {
        return new ConnectorSkin(connector);
    }

    public GConnectionSkin createConnectionSkin(final GConnection connection) {
        final String connectionType = connection.getType();

        switch (GraphController.ConnectionType.valueOf(connectionType)) {
            case EXTENDS:
                return new ExtendsConnectionSkin(connection);
            case IMPLEMENTS:
                return new ImplementsConnectionSkin(connection);
            case ASSOCIATION:
                return new AssociationConnectionSkin(connection);
        }

        return null;
    }

}
