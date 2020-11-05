package class_diagram_editor.presentation;

import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Editable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.presentation.create_element.CreateElementService;
import class_diagram_editor.presentation.create_element.CreateElementView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

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

            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        if (item instanceof ClassModel) {
                            final ClassModel classModel = (ClassModel) item;

                            CreateElementView.showCreateElementDialog(new CreateElementService(ClassDiagram.getInstance().getIdOf(classModel), classModel));
                        } else if (item instanceof InterfaceModel) {
                            final InterfaceModel interfaceModel = (InterfaceModel) item;

                            CreateElementView.showCreateElementDialog(new CreateElementService(ClassDiagram.getInstance().getIdOf(interfaceModel), interfaceModel));
                        }
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });

            setGraphic(bdpBackground);
        }
    }

}
