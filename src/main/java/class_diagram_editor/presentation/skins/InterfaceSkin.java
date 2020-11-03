package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.create_element.CreateElementService;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class InterfaceSkin extends DefaultNodeSkin {

    public static final String TYPE = "interface";

    private static final String SEPARATOR_CLASS = "diagram-separator";

    private final VBox layout;

    private final InterfaceModel interfaceModel;

    public InterfaceSkin(final GNode node, InterfaceModel interfaceModel) {
        super(node);

        this.interfaceModel = interfaceModel;

        layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);
        buildUI();

        interfaceModel.registerForUpdates(this::buildUI);

        getRoot().getChildren().add(layout);
        getRoot().visibleProperty().bind(interfaceModel.isDisplayedProperty());

        getRoot().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                try {
                    CreateElementView.showCreateElementDialog(new CreateElementService(getItem().getId(), interfaceModel));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    private void buildUI() {
        Node header = getHeader();
        Node methods = getMethods();

        Separator methodsSeparator = new Separator();
        methodsSeparator.getStyleClass().add(SEPARATOR_CLASS);

        layout.getChildren().clear();
        layout.getChildren().addAll(header, methodsSeparator, methods);
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
