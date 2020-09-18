package class_diagram_editor.presentation.skins.arrows;

import de.tesis.dynaware.grapheditor.utils.ArrowHead;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

public class AssociationArrowHead extends ArrowHead {

    private double x;
    private double y;
    private double length;
    private double width;

    @Override
    public void setCenter(double pX, double pY) {
        super.setCenter(pX, pY);

        this.x = pX;
        this.y = pY;
    }

    @Override
    public void setLength(double pLength) {
        super.setLength(pLength);

        this.length = pLength;
    }

    @Override
    public void setWidth(double pWidth) {
        super.setWidth(pWidth);

        this.width = pWidth;
    }

    @Override
    public void draw() {
        getElements().clear();

        getElements().add(new MoveTo(x + width / 2, y - length));
        getElements().add(new LineTo(x, y + length / 2));
        getElements().add(new LineTo(x - width / 2, y - length));
    }

}
