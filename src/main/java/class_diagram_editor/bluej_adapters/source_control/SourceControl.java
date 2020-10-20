package class_diagram_editor.bluej_adapters.source_control;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.MissingJavaFileException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;
import class_diagram_editor.code_generation.ClassDiagramGenerator;
import class_diagram_editor.code_generation.CodeElement;
import class_diagram_editor.code_generation.JavaCodeGenerator;
import class_diagram_editor.diagram.ClassDiagram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

/**
 * Encapsulates the BlueJ Editor and contains methods to edit source code.
 */
public class SourceControl implements SourceCodeControl {

    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final TextLocation START_LOCATION = new TextLocation(0, 0);

    private final BProject project;

    /**
     * Creates a new {@link SourceControl}.
     *
     * @param project the BlueJ project which is edited.
     */
    public SourceControl(BProject project) {
        this.project = project;
    }

    @Override
    public void generateCode(ClassDiagram classDiagram) {
        Iterator<CodeElement> iterator = classDiagram.iterator();

        try {
            BPackage bpackage = project.getPackages()[0];

            while (iterator.hasNext()) {
                CodeElement codeElement = iterator.next();

                Editor editor = createFile(bpackage, codeElement);

                if (editor != null) {
                    generateElement(editor, codeElement);
                } else {
                    System.err.println("Error generating code for " + codeElement.getName() + JAVA_FILE_EXTENSION);
                }
            }
        } catch (ProjectNotOpenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ClassDiagram generateDiagram() {
        final ClassDiagramGenerator classDiagramGenerator = new ClassDiagramGenerator();

        try {
            BPackage bpackage = project.getPackages()[0];

            for (BClass bClass : bpackage.getClasses()) {
                final Editor editor = bClass.getEditor();

                final String elementContent = editor.getText(START_LOCATION, getEndLocation(editor));

                classDiagramGenerator.addSource(bClass.getName(), elementContent);
            }
        } catch (ProjectNotOpenException | PackageNotFoundException e) {
            e.printStackTrace();
        }

        return classDiagramGenerator.generate();
    }

    private Editor createFile(BPackage bPackage, CodeElement codeElement) {
        Editor editor = null;

        try {
            File javaFile = new File(bPackage.getDir(), codeElement.getLastGeneratedName() + JAVA_FILE_EXTENSION);

            boolean elementRenamed = !codeElement.getName().equals(codeElement.getLastGeneratedName());
            if (elementRenamed) {
                javaFile = renameOldClassWithNew(bPackage, javaFile, codeElement);
            }

            if (javaFile.exists() && !elementRenamed) {
                editor = bPackage.getBClass(codeElement.getName()).getEditor();
            } else {
                if (javaFile.createNewFile() || javaFile.exists()) {
                    editor = bPackage.newClass(codeElement.getName()).getEditor();
                }
            }
        } catch (ProjectNotOpenException | PackageNotFoundException | MissingJavaFileException
                | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return editor;
    }

    private File renameOldClassWithNew(BPackage bPackage, File javaFile, CodeElement codeElement)
            throws ProjectNotOpenException, PackageNotFoundException, ClassNotFoundException, IOException {
        File newJavaFile = new File(bPackage.getDir(), codeElement.getName() + JAVA_FILE_EXTENSION);

        if (javaFile.exists()) {
            Files.copy(Paths.get(javaFile.toURI()), Paths.get(newJavaFile.toURI()),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }

        BClass bClass = bPackage.getBClass(codeElement.getLastGeneratedName());

        if (bClass != null) {
            bClass.remove();
        }

        if (javaFile.exists()) {
            javaFile.delete();
        }

        return newJavaFile;
    }

    private void generateElement(Editor editor, CodeElement codeElement) {
        editor.setReadOnly(true);

        final TextLocation endLocation = getEndLocation(editor);

        final JavaCodeGenerator codeGenerator = new JavaCodeGenerator(
                codeElement.getLastGeneratedName(), editor.getText(START_LOCATION, endLocation));

        codeElement.accept(codeGenerator);

        editor.setText(START_LOCATION, endLocation, codeGenerator.getLastGeneratedCode());

        editor.saveFile();

        editor.setReadOnly(false);
    }

    private TextLocation getEndLocation(Editor editor) {
        // line index is zero indexed
        int lastLine = editor.getLineCount() - 1;
        int lastColumn = editor.getLineLength(lastLine);

        return new TextLocation(lastLine, lastColumn);
    }
}
