package class_diagram_editor.presentation.create_element.methods_tab;

import class_diagram_editor.diagram.MethodModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import static class_diagram_editor.presentation.skins.ClassSkin.STATIC_CLASS;

public class DeletableMethodListCell extends ListCell<MethodModel> {

    private final Callback<MethodModel, Void> onDeleted;

    public DeletableMethodListCell(Callback<MethodModel, Void> onDeleted) {
        this.onDeleted = onDeleted;
    }

    @Override
    protected void updateItem(MethodModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            BorderPane bdpBackground = new BorderPane();

            Label lblAttribute = new Label(getMethodSignatureWithoutParameterNames(item));

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

    private String getMethodSignatureWithoutParameterNames(MethodModel methodModel) {
        class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator methodGenerator = new class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator();

        final String methodUML = methodGenerator.generate(methodModel).trim();

        StringBuilder stringBuilder = new StringBuilder(methodUML.substring(0, methodUML.indexOf("(") + 1));
        final String delimiter = ", ";
        methodModel.getParameters().forEach(attributeModel -> {
            stringBuilder.append(attributeModel.getType())
                    .append(delimiter);
        });

        if (!methodModel.getParameters().isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
        }

        stringBuilder.append(methodUML.substring(methodUML.lastIndexOf(")")));

        return stringBuilder.toString();
    }
}
