package class_diagram_editor.presentation.create_element.methods_tab;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MethodsTabViewModel {

    private boolean isConstructor = false;
    private Visibility visibility = Visibility.PUBLIC;

    private final StringProperty className;
    private final ListProperty<InterfaceModel> implementedInterfaces;
    private final ObjectProperty<ClassModel> extendsElement;
    private final BooleanProperty isClassAbstract;

    private final BooleanProperty isStatic = new SimpleBooleanProperty();
    private final BooleanProperty isAbstract = new SimpleBooleanProperty();

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty returnType = new SimpleStringProperty();

    private final ListProperty<AttributeModel> parameters = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private final StringProperty parameterName = new SimpleStringProperty();
    private final StringProperty parameterType = new SimpleStringProperty();

    private final ListProperty<MethodModel> methods;

    private MethodModel selectedMethod = null;
    private final BooleanProperty methodSelected = new SimpleBooleanProperty();

    private AttributeModel selectedParameter = null;
    private final BooleanProperty parameterSelected = new SimpleBooleanProperty();

    private final Collection<String> availableTypes;

    public MethodsTabViewModel(Collection<String> availableTypes,
                               List<MethodModel> methods, StringProperty className,
                               ListProperty<InterfaceModel> implementedInterfaces, ObjectProperty<ClassModel> extendsElement,
                               BooleanProperty isClassAbstract) {
        this.availableTypes = availableTypes;
        this.methods = new SimpleListProperty<>(FXCollections.observableArrayList(methods));
        this.className = className;
        this.implementedInterfaces = implementedInterfaces;
        this.extendsElement = extendsElement;
        this.isClassAbstract = isClassAbstract;
    }

    public void selectParameter(AttributeModel attributeModel) {
        if (attributeModel != null) {
            parameterName.set(attributeModel.getName());
            parameterType.set(attributeModel.getType());
        }

        selectedParameter = attributeModel;
        parameterSelected.set(attributeModel != null);
    }

    public void addParameter() {
        AttributeModel attributeModel = new AttributeModel();
        setParameterValues(attributeModel);

        parameters.get().add(attributeModel);

        clearParameterValues();
    }

    public void deleteParameter(AttributeModel attributeModel) {
        parameters.get().remove(attributeModel);
    }

    public void editParameter() {
        if (selectedParameter != null) {
            setParameterValues(selectedParameter);
            clearParameterValues();
        }
    }

    private void setParameterValues(AttributeModel attributeModel) {
        attributeModel.setName(parameterName.get());
        attributeModel.setType(parameterType.get());
        attributeModel.setFinal(false);
        attributeModel.setStatic(false);
        attributeModel.setVisibility(Visibility.PUBLIC);
    }

    private void clearParameterValues() {
        parameterName.set("");
        parameterType.set("");

        selectedParameter = null;
        parameterSelected.set(false);
    }

    public void selectMethod(MethodModel methodModel) {
        if (methodModel != null) {
            isConstructor = methodModel.isConstructor();
            name.set(methodModel.getName());
            returnType.set(methodModel.getReturnType());
            visibility = methodModel.getVisibility();
            isStatic.set(methodModel.isStatic());
            isAbstract.set(methodModel.isAbstract());
            parameters.set(FXCollections.observableArrayList(methodModel.getParameters()));
        }

        selectedMethod = methodModel;
        methodSelected.set(methodModel != null);
    }

    public void addMethod() {
        MethodModel methodModel = new MethodModel();
        setMethodValues(methodModel);

        methods.get().add(methodModel);

        clearMethodValues();
    }

    public void editMethod() {
        if (selectedMethod != null) {
            setMethodValues(selectedMethod);
            clearMethodValues();
        }
    }

    public void deleteMethod(MethodModel methodModel) {
        methods.get().remove(methodModel);
    }

    private void setMethodValues(MethodModel methodModel) {
        methodModel.setConstructor(isConstructor);

        if (isConstructor) {
            methodModel.setName(className.get());
        } else {
            methodModel.setName(name.get());
        }

        methodModel.setReturnType(returnType.get());
        methodModel.setVisibility(visibility);
        methodModel.setStatic(isStatic.get());
        methodModel.setAbstract(isAbstract.get());
        methodModel.setParameters(new ArrayList<>(parameters.get()));
    }

    private void clearMethodValues() {
        returnType.set("");
        isAbstract.set(false);
        isStatic.set(false);
        parameters.clear();
        parameterName.set("");
        parameterType.set("");

        // call this last to avoid methodExist call before all values are cleared
        name.set("");

        selectedMethod = null;
        methodSelected.set(false);
    }


    public boolean parameterExists(String parameterName) {
        for (AttributeModel attributeModel : parameters) {
            if (attributeModel.getName().equals(parameterName)) {
                return true;
            }
        }

        return false;
    }

    public boolean methodExists(String methodName) {
        Collection<MethodModel> allMethods = new ArrayList<>(methods);

        for (InterfaceModel interfaceModel : implementedInterfaces) {
            allMethods.addAll(interfaceModel.getMethodsWithExtending());
        }

        if (extendsElement.isNotNull().get()) {
            allMethods.addAll(extendsElement.get().getMethodsWithExtending());
        }

        for (MethodModel methodModel : allMethods) {
            if (methodModel.getName().equals(methodName) || (methodModel.isConstructor() && isConstructor)) {
                if (methodParameterEqual(methodModel)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean methodParameterEqual(MethodModel methodModel) {
        if (methodModel.getParameters().size() != parameters.size()) {
            return false;
        }

        List<AttributeModel> testParameters = methodModel.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            AttributeModel testParameter = testParameters.get(i);
            AttributeModel parameter = parameters.get(i);

            if (!testParameter.getType().equals(parameter.getType())) {
                return false;
            }
        }

        return true;
    }

    public boolean methodExists() {
        return methodExists(name.get());
    }


    public Collection<String> getAvailableTypes() {
        return availableTypes;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void setIsConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public BooleanProperty isStaticProperty() {
        return isStatic;
    }

    public BooleanProperty isAbstractProperty() {
        return isAbstract;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty returnTypeProperty() {
        return returnType;
    }

    public ListProperty<AttributeModel> parametersProperty() {
        return parameters;
    }

    public StringProperty parameterNameProperty() {
        return parameterName;
    }

    public StringProperty parameterTypeProperty() {
        return parameterType;
    }

    public ListProperty<MethodModel> methodsProperty() {
        return methods;
    }

    public List<MethodModel> getMethods() {
        return methods.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public BooleanProperty methodSelectedProperty() {
        return methodSelected;
    }

    public BooleanProperty parameterSelectedProperty() {
        return parameterSelected;
    }

    public BooleanProperty isClassAbstractProperty() {
        return isClassAbstract;
    }
}
