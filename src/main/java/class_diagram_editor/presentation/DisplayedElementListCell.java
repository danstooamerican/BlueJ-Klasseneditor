package class_diagram_editor.presentation;

import class_diagram_editor.diagram.Editable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;

public class DisplayedElementListCell extends ListCell<Editable<?>> {

    @Override
    protected void updateItem(Editable<?> item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            BorderPane bdpBackground = new BorderPane();

            Label lblName = new Label(item.toString());

            lblName.setAlignment(Pos.CENTER_LEFT);
            lblName.setMaxWidth(Double.MAX_VALUE);
            bdpBackground.setCenter(lblName);

            CheckBox ckbIsDisplayed = new CheckBox();
            ckbIsDisplayed.selectedProperty().bindBidirectional(item.isDisplayedProperty());
            ckbIsDisplayed.setPadding(new Insets(0, 8, 0, 8));

            bdpBackground.setLeft(ckbIsDisplayed);


            setGraphic(bdpBackground);
        }
    }

}
