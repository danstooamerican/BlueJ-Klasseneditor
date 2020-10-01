package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
public class ClassModel implements CodeElement, Connectable {

    private StringProperty name = new SimpleStringProperty();

    private boolean isAbstract;

    private Connectable extendsType;
    private Set<InterfaceModel> implementsInterfaces;
    private Map<String, Connectable> associations;

    private Set<AttributeModel> attributes;
    private Set<MethodModel> methods;

    public ClassModel() {
        this.implementsInterfaces = new HashSet<>();
        this.associations = new HashMap<>();
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
    public void addExtendsRelation(Connectable extendable) {
        extendsType = extendable;
    }

    @Override
    public void removeExtendsRelation(Connectable connectable) {
        if (extendsType != null && extendsType.equals(connectable)) {
            extendsType = null;
        }
    }

    @Override
    public Collection<Connectable> getExtendsRelations() {
        Collection<Connectable> extendsRelations = new ArrayList<>();

        if (extendsType != null) {
            extendsRelations.add(extendsType);
        }

        return extendsRelations;
    }

    public Connectable getExtendsClass() {
        return extendsType;
    }

    @Override
    public void removeReferencesTo(Connectable removedElement) {
        if (extendsType != null && extendsType.getName().equals(removedElement.getName())) {
            extendsType = null;
        }

        implementsInterfaces.removeIf(interfaceModel -> interfaceModel.getName().equals(removedElement.getName()));

        associations.entrySet().removeIf(pair -> pair.getValue().getName().equals(removedElement.getName()));
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
    public boolean hasAssociations() {
        return !getAssociations().isEmpty();
    }

    @Override
    public boolean addAssociation(String identifier, Connectable connectable) {
        if (getAssociations().containsKey(identifier)) {
            return false;
        }

        associations.put(identifier, connectable);

        return true;
    }

    @Override
    public void removeAssociation(String identifier) {
        associations.remove(identifier);
    }

    @Override
    public Map<String, Connectable> getAssociations() {
        final Map<String, Connectable> allAssociations = new HashMap<>(associations);

        for (InterfaceModel interfaceModel : implementsInterfaces) {
            allAssociations.putAll(interfaceModel.getAssociations());
        }

        return allAssociations;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
