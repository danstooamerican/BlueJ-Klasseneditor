package class_diagram_editor.presentation.graph_editor;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Connects the diagram editor with the underlying model and makes sure everything is created correctly.
 */
public class DiagramElementService {

    protected final ClassDiagram classDiagram;

    protected final GraphController graphController;

    /**
     * Creates a new {@link DiagramElementService}.
     */
    public DiagramElementService() {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = GraphController.getInstance();
    }

    /**
     * Adds the given {@link ClassModel class} to the diagram.
     *
     * @param classModel the {@link ClassModel class} to add.
     * @return whether the element was added successfully.
     */
    public boolean addClass(ClassModel classModel) {
        String id = addClassWithoutConnections(classModel);

        if (id != null) {
            addClassConnections(classModel, id);

            return true;
        }

        return false;
    }

    /**
     * Adds the given {@link ClassModel class} to the model and diagram but does not create visual connections
     * to linked elements. This is useful if not all connected elements are already added (i.e. code to diagram transform).
     *
     * @param classModel the {@link ClassModel class} to add.
     * @return the id of the added element.
     */
    protected String addClassWithoutConnections(ClassModel classModel) {
        String id = classDiagram.addClass(classModel);

        if (id != null) {
            graphController.addNode(GraphController.NodeType.CLASS, id);
        }

        return id;
    }

    /**
     * Adds visual connections to all linked elements of the given {@link ClassModel class}.
     *
     * @param classModel the {@link ClassModel class} to create the connections for.
     * @param id the id of the element in the {@link ClassDiagram class diagram}.
     */
    protected void addClassConnections(ClassModel classModel, String id) {
        if (classModel.isExtending()) {
            addConnections(GraphController.ConnectionType.EXTENDS, id, List.of(classModel.getExtendsClass()));
        }

        addConnections(GraphController.ConnectionType.IMPLEMENTS, id, classModel.getImplementsInterfaces()
                .stream()
                .map(interfaceModel -> (Connectable) interfaceModel)
                .collect(Collectors.toList()));

        addConnections(id, classModel.getAssociations());
    }

    /**
     * Adds the given {@link InterfaceModel interface} to the diagram.
     *
     * @param interfaceModel the {@link InterfaceModel interface} to add.
     * @return whether the element was added successfully.
     */
    public boolean addInterface(InterfaceModel interfaceModel) {
        String id = addInterfaceWithoutConnections(interfaceModel);

        if (id != null) {
            addInterfaceConnections(interfaceModel, id);

            return true;
        }

        return false;
    }

    /**
     * Adds the given {@link InterfaceModel interface} to the model and diagram but does not create visual connections
     * to linked elements. This is useful if not all connected elements are already added (i.e. code to diagram transform).
     *
     * @param interfaceModel the {@link InterfaceModel interface} to add.
     * @return the id of the added element.
     */
    protected String addInterfaceWithoutConnections(InterfaceModel interfaceModel) {
        String id = classDiagram.addInterface(interfaceModel);

        if (id != null) {
            graphController.addNode(GraphController.NodeType.INTERFACE, id);
        }

        return id;
    }

    /**
     * Adds visual connections to all linked elements of the given {@link InterfaceModel interface}.
     *
     * @param interfaceModel the {@link InterfaceModel interface} to create the connections for.
     * @param id the id of the element in the {@link ClassDiagram class diagram}.
     */
    protected void addInterfaceConnections(InterfaceModel interfaceModel, String id) {
        addConnections(GraphController.ConnectionType.EXTENDS, id, interfaceModel.getExtendsRelations());
        addConnections(id, interfaceModel.getAssociations());
    }

    private void addConnections(GraphController.ConnectionType type, String id, Collection<Connectable> connections) {
        for (Connectable connectable : connections) {
            String endId = classDiagram.getIdOf(connectable);

            if (endId != null) {
                graphController.addConnection(type, id, endId);
            }
        }
    }

    private void addConnections(String id, Map<String, Connectable> connections) {
        for (String connectionName : connections.keySet()) {
            Connectable connectable = connections.get(connectionName);

            String endId = classDiagram.getIdOf(connectable);

            if (endId != null) {
                graphController.addConnection(GraphController.ConnectionType.ASSOCIATION, id, endId, connectionName);
            }
        }
    }

    /**
     * Replaces the current {@link ClassDiagram class diagram} with the given one and triggers the diagram to update.
     *
     * @param generatedDiagram the new {@link ClassDiagram class diagram}.
     */
    public void replaceClassDiagram(ClassDiagram generatedDiagram) {
        classDiagram.replaceWith(generatedDiagram);

        graphController.clearDiagram();

        // add all elements without connections first
        for (ClassModel classModel : classDiagram.getClasses()) {
            final String id = generatedDiagram.getIdOf(classModel);

            graphController.addNode(GraphController.NodeType.CLASS, id);
        }

        for (InterfaceModel interfaceModel : classDiagram.getInterfaces()) {
            final String id = generatedDiagram.getIdOf(interfaceModel);

            graphController.addNode(GraphController.NodeType.INTERFACE, id);
        }

        // now add the connections so all elements can be connected
        for (ClassModel classModel : classDiagram.getClasses()) {
            addClassConnections(classModel, generatedDiagram.getIdOf(classModel));
        }

        for (InterfaceModel interfaceModel : classDiagram.getInterfaces()) {
            addInterfaceConnections(interfaceModel, generatedDiagram.getIdOf(interfaceModel));
        }
    }

    /**
     * Removes all connections in the diagram and adds them again. This causes the "layout algorithm" to run and
     * results in nicer connections.
     */
    public void reconnectElements() {
        for (ClassModel classModel : classDiagram.getClasses()) {
            final String id = classDiagram.getIdOf(classModel);

            graphController.clearConnections(id);
            addClassConnections(classModel, id);
        }

        for (InterfaceModel interfaceModel : classDiagram.getInterfaces()) {
            final String id = classDiagram.getIdOf(interfaceModel);

            graphController.clearConnections(id);
            addInterfaceConnections(interfaceModel, id);
        }
    }
}
