package class_diagram_editor.presentation.create_element.general_tab;

import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import javafx.fxml.FXML;
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
import javafx.scene.layout.HBox;

public class GeneralTabController {

    @FXML
    private ToggleButton tgbClass;
    @FXML private ToggleButton tgbInterface;
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

    public void initialize(GeneralTabViewModel viewModel, TabPane tabPane, Tab tabAttributes, boolean isEditMode) {
        if (isEditMode) {
            initEditMode();
        }

        initElementType(viewModel, tabPane, tabAttributes);
        initNameAndExtends(viewModel);
        initModifiers(viewModel);
        initImplements(viewModel);
    }

    private void initEditMode() {
        tgbClass.setDisable(true);
        tgbInterface.setDisable(true);
        txbElementName.setDisable(true);
    }

    private void initElementType(GeneralTabViewModel viewModel, TabPane tabPane, Tab tabAttributes) {
        tgbClass.selectedProperty().bindBidirectional(viewModel.isClassProperty());
        tgbInterface.setSelected(!viewModel.isClassProperty().get());

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
    }

    private void initNameAndExtends(GeneralTabViewModel viewModel) {
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
    }

    private void initModifiers(GeneralTabViewModel viewModel) {
        ckbElementAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
    }

    private void initImplements(GeneralTabViewModel viewModel) {
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
    }
}