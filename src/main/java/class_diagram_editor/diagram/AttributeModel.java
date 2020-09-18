package class_diagram_editor.diagram;

import lombok.Setter;

@Setter
public class AttributeModel {
    private String name;
    private String type;
    private Visibility visibility;
    private boolean isStatic;
    private boolean isFinal;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}
