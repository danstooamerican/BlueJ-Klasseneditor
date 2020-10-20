package class_diagram_editor.presentation;

import class_diagram_editor.presentation.create_element.CreateElementService;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.graph_editor.GraphController;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenView implements FxmlView<MainScreenViewModel>, Initializable {

    @InjectViewModel
    private MainScreenViewModel viewModel;

    @FXML private Button btnGenerateCode;
    @FXML private Button btnGenerateDiagram;
    @FXML private Button btnAddRandomClass;
    @FXML private Button btnAddRandomInterface;
    @FXML private Button btnCreateElement;
    @FXML private CheckBox ckbAssociation;
    @FXML private BorderPane bdpRoot;

    private GraphController graphController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addControlHandlers();

        graphController = GraphController.getInstance();

        Node graphEditor = graphController.initialize(viewModel);
        bdpRoot.centerProperty().setValue(graphEditor);

        bdpRoot.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                graphController.deleteSelectedElements();
            }

            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                graphController.selectAllNodes();
            }
        });
    }

    private void addControlHandlers() {
        btnGenerateCode.setOnAction(e -> {
            viewModel.generateCode();
        });

        btnGenerateDiagram.setOnAction(e -> {
            viewModel.generateClassDiagram();
        });

        btnAddRandomClass.setOnAction(e -> {
            String id = viewModel.addRandomClass();

            graphController.addNode(GraphController.NodeType.CLASS, id);
        });

        btnAddRandomInterface.setOnAction(e -> {
            String id = viewModel.addRandomInterface();

            graphController.addNode(GraphController.NodeType.INTERFACE, id);
        });

        ckbAssociation.setOnAction(e -> {
            viewModel.setDrawAssociation(ckbAssociation.isSelected());
        });

        btnCreateElement.setOnAction(e -> {
            try {
                CreateElementView.showCreateElementDialog(new CreateElementService());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }


}
