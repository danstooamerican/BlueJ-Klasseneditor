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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;

public class MethodsTabController {

    @FXML private ListView<MethodModel> lstMethods;

    @FXML private ToggleGroup methodType;
    @FXML private RadioButton rbnTypeConstructor;
    @FXML private RadioButton rbnTypeMethod;

    @FXML private ToggleGroup methodVisibility;
    @FXML private RadioButton rbnPublic;
    @FXML private RadioButton rbnPrivate;
    @FXML private RadioButton rbnPackage;
    @FXML private RadioButton rbnProtected;
    @FXML private HBox pnlVisibilityTitle;
    @FXML private FlowPane pnlVisibility;
    @FXML private Separator sprVisibility;

    @FXML private ToggleGroup methodModifier;
    @FXML private RadioButton rbnNoModifier;
    @FXML private RadioButton rbnStatic;
    @FXML private RadioButton rbnAbstract;

    @FXML private HBox pnlModifiers;
    @FXML private Separator sprModifiers;

    @FXML private TextField txbName;
    @FXML private TextField txbReturnType;
    @FXML private HBox pnlReturnType;
    @FXML private HBox pnlName;
    @FXML private Separator sprNameType;

    @FXML private ListView<AttributeModel> lstParameters;
    @FXML private TextField txbParameterName;
    @FXML private TextField txbParameterType;
    @FXML private Button btnAddParameter;
    @FXML private Button btnEditParameter;

    @FXML private Button btnAddMethod;
    @FXML private Button btnEditMethod;

    @FXML private Label lblParameterNameTypeError;
    @FXML private Label lblDuplicateParameterError;
    @FXML private Label lblMethodNameError;
    @FXML private Label lblDuplicateSignatureError;

    private final BooleanProperty isConstructor = new SimpleBooleanProperty();
    private final BooleanProperty methodAlreadyExists = new SimpleBooleanProperty();
    private final BooleanProperty parameterAlreadyExists = new SimpleBooleanProperty();

    public void initialize(MethodsTabViewModel viewModel, BooleanProperty isClass) {
        initMethodType(viewModel);
        initVisibility(viewModel, isClass);
        initModifiers(viewModel, isClass);
        initNameAndReturnType(viewModel);
        initMethodsList(viewModel);
        initParameterList(viewModel);
        initParameterControls(viewModel);
        initMethodControls(viewModel);
    }

