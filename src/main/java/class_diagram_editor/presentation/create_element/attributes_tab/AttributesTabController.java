package class_diagram_editor.presentation.create_element.attributes_tab;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.Visibility;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
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
    @FXML private RadioButton rbnPrivate;
    @FXML private RadioButton rbnPublic;
    @FXML private RadioButton rbnPackage;
    @FXML private RadioButton rbnProtected;

    @FXML private CheckBox ckbStatic;
    @FXML private CheckBox ckbFinal;

    @FXML private TextField txbName;
    @FXML private TextField txbType;

    @FXML private CheckBox ckbGetter;
    @FXML private CheckBox ckbSetter;

    @FXML private Button btnAddAttribute;
    @FXML private Button btnEditAttribute;

    private final BooleanProperty attributeAlreadyExists = new SimpleBooleanProperty();

    public void initialize(AttributesTabViewModel viewModel) {
        initAttributesList(viewModel);
        initVisibility(viewModel);
        initModifiers(viewModel);
        initNameAndType(viewModel);
        initControlButtons(viewModel);
    }

    private void initAttributesList(AttributesTabViewModel viewModel) {
        lstAttributes.setCellFactory(attributeModelListView -> new DeletableAttributeListCell((attibuteModel) -> {
            viewModel.deleteAttribute(attibuteModel);

            return null;
        }));
        lstAttributes.itemsProperty().bindBidirectional(viewModel.attributesProperty());
        lstAttributes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue != null) {
                viewModel.selectAttribute(newValue);

                switch (newValue.getVisibility()) {
                    case PRIVATE:
                        attributeVisibility.selectToggle(rbnPrivate);
                        break;
                    case PUBLIC:
                        attributeVisibility.selectToggle(rbnPublic);
                        break;
                    case PACKAGE_PRIVATE:
                        attributeVisibility.selectToggle(rbnPackage);
                        break;
                    case PROTECTED:
                        attributeVisibility.selectToggle(rbnProtected);
                        break;
                }

                Platform.runLater(() -> lstAttributes.getSelectionModel().clearSelection());
            }
        });
    }

    private void initVisibility(AttributesTabViewModel viewModel) {
        rbnPrivate.setUserData(Visibility.PRIVATE);
        rbnPublic.setUserData(Visibility.PUBLIC);
        rbnPackage.setUserData(Visibility.PACKAGE_PRIVATE);
        rbnProtected.setUserData(Visibility.PROTECTED);

        attributeVisibility.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setVisibility((Visibility) newValue.getUserData());
        });
    }

    private void initModifiers(AttributesTabViewModel viewModel) {
        ckbFinal.selectedProperty().bindBidirectional(viewModel.isFinalProperty());
        ckbStatic.selectedProperty().bindBidirectional(viewModel.isStaticProperty());

        ckbGetter.selectedProperty().bindBidirectional(viewModel.hasGetterProperty());
        ckbSetter.selectedProperty().bindBidirectional(viewModel.hasSetterProperty());
    }

    private void initNameAndType(AttributesTabViewModel viewModel) {
        txbName.textProperty().bindBidirectional(viewModel.nameProperty());
        txbType.textProperty().bindBidirectional(viewModel.typeProperty());

        txbName.textProperty().addListener((observable, oldValue, newValue) -> {
            attributeAlreadyExists.set(viewModel.attributeExists(newValue));
        });
    }

    private void initControlButtons(AttributesTabViewModel viewModel) {
        BooleanBinding nameOrTypeEmpty = viewModel.nameProperty().isEmpty().or(viewModel.typeProperty().isEmpty());
        btnAddAttribute.disableProperty().bind(nameOrTypeEmpty.or(attributeAlreadyExists));
        btnEditAttribute.disableProperty().bind(nameOrTypeEmpty);

        btnEditAttribute.visibleProperty().bind(viewModel.isAttributeSelected());

        btnAddAttribute.setOnAction(event -> {
            viewModel.addAttribute();
        });

        btnEditAttribute.setOnAction(event -> {
            viewModel.editAttribute();
            lstAttributes.refresh();
        });
    }
}