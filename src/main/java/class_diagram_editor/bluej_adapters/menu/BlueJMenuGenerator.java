package class_diagram_editor.bluej_adapters.menu;

import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.MenuGenerator;
import bluej.extensions.ProjectNotOpenException;
import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.bluej_adapters.source_control.SourceControl;
import class_diagram_editor.bluej_adapters.source_control.SourceCodeControl;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Adds extra JMenu entries which belong to the {@link class_diagram_editor.bluej_adapters.BlueJExtension extension}.
 */
public class BlueJMenuGenerator extends MenuGenerator {

    @Override
    public JMenuItem getToolsMenuItem(BPackage bp) {
        try {
            BProject project = bp.getProject();

            return new JMenuItem(new OpenClassEditorAction(project.getName(), new SourceControl(project)));
        } catch (ProjectNotOpenException e) {
            return null;
        }
    }

    private static class OpenClassEditorAction extends AbstractAction {
        private final SourceCodeControl sourceCodeControl;
        private final String projectTitle;

        public OpenClassEditorAction(String projectTitle, SourceCodeControl sourceCodeControl) {
            this.sourceCodeControl = sourceCodeControl;
            this.projectTitle = projectTitle;

            putValue(AbstractAction.NAME, "Klasseneditor öffnen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new ClassEditorApplication(projectTitle, sourceCodeControl).run();
        }
    }
}
