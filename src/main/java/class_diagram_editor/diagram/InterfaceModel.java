package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class InterfaceModel implements CodeElement, Associatable {

    private String name;

    private Set<Associatable> associations;
    private List<MethodModel> methods;

    public InterfaceModel() {
        this.methods = new ArrayList<>();
        this.associations = new HashSet<>();
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
