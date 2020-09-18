package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLAttributeGenerator;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLMethodGenerator;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Node skin for a 'tree-like' graph.
 */
public class ClassSkin extends DefaultNodeSkin {

    private static final String SEPARATOR_CLASS = "diagram-separator";
    private static final String ABSTRACT_CLASS = "abstract";
    private static final String STATIC_CLASS = "static";

    private final ClassModel classModel;

    public ClassSkin(final GNode node, ClassModel classModel) {
        super(node);

        this.classModel = classModel;

        VBox layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);

        Node header = getHeader();
        Node attributes = getAttributes();
        Node methods = getMethods();

        Separator attributesSeparator = new Separator();
        attributesSeparator.getStyleClass().add(SEPARATOR_CLASS);

        Separator methodsSeparator = new Separator();
        methodsSeparator.getStyleClass().add(SEPARATOR_CLASS);

        layout.getChildren().addAll(header, attributesSeparator, attributes, methodsSeparator, methods);

        getRoot().getChildren().add(layout);
    }

    private Node getHeader() {
        VBox layout = new VBox();
        layout.setPadding(new Insets(6, 8, 6, 8));
        layout.setAlignment(Pos.TOP_CENTER);

        Label lblName = new Label();
        lblName.textProperty().bind(classModel.nameProperty());

        if (classModel.isAbstract()) {
            layout.getChildren().add(new Label("<<abstract>>"));
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
