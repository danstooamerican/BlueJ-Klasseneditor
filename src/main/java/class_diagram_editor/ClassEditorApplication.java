package class_diagram_editor;

import class_diagram_editor.code_generation.SourceCodeControl;
import class_diagram_editor.presentation.MainScreenView;
import class_diagram_editor.presentation.MainScreenViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassEditorApplication extends Application implements Runnable {
    private final String title;
    private final SourceCodeControl sourceCodeControl;

    public ClassEditorApplication(String projectTitle, SourceCodeControl sourceCodeControl) {
        this.title = "Klasseneditor: " + projectTitle;
        this.sourceCodeControl = sourceCodeControl;
    }

    public ClassEditorApplication(SourceCodeControl sourceCodeControl) {
        this.title = "Klasseneditor";
        this.sourceCodeControl = sourceCodeControl;
    }

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
