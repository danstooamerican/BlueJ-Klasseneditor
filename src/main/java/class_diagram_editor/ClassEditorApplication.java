package class_diagram_editor;

import class_diagram_editor.bluej_adapters.source_control.SourceCodeControl;
import class_diagram_editor.presentation.MainScreenView;
import class_diagram_editor.presentation.MainScreenViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX entry point which is called when
 * opening the extension in {@link class_diagram_editor.bluej_adapters.menu.BlueJMenuGenerator}.
 */
public class ClassEditorApplication extends Application implements Runnable {
    private final String title;
    private final SourceCodeControl sourceCodeControl;

    /**
     * Creates a new {@link ClassEditorApplication} with the given title and {@link SourceCodeControl}.
     *
     * @param projectTitle the title of the application.
     * @param sourceCodeControl the {@link SourceCodeControl} which is used to create source code.
     */
    public ClassEditorApplication(String projectTitle, SourceCodeControl sourceCodeControl) {
        this.title = "Klasseneditor: " + projectTitle;
        this.sourceCodeControl = sourceCodeControl;
    }

    /**
     * Creates a new {@link ClassEditorApplication} with a default title and the given {@link SourceCodeControl}.
     *
     * @param sourceCodeControl the {@link SourceCodeControl} which is used to create source code.
     */
    public ClassEditorApplication(SourceCodeControl sourceCodeControl) {
        this.title = "Klasseneditor";
        this.sourceCodeControl = sourceCodeControl;
    }

    /**
     * JavaFX starter main.
     *
     * @param args the application arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle(title);

        ViewTuple<MainScreenView, MainScreenViewModel> viewTuple = FluentViewLoader.fxmlView(MainScreenView.class)
                .codeBehind(new MainScreenView())
                .viewModel(new MainScreenViewModel(sourceCodeControl))
                .load();

        Parent root = viewTuple.getView();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("skins.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void run() {
        start(new Stage());
    }
}
