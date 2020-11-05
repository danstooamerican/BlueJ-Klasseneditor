package class_diagram_editor.code_generation;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.diagram.Visibility;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import org.w3c.dom.Attr;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassDiagramGenerator {
    // key: element name, value: the parsed Java code
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
        final ClassDiagram classDiagram = new ClassDiagram();

        final Map<String, String> generatedElements = generateElements(classDiagram);

        addConnections(classDiagram, generatedElements);

        classDiagram.extractAttributesToAssociations();

        return classDiagram;
    }

    private Map<String, String> generateElements(ClassDiagram classDiagram) {
        final Map<String, String> generatedElementIds = new HashMap<>();

        for (String elementName : elementSourceCode.keySet()) {
            final JavaClass javaClass = elementSourceCode.get(elementName);

            String id;
            if (javaClass.isInterface()) {
                id = classDiagram.addInterface(generateInterface(elementName, javaClass));
            } else {
                id = classDiagram.addClass(generateClass(elementName, javaClass));
            }

            generatedElementIds.put(elementName, id);
        }

        return generatedElementIds;
    }

    private ClassModel generateClass(String elementName, JavaClass javaClass) {
        final ClassModel classModel = new ClassModel();

        classModel.setName(elementName);
        classModel.setLastGeneratedName(elementName);
        classModel.setAbstract(javaClass.isAbstract());

        classModel.setMethods(generateMethods(javaClass.getMethods(), javaClass.getConstructors()));
        classModel.setAttributes(generateAttributes(javaClass.getFields()));

        final Collection<MethodModel> methodsToRemove = new ArrayList<>();
        for (MethodModel methodModel : classModel.getMethods()) {
            if (methodModel.getName().startsWith("get")) {
                final String attributeName = methodModel.getName().replace("get", "").toUpperCase();

                for (AttributeModel attributeModel : classModel.getAttributes()) {
                    if (attributeModel.getName().toUpperCase().equals(attributeName)) {
                        attributeModel.setHasGetter(true);
                        methodsToRemove.add(methodModel);
                        break;
                    }
                }
            } else if (methodModel.getName().startsWith("set")) {
                final String attributeName = methodModel.getName().replace("set", "").toUpperCase();

                for (AttributeModel attributeModel : classModel.getAttributes()) {
                    if (attributeModel.getName().toUpperCase().equals(attributeName)) {
                        attributeModel.setHasSetter(true);
                        methodsToRemove.add(methodModel);
                        break;
                    }
                }
            }
        }

        for (MethodModel toRemove : methodsToRemove) {
            classModel.removeMethod(toRemove);
        }

        return classModel;
    }

    private InterfaceModel generateInterface(String elementName, JavaClass javaClass) {
        final InterfaceModel interfaceModel = new InterfaceModel();

        interfaceModel.setName(elementName);
        interfaceModel.setLastGeneratedName(elementName);

        interfaceModel.setMethods(generateMethods(javaClass.getMethods(), javaClass.getConstructors()));

        return interfaceModel;
    }

    private void addConnections(ClassDiagram classDiagram, Map<String, String> generatedElementIds) {
        for (String elementName : elementSourceCode.keySet()) {
            final JavaClass javaClass = elementSourceCode.get(elementName);

            if (!javaClass.isInterface()) {
                final JavaClass superClass = javaClass.getSuperJavaClass();

                if (superClass != null) {
                    classDiagram.addExtendsRelation(generatedElementIds.get(elementName), generatedElementIds.get(superClass.getName()));
                }

                ClassModel classModel = classDiagram.findElement(generatedElementIds.get(elementName));

                if (classModel != null) {
                    classModel.setImplementsInterfaces(getImplements(javaClass.getImplements(), classDiagram, generatedElementIds));
                }
            }
        }
    }

    private List<MethodModel> generateMethods(Collection<JavaMethod> javaMethods, Collection<JavaConstructor> javaConstructors) {
        final List<MethodModel> methodModels = new ArrayList<>();

        for (JavaMethod javaMethod : javaMethods) {
            if (isOverride(javaMethod)) {
                continue;
            }

            final MethodModel methodModel = new MethodModel();

            methodModel.setName(javaMethod.getName());
            methodModel.setVisibility(Visibility.PACKAGE_PRIVATE);

            final JavaClass returnType = javaMethod.getReturns();
            if (returnType != null) {
                methodModel.setReturnType(returnType.getName());
            }

            for (String modifier : javaMethod.getModifiers()) {
                switch (modifier) {
                    case "public":
                        methodModel.setVisibility(Visibility.PUBLIC);
                        break;
                    case "private":
                        methodModel.setVisibility(Visibility.PRIVATE);
                        break;
                    case "protected":
                        methodModel.setVisibility(Visibility.PROTECTED);
                        break;
                    case "abstract":
                        methodModel.setAbstract(true);
                        break;
                    case "static":
                        methodModel.setStatic(true);
                        break;
                }
            }

            methodModel.setParameters(generateParameters(javaMethod.getParameters()));

            methodModels.add(methodModel);
        }

        for (JavaConstructor javaConstructor : javaConstructors) {
            final MethodModel methodModel = new MethodModel();

            methodModel.setConstructor(true);
            methodModel.setName(javaConstructor.getName());
            methodModel.setVisibility(Visibility.PACKAGE_PRIVATE);

            for (String modifier : javaConstructor.getModifiers()) {
                switch (modifier) {
                    case "public":
                        methodModel.setVisibility(Visibility.PUBLIC);
                        break;
                    case "private":
                        methodModel.setVisibility(Visibility.PRIVATE);
                        break;
                    case "protected":
                        methodModel.setVisibility(Visibility.PROTECTED);
                        break;
                }
            }

            methodModel.setParameters(generateParameters(javaConstructor.getParameters()));

            methodModels.add(methodModel);
        }

        return methodModels;
    }

    private List<AttributeModel> generateParameters(List<JavaParameter> parameters) {
        final List<AttributeModel> attributeModels = new ArrayList<>();

        for (JavaParameter javaParameter : parameters) {
            final AttributeModel attributeModel = new AttributeModel();

            // Visibility must be set or the edit method tab crashes
            attributeModel.setVisibility(Visibility.PUBLIC);

            attributeModel.setName(javaParameter.getName());
            attributeModel.setType(javaParameter.getType().getValue());

            attributeModels.add(attributeModel);
        }

        return attributeModels;
    }

    private boolean isOverride(JavaMethod javaMethod) {
        for (JavaAnnotation javaAnnotation : javaMethod.getAnnotations()) {
            if (javaAnnotation.getType().getName().equals("Override")) {
                return true;
            }
        }

        return false;
    }

    private List<AttributeModel> generateAttributes(List<JavaField> javaFields) {
        final List<AttributeModel> attributeModels = new ArrayList<>();

        for (JavaField javaField : javaFields) {
            final AttributeModel attributeModel = new AttributeModel();

            attributeModel.setName(javaField.getName());
            attributeModel.setType(javaField.getType().getName());
            attributeModel.setVisibility(Visibility.PACKAGE_PRIVATE);

            for (String modifier : javaField.getModifiers()) {
                switch (modifier) {
                    case "public":
                        attributeModel.setVisibility(Visibility.PUBLIC);
                        break;
                    case "private":
                        attributeModel.setVisibility(Visibility.PRIVATE);
                        break;
                    case "protected":
                        attributeModel.setVisibility(Visibility.PROTECTED);
                        break;
                    case "final":
                        attributeModel.setFinal(true);
                        break;
                    case "static":
                        attributeModel.setStatic(true);
                        break;
                }
            }

            attributeModels.add(attributeModel);
        }

        return attributeModels;
    }

    private Set<InterfaceModel> getImplements(List<JavaType> javaTypes, ClassDiagram classDiagram, Map<String, String> generatedElementIds) {
        final Set<InterfaceModel> implementsLists = new HashSet<>();

        for (JavaType javaType : javaTypes) {
            final String id = generatedElementIds.get(javaType.getValue());

            final InterfaceModel interfaceModel = classDiagram.findElement(id);
            implementsLists.add(interfaceModel);
        }

        return implementsLists;
    }
}
