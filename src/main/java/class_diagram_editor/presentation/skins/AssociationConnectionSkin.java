package class_diagram_editor.presentation.skins;

import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.utils.Arrow;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;

public class AssociationConnectionSkin extends GConnectionSkin {

    public static final String TYPE = "association-connection";

    private static final String STYLE_CLASS = "association-connection";

    private static final double OFFSET_FROM_CONNECTOR = ConnectorSkin.RADIUS;

    private final Arrow arrow = new Arrow();
    private final Group root = new Group();

    public AssociationConnectionSkin(GConnection connection) {
        super(connection);

        arrow.setManaged(false);
        arrow.getStyleClass().setAll(STYLE_CLASS);
        arrow.setHeadWidth(28);
        arrow.setHeadLength(20);
        arrow.setHeadRadius(10);

        root.getChildren().addAll(arrow);
    }

    @Override
    protected void selectionChanged(boolean isSelected) {

    }

    @Override
    public void setJointSkins(List<GJointSkin> jointSkins) {

    }

    @Override
    public void draw(final Map<GConnectionSkin, Point2D[]> allPoints)
    {
        final Point2D[] points = allPoints == null ? null : allPoints.get(this);

        if (points != null && points.length >= 2)
        {
            final Point2D start = points[0];
            final Point2D end = points[points.length - 1];

            ArrowUtils.draw(arrow, start, end, OFFSET_FROM_CONNECTOR);
        }
    }

    @Override
    public Node getRoot() {
        return root;
    }
}
