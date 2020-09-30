package class_diagram_editor.presentation.graph_editor;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.presentation.MainScreenViewModel;
import class_diagram_editor.presentation.skins.AssociationConnectionSkin;
import class_diagram_editor.presentation.skins.ExtendsConnectionSkin;
import class_diagram_editor.presentation.skins.ImplementsConnectionSkin;
import class_diagram_editor.presentation.validator.UMLConnectorValidator;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.model.GraphPackage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.Optional;
import java.util.Set;

public class GraphController {

    private EditingDomain domain;
    private GModel graphModel;

    private final MainScreenViewModel viewModel;
    private final ClassDiagram classDiagram;

    private final GraphSkinFactory skinFactory;

    public GraphController(MainScreenViewModel viewModel) {
        this.viewModel = viewModel;

        this.classDiagram = viewModel.getClassDiagram();
        this.skinFactory = new GraphSkinFactory(classDiagram);
    }

    public Node initializeGraph() {
        GraphEditor graphEditor = new DefaultGraphEditor();

        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
        graphEditorContainer.setGraphEditor(graphEditor);

        addSkins(graphEditor);
        addGraphControls(graphEditor);
        addGraphModel(graphEditor);

        return graphEditorContainer;
    }

    private void addSkins(GraphEditor graphEditor) {
        graphEditor.setNodeSkinFactory(skinFactory::createNodeSkin);
        graphEditor.setConnectorSkinFactory(skinFactory::createConnectorSkin);
        graphEditor.setConnectorValidator(new UMLConnectorValidator(viewModel.getDrawAssociation()));
        graphEditor.setConnectionSkinFactory(skinFactory::createConnectionSkin);
    }

    private void addGraphControls(GraphEditor graphEditor) {
        graphEditor.setOnConnectionCreated(connection -> {
            CompoundCommand command = null;

            GConnector connectorSource = connection.getSource();
            GConnector connectorTarget = connection.getTarget();

            String sourceId = connectorSource.getParent().getId();
            String targetId = connectorTarget.getParent().getId();

            switch (connection.getType()) {
                case ExtendsConnectionSkin.TYPE:
                    viewModel.addExtendsRelation(targetId, sourceId);
                    break;
                case ImplementsConnectionSkin.TYPE:
                    viewModel.addImplementsRelation(targetId, sourceId);
                    break;
                case AssociationConnectionSkin.TYPE:
                    final String identifier = getAssociationIdentifier(sourceId);

                    connection.setId(identifier);
                    boolean successful = viewModel.addOneWayAssociationRelation(sourceId, targetId, identifier);

                    if (!successful) {
                        command = new CompoundCommand();
                        command.append(RemoveCommand.create(domain, graphModel, GraphPackage.Literals.GMODEL__CONNECTIONS, connection));
                        command.append(RemoveCommand.create(domain, connection.getSource(), GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));
                        command.append(RemoveCommand.create(domain, connection.getTarget(), GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));

                        displayAssociationDuplicateError(identifier);
                    }

                    break;
            }

            return command;
        });
    }

    private String getAssociationIdentifier(String sourceId) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Assoziation erstellen");
        dialog.setContentText("Identifier:");

        Optional<String> result = dialog.showAndWait();

        return result.orElseGet(() -> getDefaultAssociationIdentifier(sourceId));
    }

    private void displayAssociationDuplicateError(String identifier) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Duplikat erkannt");
        alert.setContentText("Die Klasse hat schon eine Assoziation mit dem Namen '" + identifier + "'");

        alert.show();
    }

    private void addGraphModel(GraphEditor graphEditor) {
        graphModel = GraphFactory.eINSTANCE.createGModel();

        graphModel.setContentWidth(10000);
        graphModel.setContentHeight(10000);

        graphEditor.setModel(graphModel);

        domain = AdapterFactoryEditingDomain.getEditingDomainFor(graphModel);
    }

    public void addNode(NodeType type, String id) {
        GNode node = GraphFactory.eINSTANCE.createGNode();

        node.setType(type.value);
        node.setId(id);

        node.setX(100);
        node.setY(100);
        node.setWidth(200);
        node.setHeight(150);

        node.getConnectors().add(createConnector("top"));
        node.getConnectors().add(createConnector("top"));

        node.getConnectors().add(createConnector("right"));
        node.getConnectors().add(createConnector("right"));

        node.getConnectors().add(createConnector("left"));
        node.getConnectors().add(createConnector("left"));

        node.getConnectors().add(createConnector("bottom"));
        node.getConnectors().add(createConnector("bottom"));

        EReference nodes = GraphPackage.Literals.GMODEL__NODES;

        CompoundCommand command= new CompoundCommand();
        command.append(AddCommand.create(domain, graphModel, nodes, node));

        if (command.canExecute()) {
            domain.getCommandStack().execute(command);
        }
    }

    private GConnector createConnector(String type) {
        final String connectorType = type + "-input";

        GConnector connector = GraphFactory.eINSTANCE.createGConnector();

        connector.setConnectionDetachedOnDrag(false);
        connector.setType(connectorType);

        return connector;
    }

    private String getDefaultAssociationIdentifier(String id) {
        Connectable connectable = classDiagram.findElement(id);

        Set<String> identifiers = connectable.getAssociations().keySet();

        final String defaultIdentifier = "association";
        for (int i = 0; ; i++) {
            String identifierCandidate = defaultIdentifier + i;

            if (!identifiers.contains(identifierCandidate)) {
                return identifierCandidate;
            }
        }
    }

    public enum NodeType {
        CLASS("class"),
        INTERFACE("interface");

        private final String value;

        NodeType(String value) {
            this.value = value;
        }
    }
}
