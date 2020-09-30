package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import de.saxsys.mvvmfx.ViewModel;

public class CreateElementViewModel implements ViewModel {

    private final ClassDiagram classDiagram;

    public CreateElementViewModel(ClassDiagram classDiagram) {
        this.classDiagram = classDiagram;
    }
}
