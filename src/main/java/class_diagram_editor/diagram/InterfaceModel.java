package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InterfaceModel implements CodeElement {

    private String name;

    private List<MethodModel> methods;

    public InterfaceModel() {
        this.methods = new ArrayList<>();
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
}
