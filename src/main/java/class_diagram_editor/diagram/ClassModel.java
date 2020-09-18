package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
public class ClassModel implements CodeElement {

    private StringProperty name = new SimpleStringProperty();

    private boolean isAbstract;

    private ClassModel extendsClass;
    private Collection<InterfaceModel> implementsInterfaces;

    private List<AttributeModel> attributes;
    private List<MethodModel> methods;

    public ClassModel() {
        this.implementsInterfaces = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    @Override
    public void accept(CodeGenerator codeGenerator) {
        codeGenerator.visitClass(this);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isExtending() {
        return extendsClass != null;
    }

    public boolean hasMethods() {
        return !methods.isEmpty();
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public ClassModel getExtendsClass() {
        return extendsClass;
    }

    public Collection<InterfaceModel> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }

    public void addAttribute(AttributeModel attributeModel) {
        attributes.add(attributeModel);
    }

    public void addInterface(InterfaceModel interfaceModel) {
        implementsInterfaces.add(interfaceModel);
    }
}
