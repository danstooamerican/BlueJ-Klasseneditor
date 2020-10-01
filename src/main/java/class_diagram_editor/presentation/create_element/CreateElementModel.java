package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.graph_editor.GraphController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class CreateElementModel {

    private final ClassDiagram classDiagram;

    private GraphController graphController;

    private boolean isClass;

    private String name;
    private String extendsElement;
    private boolean isAbstract;
    private Collection<InterfaceModel> implementedInterfaces;

    private CreateElementModel(Connectable connectable, boolean isClass) {
        this.classDiagram = ClassDiagram.getInstance();

        if (connectable != null) {
            this.name = connectable.getName();
            this.extendsElement = connectable.getExtendsRelations()
                    .stream()
                    .map(Connectable::getName)
                    .collect(Collectors.joining(", "));
        } else {
            this.name = "";
            this.extendsElement = "";
        }

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

        this.graphController = null;

        this.implementedInterfaces = classModel.getImplementsInterfaces();
        this.isAbstract = classModel.isAbstract();
    }

    public CreateElementModel(InterfaceModel interfaceModel) {
        this(interfaceModel, false);

        this.graphController = null;
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

    public String getExtendsElement() {
        return extendsElement;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public Collection<InterfaceModel> getImplementedInterfaces() {
        return implementedInterfaces;
    }
}
