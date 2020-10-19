package class_diagram_editor.presentation;

import class_diagram_editor.code_generation.SourceCodeControl;
import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
            sourceCodeControl.generate(classDiagram);
        }
    }

    public String addRandomClass() {
        ClassModel classModel = new ClassModel();
        classModel.setName("TestKlasse" + (int) (Math.random() * 100));
        classModel.setAbstract(Math.random() < 0.5);

        for (int i = 0; i < 5 && Math.random() < 0.5; i++) {
            classModel.addAttribute(createRandomAttribute(i));
        }

        for (int i = 0; i < 5 && Math.random() < 0.5; i++) {
            classModel.addMethod(createRandomMethod(i));
        }

        return classDiagram.addClass(classModel);
    }

    private MethodModel createRandomMethod(int i) {
        MethodModel methodModel = new MethodModel();
        methodModel.setName("testMethod" + i);
        methodModel.setReturnType("String");
        methodModel.setAbstract(Math.random() < 0.5);
        methodModel.setStatic(Math.random() < 0.5);
        methodModel.setVisibility(Visibility.PUBLIC);

        for (int k = 0; k < 5 && Math.random() < 0.5; k++) {
            methodModel.addParameter(createRandomAttribute(k));
        }

        return methodModel;
    }

    private AttributeModel createRandomAttribute(int i) {
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.setName("testVariable" + i);
        attributeModel.setType("String");
        attributeModel.setFinal(Math.random() < 0.5);
        attributeModel.setStatic(Math.random() < 0.5);
        attributeModel.setVisibility(Visibility.PRIVATE);

        return attributeModel;
    }

    public String addRandomInterface() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName("TestInterface" + (int) (Math.random() * 100));

        for (int i = 0; i < 5 && Math.random() < 0.5; i++) {
            interfaceModel.addMethod(createRandomMethod(i));
        }

        return classDiagram.addInterface(interfaceModel);
    }


    public void addExtendsRelation(String extendingTypeId, String superTypeId) {
        classDiagram.addExtendsRelation(superTypeId, extendingTypeId);
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
