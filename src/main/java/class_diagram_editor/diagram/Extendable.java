package class_diagram_editor.diagram;

import java.util.Collection;

public interface Extendable {

    String getName();

    boolean isExtending();

    void addExtendsRelation(Extendable extendable);

    Collection<Extendable> getExtendsRelations();

}
