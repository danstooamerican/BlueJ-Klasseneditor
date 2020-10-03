package class_diagram_editor.presentation.create_element;

import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabController;
import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabViewModel;
import class_diagram_editor.presentation.create_element.general_tab.GeneralTabController;
import class_diagram_editor.presentation.create_element.methods_tab.MethodsTabController;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

    @InjectViewModel private CreateElementViewModel viewModel;

    @FXML private GeneralTabController generalTabController;
    @FXML private AttributesTabController attributesTabController;
    @FXML private MethodsTabController methodsTabController;

    @FXML private Button btnCreateElement;
    @FXML private Button btnEditElement;
    @FXML private Button btnCancel;

    @FXML private TabPane tabPane;
    @FXML private Tab tabAttributes;

    public CreateElementView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generalTabController.initialize(viewModel.getGeneralTabViewModel(), tabPane, tabAttributes, viewModel.isEditMode());
        attributesTabController.initialize(viewModel.getAttributesTabViewModel());

        methodsTabController.initialize(viewModel);

        if (viewModel.isEditMode()) {
            btnCreateElement.setVisible(false);
            btnCreateElement.setManaged(false);
        } else {
            btnEditElement.setVisible(false);
            btnEditElement.setManaged(false);
        }

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
