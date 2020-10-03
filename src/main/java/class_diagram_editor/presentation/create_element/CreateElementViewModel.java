package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.Visibility;
import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    private AttributesTabViewModel attributesTabViewModel;

    private final BooleanProperty isClass;

    private final StringProperty name;
    private Connectable extendsElement;
    private final BooleanProperty isAbstract;

    private final ListProperty<Connectable> classModels;

    private final ListProperty<InterfaceModel> implementedInterfaces;
    private final ListProperty<InterfaceModel> unimplementedInterfaces;

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;

        this.attributesTabViewModel = new AttributesTabViewModel(createElementModel.getAttributes());

        ClassDiagram classDiagram = ClassDiagram.getInstance();

        this.isClass = new SimpleBooleanProperty(createElementModel.isClass());
        this.isAbstract = new SimpleBooleanProperty(createElementModel.isAbstract());

        this.name = new SimpleStringProperty(createElementModel.getName());
        this.extendsElement = createElementModel.getExtendsElement();

        this.implementedInterfaces = new SimpleListProperty<>(FXCollections.observableArrayList(createElementModel.getImplementedInterfaces()));

        Collection<InterfaceModel> unimplementedInterfaces = classDiagram.getInterfaces();
        unimplementedInterfaces.removeAll(createElementModel.getImplementedInterfaces());
        unimplementedInterfaces.removeIf(interfaceModel -> interfaceModel.getName().equals(name.get()));
        this.unimplementedInterfaces = new SimpleListProperty<>(FXCollections.observableArrayList(unimplementedInterfaces));

        Collection<ClassModel> classes = classDiagram.getClasses();
        classes.removeIf(classModel -> classModel.getName().equals(name.get()));
        this.classModels = new SimpleListProperty<>(FXCollections.observableArrayList(classes));
    }

    public boolean isEditMode() {
        return createElementModel.isEditMode();
    }

    public BooleanProperty isClassProperty() {
        return isClass;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty isAbstractProperty() {
        return isAbstract;
    }

    public ListProperty<InterfaceModel> implementedInterfacesProperty() {
        return implementedInterfaces;
    }

    public ListProperty<InterfaceModel> unimplementedInterfacesProperty() {
        return unimplementedInterfaces;
    }

    public ListProperty<Connectable> classesProperty() {
        return classModels;
    }

    public Connectable getExtendsElement() {
        return extendsElement;
    }

    public boolean setExtendsElement(Connectable connectable) {
        if (connectable == null) {
            throw new IllegalArgumentException();
        }

        if (!connectable.getExtendsRelations().contains(createElementModel.getEditedElement())) {
            extendsElement = connectable;

            return true;
        }

        return false;
    }

    public void deleteImplementedInterface(InterfaceModel interfaceModel) {
        implementedInterfaces.get().remove(interfaceModel);
        unimplementedInterfaces.get().add(interfaceModel);
    }

    public void addImplementedInterface(InterfaceModel interfaceModel) {
        implementedInterfaces.get().add(interfaceModel);
        unimplementedInterfaces.get().remove(interfaceModel);
    }

    public boolean createElement() {
        boolean success;

        if (isClass.get()) {
            success = createElementModel.addClass(getClassModel());
        } else {
            success = createElementModel.addInterface(getInterfaceModel());
        }

        return success;
    }

    public void editElement() {
        if (isClass.get()) {
            createElementModel.editClass(getClassModel());
        } else {
            createElementModel.editInterface(getInterfaceModel());
        }
    }

    private ClassModel getClassModel() {
        ClassModel classModel = new ClassModel();
        classModel.setName(name.get());
        classModel.setAbstract(isAbstract.get());
        classModel.setExtendsType(extendsElement);
        classModel.setImplementsInterfaces(new HashSet<>(implementedInterfaces.get()));
        classModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));
        classModel.setAttributes(new HashSet<>(attributesTabViewModel.getAttributes()));

        return classModel;
    }

    private InterfaceModel getInterfaceModel() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName(name.get());
        interfaceModel.setExtendsInterfaces(new HashSet<>(implementedInterfaces.get()));
        interfaceModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));

        return interfaceModel;
    }

    public AttributesTabViewModel getAttributesTabViewModel() {
        return attributesTabViewModel;
    }
}
