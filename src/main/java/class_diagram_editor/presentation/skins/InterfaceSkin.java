package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class InterfaceSkin extends DefaultNodeSkin {

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
