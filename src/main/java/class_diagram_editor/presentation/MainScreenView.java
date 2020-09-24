package class_diagram_editor.presentation;

import class_diagram_editor.presentation.skins.AssociationConnectionSkin;
import class_diagram_editor.presentation.skins.ExtendsConnectionSkin;
import class_diagram_editor.presentation.skins.ImplementsConnectionSkin;
import class_diagram_editor.presentation.validator.UMLConnectorValidator;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.model.GraphPackage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.net.URL;
import java.util.Optional;
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
    private CheckBox ckbAssociation;

    @FXML
    private BorderPane bdpRoot;

    private BooleanProperty drawAssociation;

    private GModel graphModel;
    private EditingDomain domain;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.drawAssociation = new SimpleBooleanProperty();

        addControlHandlers();
        initializeGraph();
    }

    private void addControlHandlers() {
        btnGenerateCode.setOnAction(e -> {
            viewModel.generateCode();
        });

        btnAddRandomClass.setOnAction(e -> {
            viewModel.addRandomClass();
        });

        btnAddRandomInterface.setOnAction(e -> {
            viewModel.addRandomInterface();
        });

        ckbAssociation.setOnAction(e -> {
            drawAssociation.set(ckbAssociation.isSelected());
        });
    }

    private void initializeGraph() {
        GraphEditor graphEditor = new DefaultGraphEditor();

        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
        graphEditorContainer.setGraphEditor(graphEditor);

        addSkins(graphEditor);
        addGraphControls(graphEditor);
        addGraphModel(graphEditor);

        bdpRoot.centerProperty().setValue(graphEditorContainer);
    }

    private void addSkins(GraphEditor graphEditor) {
        graphEditor.setNodeSkinFactory(viewModel::createNodeSkin);
        graphEditor.setConnectorSkinFactory(viewModel::createConnectorSkin);
        graphEditor.setConnectorValidator(new UMLConnectorValidator(drawAssociation));
        graphEditor.setConnectionSkinFactory(viewModel::createConnectionSkin);
    }

    private void addGraphControls(GraphEditor graphEditor) {
        graphEditor.setOnConnectionCreated(connection -> {
            CompoundCommand command = null;

            GConnector connectorSource = connection.getSource();
            GConnector connectorTarget = connection.getTarget();

            String sourceId = connectorSource.getParent().getId();
            String targetId = connectorTarget.getParent().getId();

            switch (connection.getType()) {
                case ExtendsConnectionSkin.TYPE:
                    viewModel.addExtendsRelation(targetId, sourceId);
                    break;
                case ImplementsConnectionSkin.TYPE:
                    viewModel.addImplementsRelation(targetId, sourceId);
                    break;
                case AssociationConnectionSkin.TYPE:
                    final String identifier = getAssociationIdentifier(sourceId);

                    connection.setId(identifier);
                    boolean successful = viewModel.addOneWayAssociationRelation(sourceId, targetId, identifier);

                    if (!successful) {
                        command = new CompoundCommand();
                        command.append(RemoveCommand.create(domain, graphModel, GraphPackage.Literals.GMODEL__CONNECTIONS, connection));
                        command.append(RemoveCommand.create(domain, connection.getSource(), GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));
                        command.append(RemoveCommand.create(domain, connection.getTarget(), GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));

                        displayAssociationDuplicateError(identifier);
                    }

                    break;
            }

            return command;
        });
    }

    private String getAssociationIdentifier(String sourceId) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Assoziation erstellen");
        dialog.setContentText("Identifier:");

        Optional<String> result = dialog.showAndWait();

        return result.orElseGet(() -> viewModel.getDefaultAssociationIdentifier(sourceId));
    }

    private void displayAssociationDuplicateError(String identifier) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Duplikat erkannt");
        alert.setContentText("Die Klasse hat schon eine Assoziation mit dem Namen '" + identifier + "'");

        alert.show();
    }

    private void addGraphModel(GraphEditor graphEditor) {
        graphModel = GraphFactory.eINSTANCE.createGModel();

        graphModel.setContentWidth(10000);
        graphModel.setContentHeight(10000);

        graphEditor.setModel(graphModel);

        domain = AdapterFactoryEditingDomain.getEditingDomainFor(graphModel);
        viewModel.init(domain, graphModel);
    }
}
