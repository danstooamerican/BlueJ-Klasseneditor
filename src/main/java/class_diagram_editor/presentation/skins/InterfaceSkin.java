package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.create_element.CreateElementModel;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.create_element.CreateElementViewModel;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InterfaceSkin extends DefaultNodeSkin {

    public static final String TYPE = "interface";

    private static final String SEPARATOR_CLASS = "diagram-separator";

    private final InterfaceModel interfaceModel;

    public InterfaceSkin(final GNode node, InterfaceModel interfaceModel) {
        super(node);

        this.interfaceModel = interfaceModel;

        VBox layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);

        Node header = getHeader();
        Node methods = getMethods();

        Separator methodsSeparator = new Separator();
        methodsSeparator.getStyleClass().add(SEPARATOR_CLASS);

        layout.getChildren().addAll(header, methodsSeparator, methods);

        getRoot().getChildren().add(layout);

        getRoot().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                CreateElementView.showCreateElementDialog(new CreateElementModel(getItem().getId(), interfaceModel));
            }
        });
    }

    private Node getHeader() {
        VBox layout = new VBox();
        layout.setPadding(new Insets(6, 8, 6, 8));
        layout.setAlignment(Pos.TOP_CENTER);

        layout.getChildren().add(new Label("<<interface>>"));

        layout.getChildren().add(new Label(interfaceModel.getName()));

        return layout;
    }

    private Node getMethods() {
        VBox layout = new VBox();

        if (interfaceModel.hasMethods()) {
            UMLMethodGenerator methodGenerator = new UMLMethodGenerator();

            layout.setPadding(new Insets(6, 8, 6, 8));
            layout.setAlignment(Pos.TOP_LEFT);

            for (MethodModel method : interfaceModel.getMethods()) {
                final String methodEntry = methodGenerator.generate(method).trim();

                layout.getChildren().add(new Label(methodEntry));
            }
        }

        return layout;
    }
}
