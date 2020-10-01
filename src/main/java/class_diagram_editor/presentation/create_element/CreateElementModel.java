package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.graph_editor.GraphController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CreateElementModel {

    private final ClassDiagram classDiagram;

    private GraphController graphController;

    private final boolean isClass;

    private final String name;
    private Connectable extendsElement;
    private boolean isAbstract;
    private Collection<InterfaceModel> implementedInterfaces;

    private CreateElementModel(Connectable connectable, boolean isClass) {
        this.classDiagram = ClassDiagram.getInstance();

        if (connectable != null) {
            this.name = connectable.getName();
        } else {
            this.name = "";
        }

        this.graphController = null;
        this.extendsElement = null;
        this.implementedInterfaces = new ArrayList<>();
        this.isAbstract = false;
        this.isClass = isClass;
    }

    public CreateElementModel(GraphController graphController) {
        this(null, true);

        this.graphController = graphController;
    }

    public CreateElementModel(ClassModel classModel) {
        this(classModel, true);

        this.implementedInterfaces = classModel.getImplementsInterfaces();
        this.isAbstract = classModel.isAbstract();
        this.extendsElement = classModel.getExtendsClass();
    }

    public CreateElementModel(InterfaceModel interfaceModel) {
        this(interfaceModel, false);

        this.implementedInterfaces = interfaceModel.getExtendsRelations()
                .stream()
                .map(connectable -> (InterfaceModel) connectable)
                .collect(Collectors.toList());
    }

    public void addClass(ClassModel classModel) {
        String id = classDiagram.addClass(classModel);

        graphController.addNode(GraphController.NodeType.CLASS, id);

        if (classModel.getExtendsClass() != null) {
            addConnections(GraphController.ConnectionType.EXTENDS, id, List.of(classModel.getExtendsClass()));
        }

        addConnections(GraphController.ConnectionType.IMPLEMENTS, id, classModel.getImplementsInterfaces()
                .stream()
                .map(interfaceModel -> (Connectable) interfaceModel)
                .collect(Collectors.toList()));
    }

    public void addInterface(InterfaceModel interfaceModel) {
        String id = classDiagram.addInterface(interfaceModel);

        graphController.addNode(GraphController.NodeType.INTERFACE, id);

        addConnections(GraphController.ConnectionType.EXTENDS, id, interfaceModel.getExtendsRelations());
    }

    private void addConnections(GraphController.ConnectionType type, String id, Collection<Connectable> connections) {
        for (Connectable connectable : connections) {
            String endId = classDiagram.getIdOf(connectable);

            if (endId != null) {
                graphController.addConnection(type, id, endId);
            }
        }
    }

    public boolean isEditMode() {
        return graphController == null;
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
}
