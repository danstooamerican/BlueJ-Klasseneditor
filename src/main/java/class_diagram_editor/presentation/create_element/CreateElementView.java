package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
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

    @InjectViewModel
    private CreateElementViewModel viewModel;

    @FXML
    private AttributesTabViewController attributesTabViewController;

    @FXML private Button btnCreateElement;
    @FXML private Button btnEditElement;
    @FXML private Button btnCancel;

    @FXML private ToggleButton tgbClass;
    @FXML private ToggleButton tgbInterface;
    @FXML private ToggleGroup elementType;
    @FXML private TextField txbElementName;
    @FXML private ComboBox<Connectable> cbbElementExtends;
    @FXML private CheckBox ckbElementAbstract;
    @FXML private ListView<InterfaceModel> lstImplements;
    @FXML private ComboBox<InterfaceModel> cbbElementImplements;
    @FXML private Button btnAddImplements;
    @FXML private Label lblImplementsTitle;
    @FXML private HBox pnlElementExtends;
    @FXML private HBox pnlElementAbstract;
    @FXML private Separator sprElementAbstract;

    @FXML private TabPane tabPane;
    @FXML private Tab tabAttributes;

    public CreateElementView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attributesTabViewController.initialize(viewModel);

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
    }
}
