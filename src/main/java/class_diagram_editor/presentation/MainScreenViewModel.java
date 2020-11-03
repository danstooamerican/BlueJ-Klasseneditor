package class_diagram_editor.presentation;

import class_diagram_editor.bluej_adapters.source_control.GenerationType;
import class_diagram_editor.bluej_adapters.source_control.SourceCodeControl;
import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Editable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import class_diagram_editor.presentation.graph_editor.DiagramElementService;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Collection;

public class MainScreenViewModel implements ViewModel {
    private final SourceCodeControl sourceCodeControl;

    private final ClassDiagram classDiagram;

    private final BooleanProperty drawAssociation;

    private final DiagramElementService diagramElementService;

    public MainScreenViewModel(SourceCodeControl sourceCodeControl) {
        this.diagramElementService = new DiagramElementService();

        this.sourceCodeControl = sourceCodeControl;
        this.classDiagram = ClassDiagram.getInstance();

        this.drawAssociation = new SimpleBooleanProperty();
    }

    public void generateCode(GenerationType generationType) {
        if (sourceCodeControl != null) {
            sourceCodeControl.generateCode(classDiagram, generationType);
        }
    }

    public void generateCode(Collection<String> selectedElementIds, GenerationType generationType) {
        if (sourceCodeControl != null) {
            sourceCodeControl.generateCode(classDiagram.getSubDiagram(selectedElementIds), generationType);
        }
    }

    public void generateClassDiagram() {
        if (sourceCodeControl != null) {
            final ClassDiagram generatedDiagram = sourceCodeControl.generateDiagram();

            diagramElementService.replaceClassDiagram(generatedDiagram);
        }
    }

    public void addExtendsRelation(String extendingTypeId, String superTypeId) {
        classDiagram.addExtendsRelation(extendingTypeId, superTypeId);
    }

    public void addImplementsRelation(String classId, String interfaceId) {
        classDiagram.addImplementsRelation(classId, interfaceId);
    }

    public boolean addOneWayAssociationRelation(String startId, String endId, String identifier) {
        return classDiagram.addAssociationRelation(startId, endId, identifier);
    }

    public ClassDiagram getClassDiagram() {
        return classDiagram;
    }

    public BooleanProperty getDrawAssociation() {
        return drawAssociation;
    }

    public void setDrawAssociation(boolean value) {
        drawAssociation.set(value);
    }

    public void reconnectElements() {
        this.diagramElementService.reconnectElements();
    }

    public ListProperty<Editable<?>> getAllElements() {
        return classDiagram.allElementsProperty();
    }
}
