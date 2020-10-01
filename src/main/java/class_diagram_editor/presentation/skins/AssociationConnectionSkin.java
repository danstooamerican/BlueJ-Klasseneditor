package class_diagram_editor.presentation.skins;

import class_diagram_editor.presentation.graph_editor.GraphController;
import class_diagram_editor.presentation.skins.arrows.AssociationArrowHead;
import class_diagram_editor.presentation.skins.arrows.UMLArrow;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.utils.Arrow;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.transform.Rotate;

import java.util.List;
import java.util.Map;

public class AssociationConnectionSkin extends BaseConnectionSkin {

    public static final String TYPE = GraphController.ConnectionType.ASSOCIATION.name();;

    private static final String STYLE_CLASS = "association-connection";

    private final Label identifier;

    public AssociationConnectionSkin(GConnection connection) {
        super(connection);

        this.arrow = new UMLArrow(new AssociationArrowHead());
        arrow.setManaged(false);
        arrow.getStyleClass().setAll(STYLE_CLASS);
        arrow.setHeadWidth(28);
        arrow.setHeadLength(15);

        identifier = new Label(connection.getId());

        root.getChildren().clear();
        root.getChildren().addAll(arrow, identifier, selectionHalo);
    }

    @Override
    public void draw(final Map<GConnectionSkin, Point2D[]> allPoints) {
        super.draw(allPoints);

        final Point2D[] points = allPoints == null ? null : allPoints.get(this);

        if (points != null && points.length >= 2) {
            final Point2D start = points[0];
            final Point2D end = points[points.length - 1];

            final double angleInRadians = getAngleInRadians(start, end);

            final double distance = start.distance(end);

            final double verticalLineOffset = 10;
            identifier.setTranslateX(start.getX() + distance / 2 * Math.sin(angleInRadians));
            identifier.setTranslateY(start.getY() + (distance / 2 + verticalLineOffset) * Math.cos(angleInRadians));
        }
    }

    private double getAngleInRadians(Point2D start, Point2D end) {
        final double deltaX = end.getX() - start.getX();
        final double deltaY = end.getY() - start.getY();

        return Math.atan2(deltaX, deltaY);
    }
}
