package class_diagram_editor.diagram;

import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents an UML representation of a Java method.
 */
@Setter
public class MethodModel {

    private MethodModel lastGenerated;

    private String name;
    private boolean isConstructor;
    private String returnType;
    private Visibility visibility;
    private boolean isStatic;
    private boolean isAbstract;

    private List<AttributeModel> parameters;

    /**
     * Creates a new {@link MethodModel}.
     */
    public MethodModel() {
        this.parameters = new ArrayList<>();
        this.lastGenerated = null;
    }

    /**
     * @return the name of the {@link MethodModel method}.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the return type of the {@link MethodModel method}.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * @return whether the {@link MethodModel method} has a return type which is not null or empty.
     *         If the method is a constructor the method always returns false.
     */
    public boolean hasReturnType() {
        return !isConstructor && returnType != null && !returnType.trim().isEmpty();
    }

    /**
     * @return a list of {@link AttributeModel parameters} of the {@link MethodModel method}.
     */
    public List<AttributeModel> getParameters() {
        return parameters;
    }

    /**
     * @return the visibility of the {@link MethodModel method}.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @return whether the {@link MethodModel method} is static.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * @return whether the {@link MethodModel method} is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * @return whether the {@link MethodModel method} is a constructor.
     */
    public boolean isConstructor() {
        return isConstructor;
    }

    /**
     * @return the last generated state of this {@link MethodModel method}. This state can be set with {@code markEdited()}.
     */
    public MethodModel getLastGenerated() {
        if (lastGenerated != null) {
            return lastGenerated;
        }

        return this;
    }

    /**
     * Adds a new method parameter. Adding the same object multiple times is ignored.
     *
     * @param attributeModel the {@link AttributeModel attribute} to add.
     */
    public void addParameter(@NonNull AttributeModel attributeModel) {
        Objects.requireNonNull(attributeModel, "attributeModel cannot be null");

        if (!parameters.contains(attributeModel)) {
            parameters.add(attributeModel);
        }
    }

    /**
     * Remembers the current state of this {@link MethodModel method} if it has not been marked edited before.
     * The remembered state is used in code generation to retrieve the method body of the old method.
     */
    public void markEdited() {
        this.lastGenerated = this.clone();
    }

    /**
     * Forgets the last edited state. This method is usually called after the generation finished.
     */
    public void unmarkEdited() {
        this.lastGenerated = null;
    }

    /**
     * @return a clone of this {@link MethodModel method}.
     */
    public MethodModel clone() {
        MethodModel methodModel = new MethodModel();

        methodModel.setName(name);
        methodModel.setAbstract(isAbstract);
        methodModel.setReturnType(returnType);
        methodModel.setVisibility(visibility);
        methodModel.setConstructor(isConstructor);
        methodModel.setStatic(isStatic);

        List<AttributeModel> parametersCopy = new ArrayList<>();

        for (AttributeModel attributeModel : parameters) {
            parametersCopy.add(attributeModel.clone());
        }

        methodModel.setParameters(parametersCopy);

        return methodModel;
    }
}
