package class_diagram_editor.diagram;

import lombok.Setter;

@Setter
public class AttributeModel {
    private String name;
    private String type;
    private Visibility visibility;
    private boolean isStatic;
    private boolean isFinal;

    private boolean hasGetter;
    private boolean hasSetter;

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

    public boolean hasGetter() {
        return hasGetter;
    }

    public boolean hasSetter() {
        return hasSetter;
    }

    public MethodModel getSetter() {
        MethodModel setter = null;

        if (hasSetter) {
            setter = new MethodModel();

            final String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
            setter.setName("set" + methodName);

            setter.setReturnType(null);
            setter.setVisibility(Visibility.PUBLIC);

            AttributeModel attributeModel = new AttributeModel();
            attributeModel.setName(name);
            attributeModel.setType(type);

            setter.addParameter(attributeModel);
        }

        return setter;
    }

    public MethodModel getGetter() {
        MethodModel getter = null;

        if (hasGetter) {
            getter = new MethodModel();

            final String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
            getter.setName("get" + methodName);

            getter.setReturnType(type);
            getter.setVisibility(Visibility.PUBLIC);
        }

        return getter;
    }
}
