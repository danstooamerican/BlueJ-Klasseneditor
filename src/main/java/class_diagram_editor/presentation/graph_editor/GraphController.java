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

/**
 * Handles all updates to the diagram.
 */
public class GraphController {

    private static GraphController instance;

    /**
     * @return the current {@link GraphController} instance.
     */
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

    /**
     * Sets up the visual representation of the
     * {@link ClassDiagram class diagram} of the {@link MainScreenViewModel view model}.
     *
     * @param viewModel the view model which handles the diagram
     * @return the {@link Node} which holds the diagram and can be added to the view.
     */
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

    /**
     * @return the ids of all currently selected diagram elements.
     */
    public Collection<String> getSelectedElementIds() {
        return graphEditor.getSelectionManager().getSelectedNodes()
                .stream()
                .map(GNode::getId)
                .collect(toList());
    }

    /**
     * Removes all selected elements from the diagram.
     */
    public void deleteSelectedElements() {
        deleteSelectedNodes();
        deleteSelectedConnections();
    }

    private void deleteSelectedNodes() {
        classDiagram.deleteElements(getSelectedElementIds());

        graphEditor.getSelectionManager().getSelectedNodes().forEach(gNode -> {
            Commands.removeNode(graphModel, gNode);
        });
    }

    private void deleteSelectedConnections() {
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
        graphEditor.setConnectorValidator(new UMLConnectorValidator(viewModel.getDrawAssociation(), viewModel.getClassDiagram()));
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
                    viewModel.addExtendsRelation(sourceId, targetId);
                    break;
                case IMPLEMENTS:
                    viewModel.addImplementsRelation(sourceId, targetId);
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

    /**
     * Adds a node of the given {@link NodeType type} to the diagram.
     *
     * @param type the {@link NodeType type} of the element.
     * @param id the id of the element.
     */
    public void addNode(NodeType type, String id) {
        GNode node = GraphFactory.eINSTANCE.createGNode();

        node.setType(type.value);
        node.setId(id);

        node.setX(100);
        node.setY(100);
        node.setWidth(200);
        node.setHeight(150);

        // getConnectionConnector(...) depends on the order these connectors are added to the node
        node.getConnectors().add(createConnector("top"));
        node.getConnectors().add(createConnector("top"));

        node.getConnectors().add(createConnector("right"));
        node.getConnectors().add(createConnector("right"));

        node.getConnectors().add(createConnector("bottom"));
        node.getConnectors().add(createConnector("bottom"));

        node.getConnectors().add(createConnector("left"));
        node.getConnectors().add(createConnector("left"));

        EReference nodes = GraphPackage.Literals.GMODEL__NODES;

        CompoundCommand command= new CompoundCommand();
        command.append(AddCommand.create(domain, graphModel, nodes, node));

        if (command.canExecute()) {
            domain.getCommandStack().execute(command);
        }
    }

    /**
     * Adds a connection between the start node and end node which has the given {@link ConnectionType type}.
     *
     * @param type the {@link ConnectionType type} of the connection.
     * @param startId the id of the start node.
     * @param endId the id of the end node.
     */
    public void addConnection(ConnectionType type, String startId, String endId) {
        addConnection(type, startId, endId, null);
    }

    /**
     * Adds a connection between the start node and end node which has the given {@link ConnectionType type} and
     * name.
     *
     * @param type the {@link ConnectionType type} of the connection
     * @param startId the id of the start node.
     * @param endId the id of the end node.
     * @param name the name of the connection.
     */
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
            final double startCenterX = start.getX() + start.getWidth() / 2;
            final double startCenterY = start.getY() + start.getHeight() / 2;

            final double dividerSlope = Math.abs((startCenterY - start.getY()) / (startCenterX - start.getX()));

            final double endCenterX = end.getX() + end.getWidth() / 2;
            final double endCenterY = end.getY() + end.getHeight() / 2;

            final double nodeSlope = Math.abs((endCenterY - startCenterY) / (endCenterX - startCenterX));

            GConnector startConnector = start.getConnectors().get(
                    getStartConnectionConnector(startCenterX, startCenterY, endCenterX, endCenterY, dividerSlope, nodeSlope));

            GConnector endConnector = end.getConnectors().get(
                    getEndConnectionConnector(startCenterX, startCenterY, endCenterX, endCenterY, dividerSlope, nodeSlope));

            AddConnectionCommand command = new AddConnectionCommand(graphModel, startConnector, endConnector, type.name(), name);
            domain.getCommandStack().execute(command);
        }
    }

    private int getStartConnectionConnector(double startCenterX, double startCenterY, double endCenterX, double endCenterY, double dividerSlope, double nodeSlope) {
        // has to be hardcoded because the connector list doesn't follow a specific order

        final int connector;
        if (endCenterX >= startCenterX) {
            if (endCenterY >= startCenterY) {
                if (dividerSlope >= nodeSlope) {
                    connector = 3;
                } else {
                    connector = 5;
                }
            } else {
                if (dividerSlope >= nodeSlope) {
                    connector = 2;
                } else {
                    connector = 1;
                }
            }
        } else {
            if (endCenterY >= startCenterY) {
                if (dividerSlope >= nodeSlope) {
                    connector = 7;
                } else {
                    connector = 4;
                }
            } else {
                if (dividerSlope >= nodeSlope) {
                    connector = 6;
                } else {
                    connector = 0;
                }
            }
        }

        return connector;
    }

    private int getEndConnectionConnector(double startCenterX, double startCenterY, double endCenterX, double endCenterY, double dividerSlope, double nodeSlope) {
        // has to be hardcoded because the connector list doesn't follow a specific order

        final int connector;
        if (endCenterX >= startCenterX) {
            if (endCenterY >= startCenterY) {
                if (dividerSlope >= nodeSlope) {
                    connector = 6;
                } else {
                    connector = 0;
                }
            } else {
                if (dividerSlope >= nodeSlope) {
                    connector = 7;
                } else {
                    connector = 4;
                }
            }
        } else {
            if (endCenterY >= startCenterY) {
                if (dividerSlope >= nodeSlope) {
                    connector = 2;
                } else {
                    connector = 1;
                }
            } else {
                if (dividerSlope >= nodeSlope) {
                    connector = 3;
                } else {
                    connector = 5;
                }
            }
        }

        return connector;
    }

    /**
     * Clears all connections which are outgoing from the element with the given id.
     *
     * @param id the id of the element.
     */
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
                    if (connection.getSource().equals(connector)) {
                        ConnectionCommands.removeConnection(graphModel, connection);
                    }
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

    /**
     * Selects all nodes in the diagram.
     */
    public void selectAllNodes() {
        graphEditor.getSelectionManager().selectAll();
    }

    /**
     * Clears the diagram.
     */
    public void clearDiagram() {
        Commands.clear(graphModel);
    }

    /**
     * The type of a diagram element.
     */
    public enum NodeType {
        CLASS(ClassSkin.TYPE),
        INTERFACE(InterfaceSkin.TYPE);

        private final String value;

        /**
         * Creates a new {@link NodeType}.
         *
         * @param value the internal identifier of the type.
         */
        NodeType(String value) {
            this.value = value;
        }
    }

    /**
     * The type of a diagram conn
     */
    public enum ConnectionType {
        EXTENDS,
        IMPLEMENTS,
        ASSOCIATION,
    }
}
