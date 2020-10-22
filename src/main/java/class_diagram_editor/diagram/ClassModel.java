package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.JavaCodeGenerator;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an UML representation of a Java class.
 */
@Setter
public class ClassModel extends Editable<ClassModel> implements CodeElement, Connectable {
    private String lastGeneratedName;
    private String name;

    private boolean isAbstract;

    @Setter(AccessLevel.NONE)
    private ClassModel extendsType;
    private Set<InterfaceModel> implementsInterfaces;
    private Map<String, Connectable> associations;

    private List<AttributeModel> attributes;
    private List<MethodModel> methods;

    /**
     * Creates a new {@link ClassModel}.
     */
    public ClassModel() {
        super();

        this.lastGeneratedName = null;
        this.implementsInterfaces = new HashSet<>();
        this.associations = new HashMap<>();
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    @Override
    public void accept(JavaCodeGenerator codeGenerator) {
        codeGenerator.visitClass(this);
        this.lastGeneratedName = name;
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
        if (extendable instanceof ClassModel){
            extendsType = (ClassModel) extendable;
        }
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
    public void removeReferencesTo(Connectable removedElement) {
        if (extendsType != null && extendsType.getName().equals(removedElement.getName())) {
            extendsType = null;
        }

        implementsInterfaces.removeIf(interfaceModel -> interfaceModel.getName().equals(removedElement.getName()));

        associations.entrySet().removeIf(pair -> pair.getValue().getName().equals(removedElement.getName()));
    }


    /**
     * @return whether the class is implementing any {@link InterfaceModel interfaces}.
     */
    public boolean isImplementingInterfaces() {
        return !implementsInterfaces.isEmpty();
    }

    /**
     * Adds the given {@link InterfaceModel interface} to the list of implemented interfaces of this class.
     *
     * @param interfaceModel the {@link InterfaceModel interface} which is implemented.
     */
    public void addInterface(InterfaceModel interfaceModel) {
        implementsInterfaces.add(interfaceModel);
    }

    /**
     * @return all implemented {@link InterfaceModel interfaces} of this class.
     */
    public Collection<InterfaceModel> getImplementsInterfaces() {
        return implementsInterfaces;
    }


    /**
     * @return whether this {@link ClassModel class} has any {@link MethodModel methods}.
     */
    public boolean hasMethods() {
        return !methods.isEmpty();
    }

    /**
     * Adds a new {@link MethodModel method} to this {@link ClassModel class}.
     *
     * @param methodModel the {@link MethodModel method} to add.
     */
    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }

    /**
     * @return all {@link MethodModel methods} of this {@link ClassModel class}.
     */
    public List<MethodModel> getMethods() {
        return new ArrayList<>(methods);
    }

    /**
     * @return all added {@link MethodModel methods} of this {@link ClassModel class} including the
     *         {@link MethodModel methods} of the extended {@link ClassModel class}.
     */
    public Collection<MethodModel> getMethodsWithExtending() {
        Collection<MethodModel> allMethods = new ArrayList<>(methods);

        if (extendsType != null) {
            allMethods.addAll(extendsType.getMethodsWithExtending());
        }

        return allMethods;
    }


    /**
     * @return whether the class has any {@link AttributeModel attributes}.
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    /**
     * Adds the given {@link AttributeModel attribute} to the {@link ClassModel class}.
     *
     * @param attributeModel the {@link AttributeModel attribute} to add.
     */
    public void addAttribute(AttributeModel attributeModel) {
        attributes.add(attributeModel);
    }

    /**
     * @return all {@link AttributeModel attributes} of this {@link ClassModel class}.
     */
    public List<AttributeModel> getAttributes() {
        return new ArrayList<>(attributes);
    }

    /**
     * @return the last {@link ClassModel class} which is extended by this {@link ClassModel class}.
     *         This is equal to the last added extends relation to a {@link ClassModel}.
     */
    public ClassModel getExtendsClass() {
        return extendsType;
    }


    /**
     * @return whether the class is marked as abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
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
    public String toString() {
        return name;
    }

}
