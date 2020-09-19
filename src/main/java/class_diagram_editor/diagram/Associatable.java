package class_diagram_editor.diagram;

import java.util.Map;

public interface Associatable {

    String getName();

    boolean hasAssociations();

    void addAssociation(String identifier, Associatable associatable);

    void removeAssociation(String identifier);

    Map<String, Associatable> getAssociations();

}
