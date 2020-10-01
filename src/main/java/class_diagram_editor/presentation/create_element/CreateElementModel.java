package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.graph_editor.GraphController;

public class CreateElementModel {

    private final ClassDiagram classDiagram;

    private final GraphController graphController;

    private final boolean isEditMode;

    public CreateElementModel(ClassDiagram classDiagram, GraphController graphController) {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = graphController;
        this.isEditMode = false;
    }

    public CreateElementModel(ClassModel classModel) {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = null;
        this.isEditMode = true;
    }

    public CreateElementModel(InterfaceModel interfaceModel) {
        this.classDiagram = ClassDiagram.getInstance();
        this.graphController = null;
        this.isEditMode = true;
    }

    public boolean isEditMode() {
        return isEditMode;
    }
}
