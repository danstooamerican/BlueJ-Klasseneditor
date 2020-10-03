package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.Visibility;
import class_diagram_editor.presentation.MainScreenView;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateElementView implements FxmlView<CreateElementViewModel>, Initializable {

    public static void showCreateElementDialog(CreateElementModel createElementModel) {
        Stage stage = new Stage();

        if (createElementModel.isEditMode()) {
            stage.setTitle("Element bearbeiten");
        } else {
            stage.setTitle("Element hinzufügen");
        }

        ViewTuple<CreateElementView, CreateElementViewModel> viewTuple = FluentViewLoader.fxmlView(CreateElementView.class)
                .codeBehind(new CreateElementView(stage))
                .viewModel(new CreateElementViewModel(createElementModel))
                .load();

        Parent root = viewTuple.getView();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(CreateElementView.class.getResource("../../skins.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private final Stage stage;

    private final BooleanProperty attributeAlreadyExists = new SimpleBooleanProperty();

    @InjectViewModel
    private CreateElementViewModel viewModel;

    @FXML
    private Button btnCreateElement;

    @FXML
    private Button btnEditElement;

    @FXML
    private Button btnCancel;

    @FXML
    private ToggleButton tgbClass;

    @FXML
    private ToggleButton tgbInterface;

    @FXML
    private ToggleGroup elementType;

    @FXML
    private TextField txbElementName;

    @FXML
    private ComboBox<Connectable> cbbElementExtends;

    @FXML
    private CheckBox ckbElementAbstract;

    @FXML
    private ListView<InterfaceModel> lstImplements;

    @FXML
    private ComboBox<InterfaceModel> cbbElementImplements;

    @FXML
    private Button btnAddImplements;

    @FXML
    private Label lblImplementsTitle;

    @FXML
    private HBox pnlElementExtends;

    @FXML
    private HBox pnlElementAbstract;

    @FXML
    private Separator sprElementAbstract;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabAttributes;

    @FXML
    private ListView<AttributeModel> lstAttributes;

    @FXML
    private ToggleGroup attributeVisibility;

    @FXML
    private RadioButton rbnAttributePrivate;

    @FXML
    private RadioButton rbnAttributePublic;

    @FXML
    private RadioButton rbnAttributePackage;

    @FXML
    private RadioButton rbnAttributeProtected;

    @FXML
    private CheckBox ckbAttributeStatic;

    @FXML
    private CheckBox ckbAttributeFinal;

    @FXML
    private TextField txbAttributeName;

    @FXML
    private TextField txbAttributeType;

    @FXML
    private CheckBox ckbAttributeGetter;

    @FXML
    private CheckBox ckbAttributeSetter;

    @FXML
    private Button btnAddAttribute;

    @FXML
    private Button btnEditAttribute;

    public CreateElementView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel.isEditMode()) {
            btnCreateElement.setVisible(false);
            btnCreateElement.setManaged(false);

            tgbClass.setDisable(true);
            tgbInterface.setDisable(true);
            txbElementName.setDisable(true);
        } else {
            btnEditElement.setVisible(false);
            btnEditElement.setManaged(false);
        }

        tgbClass.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                lblImplementsTitle.setText("Implementierte Interfaces");

                tabPane.getTabs().add(1, tabAttributes);
            } else {
                lblImplementsTitle.setText("Erweiterte Interfaces");

                tabPane.getTabs().remove(tabAttributes);
            }

            pnlElementExtends.setVisible(newValue);
            pnlElementExtends.setManaged(newValue);

            pnlElementAbstract.setVisible(newValue);
            pnlElementAbstract.setManaged(newValue);

            sprElementAbstract.setVisible(newValue);
            sprElementAbstract.setManaged(newValue);
        });

        tgbClass.selectedProperty().bindBidirectional(viewModel.isClassProperty());
        tgbInterface.setSelected(!viewModel.isClassProperty().get());

        txbElementName.textProperty().bindBidirectional(viewModel.nameProperty());

        cbbElementExtends.itemsProperty().bind(viewModel.classesProperty());
        cbbElementExtends.getSelectionModel().select(viewModel.getExtendsElement());
        cbbElementExtends.setOnAction(event -> {
            if (cbbElementExtends.getValue() == null) {
                return;
            }

            boolean success = viewModel.setExtendsElement(cbbElementExtends.getValue());

            if (!success) {
                cbbElementExtends.getSelectionModel().select(viewModel.getExtendsElement());

                Alert dialog = new Alert(Alert.AlertType.ERROR);

                dialog.setTitle("Zyklische Vererbungshierarchie");
                dialog.setContentText("A -> B -> A");

                dialog.show();
            }
        });

        ckbElementAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
        lstImplements.itemsProperty().bindBidirectional(viewModel.implementedInterfacesProperty());

        lstImplements.setCellFactory(interfaceModelListView -> new DeletableInterfaceListCell((interfaceModel) -> {
            viewModel.deleteImplementedInterface(interfaceModel);

            return null;
        }));

        cbbElementImplements.itemsProperty().bind(viewModel.unimplementedInterfacesProperty());

        btnAddImplements.setOnAction(event -> {
            if (!cbbElementImplements.getSelectionModel().isEmpty()) {
                InterfaceModel selectedInterface = cbbElementImplements.getValue();

                viewModel.addImplementedInterface(selectedInterface);
            }
        });

        btnCancel.setOnAction(event -> {
            stage.close();
        });

        btnCreateElement.setOnAction(event -> {
            if (!viewModel.createElement()) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);

                dialog.setTitle("Element existiert schon");
                dialog.setContentText("Ein Element mit dem gleichen Namen existiert schon");

                dialog.show();
            } else {
                stage.close();
            }
        });

        btnEditElement.setOnAction(event -> {
            viewModel.editElement();
            stage.close();
        });

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
