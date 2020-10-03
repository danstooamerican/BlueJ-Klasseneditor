package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.Visibility;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class AttributesTabController {

    @FXML private ListView<AttributeModel> lstAttributes;

    @FXML private ToggleGroup attributeVisibility;
    @FXML private RadioButton rbnAttributePrivate;
    @FXML private RadioButton rbnAttributePublic;
    @FXML private RadioButton rbnAttributePackage;
    @FXML private RadioButton rbnAttributeProtected;

    @FXML private CheckBox ckbAttributeStatic;
    @FXML private CheckBox ckbAttributeFinal;

    @FXML private TextField txbAttributeName;
    @FXML private TextField txbAttributeType;

    @FXML private CheckBox ckbAttributeGetter;
    @FXML private CheckBox ckbAttributeSetter;

    @FXML private Button btnAddAttribute;
    @FXML private Button btnEditAttribute;

    private final BooleanProperty attributeAlreadyExists = new SimpleBooleanProperty();

    public void initialize(CreateElementViewModel viewModel) {
        lstAttributes.setCellFactory(attributeModelListView -> new DeletableAttributeListCell((attibuteModel) -> {
            viewModel.deleteAttribute(attibuteModel);

            return null;
        }));
        lstAttributes.itemsProperty().bindBidirectional(viewModel.attributesProperty());
        lstAttributes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.selectAttribute(newValue);

            switch (newValue.getVisibility()) {
                case PRIVATE:
                    attributeVisibility.selectToggle(rbnAttributePrivate);
                    break;
                case PUBLIC:
                    attributeVisibility.selectToggle(rbnAttributePublic);
                    break;
                case PACKAGE_PRIVATE:
                    attributeVisibility.selectToggle(rbnAttributePackage);
                    break;
                case PROTECTED:
                    attributeVisibility.selectToggle(rbnAttributeProtected);
                    break;
            }
        });

        rbnAttributePrivate.setUserData(Visibility.PRIVATE);
        rbnAttributePublic.setUserData(Visibility.PUBLIC);
        rbnAttributePackage.setUserData(Visibility.PACKAGE_PRIVATE);
        rbnAttributeProtected.setUserData(Visibility.PROTECTED);

        attributeVisibility.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setAttributeVisiblity((Visibility) newValue.getUserData());
        });

        ckbAttributeFinal.selectedProperty().bindBidirectional(viewModel.attributeFinalProperty());
        ckbAttributeStatic.selectedProperty().bindBidirectional(viewModel.attributeStaticProperty());
        txbAttributeName.textProperty().bindBidirectional(viewModel.attributeNameProperty());
        txbAttributeType.textProperty().bindBidirectional(viewModel.attributeTypeProperty());
        ckbAttributeGetter.selectedProperty().bindBidirectional(viewModel.attributeGetterProperty());
        ckbAttributeSetter.selectedProperty().bindBidirectional(viewModel.attributeSetterProperty());

        btnAddAttribute.disableProperty().bind(viewModel.attributeNameProperty().isEmpty().or(viewModel.attributeTypeProperty().isEmpty()).or(attributeAlreadyExists));
        btnEditAttribute.disableProperty().bind(viewModel.attributeNameProperty().isEmpty().or(viewModel.attributeTypeProperty().isEmpty()));

        btnEditAttribute.visibleProperty().bind(lstAttributes.getSelectionModel().selectedIndexProperty().greaterThan(-1));

        txbAttributeName.textProperty().addListener((observable, oldValue, newValue) -> {
            attributeAlreadyExists.set(viewModel.attributeExists(newValue));
        });

        btnAddAttribute.setOnAction(event -> {
            viewModel.addAttribute();
        });

        btnEditAttribute.setOnAction(event -> {
            viewModel.editAttribute(lstAttributes.getSelectionModel().getSelectedItem());
            lstAttributes.refresh();
        });
    }
}
