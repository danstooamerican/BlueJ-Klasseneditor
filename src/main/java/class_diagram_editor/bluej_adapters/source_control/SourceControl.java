package class_diagram_editor.bluej_adapters.source_control;

import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.MissingJavaFileException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;
import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.CodeGenerator;
import class_diagram_editor.code_generation.SourceCodeControl;
import class_diagram_editor.diagram.ClassDiagram;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class SourceControl implements SourceCodeControl {

    private static final TextLocation START_LOCATION = new TextLocation(0, 0);

    private final BProject project;

    public SourceControl(BProject project) {
        this.project = project;
    }

    @Override
    public void generate(ClassDiagram classDiagram) {
        Iterator<CodeElement> iterator = classDiagram.iterator();

        try {
            BPackage bpackage = project.getPackages()[0];

            while (iterator.hasNext()) {
                CodeElement codeElement = iterator.next();

                Editor editor = createFile(bpackage, codeElement);

                if (editor != null) {
                    generateElement(editor, codeElement);
                } else {
                    System.err.println("Error generating code for " + codeElement.getName() + ".java");
                }
            }
        } catch (ProjectNotOpenException e) {
            e.printStackTrace();
        }
    }

    private Editor createFile(BPackage bPackage, CodeElement codeElement) {
        Editor editor = null;

        try {
            File javaFile = new File(bPackage.getDir(), codeElement.getName() + ".java");

            if (javaFile.exists()) {
                editor = bPackage.getBClass(codeElement.getName()).getEditor();
            } else {
                if (javaFile.createNewFile()) {
                    editor = bPackage.newClass(codeElement.getName()).getEditor();
                }
            }
        } catch (ProjectNotOpenException | PackageNotFoundException | MissingJavaFileException | IOException e) {
            e.printStackTrace();
        }

        return editor;
    }

    private void generateElement(Editor editor, CodeElement codeElement) {
        editor.setReadOnly(true);

        // line index is zero indexed
        int lastLine = editor.getLineCount() - 1;
        int lastColumn = editor.getLineLength(lastLine);
        final TextLocation endLocation = new TextLocation(lastLine, lastColumn);

        final CodeGenerator codeGenerator = new CodeGenerator(codeElement.getName(), editor.getText(START_LOCATION, endLocation));

        codeElement.accept(codeGenerator);

        editor.setText(START_LOCATION, endLocation, codeGenerator.getLastGeneratedCode());

        editor.saveFile();

        editor.setReadOnly(false);
    }
}
