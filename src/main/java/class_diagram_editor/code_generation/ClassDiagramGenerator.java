package class_diagram_editor.code_generation;

import class_diagram_editor.diagram.ClassDiagram;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ClassDiagramGenerator {
    private final Map<String, JavaClass> elementSourceCode;

    public ClassDiagramGenerator() {
        this.elementSourceCode = new HashMap<>();
    }

    /**
     * Adds the given {@code sourceCode} to the elements to parse for the {@link class_diagram_editor.diagram.ClassDiagram}.
     * If the given source code cannot be parsed the element is ignored.
     *
     * @param elementName the elementName of the element.
     * @param sourceCode the Java source code of the element.
     */
    public void addSource(String elementName, String sourceCode) {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();

            builder.addSource(new StringReader(sourceCode));

            JavaClass javaClass = builder.getClassByName(elementName);

            if (javaClass != null) {
                elementSourceCode.put(elementName, javaClass);
            }
        } catch (Exception ex) { // ParseException is thrown but not declared to be thrown
            // ignore this element
        }
    }

    /**
     * Creates a new {@link class_diagram_editor.diagram.ClassDiagram class diagram} which contains the elements which
     * were added with {@code addSource(String name, String elementContent)}.
     */
    public ClassDiagram generate() {
        return new ClassDiagram();
    }
}
