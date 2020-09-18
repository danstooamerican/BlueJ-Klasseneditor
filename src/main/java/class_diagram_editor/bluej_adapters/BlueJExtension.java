package class_diagram_editor.bluej_adapters;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.bluej_adapters.menu.BlueJMenuGenerator;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.EventQueue;

public class BlueJExtension extends Extension {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JFXPanel();

            Platform.runLater(new ClassEditorApplication());
        });
    }

    public boolean isCompatible() {
        return true;
    }

    public void startup(BlueJ blueJ) {
        blueJ.setMenuGenerator(new BlueJMenuGenerator());
    }

    public String getName() {
        return "Klasseneditor";
    }

    public String getVersion() {
        return "0.0.1";
    }
}
