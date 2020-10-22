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

    private final CreateElementService createElementService;

    private final GeneralTabViewModel generalTabViewModel;
    private final AttributesTabViewModel attributesTabViewModel;
    private final MethodsTabViewModel methodsTabViewModel;

    private final BooleanProperty canSubmit = new SimpleBooleanProperty();

    public CreateElementViewModel(CreateElementService createElementService) {
        this.createElementService = createElementService;
        ClassDiagram classDiagram = ClassDiagram.getInstance();

        this.generalTabViewModel = new GeneralTabViewModel(
                createElementService,
                classDiagram.getInterfaces(),
                classDiagram.getClasses());

        this.attributesTabViewModel = new AttributesTabViewModel(createElementService.getAttributes());

        this.methodsTabViewModel = new MethodsTabViewModel(
                createElementService.getMethods(),
                generalTabViewModel.nameProperty(),
                generalTabViewModel.implementedInterfacesProperty(),
                generalTabViewModel.extendsElementProperty(),
                generalTabViewModel.isAbstractProperty());

        this.canSubmit.bind(generalTabViewModel.nameProperty().isNotEmpty());
    }

    public boolean isEditMode() {
        return createElementService.isEditMode();
    }

    public boolean createElement() {
        boolean success;

        if (generalTabViewModel.isClassProperty().get()) {
            success = createElementService.addClass(getClassModel());
        } else {
            success = createElementService.addInterface(getInterfaceModel());
        }

        return success;
    }

    public void editElement() {
        if (generalTabViewModel.isClassProperty().get()) {
            createElementService.editClass(getClassModel());
        } else {
            createElementService.editInterface(getInterfaceModel());
        }
    }

    private ClassModel getClassModel() {
        ClassModel classModel = new ClassModel();
        classModel.setName(generalTabViewModel.nameProperty().get());
        classModel.setAbstract(generalTabViewModel.isAbstractProperty().get());
        classModel.addExtendsRelation(generalTabViewModel.getExtendsElement());
        classModel.setImplementsInterfaces(new HashSet<>(generalTabViewModel.implementedInterfacesProperty().get()));
        classModel.setAssociations(new HashMap<>(createElementService.getAssociations()));
        classModel.setAttributes(new ArrayList<>(attributesTabViewModel.getAttributes()));
        classModel.setMethods(new ArrayList<>(methodsTabViewModel.getMethods()));

        return classModel;
    }

    private InterfaceModel getInterfaceModel() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName(generalTabViewModel.nameProperty().get());
        interfaceModel.setExtendsInterfaces(new HashSet<>(generalTabViewModel.implementedInterfacesProperty().get()));
        interfaceModel.setAssociations(new HashMap<>(createElementService.getAssociations()));
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