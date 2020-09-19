package class_diagram_editor.diagram;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
public class MethodModel {
    private String name;
    private String returnType;
    private Visibility visibility;
    private boolean isStatic;
    private boolean isAbstract;

    private List<AttributeModel> attributes;

    public MethodModel() {
        this.attributes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void addAttribute(AttributeModel attributeModel) {
        attributes.add(attributeModel);
    }
}
