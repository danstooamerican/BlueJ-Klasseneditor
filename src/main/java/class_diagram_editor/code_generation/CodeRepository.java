package class_diagram_editor.code_generation;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the body of methods inside a Java class which can be retrieved with the method signature.
 */
public class CodeRepository {

    private static final String FOUR_SPACE_INDENT = " {4}";

    private final Map<String, String> methodImplementations;

    /**
     * Creates a new {@link CodeRepository} for the given java source code of a Java class with the given name.
     * If the source code cannot be parsed the {@link CodeRepository} is empty.
     *
     * @param elementName the name of the Java class.
     * @param sourceCode the source code of the Java class.
     */
    public CodeRepository(String elementName, String sourceCode) {
        this.methodImplementations = new HashMap<>();

        initialize(elementName, sourceCode);
    }

    /**
     * Creates a new empty {@link CodeRepository}.
     */
    public CodeRepository() {
        this.methodImplementations = new HashMap<>();
    }

    private void initialize(String elementName, String sourceCode) {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();

            builder.addSource(new StringReader(sourceCode));

            JavaClass javaClass = builder.getClassByName(elementName);

            if (javaClass != null) {
                extractMethodBodies(javaClass);
            }
        } catch (Exception ex) { // ParseException is thrown but not declared to be thrown.
            this.methodImplementations.clear();
        }
    }

    private void extractMethodBodies(JavaClass javaClass) {
        for (JavaMethod javaMethod : javaClass.getMethods()) {
            String methodSignature = javaMethod.getDeclarationSignature(true);
            methodSignature = methodSignature.replaceAll("java.lang.", "");

            String methodCode = javaMethod.getCodeBlock();
            methodCode = methodCode.replaceAll("java.lang.", "");

            // the method code includes the method signature and we just want the method body
            String methodBody = methodCode.substring(methodCode.indexOf("{") + 1, methodCode.lastIndexOf("}"));

            // remove symbols which might impact the formatting in the code generation
            methodBody = methodBody.replaceAll(System.lineSeparator(), "");
            methodBody = methodBody.replaceAll("\n", "");
            methodBody = methodBody.replaceAll("\r", "");
            methodBody = methodBody.replaceAll(";", ";" + System.lineSeparator());
            methodBody = methodBody.substring(0, methodBody.lastIndexOf(System.lineSeparator()));
            methodBody = methodBody.trim();
            methodBody = methodBody.replaceAll(FOUR_SPACE_INDENT, "");

            methodImplementations.put(methodSignature, methodBody);
        }
    }

    /**
     * Gets the method body of the method with the given method signature.
     *
     * @param methodSignature the signature of the method.
     * @return the method body or an empty string if it does not exist.
     */
    public String getMethodBody(String methodSignature) {
        if (methodImplementations.containsKey(methodSignature)) {
            return methodImplementations.get(methodSignature);
        }

        return "";
    }
}
