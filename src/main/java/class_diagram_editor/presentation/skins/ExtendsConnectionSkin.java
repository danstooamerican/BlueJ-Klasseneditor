package class_diagram_editor.presentation.skins;

import class_diagram_editor.presentation.graph_editor.GraphController;
import de.tesis.dynaware.grapheditor.model.GConnection;

public class ExtendsConnectionSkin extends BaseConnectionSkin {

    public static final String TYPE = GraphController.ConnectionType.EXTENDS.name();;

    private static final String STYLE_CLASS = "extends-connection";

    public ExtendsConnectionSkin(GConnection connection) {
        super(connection);

        arrow.setManaged(false);
        arrow.getStyleClass().setAll(STYLE_CLASS);
        arrow.setHeadWidth(28);
        arrow.setHeadLength(20);
    }
}
