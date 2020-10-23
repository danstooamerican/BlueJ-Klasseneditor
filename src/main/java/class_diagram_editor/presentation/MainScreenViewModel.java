package class_diagram_editor.presentation;

import class_diagram_editor.bluej_adapters.source_control.SourceCodeControl;
import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import class_diagram_editor.presentation.graph_editor.DiagramElementService;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Collection;

public class MainScreenViewModel implements ViewModel {
    private final SourceCodeControl sourceCodeControl;

    private final ClassDiagram classDiagram;

    private final BooleanProperty drawAssociation;

    public MainScreenViewModel(SourceCodeControl sourceCodeControl) {
        this.sourceCodeControl = sourceCodeControl;
        this.classDiagram = ClassDiagram.getInstance();

        this.drawAssociation = new SimpleBooleanProperty();
    }

    public void generateCode() {
        if (sourceCodeControl != null) {
            sourceCodeControl.generateCode(classDiagram);
        }
    }

    public void generateCode(Collection<String> selectedElementIds) {
        if (sourceCodeControl != null) {
            sourceCodeControl.generateCode(classDiagram.getSubDiagram(selectedElementIds));
        }
    }

    public void generateClassDiagram() {
        if (sourceCodeControl != null) {
            final ClassDiagram generatedDiagram = sourceCodeControl.generateDiagram();

            DiagramElementService diagramElementService = new DiagramElementService();

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

}
