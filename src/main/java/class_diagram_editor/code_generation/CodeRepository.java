package class_diagram_editor.code_generation;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the body of methods inside a Java class which can be retrieved with the method signature.
 */
public class CodeRepository {

    private static final String FOUR_SPACE_INDENT = " {4}";

    private final Map<String, String> methodImplementations;
    private final Map<String, String> methodComments;

    private String classImports;

    /**
     * Creates a new {@link CodeRepository} for the given java source code of a Java class with the given name.
     * If the source code cannot be parsed the {@link CodeRepository} is empty.
     *
     * @param elementName the name of the Java class.
     * @param sourceCode the source code of the Java class.
     */
    public CodeRepository(String elementName, String sourceCode) {
        this.methodImplementations = new HashMap<>();
        this.methodComments = new HashMap<>();

        initialize(elementName, sourceCode);
    }

    /**
     * Creates a new empty {@link CodeRepository}.
     */
    public CodeRepository() {
        this.methodImplementations = new HashMap<>();
        this.methodComments = new HashMap<>();
    }

    private void initialize(String elementName, String sourceCode) {
        if (sourceCode.isEmpty()) {
            return;
        }

        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();

            builder.addSource(new StringReader(sourceCode));

            JavaClass javaClass = builder.getClassByName(elementName);

            if (javaClass != null) {
                classImports = extractImports(javaClass);

                extractMethodBodies(javaClass);
            }
        } catch (Exception ex) { // ParseException is thrown but not declared to be thrown
            ex.printStackTrace();
            this.methodImplementations.clear();
        }
    }

    private String extractImports(JavaClass javaClass) {
        final StringBuilder stringBuilder = new StringBuilder();

        javaClass.getSource().getImports().forEach(i ->
                stringBuilder.append("import ").append(i).append(";").append(System.lineSeparator()));

        return stringBuilder.toString();
    }

    private void extractMethodBodies(JavaClass javaClass) {
        for (JavaMethod javaMethod : javaClass.getMethods()) {
            String methodSignature = javaMethod.getDeclarationSignature(true);
            methodSignature = methodSignature.replaceAll("java.lang.", "");

            String methodCode = javaMethod.getSourceCode();
            methodCode = methodCode.replaceAll("java.lang.", "");
            methodCode = methodCode.trim();
            methodCode = methodCode.replaceAll(FOUR_SPACE_INDENT, "");

            methodImplementations.put(methodSignature, methodCode);

            methodComments.put(methodSignature, extractMethodComment(javaMethod.getCodeBlock()));
        }

        for (JavaConstructor javaConstructor : javaClass.getConstructors()) {
            String methodSignature = javaConstructor.getCallSignature();

            // getCallSignature() only returns the signature without parameter types so they have to be added again
            for (JavaParameter javaParameter : javaConstructor.getParameters()) {
                methodSignature = methodSignature.replace(javaParameter.getName(), javaParameter.getType() + " " + javaParameter.getName());
            }

            methodSignature = methodSignature.replaceAll("java.lang.", "");

            for (int i = javaConstructor.getModifiers().size() - 1; i >= 0; i--) {
                methodSignature = javaConstructor.getModifiers().get(i) + " " + methodSignature;
            }

            String methodCode = javaConstructor.getSourceCode();
            methodCode = methodCode.replaceAll("java.lang.", "");
            methodCode = methodCode.trim();
            methodCode = methodCode.replaceAll(FOUR_SPACE_INDENT, "");

            methodImplementations.put(methodSignature, methodCode);
            methodComments.put(methodSignature, extractMethodComment(javaConstructor.getCodeBlock()));
        }
    }

    private String extractMethodComment(String codeBlock) {
        final String END_TOKEN = "*/";
        final int startIndex = codeBlock.indexOf("/**");
        final int endIndex = codeBlock.lastIndexOf(END_TOKEN) + END_TOKEN.length();

        if (startIndex >= 0 && endIndex >= 0) {
            return codeBlock.substring(startIndex, endIndex);
        }

        return "";
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

    /**
     * @return all import statements.
     */
    public String getImports() {
        return classImports;
    }

    /**
     * Gets the comment of the method with the given method signature.
     *
     * @param methodSignature he signature of the method.
     * @return the method comment or an empty string if it does not exist.
     */
    public String getMethodComment(String methodSignature) {
        if (methodComments.containsKey(methodSignature)) {
            return methodComments.get(methodSignature);
        }

        return "";
    }
}
