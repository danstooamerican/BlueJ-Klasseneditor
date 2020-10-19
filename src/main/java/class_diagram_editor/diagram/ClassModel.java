package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.JavaCodeGenerator;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
public class ClassModel extends Editable<ClassModel> implements CodeElement, Connectable {
    private String lastGeneratedName;
    private String name;

    private boolean isAbstract;

    private Connectable extendsType;
    private Set<InterfaceModel> implementsInterfaces;
    private Map<String, Connectable> associations;

    private List<AttributeModel> attributes;
    private Set<MethodModel> methods;

    public ClassModel() {
        super();

        this.lastGeneratedName = null;
        this.implementsInterfaces = new HashSet<>();
        this.associations = new HashMap<>();
        this.attributes = new ArrayList<>();
        this.methods = new HashSet<>();
    }

    @Override
    protected void performEdit(ClassModel classModel) {
        this.name = classModel.getName();
        this.isAbstract = classModel.isAbstract;
        this.extendsType = classModel.extendsType;
        this.implementsInterfaces = classModel.implementsInterfaces;
        this.associations = classModel.associations;
        this.attributes = classModel.attributes;
        this.methods = classModel.methods;
    }

    @Override
    public void accept(JavaCodeGenerator codeGenerator) {
        codeGenerator.visitClass(this);
        this.lastGeneratedName = name;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLastGeneratedName() {
        if (lastGeneratedName == null) {
            return getName();
        }

        return lastGeneratedName;
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean isExtending(Connectable connectable) {
        if (extendsType != null) {
            boolean isExtending = connectable.equals(extendsType);

            if (!isExtending) {
                return extendsType.isExtending(connectable);
            }

            return true;
        }

        return false;
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
        return name;
    }

}
