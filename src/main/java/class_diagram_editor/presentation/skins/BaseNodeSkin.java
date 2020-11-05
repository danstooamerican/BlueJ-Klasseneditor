package class_diagram_editor.presentation.skins;

import class_diagram_editor.presentation.graph_editor.DiagramElementService;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import de.tesis.dynaware.grapheditor.utils.ResizableBox;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

public abstract class BaseNodeSkin extends DefaultNodeSkin {

    private final DiagramElementService diagramElementService;

    public BaseNodeSkin(GNode node) {
        super(node);

        this.diagramElementService = new DiagramElementService();
    }

    @Override
    protected DraggableBox createContainer() {
        return new ResizableBox() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                layoutConnectors();
            }

            @Override
            public void positionMoved() {
                super.positionMoved();

                BaseNodeSkin.super.impl_positionMoved();
            }

            @Override
            protected void handleMouseReleased(MouseEvent pEvent) {
                super.handleMouseReleased(pEvent);

                Platform.runLater(diagramElementService::reconnectElements);
            }
        };
    }

}
