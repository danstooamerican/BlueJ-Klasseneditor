package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.InterfaceModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    private final BooleanProperty isClass;

    private final StringProperty name;
    private final StringProperty extendsElement;
    private final BooleanProperty isAbstract;
    private final ListProperty<InterfaceModel> implementedInterfaces;

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;

        this.isClass = new SimpleBooleanProperty(createElementModel.isClass());
        this.isAbstract = new SimpleBooleanProperty(createElementModel.isAbstract());

        this.name = new SimpleStringProperty(createElementModel.getName());
        this.extendsElement = new SimpleStringProperty(createElementModel.getExtendsElement());

        this.implementedInterfaces = new SimpleListProperty<>(FXCollections.observableArrayList(createElementModel.getImplementedInterfaces()));
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

    public StringProperty extendsElementProperty() {
        return extendsElement;
    }

    public BooleanProperty isAbstractProperty() {
        return isAbstract;
    }

    public ListProperty<InterfaceModel> implementedInterfacesProperty() {
        return implementedInterfaces;
    }
}
