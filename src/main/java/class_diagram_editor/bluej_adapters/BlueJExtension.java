package class_diagram_editor.bluej_adapters;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.bluej_adapters.menu.BlueJMenuGenerator;
import class_diagram_editor.bluej_adapters.source_control.PrintCodeSourceControl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.EventQueue;

public class BlueJExtension extends Extension {

    private static final String NAME = "Klasseneditor";
    private static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JFXPanel();

            Platform.runLater(new ClassEditorApplication(new PrintCodeSourceControl()));
        });
    }

    public boolean isCompatible() {
        return true;
    }

    public void startup(BlueJ blueJ) {
        blueJ.setMenuGenerator(new BlueJMenuGenerator());
    }

    public String getName() {
        return NAME;
    }

    public String getVersion() {
        return VERSION;
    }
}
