package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.graph_editor.GraphController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateElementModel {

    private final ClassDiagram classDiagram;

    private final GraphController graphController;

    private final Connectable editedElement;

    private final boolean isClass;

    private String id;
    private final String name;
    private Connectable extendsElement;
    private boolean isAbstract;
    private Collection<InterfaceModel> implementedInterfaces;
    private final Map<String, Connectable> associations;
    private List<AttributeModel> attributes;
    private List<MethodModel> methods;

    private CreateElementModel(String id, Connectable connectable, boolean isClass) {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = GraphController.getInstance();

        this.editedElement = connectable;

        if (connectable != null) {
            this.name = connectable.getName();
            this.associations = connectable.getAssociations();
        } else {
            this.name = "";
            this.associations = new HashMap<>();
        }

        this.id = id;
        this.extendsElement = null;
        this.implementedInterfaces = new ArrayList<>();

        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();

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
        this.attributes = classModel.getAttributes();
        this.methods = classModel.getMethods();
    }

    public CreateElementModel(String id, InterfaceModel interfaceModel) {
        this(id, interfaceModel, false);

        this.implementedInterfaces = interfaceModel.getExtendsRelations()
                .stream()
                .map(connectable -> (InterfaceModel) connectable)
                .collect(Collectors.toList());

        this.methods = interfaceModel.getMethods();
    }

    public boolean addClass(ClassModel classModel) {
        String id = classDiagram.addClass(classModel);

        if (id != null) {
            graphController.addNode(GraphController.NodeType.CLASS, id);

            addClassConnections(classModel, id);

            return true;
        }

        return false;
    }

    public boolean addInterface(InterfaceModel interfaceModel) {
        String id = classDiagram.addInterface(interfaceModel);

        if (id != null) {
            graphController.addNode(GraphController.NodeType.INTERFACE, id);

            addInterfaceConnections(interfaceModel, id);

            return true;
        }

        return false;
    }

    public void editClass(ClassModel classModel) {
        graphController.clearConnections(id);
        addClassConnections(classModel, id);

        classDiagram.edit(id, classModel);
        classModel.notifyChange();
    }

    public void editInterface(InterfaceModel interfaceModel) {
        graphController.clearConnections(id);
        addInterfaceConnections(interfaceModel, id);

        classDiagram.edit(id, interfaceModel);
    }

    private void addClassConnections(ClassModel classModel, String id) {
        if (classModel.getExtendsClass() != null) {
            addConnections(GraphController.ConnectionType.EXTENDS, id, List.of(classModel.getExtendsClass()));
        }

        addConnections(GraphController.ConnectionType.IMPLEMENTS, id, classModel.getImplementsInterfaces()
                .stream()
                .map(interfaceModel -> (Connectable) interfaceModel)
                .collect(Collectors.toList()));

        addConnections(id, classModel.getAssociations());
    }

    private void addInterfaceConnections(InterfaceModel interfaceModel, String id) {
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

    public Connectable getEditedElement() {
        return editedElement;
    }

    public boolean isEditMode() {
        return editedElement != null;
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

    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }
}
