package class_diagram_editor.presentation.graph_editor;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.presentation.MainScreenViewModel;
import class_diagram_editor.presentation.skins.ClassSkin;
import class_diagram_editor.presentation.skins.InterfaceSkin;
import class_diagram_editor.presentation.validator.UMLConnectorValidator;
import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.connections.ConnectionCommands;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GConnection;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class GraphController {

    private static GraphController instance;

    public static GraphController getInstance() {
        if (instance == null) {
            instance = new GraphController();
        }

        return instance;
    }

    private EditingDomain domain;
    private GModel graphModel;

    private MainScreenViewModel viewModel;
    private ClassDiagram classDiagram;

    private GraphEditor graphEditor;
    private GraphSkinFactory skinFactory;

    private GraphController() {

    }

    public Node initialize(MainScreenViewModel viewModel) {
        this.viewModel = viewModel;

        this.classDiagram = viewModel.getClassDiagram();
        this.skinFactory = new GraphSkinFactory(classDiagram);

        return initializeGraph();
    }

    private Node initializeGraph() {
        graphEditor = new DefaultGraphEditor();

        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
        graphEditorContainer.setGraphEditor(graphEditor);

        addSkins(graphEditor);
        addGraphControls(graphEditor);
        addGraphModel(graphEditor);

        return graphEditorContainer;
    }

    public void deleteSelectedElements() {
        deleteNodes();
        deleteConnections();
    }

    private void deleteNodes() {
        classDiagram.deleteElements(graphEditor.getSelectionManager().getSelectedNodes()
                .stream()
                .map(GNode::getId)
                .collect(toList())
        );

        graphEditor.getSelectionManager().getSelectedNodes().forEach(gNode -> {
            Commands.removeNode(graphModel, gNode);
        });
    }

    private void deleteConnections() {
        graphEditor.getSelectionManager().getSelectedConnections().forEach(connection -> {
            String startId = connection.getSource().getParent().getId();
            String endId = connection.getTarget().getParent().getId();

            switch (ConnectionType.valueOf(connection.getType().toUpperCase())) {
                case EXTENDS:
                    classDiagram.deleteExtendsConnection(startId, endId);
                    break;
                case IMPLEMENTS:
                    classDiagram.deleteImplementsConnection(startId, endId);
                    break;
                case ASSOCIATION:
                    classDiagram.deleteAssociationConnection(startId, connection.getId());
                    break;
            }

            ConnectionCommands.removeConnection(graphModel, connection);
        });
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

            switch (ConnectionType.valueOf(connection.getType())) {
                case EXTENDS:
                    viewModel.addExtendsRelation(targetId, sourceId);
                    break;
                case IMPLEMENTS:
                    viewModel.addImplementsRelation(targetId, sourceId);
                    break;
                case ASSOCIATION:
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

    public void addConnection(ConnectionType type, String startId, String endId) {
        addConnection(type, startId, endId, null);
    }

    public void addConnection(ConnectionType type, String startId, String endId, String name) {
        GNode start = null;
        GNode end = null;

        for (GNode node : graphModel.getNodes()) {
            if (node.getId().equals(startId)) {
                start = node;
            } else if (node.getId().equals(endId)) {
                end = node;
            }
        }

        if (start != null && end != null) {
            GConnector startConnector = start.getConnectors().get(0);
            GConnector endConnector = end.getConnectors().get(0);

            AddConnectionCommand command = new AddConnectionCommand(graphModel, startConnector, endConnector, type.name(), name);
            domain.getCommandStack().execute(command);
        }
    }

    public void clearConnections(String id) {
        GNode toClear = null;

        for (GNode node : graphModel.getNodes()) {
            if (node.getId().equals(id)) {
                toClear = node;
                break;
            }
        }

        if (toClear != null) {
            Collection<GConnector> connectors = new ArrayList<>(toClear.getConnectors());

            for (GConnector connector : connectors){
                Collection<GConnection> toDelete = new ArrayList<>(connector.getConnections());

                for (GConnection connection : toDelete) {
                    ConnectionCommands.removeConnection(graphModel, connection);
                }
            }
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

    public void selectAllNodes() {
        graphEditor.getSelectionManager().selectAll();
    }

    public enum NodeType {
        CLASS(ClassSkin.TYPE),
        INTERFACE(InterfaceSkin.TYPE);

        private final String value;

        NodeType(String value) {
            this.value = value;
        }
    }

    public enum ConnectionType {
        EXTENDS,
        IMPLEMENTS,
        ASSOCIATION,
    }
}