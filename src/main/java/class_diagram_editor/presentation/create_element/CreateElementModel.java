package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.graph_editor.GraphController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateElementModel {

    private final ClassDiagram classDiagram;

    private GraphController graphController;
    private final boolean isEditMode;

    private final boolean isClass;

    private final String id;
    private final String name;
    private Connectable extendsElement;
    private boolean isAbstract;
    private Collection<InterfaceModel> implementedInterfaces;
    private final Map<String, Connectable> associations;

    private CreateElementModel(String id, Connectable connectable, boolean isClass) {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = GraphController.getInstance();

        this.isEditMode = connectable != null;

        if (isEditMode) {
            this.name = connectable.getName();
            this.associations = connectable.getAssociations();
        } else {
            this.name = "";
            this.associations = new HashMap<>();
        }

        this.id = id;
        this.extendsElement = null;
        this.implementedInterfaces = new ArrayList<>();

        this.isAbstract = false;
        this.isClass = isClass;
    }

    public CreateElementModel() {
        this(null, null, true);
    }

    public CreateElementModel(String id, ClassModel classModel) {
        this(id, classModel, true);

        this.implementedInterfaces = classModel.getImplementsInterfaces();
        this.isAbstract = classModel.isAbstract();
        this.extendsElement = classModel.getExtendsClass();
    }

    public CreateElementModel(String id, InterfaceModel interfaceModel) {
        this(id, interfaceModel, false);

        this.implementedInterfaces = interfaceModel.getExtendsRelations()
                .stream()
                .map(connectable -> (InterfaceModel) connectable)
                .collect(Collectors.toList());
    }

    public void addClass(ClassModel classModel) {
        String id = classDiagram.addClass(classModel);

        graphController.addNode(GraphController.NodeType.CLASS, id);

        addClassConnections(classModel);
    }

    public void addInterface(InterfaceModel interfaceModel) {
        String id = classDiagram.addInterface(interfaceModel);

        graphController.addNode(GraphController.NodeType.INTERFACE, id);

        addInterfaceConnections(interfaceModel);
    }

    public void editClass(ClassModel classModel) {
        graphController.clearConnections(id);
        addClassConnections(classModel);

        classDiagram.edit(id, classModel);
    }

    public void editInterface(InterfaceModel interfaceModel) {
        graphController.clearConnections(id);
        addInterfaceConnections(interfaceModel);

        classDiagram.edit(id, interfaceModel);
    }

    private void addClassConnections(ClassModel classModel) {
        if (classModel.getExtendsClass() != null) {
            addConnections(GraphController.ConnectionType.EXTENDS, id, List.of(classModel.getExtendsClass()));
        }

        addConnections(GraphController.ConnectionType.IMPLEMENTS, id, classModel.getImplementsInterfaces()
                .stream()
                .map(interfaceModel -> (Connectable) interfaceModel)
                .collect(Collectors.toList()));

        addConnections(id, classModel.getAssociations());
    }

    private void addInterfaceConnections(InterfaceModel interfaceModel) {
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

    public boolean isEditMode() {
        return isEditMode;
    }

    public boolean isClass() {
        return isClass;
    }

    public String getName() {
        return name;
    }

    public Connectable getExtendsElement() {
        return extendsElement;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public Collection<InterfaceModel> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    public Map<String, Connectable> getAssociations() {
        return associations;
    }
}
