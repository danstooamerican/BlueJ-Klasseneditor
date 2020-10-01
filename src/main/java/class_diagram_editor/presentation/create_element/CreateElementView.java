package class_diagram_editor.presentation.create_element;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
                .codeBehind(new CreateElementView())
                .viewModel(new CreateElementViewModel(createElementModel))
                .load();

        Parent root = viewTuple.getView();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @InjectViewModel
    private CreateElementViewModel viewModel;

    @FXML
    private Button btnCreateElement;

    @FXML
    private Button btnEditElement;

    @FXML
    private ToggleButton tgbClass;

    @FXML
    private ToggleButton tgbInterface;

    @FXML
    private ToggleGroup elementType;

    @FXML
    private TextField txbElementName;

    @FXML
    private TextField txbElementExtends;

    @FXML
    private CheckBox ckbElementAbstract;

    @FXML
    private ListView<InterfaceModel> lstImplements;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel.isEditMode()) {
            btnCreateElement.setVisible(false);
            btnCreateElement.setManaged(false);
        } else {
            btnEditElement.setVisible(false);
            btnEditElement.setManaged(false);
        }

        tgbClass.selectedProperty().bindBidirectional(viewModel.isClassProperty());
        tgbInterface.setSelected(!viewModel.isClassProperty().get());

        txbElementName.textProperty().bindBidirectional(viewModel.nameProperty());
        txbElementExtends.textProperty().bindBidirectional(viewModel.extendsElementProperty());
        ckbElementAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
        lstImplements.itemsProperty().bindBidirectional(viewModel.implementedInterfacesProperty());
    }
}
