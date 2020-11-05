package class_diagram_editor.presentation.graph_editor;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.Connectable;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphLayout {

    private final Map<String, Point2D> elementPositions;

    public GraphLayout() {
        this.elementPositions = new HashMap<>();
    }

    public GraphLayout(List<GNode> nodes, ClassDiagram classDiagram) {
        this.elementPositions = new HashMap<>();

        for (GNode node : nodes) {
            final Connectable connectable = classDiagram.findElement(node.getId());

            if (connectable != null) {
                elementPositions.put(connectable.getName(), new Point2D(node.getX(), node.getY()));
            }
        }
    }

    public Point2D getPosition(String elementName) {
        if (elementPositions.containsKey(elementName)) {
            return elementPositions.get(elementName);
        }

        return new Point2D(100, 100);
    }
}
