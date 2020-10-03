package class_diagram_editor.presentation.create_element.attributes_tab;

import class_diagram_editor.diagram.AttributeModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import static class_diagram_editor.presentation.skins.ClassSkin.STATIC_CLASS;

public class DeletableAttributeListCell extends ListCell<AttributeModel> {

    private final Callback<AttributeModel, Void> onDeleted;

    public DeletableAttributeListCell(Callback<AttributeModel, Void> onDeleted) {
        this.onDeleted = onDeleted;
    }

    @Override
    protected void updateItem(AttributeModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            BorderPane bdpBackground = new BorderPane();

            class_diagram_editor.presentation.main_screen.skins.generators.UMLAttributeGenerator attributeGenerator = new class_diagram_editor.presentation.main_screen.skins.generators.UMLAttributeGenerator();

            Label lblAttribute = new Label(attributeGenerator.generate(item).trim());

            if (item.isStatic()) {
                lblAttribute.getStyleClass().add(STATIC_CLASS);
            }

            lblAttribute.setAlignment(Pos.CENTER_LEFT);
            lblAttribute.setMaxWidth(Double.MAX_VALUE);
            bdpBackground.setCenter(lblAttribute);

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
