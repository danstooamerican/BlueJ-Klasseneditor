package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.InterfaceModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class DeletableListCell extends ListCell<InterfaceModel> {

    private Callback<InterfaceModel, Void> onDeleted;

    public DeletableListCell(Callback<InterfaceModel, Void> onDeleted) {
        this.onDeleted = onDeleted;
    }

    @Override
    protected void updateItem(InterfaceModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            BorderPane bdpBackground = new BorderPane();

            Label lblName = new Label(item.getName());
            lblName.setAlignment(Pos.CENTER_LEFT);
            lblName.setMaxWidth(Double.MAX_VALUE);
            bdpBackground.setCenter(lblName);

            Button btnDelete = new Button("Löschen");
            btnDelete.setOnAction(event -> {
                if (onDeleted != null) {
                    onDeleted.call(item);
                }
            });
            bdpBackground.setRight(btnDelete);

            setGraphic(bdpBackground);
        }
    }
}
