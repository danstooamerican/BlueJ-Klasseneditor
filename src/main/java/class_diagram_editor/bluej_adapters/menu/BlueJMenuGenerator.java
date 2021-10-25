package class_diagram_editor.bluej_adapters.menu;

import bluej.extensions2.BPackage;
import bluej.extensions2.BProject;
import bluej.extensions2.MenuGenerator;
import bluej.extensions2.ProjectNotOpenException;
import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.bluej_adapters.source_control.SourceControl;
import javafx.scene.control.MenuItem;

/**
 * Adds extra JMenu entries which belong to the {@link class_diagram_editor.bluej_adapters.BlueJExtension extension}.
 */
public class BlueJMenuGenerator extends MenuGenerator {

    @Override
    public MenuItem getToolsMenuItem(BPackage bp) {
        try {
            BProject project = bp.getProject();

            MenuItem menuItem = new MenuItem("Klasseneditor öffnen");

            final String projectName = project.getName();
            final SourceControl sourceControl = new SourceControl(project);

            menuItem.setOnAction(event -> {
                new ClassEditorApplication(projectName, sourceControl).run();
            });

            return menuItem;
        } catch (ProjectNotOpenException e) {
            return null;
        }
    }
}
