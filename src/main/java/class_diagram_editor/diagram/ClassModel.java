package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
public class ClassModel implements CodeElement, Associatable, Extendable {

    private StringProperty name = new SimpleStringProperty();

    private boolean isAbstract;

    private Extendable extendsType;
    private Set<InterfaceModel> implementsInterfaces;
    private Set<Associatable> associations;

    private Set<AttributeModel> attributes;
    private Set<MethodModel> methods;

    public ClassModel() {
        this.implementsInterfaces = new HashSet<>();
        this.associations = new HashSet<>();
        this.attributes = new HashSet<>();
        this.methods = new HashSet<>();
    }

    @Override
    public void accept(CodeGenerator codeGenerator) {
        codeGenerator.visitClass(this);
    }


    public boolean isAbstract() {
        return isAbstract;
    }


    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }


    public Collection<InterfaceModel> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public boolean isImplementingInterfaces() {
        return !implementsInterfaces.isEmpty();
    }

    public void addInterface(InterfaceModel interfaceModel) {
        implementsInterfaces.add(interfaceModel);
    }


    @Override
    public boolean isExtending() {
        return extendsType != null;
    }

    @Override
    public void addExtendsRelation(Extendable extendable) {
        extendsType = extendable;
    }

    @Override
    public Collection<Extendable> getExtendsRelations() {
        return List.of(extendsType);
    }


    public List<MethodModel> getMethods() {
        return new ArrayList<>(methods);
    }

    public boolean hasMethods() {
        return !methods.isEmpty();
    }

    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }


    public List<AttributeModel> getAttributes() {
        return new ArrayList<>(attributes);
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public void addAttribute(AttributeModel attributeModel) {
        attributes.add(attributeModel);
    }


    @Override
    public void addAssociation(Associatable associatable) {
        associations.add(associatable);
    }

    @Override
    public void removeAssociation(Associatable associatable) {
        associations.remove(associatable);
    }

    @Override
    public Collection<Associatable> getAssociations() {
        return new ArrayList<>(associations);
    }
}
