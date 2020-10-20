package class_diagram_editor.presentation.create_element.general_tab;

import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.create_element.CreateElementService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collection;

public class GeneralTabViewModel {
    private final Connectable editedElement;

    private final BooleanProperty isClass;

    private final StringProperty name;
    private final ObjectProperty<ClassModel> extendsElement;
    private final BooleanProperty isAbstract;

    private final ListProperty<ClassModel> classModels;

    private final ListProperty<InterfaceModel> implementedInterfaces;
    private final ListProperty<InterfaceModel> unimplementedInterfaces;

    public GeneralTabViewModel(CreateElementService createElementService, Collection<InterfaceModel> interfaceModels, Collection<ClassModel> classModels) {
        this.editedElement = createElementService.getEditedElement();

        this.isClass = new SimpleBooleanProperty(createElementService.isClass());
        this.isAbstract = new SimpleBooleanProperty(createElementService.isAbstract());

        this.name = new SimpleStringProperty(createElementService.getName());
        this.extendsElement = new SimpleObjectProperty<>(createElementService.getExtendsElement());

        this.implementedInterfaces = new SimpleListProperty<>(FXCollections.observableArrayList(createElementService.getImplementedInterfaces()));
        Collection<InterfaceModel> unimplementedInterfaces = new ArrayList<>(interfaceModels);
        unimplementedInterfaces.removeAll(createElementService.getImplementedInterfaces());
        unimplementedInterfaces.removeIf(this::filterWithSameNameOrCycle);

        this.unimplementedInterfaces = new SimpleListProperty<>(FXCollections.observableArrayList(unimplementedInterfaces));
        Collection<ClassModel> classes = new ArrayList<>(classModels);
        classes.removeIf(this::filterWithSameNameOrCycle);
        this.classModels = new SimpleListProperty<>(FXCollections.observableArrayList(classes));
    }

    private boolean filterWithSameNameOrCycle(Connectable connectable) {
        return connectable.getName().equals(name.get()) || (editedElement != null && connectable.isExtending(editedElement));
    }

    public void deleteImplementedInterface(InterfaceModel interfaceModel) {
        implementedInterfaces.get().remove(interfaceModel);
        unimplementedInterfaces.get().add(interfaceModel);
    }

    public boolean addImplementedInterface(InterfaceModel interfaceModel) {
        if (editedElement != null && interfaceModel.isExtending(editedElement)) {
            return false;
        }

        implementedInterfaces.get().add(interfaceModel);
        unimplementedInterfaces.get().remove(interfaceModel);

        return true;
    }


    public boolean setExtendsElement(ClassModel classModel) {
        if (classModel == null) {
            throw new IllegalArgumentException();
        }

        if (!classModel.isExtending(editedElement)) {
            extendsElement.set(classModel);

            return true;
        }

        return false;
    }

    public ClassModel getExtendsElement() {
        return extendsElement.get();
    }

    public ObjectProperty<ClassModel> extendsElementProperty() {
        return extendsElement;
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

    public ListProperty<ClassModel> classesProperty() {
        return classModels;
    }
}