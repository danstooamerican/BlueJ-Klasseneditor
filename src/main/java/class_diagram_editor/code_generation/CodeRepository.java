package class_diagram_editor.code_generation;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CodeRepository {

    private final Map<String, String> methodImplementations;

    public CodeRepository(String elementName, String sourceCode) {
        this.methodImplementations = new HashMap<>();

        initialize(elementName, sourceCode);
    }

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

            String methodBody = methodCode.substring(methodCode.indexOf("{") + 1, methodCode.lastIndexOf("}"));
            methodBody = methodBody.replaceAll(System.lineSeparator(), "");
            methodBody = methodBody.replaceAll("\n", "");
            methodBody = methodBody.replaceAll("\r", "");
            methodBody = methodBody.replaceAll(";", ";" + System.lineSeparator());
            methodBody = methodBody.substring(0, methodBody.lastIndexOf(System.lineSeparator()));
            methodBody = methodBody.trim();
            methodBody = methodBody.replaceAll("    ", "");

            methodImplementations.put(methodSignature, methodBody);
        }
    }

    public String getMethodBody(String methodSignature) {
        if (methodImplementations.containsKey(methodSignature)) {
            return methodImplementations.get(methodSignature);
        }

        return "";
    }
}
