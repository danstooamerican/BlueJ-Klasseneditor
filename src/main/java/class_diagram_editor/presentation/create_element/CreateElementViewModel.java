package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.Visibility;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    private final BooleanProperty isClass;

    private final StringProperty name;
    private Connectable extendsElement;
    private final BooleanProperty isAbstract;

    private final ListProperty<Connectable> classModels;

    private final ListProperty<InterfaceModel> implementedInterfaces;
    private final ListProperty<InterfaceModel> unimplementedInterfaces;

    private final ListProperty<AttributeModel> attributes;

    private Visibility attributeVisiblity = Visibility.PRIVATE;
    private final StringProperty attributeName = new SimpleStringProperty();
    private final StringProperty attributeType = new SimpleStringProperty();
    private final BooleanProperty attributeStatic = new SimpleBooleanProperty();
    private final BooleanProperty attributeFinal = new SimpleBooleanProperty();
    private final BooleanProperty attributeGetter = new SimpleBooleanProperty();
    private final BooleanProperty attributeSetter = new SimpleBooleanProperty();

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;
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

        this.attributes = new SimpleListProperty<>(FXCollections.observableArrayList(createElementModel.getAttributes()));
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

    public ListProperty<AttributeModel> attributesProperty() {
        return attributes;
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
        classModel.setAttributes(new HashSet<>(attributes.get()));

        return classModel;
    }

    private InterfaceModel getInterfaceModel() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setName(name.get());
        interfaceModel.setExtendsInterfaces(new HashSet<>(implementedInterfaces.get()));
        interfaceModel.setAssociations(new HashMap<>(createElementModel.getAssociations()));

        return interfaceModel;
    }

    public StringProperty attributeNameProperty() {
        return attributeName;
    }

    public StringProperty attributeTypeProperty() {
        return attributeType;
    }

    public BooleanProperty attributeStaticProperty() {
        return attributeStatic;
    }

    public BooleanProperty attributeFinalProperty() {
        return attributeFinal;
    }

    public BooleanProperty attributeGetterProperty() {
        return attributeGetter;
    }

    public BooleanProperty attributeSetterProperty() {
        return attributeSetter;
    }

    public void setAttributeVisiblity(Visibility attributeVisiblity) {
        this.attributeVisiblity = attributeVisiblity;
    }

    public boolean attributeExists(String name) {
        for (AttributeModel attributeModel : attributes.get()) {
            if (attributeModel.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void addAttribute() {
        AttributeModel attributeModel = new AttributeModel();
        setAttributeValues(attributeModel);

        attributes.get().add(attributeModel);

        clearAttributeValues();
    }

    public void editAttribute(AttributeModel selectedItem) {
        setAttributeValues(selectedItem);
        clearAttributeValues();
    }

    public void deleteAttribute(AttributeModel attibuteModel) {
        attributes.get().remove(attibuteModel);
    }

    private void setAttributeValues(AttributeModel attributeModel) {
        attributeModel.setName(attributeName.get());
        attributeModel.setType(attributeType.get());
        attributeModel.setVisibility(attributeVisiblity);
        attributeModel.setStatic(attributeStatic.get());
        attributeModel.setFinal(attributeFinal.get());
    }

    private void clearAttributeValues() {
        attributeName.set("");
        attributeType.set("");
        attributeFinal.set(false);
        attributeStatic.set(false);
        attributeGetter.set(false);
        attributeSetter.set(false);
    }

    public void selectAttribute(AttributeModel attributeModel) {
        attributeName.set(attributeModel.getName());
        attributeType.set(attributeModel.getType());
        attributeVisiblity = attributeModel.getVisibility();
        attributeStatic.set(attributeModel.isStatic());
        attributeFinal.set(attributeModel.isFinal());
    }
}
