package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.create_element.CreateElementService;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLAttributeGenerator;
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

public class ClassSkin extends DefaultNodeSkin {

    public static final String TYPE = "class";

    private static final String SEPARATOR_CLASS = "diagram-separator";
    public static final String ABSTRACT_CLASS = "abstract";
    public static final String STATIC_CLASS = "static";

    private final VBox layout;

    private final ClassModel classModel;

    public ClassSkin(final GNode node, ClassModel classModel) {
        super(node);

        this.classModel = classModel;

        layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);
        buildUI();

        classModel.registerForUpdates(this::buildUI);

        getRoot().getChildren().add(layout);
        getRoot().visibleProperty().bind(classModel.isDisplayedProperty());

        getRoot().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                try {
                    CreateElementView.showCreateElementDialog(new CreateElementService(getItem().getId(), classModel));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    private void buildUI() {
        Node header = getHeader();
        Node attributes = getAttributes();
        Node methods = getMethods();

        Node attributesSeparator = new Separator();
        attributesSeparator.getStyleClass().add(SEPARATOR_CLASS);

        Node methodsSeparator = new Separator();
        methodsSeparator.getStyleClass().add(SEPARATOR_CLASS);

        layout.getChildren().clear();
        layout.getChildren().addAll(header, attributesSeparator, attributes, methodsSeparator, methods);
    }

    private Node getHeader() {
        VBox layout = new VBox();
        layout.setPadding(new Insets(6, 8, 6, 8));
        layout.setAlignment(Pos.TOP_CENTER);

        Label lblName = new Label(classModel.getName());

        if (classModel.isAbstract()) {
            Label lblAbstract = new Label("<<abstract>>");

            layout.getChildren().add(lblAbstract);
            lblName.getStyleClass().add(ABSTRACT_CLASS);
        }

        layout.getChildren().add(lblName);

        return layout;
    }

    private Node getAttributes() {
        VBox layout = new VBox();

        if (classModel.hasAttributes()) {
            UMLAttributeGenerator attributeGenerator = new UMLAttributeGenerator();

            layout.setPadding(new Insets(6, 8, 6, 8));
            layout.setAlignment(Pos.TOP_LEFT);

            for (AttributeModel attributeModel : classModel.getAttributes()) {
                final String attributeEntry = attributeGenerator.generate(attributeModel).trim();

                Label lblAttribute = new Label(attributeEntry);

                if (attributeModel.isStatic()) {
                    lblAttribute.getStyleClass().add(STATIC_CLASS);
                }

                layout.getChildren().add(lblAttribute);
            }
        }

        return layout;
    }

    private Node getMethods() {
        VBox layout = new VBox();

        if (classModel.hasMethods()) {
            UMLMethodGenerator methodGenerator = new UMLMethodGenerator();

            layout.setPadding(new Insets(6, 8, 6, 8));
            layout.setAlignment(Pos.TOP_LEFT);

            for (MethodModel method : classModel.getMethods()) {
                final String methodEntry = methodGenerator.generate(method).trim();

                Label lblMethod = new Label(methodEntry);

                if (method.isAbstract()) {
                    lblMethod.getStyleClass().add(ABSTRACT_CLASS);
                } else if (method.isStatic()) {
                    lblMethod.getStyleClass().add(STATIC_CLASS);
                }

                layout.getChildren().add(lblMethod);
            }
        }

        return layout;
    }
}
