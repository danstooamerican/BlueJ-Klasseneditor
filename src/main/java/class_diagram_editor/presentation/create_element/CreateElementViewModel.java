package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabViewModel;
import class_diagram_editor.presentation.create_element.general_tab.GeneralTabViewModel;
import de.saxsys.mvvmfx.ViewModel;

import java.util.HashMap;
import java.util.HashSet;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    private final GeneralTabViewModel generalTabViewModel;
    private final AttributesTabViewModel attributesTabViewModel;

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;
        ClassDiagram classDiagram = ClassDiagram.getInstance();

        this.generalTabViewModel = new GeneralTabViewModel(createElementModel, classDiagram.getInterfaces(), classDiagram.getClasses());
        this.attributesTabViewModel = new AttributesTabViewModel(createElementModel.getAttributes());
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
        classModel.setAttributes(new HashSet<>(attributesTabViewModel.getAttributes()));

        return classModel;
    }

    private InterfaceModel getInterfaceModel() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName(generalTabViewModel.nameProperty().get());
        interfaceModel.setExtendsInterfaces(new HashSet<>(generalTabViewModel.implementedInterfacesProperty().get()));
        interfaceModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));

        return interfaceModel;
    }

    public AttributesTabViewModel getAttributesTabViewModel() {
        return attributesTabViewModel;
    }

    public GeneralTabViewModel getGeneralTabViewModel() {
        return generalTabViewModel;
    }
}