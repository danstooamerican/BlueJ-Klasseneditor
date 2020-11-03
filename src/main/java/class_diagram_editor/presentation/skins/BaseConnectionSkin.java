package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.Editable;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.utils.Arrow;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.Map;

public abstract class BaseConnectionSkin extends GConnectionSkin {

    private static final double OFFSET_FROM_CONNECTOR = ConnectorSkin.RADIUS;

    private static final double HALO_BREADTH_OFFSET = 5;
    private static final double HALO_LENGTH_OFFSET_START = 1;
    private static final double HALO_LENGTH_OFFSET_END = 12;

    private static final String STYLE_CLASS_SELECTION_HALO = "connection-selection-halo";

    private final Line haloFirstSide = new Line();
    private final Line haloSecondSide = new Line();
    protected final Group selectionHalo = new Group(haloFirstSide, haloSecondSide);

    protected Arrow arrow = new Arrow();
    protected final Group root = new Group();

    public BaseConnectionSkin(GConnection connection, Editable<Connectable> start, Editable<Connectable> end) {
        super(connection);

        haloFirstSide.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
        haloSecondSide.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
        selectionHalo.setVisible(false);

        root.setOnMousePressed(this::handleMousePressed);
        root.setOnMouseDragged((Event::consume));

        root.getChildren().addAll(arrow, selectionHalo);

        getRoot().visibleProperty().bind(start.isDisplayedProperty().and(end.isDisplayedProperty()));
    }

    protected void handleMousePressed(final MouseEvent event) {
        final GraphEditor editor = getGraphEditor();
        if (editor == null) {
            return;
        }

        if (!isSelected()) {
            getGraphEditor().getSelectionManager().clearSelection();
            editor.getSelectionManager().select(getItem());
        }
    }

    @Override
    protected void selectionChanged(final boolean selected) {
        selectionHalo.setVisible(selected);

        if (selected) {
            drawSelectionHalo();
        }
    }

    @Override
    public void draw(final Map<GConnectionSkin, Point2D[]> allPoints) {
        if (isSelected()) {
            drawSelectionHalo();
        }

        final Point2D[] points = allPoints == null ? null : allPoints.get(this);

        if (points != null && points.length >= 2) {
            final Point2D start = points[0];
            final Point2D end = points[points.length - 1];

            ArrowUtils.draw(arrow, start, end, OFFSET_FROM_CONNECTOR);
        }
    }

    private void drawSelectionHalo() {
        final Point2D arrowStart = arrow.getStart();
        final Point2D arrowEnd = arrow.getEnd();

        final double deltaX = arrowEnd.getX() - arrowStart.getX();
        final double deltaY = arrowEnd.getY() - arrowStart.getY();

        final double angle = Math.atan2(deltaX, deltaY);

        final double breadthOffsetX = HALO_BREADTH_OFFSET * Math.cos(angle);
        final double breadthOffsetY = HALO_BREADTH_OFFSET * Math.sin(angle);

        final double lengthOffsetStartX = HALO_LENGTH_OFFSET_START * Math.sin(angle);
        final double lengthOffsetStartY = HALO_LENGTH_OFFSET_START * Math.cos(angle);

        final double lengthOffsetEndX = HALO_LENGTH_OFFSET_END * Math.sin(angle);
        final double lengthOffsetEndY = HALO_LENGTH_OFFSET_END * Math.cos(angle);

        haloFirstSide.setStartX(GeometryUtils.moveOffPixel(arrowStart.getX() - breadthOffsetX + lengthOffsetStartX));
        haloFirstSide.setStartY(GeometryUtils.moveOffPixel(arrowStart.getY() + breadthOffsetY + lengthOffsetStartY));

        haloSecondSide.setStartX(GeometryUtils.moveOffPixel(arrowStart.getX() + breadthOffsetX + lengthOffsetStartX));
        haloSecondSide.setStartY(GeometryUtils.moveOffPixel(arrowStart.getY() - breadthOffsetY + lengthOffsetStartY));

        haloFirstSide.setEndX(GeometryUtils.moveOffPixel(arrowEnd.getX() - breadthOffsetX - lengthOffsetEndX));
        haloFirstSide.setEndY(GeometryUtils.moveOffPixel(arrowEnd.getY() + breadthOffsetY - lengthOffsetEndY));

        haloSecondSide.setEndX(GeometryUtils.moveOffPixel(arrowEnd.getX() + breadthOffsetX - lengthOffsetEndX));
        haloSecondSide.setEndY(GeometryUtils.moveOffPixel(arrowEnd.getY() - breadthOffsetY - lengthOffsetEndY));
    }

    @Override
    public void setJointSkins(List<GJointSkin> jointSkins) {

    }

    @Override
    public Node getRoot() {
        return root;
    }
}
