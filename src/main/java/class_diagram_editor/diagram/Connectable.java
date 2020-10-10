package class_diagram_editor.diagram;

import lombok.NonNull;

import java.util.Collection;
import java.util.Map;

/***
 * Represents an UML element which can be connected to other elements.
 */
public interface Connectable {

    /**
     * @return the name of the element.
     */
    String getName();

    /**
     * @return whether the element has any associations.
     */
    boolean hasAssociations();

    /**
     * Adds a new association to the given {@link Connectable} with the given identifier.
     *
     * @param identifier the identifier of the association.
     * @param connectable the associated element.
     *
     * @return whether the association was created successfully.
     */
    boolean addAssociation(@NonNull String identifier, @NonNull Connectable connectable);

    /**
     * Removes the association with the given identifier.
     *
     * @param identifier the identifier of the association.
     */
    void removeAssociation(@NonNull String identifier);

    /**
     * @return all associations of the element. This includes associations of elements which are extended.
     */
    Map<String, Connectable> getAssociations();

    /**
     * @return whether the element is extending any other element.
     */
    boolean isExtending();

    /**
     * Checks if the given element is extended.
     *
     * @param connectable the element to check.
     *
     * @return whether the given element is extended.
     */
    boolean isExtending(Connectable connectable);

    /**
     * Adds a new extends relation to the given element.
     *
     * @param extendable the element which is extended.
     */
    void addExtendsRelation(@NonNull Connectable extendable);

    /**
     * Removes the extends relation to the given element.
     *
     * @param connectable the element to remove from the extends relation.
     */
    void removeExtendsRelation(@NonNull Connectable connectable);

    /**
     * @return all elements which are extended.
     */
    Collection<Connectable> getExtendsRelations();

    /**
     * Removes all extends relations and associations which include the given element.
     *
     * @param removedElement the element to remove all references for.
     */
    void removeReferencesTo(Connectable removedElement);
}
