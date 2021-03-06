package class_diagram_editor.presentation;

import class_diagram_editor.bluej_adapters.source_control.GenerationType;
import class_diagram_editor.diagram.Editable;
import class_diagram_editor.presentation.create_element.CreateElementService;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.graph_editor.GraphController;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import static javafx.scene.control.ButtonType.OK;

public class MainScreenView implements FxmlView<MainScreenViewModel>, Initializable {

    @InjectViewModel
    private MainScreenViewModel viewModel;

    @FXML private Button btnGenerateCode;
    @FXML private Button btnGenerateCodeSelected;
    @FXML private Button btnGenerateDiagram;
    @FXML private Button btnCreateElement;
    @FXML private CheckBox ckbAssociation;
    @FXML private BorderPane bdpRoot;
    @FXML private ListView<Editable<?>> lstAllElements;

    private GraphController graphController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addControlHandlers();
        initAllElementsList();

        graphController = GraphController.getInstance();

        Node graphEditor = graphController.initialize(viewModel);
        graphController.loadLayout(viewModel.getLayoutDirPath());

        bdpRoot.centerProperty().setValue(graphEditor);

        bdpRoot.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                graphController.deleteSelectedElements();
            }

            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                graphController.selectAllNodes();
            }
        });

        viewModel.generateClassDiagram();
    }

    private void addControlHandlers() {
        btnGenerateCode.setOnAction(e -> {
            final GenerationType generationType = showCodeGenerationConfirmation();

            final String generationDir = viewModel.generateCode(generationType);

            if (generationDir != null) {
                graphController.saveLayout(generationDir);
            }
        });

        btnGenerateCodeSelected.setOnAction(e -> {
            final Collection<String> selectedElementIds = graphController.getSelectedElementIds();

            if (!selectedElementIds.isEmpty()) {
                final GenerationType generationType = showCodeGenerationConfirmation();

                final String generationDir = viewModel.generateCode(selectedElementIds, generationType);

                if (generationDir != null) {
                    graphController.saveLayout(generationDir);
                }
            } else {
                showNoElementsSelectedDialog();
            }
        });

        btnGenerateDiagram.setOnAction(e -> {
            final GenerationType generationType = showDiagramGenerationConfirmation();

            if (!generationType.equals(GenerationType.NO_GENERATION)) {
                viewModel.generateClassDiagram();
            }
        });

        ckbAssociation.setOnAction(e -> {
            viewModel.setDrawAssociation(ckbAssociation.isSelected());
        });

        btnCreateElement.setOnAction(e -> {
            try {
                CreateElementView.showCreateElementDialog(new CreateElementService());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initAllElementsList() {
        lstAllElements.setCellFactory(param -> new DisplayedElementListCell());
        lstAllElements.itemsProperty().bind(viewModel.getAllElements());
    }

    private GenerationType showCodeGenerationConfirmation() {
        final String backupText = "Mit Backup";
        ButtonType btnBackup = new ButtonType(backupText, ButtonBar.ButtonData.YES);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Code generieren");
        alert.setHeaderText("Mit dieser Aktion können bereits bestehende Klassen überschrieben werden.");
        alert.getButtonTypes().add(btnBackup);

        ButtonType buttonType = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType.getText().equals(backupText)) {
            return GenerationType.WITH_BACKUP;
        } else if (buttonType.equals(OK)) {
            return GenerationType.NO_BACKUP;
        }

        return GenerationType.NO_GENERATION;
    }

    private GenerationType showDiagramGenerationConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Diagramm generieren");
        alert.setHeaderText("Mit dieser Aktion wird das bereits bestehende Diagramm überschrieben.");


        ButtonType buttonType = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType.equals(OK)) {
            return GenerationType.NO_BACKUP;
        }

        return GenerationType.NO_GENERATION;
    }

    private void showNoElementsSelectedDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Code generieren");
        alert.setHeaderText("Es wurden keine Elemente zum Generieren ausgewählt.");

        alert.show();
    }
}
