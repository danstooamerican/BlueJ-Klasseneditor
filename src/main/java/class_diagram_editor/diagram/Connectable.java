package class_diagram_editor.diagram;

import java.util.Collection;
import java.util.Map;

public interface Connectable {

    String getName();

    boolean hasAssociations();

    boolean addAssociation(String identifier, Connectable connectable);

    void removeAssociation(String identifier);

    Map<String, Connectable> getAssociations();

    boolean isExtending();

    void addExtendsRelation(Connectable extendable);

    Collection<Connectable> getExtendsRelations();

}
