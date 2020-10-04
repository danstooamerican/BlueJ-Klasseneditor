package class_diagram_editor.presentation.create_element.attributes_tab;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.Visibility;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.List;

public class AttributesTabViewModel {
    private final ListProperty<AttributeModel> attributes;

    private Visibility visibility;
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final BooleanProperty isStatic = new SimpleBooleanProperty();
    private final BooleanProperty isFinal = new SimpleBooleanProperty();
    private final BooleanProperty hasGetter = new SimpleBooleanProperty();
    private final BooleanProperty hasSetter = new SimpleBooleanProperty();

    private AttributeModel selectedAttribute = null;
    private final BooleanProperty attributeSelected = new SimpleBooleanProperty();

    public AttributesTabViewModel(List<AttributeModel> attributes) {
        this.visibility = Visibility.PRIVATE;
        this.attributes = new SimpleListProperty<>(FXCollections.observableArrayList(attributes));
    }

    public void selectAttribute(AttributeModel attributeModel) {
        if (attributeModel != null) {
            name.set(attributeModel.getName());
            type.set(attributeModel.getType());
            visibility = attributeModel.getVisibility();
            isStatic.set(attributeModel.isStatic());
            isFinal.set(attributeModel.isFinal());
        }

        selectedAttribute = attributeModel;
        attributeSelected.set(attributeModel != null);
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

    public void editAttribute() {
        if (selectedAttribute != null) {
            setAttributeValues(selectedAttribute);
            clearAttributeValues();
        }
    }

    public void deleteAttribute(AttributeModel attibuteModel) {
        attributes.get().remove(attibuteModel);
    }

    private void setAttributeValues(AttributeModel attributeModel) {
        attributeModel.setName(name.get());
        attributeModel.setType(type.get());
        attributeModel.setVisibility(visibility);
        attributeModel.setStatic(isStatic.get());
        attributeModel.setFinal(isFinal.get());
    }

    private void clearAttributeValues() {
        name.set("");
        type.set("");
        isFinal.set(false);
        isStatic.set(false);
        hasGetter.set(false);
        hasSetter.set(false);

        selectedAttribute = null;
        attributeSelected.set(false);
    }


    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<AttributeModel> getAttributes() {
        return attributes.get();
    }

    public ListProperty<AttributeModel> attributesProperty() {
        return attributes;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public BooleanProperty isStaticProperty() {
        return isStatic;
    }

    public BooleanProperty isFinalProperty() {
        return isFinal;
    }

    public BooleanProperty hasGetterProperty() {
        return hasGetter;
    }

    public BooleanProperty hasSetterProperty() {
        return hasSetter;
    }

    public BooleanProperty isAttributeSelected() {
        return attributeSelected;
    }
}