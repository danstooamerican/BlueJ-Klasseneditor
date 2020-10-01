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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
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

        stage.setScene(scene);
        stage.show();
    }

    private final Stage stage;

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

    public CreateElementView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel.isEditMode()) {
            btnCreateElement.setVisible(false);
            btnCreateElement.setManaged(false);
        } else {
            btnEditElement.setVisible(false);
            btnEditElement.setManaged(false);
        }

        tgbClass.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                lblImplementsTitle.setText("Implementierte Interfaces");
            } else {
                lblImplementsTitle.setText("Erweiterte Interfaces");
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
        cbbElementExtends.setOnAction(event -> {
            viewModel.setExtendsElement(cbbElementExtends.getValue());
        });

        ckbElementAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
        lstImplements.itemsProperty().bindBidirectional(viewModel.implementedInterfacesProperty());

        lstImplements.setCellFactory(interfaceModelListView -> new DeletableListCell((interfaceModel) -> {
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
            viewModel.createElement();
            stage.close();
        });
    }
}
