package class_diagram_editor.presentation;

import class_diagram_editor.presentation.graph_editor.GraphController;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenView implements FxmlView<MainScreenViewModel>, Initializable {

    @InjectViewModel
    private MainScreenViewModel viewModel;

    @FXML
    private Button btnGenerateCode;

    @FXML
    private Button btnAddRandomClass;

    @FXML
    private Button btnAddRandomInterface;

    @FXML
    private Button btnCreateElement;

    @FXML
    private CheckBox ckbAssociation;

    @FXML
    private BorderPane bdpRoot;

    private GraphController graphController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addControlHandlers();

        graphController = new GraphController(viewModel);

        Node graphEditor = graphController.initializeGraph();
        bdpRoot.centerProperty().setValue(graphEditor);
    }

    private void addControlHandlers() {
        btnGenerateCode.setOnAction(e -> {
            viewModel.generateCode();
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
    }
}
