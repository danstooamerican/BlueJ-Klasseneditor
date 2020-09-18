package class_diagram_editor.diagram;

import java.util.Collection;

public interface Associatable {

    String getName();

    void addAssociation(Associatable associatable);

    void removeAssociation(Associatable associatable);

    Collection<Associatable> getAssociations();

}
