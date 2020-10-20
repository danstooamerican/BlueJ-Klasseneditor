package class_diagram_editor.presentation.create_element;

import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.presentation.create_element.attributes_tab.AttributesTabController;
import class_diagram_editor.presentation.create_element.general_tab.GeneralTabController;
import class_diagram_editor.presentation.create_element.methods_tab.MethodsTabController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateElementView implements Initializable {

    public static void showCreateElementDialog(CreateElementService createElementService) throws IOException {
        Stage stage = new Stage();

        if (createElementService.isEditMode()) {
            stage.setTitle("Element bearbeiten");
        } else {
            stage.setTitle("Element hinzufügen");
        }

        FXMLLoader loader = new FXMLLoader(CreateElementView.class.getResource("CreateElementView.fxml"));
        loader.setClassLoader(CreateElementView.class.getClassLoader());
        loader.setController(new CreateElementView(stage, new CreateElementViewModel(createElementService)));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ClassEditorApplication.class.getResource("skins.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private final Stage stage;

    private final CreateElementViewModel viewModel;

    @FXML private GeneralTabController generalTabController;
    @FXML private AttributesTabController attributesTabController;
    @FXML private MethodsTabController methodsTabController;

    @FXML private Button btnCreateElement;
    @FXML private Button btnEditElement;
    @FXML private Button btnCancel;

    @FXML private TabPane tabPane;
    @FXML private Tab tabAttributes;

    public CreateElementView(Stage stage, CreateElementViewModel viewModel) {
        this.stage = stage;
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generalTabController.initialize(viewModel.getGeneralTabViewModel(), tabPane, tabAttributes, viewModel.isEditMode());
        attributesTabController.initialize(viewModel.getAttributesTabViewModel());
        methodsTabController.initialize(viewModel.getMethodsTabViewModel(), viewModel.getGeneralTabViewModel().isClassProperty());

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

                dialog.setTitle("Doppeltes Element");
                dialog.setContentText("Ein Element mit dem gleichen Namen existiert schon");

                dialog.show();
            } else {
                stage.close();
            }
        });

        btnCreateElement.disableProperty().bind(viewModel.canSubmitProperty().not());

        btnEditElement.setOnAction(event -> {
            viewModel.editElement();
            stage.close();
        });

        btnEditElement.disableProperty().bind(viewModel.canSubmitProperty().not());
    }
}
