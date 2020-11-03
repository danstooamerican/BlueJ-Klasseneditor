package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.Editable;
import class_diagram_editor.presentation.graph_editor.GraphController;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.scene.Node;

public class ImplementsConnectionSkin extends BaseConnectionSkin {

    public static final String TYPE = GraphController.ConnectionType.IMPLEMENTS.name();

    private static final String STYLE_CLASS = "implements-connection";

    public ImplementsConnectionSkin(GConnection connection, Editable<Connectable> start, Editable<Connectable> end) {
        super(connection, start, end);

        arrow.setManaged(false);
        arrow.getStyleClass().setAll(STYLE_CLASS);
        arrow.setHeadWidth(28);
        arrow.setHeadLength(20);
    }
}
