package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabViewModel;
import class_diagram_editor.presentation.create_element.general_tab.GeneralTabViewModel;
import class_diagram_editor.presentation.create_element.methods_tab.MethodsTabViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    private final GeneralTabViewModel generalTabViewModel;
    private final AttributesTabViewModel attributesTabViewModel;
    private final MethodsTabViewModel methodsTabViewModel;

    private final BooleanProperty canSubmit = new SimpleBooleanProperty();

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;
        ClassDiagram classDiagram = ClassDiagram.getInstance();

        this.generalTabViewModel = new GeneralTabViewModel(createElementModel, classDiagram.getInterfaces(), classDiagram.getClasses());
        this.attributesTabViewModel = new AttributesTabViewModel(createElementModel.getAttributes());
        this.methodsTabViewModel = new MethodsTabViewModel(createElementModel.getMethods(), generalTabViewModel.nameProperty());

        this.canSubmit.bind(generalTabViewModel.nameProperty().isNotEmpty());
    }

    public boolean isEditMode() {
        return createElementModel.isEditMode();
    }

    public boolean createElement() {
        boolean success;

        if (generalTabViewModel.isClassProperty().get()) {
            success = createElementModel.addClass(getClassModel());
        } else {
            success = createElementModel.addInterface(getInterfaceModel());
        }

        return success;
    }

    public void editElement() {
        if (generalTabViewModel.isClassProperty().get()) {
            createElementModel.editClass(getClassModel());
        } else {
            createElementModel.editInterface(getInterfaceModel());
        }
    }

    private ClassModel getClassModel() {
        ClassModel classModel = new ClassModel();
        classModel.setName(generalTabViewModel.nameProperty().get());
        classModel.setAbstract(generalTabViewModel.isAbstractProperty().get());
        classModel.setExtendsType(generalTabViewModel.getExtendsElement());
        classModel.setImplementsInterfaces(new HashSet<>(generalTabViewModel.implementedInterfacesProperty().get()));
        classModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));
        classModel.setAttributes(new ArrayList<>(attributesTabViewModel.getAttributes()));
        classModel.setMethods(new HashSet<>(methodsTabViewModel.getMethods()));

        return classModel;
    }

    private InterfaceModel getInterfaceModel() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName(generalTabViewModel.nameProperty().get());
        interfaceModel.setExtendsInterfaces(new HashSet<>(generalTabViewModel.implementedInterfacesProperty().get()));
        interfaceModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));
        interfaceModel.setMethods(new ArrayList<>(methodsTabViewModel.getMethods()));

        return interfaceModel;
    }

    public AttributesTabViewModel getAttributesTabViewModel() {
        return attributesTabViewModel;
    }

    public GeneralTabViewModel getGeneralTabViewModel() {
        return generalTabViewModel;
    }

    public MethodsTabViewModel getMethodsTabViewModel() {
        return methodsTabViewModel;
    }

    public BooleanProperty canSubmitProperty() {
        return canSubmit;
    }
}