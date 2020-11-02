package class_diagram_editor.diagram;

import lombok.Setter;

/**
 * Represents an UML representation of a {@link ClassModel class} attribute or a {@link MethodModel parameter}.
 * TODO: create separate ParameterModel class.
 */
@Setter
public class AttributeModel {
    private String name;
    private String type;
    private Visibility visibility;
    private boolean isStatic;
    private boolean isFinal;

    private boolean hasGetter;
    private boolean hasSetter;

    /**
     * @return the name of the {@link AttributeModel attribute}.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the data type of the {@link AttributeModel attribute}.
     */
    public String getType() {
        return type;
    }

    /**
     * @return whether the {@link AttributeModel attribute} is static.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * @return whether the {@link AttributeModel attribute} is final.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * @return the {@link Visibility visibility} of the {@link AttributeModel attribute}.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @return whether the {@link AttributeModel attribute} has a getter-method associated.
     */
    public boolean hasGetter() {
        return hasGetter;
    }

    /**
     * @return whether the {@link AttributeModel attribute} has a setter-method associated.
     */
    public boolean hasSetter() {
        return hasSetter;
    }

    /**
     * Creates a new {@link MethodModel method} which represents the associated setter-method of the {@link AttributeModel attribute}.
     *
     * @return the setter method of the {@link AttributeModel attribute}.
     */
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

    /**
     * Creates a new {@link MethodModel method} which represents the associated getter-method of the {@link AttributeModel attribute}.
     *
     * @return the getter method of the {@link AttributeModel attribute}.
     */
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

    /**
     * @return a clone of this {@link AttributeModel attribute model}.
     */
    public AttributeModel clone() {
        AttributeModel attributeModelCopy = new AttributeModel();

        attributeModelCopy.setName(name);
        attributeModelCopy.setHasSetter(hasSetter);
        attributeModelCopy.setHasGetter(hasGetter);
        attributeModelCopy.setType(type);
        attributeModelCopy.setStatic(isStatic);
        attributeModelCopy.setFinal(isFinal);
        attributeModelCopy.setVisibility(visibility);

        return attributeModelCopy;
    }
}
