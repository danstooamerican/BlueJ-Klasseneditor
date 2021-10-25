package class_diagram_editor.bluej_adapters;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import class_diagram_editor.ClassEditorApplication;
import class_diagram_editor.bluej_adapters.menu.BlueJMenuGenerator;
import class_diagram_editor.bluej_adapters.source_control.PrintCodeSourceControl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.EventQueue;

/**
 * BlueJ Extension entry point
 */
public class BlueJExtension extends Extension {

    private static final String NAME = "Klasseneditor";
    private static final String VERSION = "0.0.1";

    /**
     * Main method for starting the application directly
     *
     * @param args application arguments (ignored)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JFXPanel();

            Platform.runLater(new ClassEditorApplication(new PrintCodeSourceControl()));
        });
    }

    /**
     * @return whether the extension is compatible with the current BlueJ
     */
    public boolean isCompatible() {
        return true;
    }

    /**
     * Method hook which is executed when the extension is launched.
     *
     * @param blueJ the BlueJ instance which launches the extension.
     */
    public void startup(BlueJ blueJ) {
        blueJ.setMenuGenerator(new BlueJMenuGenerator());
    }

    /**
     * @return the name of the extension.
     */
    public String getName() {
        return NAME;
    }

    /**
     * @return the version of the extension. TODO: update version on release
     */
    public String getVersion() {
        return VERSION;
    }
}
