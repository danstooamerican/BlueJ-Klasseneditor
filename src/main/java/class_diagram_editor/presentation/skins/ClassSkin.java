package class_diagram_editor.presentation.skins;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.create_element.CreateElementModel;
import class_diagram_editor.presentation.create_element.CreateElementView;
import class_diagram_editor.presentation.create_element.CreateElementViewModel;
import class_diagram_editor.presentation.main_screen.skins.generators.UMLAttributeGenerator;
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

public class ClassSkin extends DefaultNodeSkin {

    public static final String TYPE = "class";

    private static final String SEPARATOR_CLASS = "diagram-separator";
    public static final String ABSTRACT_CLASS = "abstract";
    public static final String STATIC_CLASS = "static";

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

        getRoot().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                CreateElementView.showCreateElementDialog(new CreateElementModel(getItem().getId(), classModel));
            }
        });
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
