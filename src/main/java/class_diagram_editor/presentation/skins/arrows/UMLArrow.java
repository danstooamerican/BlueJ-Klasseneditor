package class_diagram_editor.presentation.skins.arrows;

import de.tesis.dynaware.grapheditor.utils.Arrow;
import de.tesis.dynaware.grapheditor.utils.ArrowHead;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;

/**
 * Extends the {@link Arrow} class by a customizable {@link ArrowHead}.
 */
public class UMLArrow extends Arrow {
    private static final String STYLE_CLASS_LINE = "arrow-line";
    private static final String STYLE_CLASS_HEAD = "arrow-head";

    private final Line line = new Line();
    private final ArrowHead head;

    private double startX;
    private double startY;

    private double endX;
    private double endY;

    /**
     * Creates a new {@link UMLArrow}.
     */
    public UMLArrow(ArrowHead arrowHead) {
        if (arrowHead != null) {
            this.head = arrowHead;
        } else {
            this.head = new ArrowHead();
        }

        line.getStyleClass().add(STYLE_CLASS_LINE);
        head.getStyleClass().add(STYLE_CLASS_HEAD);

        getChildren().addAll(line, head);
    }

    /**
     * Sets the width of the arrow-head.
     *
     * @param width
     *            the width of the arrow-head
     */
    @Override
    public void setHeadWidth(final double width) {
        head.setWidth(width);
    }

    /**
     * Sets the length of the arrow-head.
     *
     * @param length
     *            the length of the arrow-head
     */
    @Override
    public void setHeadLength(final double length) {
        head.setLength(length);
    }

    /**
     * Sets the radius of curvature of the {@link ArcTo} at the base of the
     * arrow-head.
     *
     * <p>
     * If this value is less than or equal to zero, a straight line will be
     * drawn instead. The default is -1.
     * </p>
     *
     * @param radius
     *            the radius of curvature of the arc at the base of the
     *            arrow-head
     */
    @Override
    public void setHeadRadius(final double radius) {
        head.setRadiusOfCurvature(radius);
    }

    /**
     * Gets the start point of the arrow.
     *
     * @return the start {@link Point2D} of the arrow
     */
    @Override
    public Point2D getStart() {
        return new Point2D(startX, startY);
    }

    /**
     * Sets the start position of the arrow.
     *
     * @param pStartX
     *            the x-coordinate of the start position of the arrow
     * @param pStartY
     *            the y-coordinate of the start position of the arrow
     */
    @Override
    public void setStart(final double pStartX, final double pStartY) {
        startX = pStartX;
        startY = pStartY;
    }

    /**
     * Gets the start point of the arrow.
     *
     * @return the start {@link Point2D} of the arrow
     */
    @Override
    public Point2D getEnd() {
        return new Point2D(endX, endY);
    }

    /**
     * Sets the end position of the arrow.
     *
     * @param pEndX
     *            the x-coordinate of the end position of the arrow
     * @param pEndY
     *            the y-coordinate of the end position of the arrow
     */
    @Override
    public void setEnd(final double pEndX, final double pEndY) {
        endX = pEndX;
        endY = pEndY;
    }

    /**
     * Draws the arrow for its current size and position values.
     */
    @Override
    public void draw() {
        final double deltaX = endX - startX;
        final double deltaY = endY - startY;

        final double angle = Math.atan2(deltaX, deltaY);

        final double headX = endX - head.getLength() / 2 * Math.sin(angle);
        final double headY = endY - head.getLength() / 2 * Math.cos(angle);

        line.setStartX(GeometryUtils.moveOffPixel(startX));
        line.setStartY(GeometryUtils.moveOffPixel(startY));
        line.setEndX(GeometryUtils.moveOffPixel(endX));
        line.setEndY(GeometryUtils.moveOffPixel(endY));

        head.setCenter(headX, headY);
        head.setAngle(Math.toDegrees(-angle));
        head.draw();
    }
}
