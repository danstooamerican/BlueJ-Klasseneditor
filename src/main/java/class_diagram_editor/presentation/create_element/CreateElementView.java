package class_diagram_editor.presentation.create_element;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateElementView implements FxmlView<CreateElementViewModel>, Initializable {

    @InjectViewModel
    private CreateElementViewModel viewModel;

    @FXML
    private Button btnCreateElement;

    @FXML
    private Button btnEditElement;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel.isEditMode()) {
            btnCreateElement.setVisible(false);
            btnCreateElement.setManaged(false);
        } else {
            btnEditElement.setVisible(false);
            btnEditElement.setManaged(false);
        }
    }
}
