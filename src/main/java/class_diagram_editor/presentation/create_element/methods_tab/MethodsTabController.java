package class_diagram_editor.presentation.create_element.methods_tab;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import class_diagram_editor.presentation.create_element.attributes_tab.DeletableAttributeListCell;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class MethodsTabController {

    @FXML private ListView<MethodModel> lstMethods;

    @FXML private RadioButton rbnTypeMethod;

    @FXML private ToggleGroup methodVisibility;
    @FXML private RadioButton rbnPublic;
    @FXML private RadioButton rbnPrivate;
    @FXML private RadioButton rbnPackage;
    @FXML private RadioButton rbnProtected;

    @FXML private RadioButton rbnStatic;
    @FXML private RadioButton rbnAbstract;

    @FXML private HBox pnlModifiers;
    @FXML private Separator sprModifiers;

    @FXML private TextField txbName;
    @FXML private TextField txbReturnType;
    @FXML private HBox pnlReturnType;

    @FXML private ListView<AttributeModel> lstParameters;
    @FXML private TextField txbParameterName;
    @FXML private TextField txbParameterType;
    @FXML private Button btnAddParameter;
    @FXML private Button btnEditParameter;

    @FXML private Button btnAddMethod;
    @FXML private Button btnEditMethod;

    private final BooleanProperty methodAlreadyExists = new SimpleBooleanProperty();
    private final BooleanProperty parameterAlreadyExists = new SimpleBooleanProperty();

    public void initialize(MethodsTabViewModel viewModel) {
        initMethodType(viewModel);
        initVisibility(viewModel);
        initModifiers(viewModel);
        initNameAndReturnType(viewModel);
        initMethodsList(viewModel);
        initParameterList(viewModel);
        initParameterControls(viewModel);
        initMethodControls(viewModel);
    }

    private void initMethodType(MethodsTabViewModel viewModel) {
        rbnTypeMethod.selectedProperty().addListener((observable, oldValue, newValue) -> {
            pnlModifiers.visibleProperty().set(newValue);
            pnlModifiers.managedProperty().set(newValue);

            sprModifiers.visibleProperty().set(newValue);
            sprModifiers.managedProperty().set(newValue);

            pnlReturnType.visibleProperty().set(newValue);
            pnlReturnType.managedProperty().set(newValue);

            viewModel.setIsMethod(newValue);
        });
    }

    private void initVisibility(MethodsTabViewModel viewModel) {
        rbnPrivate.setUserData(Visibility.PRIVATE);
        rbnPublic.setUserData(Visibility.PUBLIC);
        rbnPackage.setUserData(Visibility.PACKAGE_PRIVATE);
        rbnProtected.setUserData(Visibility.PROTECTED);

        methodVisibility.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setVisibility((Visibility) newValue.getUserData());
        });
    }

    private void initModifiers(MethodsTabViewModel viewModel) {
        rbnAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
        rbnStatic.selectedProperty().bindBidirectional(viewModel.isStaticProperty());
    }

    private void initNameAndReturnType(MethodsTabViewModel viewModel) {
        txbName.textProperty().bindBidirectional(viewModel.nameProperty());

        txbName.textProperty().addListener((observable, oldValue, newValue) -> {
            methodAlreadyExists.set(viewModel.methodExists(newValue));
        });

        txbReturnType.textProperty().bindBidirectional(viewModel.returnTypeProperty());
    }

    private void initMethodsList(MethodsTabViewModel viewModel) {
        lstMethods.setCellFactory(methodViewModel -> new DeletableMethodListCell((method) -> {
            viewModel.deleteMethod(method);
            methodAlreadyExists.set(viewModel.methodExists());
            return null;
        }));

        lstMethods.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectMethod(newValue);

                switch (newValue.getVisibility()) {
                    case PRIVATE:
                        methodVisibility.selectToggle(rbnPrivate);
                        break;
                    case PUBLIC:
                        methodVisibility.selectToggle(rbnPublic);
                        break;
                    case PACKAGE_PRIVATE:
                        methodVisibility.selectToggle(rbnPackage);
                        break;
                    case PROTECTED:
                        methodVisibility.selectToggle(rbnProtected);
                        break;
                }

                Platform.runLater(() -> lstMethods.getSelectionModel().clearSelection());
            }
        });

        lstMethods.itemsProperty().bindBidirectional(viewModel.methodsProperty());
    }

    private void initParameterList(MethodsTabViewModel viewModel) {
        lstParameters.setCellFactory(methodViewModel -> new DeletableAttributeListCell((parameter) -> {
            viewModel.deleteParameter(parameter);
            methodAlreadyExists.set(viewModel.methodExists());
            return null;
        }));

        lstParameters.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectParameter(newValue);

                Platform.runLater(() -> lstMethods.getSelectionModel().clearSelection());
            }
        });

        lstParameters.itemsProperty().bindBidirectional(viewModel.parametersProperty());
    }

    private void initParameterControls(MethodsTabViewModel viewModel) {
        txbParameterName.textProperty().bindBidirectional(viewModel.parameterNameProperty());

        txbParameterName.textProperty().addListener((observable, oldValue, newValue) -> {
            parameterAlreadyExists.set(viewModel.parameterExists(newValue));
        });

        txbParameterType.textProperty().bindBidirectional(viewModel.parameterTypeProperty());

        BooleanBinding nameOrTypeEmpty = viewModel.parameterNameProperty().isEmpty().or(viewModel.parameterTypeProperty().isEmpty());

        btnAddParameter.disableProperty().bind(nameOrTypeEmpty.or(parameterAlreadyExists));
        btnEditParameter.disableProperty().bind(nameOrTypeEmpty);

        btnEditParameter.visibleProperty().bind(viewModel.parameterSelectedProperty());

        btnAddParameter.setOnAction(event -> {
            viewModel.addParameter();
            methodAlreadyExists.set(viewModel.methodExists());
        });

        btnEditParameter.setOnAction(event -> {
            viewModel.editParameter();
            lstParameters.refresh();
            methodAlreadyExists.set(viewModel.methodExists());
        });
    }

    private void initMethodControls(MethodsTabViewModel viewModel) {
        btnEditMethod.visibleProperty().bind(viewModel.methodSelectedProperty());

        BooleanBinding nameOrTypeEmpty = viewModel.nameProperty().isEmpty().or(viewModel.returnTypeProperty().isEmpty());
        btnAddMethod.disableProperty().bind(nameOrTypeEmpty.or(methodAlreadyExists));
        btnEditParameter.disableProperty().bind(nameOrTypeEmpty);

        btnAddMethod.setOnAction(event -> {
            viewModel.addMethod();
        });

        btnEditMethod.setOnAction(event -> {
            viewModel.editMethod();
            lstMethods.refresh();
        });
    }

}
