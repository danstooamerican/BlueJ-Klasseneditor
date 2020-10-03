package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
public class InterfaceModel implements CodeElement, Connectable {

    private String name;

    private Map<String, Connectable> associations;
    private Set<Connectable> extendsInterfaces;
    private List<MethodModel> methods;

    public InterfaceModel() {
        this.methods = new ArrayList<>();
        this.extendsInterfaces = new HashSet<>();
        this.associations = new HashMap<>();
    }

    @Override
    public void accept(CodeGenerator codeGenerator) {
        codeGenerator.visitInterface(this);
    }

    public boolean hasMethods() {
        return !methods.isEmpty();
    }

    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }

    @Override
    public boolean isExtending() {
        return !extendsInterfaces.isEmpty();
    }

    @Override
    public boolean isExtending(Connectable connectable) {
        for (Connectable extendsType : extendsInterfaces) {
            boolean isExtending = connectable.equals(extendsType);

            if (isExtending) {
                return true;
            }

            boolean transitiveExtending = extendsType.isExtending(connectable);

            if (transitiveExtending) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addExtendsRelation(Connectable extendable) {
        extendsInterfaces.add(extendable);
    }

    @Override
    public void removeExtendsRelation(Connectable connectable) {
        extendsInterfaces.remove(connectable);
    }

    @Override
    public Collection<Connectable> getExtendsRelations() {
        return new ArrayList<>(extendsInterfaces);
    }

    public String getName() {
        return name;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    @Override
    public boolean hasAssociations() {
        return !associations.isEmpty();
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
        Map<String, Connectable> allAssociations = new HashMap<>(associations);

        for (Connectable interfaceModel : extendsInterfaces) {
            allAssociations.putAll(interfaceModel.getAssociations());
        }

        return allAssociations;
    }

    @Override
    public void removeReferencesTo(Connectable removedElement) {
        extendsInterfaces.removeIf(interfaceModel -> interfaceModel.getName().equals(removedElement.getName()));

        associations.entrySet().removeIf(pair -> pair.getValue().getName().equals(removedElement.getName()));
    }

    @Override
    public String toString() {
        return name;
    }

    public void edit(InterfaceModel interfaceModel) {
        this.associations = interfaceModel.associations;
        this.extendsInterfaces = interfaceModel.extendsInterfaces;
        this.methods = interfaceModel.methods;
    }
}