    private void initMethodType(MethodsTabViewModel viewModel) {
        rbnTypeMethod.selectedProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setIsConstructor(!newValue);
        });

        rbnTypeConstructor.selectedProperty().bindBidirectional(isConstructor);
    }

    private void initVisibility(MethodsTabViewModel viewModel, BooleanProperty isClass) {
        rbnPrivate.setUserData(Visibility.PRIVATE);
        rbnPublic.setUserData(Visibility.PUBLIC);
        rbnPackage.setUserData(Visibility.PACKAGE_PRIVATE);
        rbnProtected.setUserData(Visibility.PROTECTED);

        methodVisibility.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setVisibility((Visibility) newValue.getUserData());
        });

        isClass.addListener((observable, oldValue, newValue) -> {
            // if Interface is selected
            if (!newValue) {
                viewModel.setVisibility(Visibility.PUBLIC);
                methodVisibility.selectToggle(rbnPublic);
            }
        });

        pnlVisibilityTitle.visibleProperty().bind(isClass);
        pnlVisibilityTitle.managedProperty().bind(isClass);

        pnlVisibility.visibleProperty().bind(isClass);
        pnlVisibility.managedProperty().bind(isClass);

        sprVisibility.visibleProperty().bind(isClass);
        sprVisibility.managedProperty().bind(isClass);
    }

    private void initModifiers(MethodsTabViewModel viewModel, BooleanProperty isClass) {
        rbnAbstract.selectedProperty().bindBidirectional(viewModel.isAbstractProperty());
        rbnStatic.selectedProperty().bindBidirectional(viewModel.isStaticProperty());

        isClass.addListener((observable, oldValue, newValue) -> {
            // if Interface is selected
            if (!newValue) {
                methodModifier.selectToggle(rbnNoModifier);
            }
        });

        rbnAbstract.visibleProperty().bind(viewModel.isClassAbstractProperty());
        rbnAbstract.managedProperty().bind(viewModel.isClassAbstractProperty());

        pnlModifiers.visibleProperty().bind(isClass.and(rbnTypeMethod.selectedProperty()));
        pnlModifiers.managedProperty().bind(isClass.and(rbnTypeMethod.selectedProperty()));

        sprModifiers.visibleProperty().bind(isClass.and(rbnTypeMethod.selectedProperty()));
        sprModifiers.managedProperty().bind(isClass.and(rbnTypeMethod.selectedProperty()));
    }

    private void initNameAndReturnType(MethodsTabViewModel viewModel) {
        txbName.textProperty().bindBidirectional(viewModel.nameProperty());

        txbName.textProperty().addListener((observable, oldValue, newValue) -> {
            methodAlreadyExists.set(viewModel.methodExists(newValue));
        });

        txbReturnType.textProperty().bindBidirectional(viewModel.returnTypeProperty());
        TextFields.bindAutoCompletion(txbReturnType, viewModel.getAvailableTypes());

        pnlName.visibleProperty().bind(rbnTypeMethod.selectedProperty());
        pnlName.managedProperty().bind(rbnTypeMethod.selectedProperty());

        pnlReturnType.visibleProperty().bind(rbnTypeMethod.selectedProperty());
        pnlReturnType.managedProperty().bind(rbnTypeMethod.selectedProperty());

        sprNameType.visibleProperty().bind(rbnTypeMethod.selectedProperty());
        sprNameType.managedProperty().bind(rbnTypeMethod.selectedProperty());
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

                if (newValue.isConstructor()) {
                    methodType.selectToggle(rbnTypeConstructor);
                } else {
                    methodType.selectToggle(rbnTypeMethod);
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
        TextFields.bindAutoCompletion(txbParameterType, viewModel.getAvailableTypes());

        BooleanBinding nameOrTypeEmpty = viewModel.parameterNameProperty().isEmpty().or(viewModel.parameterTypeProperty().isEmpty());

        btnAddParameter.disableProperty().bind(nameOrTypeEmpty.or(parameterAlreadyExists));
        btnEditParameter.disableProperty().bind(nameOrTypeEmpty);

        btnEditParameter.visibleProperty().bind(viewModel.parameterSelectedProperty());
        btnEditParameter.managedProperty().bind(viewModel.parameterSelectedProperty());

        lblParameterNameTypeError.visibleProperty().bind(nameOrTypeEmpty);
        lblParameterNameTypeError.managedProperty().bind(nameOrTypeEmpty);

        lblDuplicateParameterError.visibleProperty().bind(parameterAlreadyExists);
        lblDuplicateParameterError.managedProperty().bind(parameterAlreadyExists);

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
        btnEditMethod.managedProperty().bind(viewModel.methodSelectedProperty());

        BooleanBinding nameEmpty = viewModel.nameProperty().isEmpty();
        btnAddMethod.disableProperty().bind(nameEmpty
                .and(isConstructor.not()).or(methodAlreadyExists)
                .or(isConstructor.and(viewModel.classNameProperty().isEmpty())));

        btnEditMethod.disableProperty().bind(nameEmpty);

        lblMethodNameError.visibleProperty().bind(nameEmpty);
        lblMethodNameError.managedProperty().bind(nameEmpty);

        lblDuplicateSignatureError.visibleProperty().bind(methodAlreadyExists);
        lblDuplicateSignatureError.managedProperty().bind(methodAlreadyExists);

        btnAddMethod.setOnAction(event -> {
            viewModel.addMethod();
        });

        btnEditMethod.setOnAction(event -> {
            viewModel.editMethod();
            lstMethods.refresh();
        });
    }

}
