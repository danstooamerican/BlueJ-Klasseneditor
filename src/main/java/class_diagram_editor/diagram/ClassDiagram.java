package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClassDiagram {

    private final Map<String, ClassModel> classes;
    private final Map<String, InterfaceModel> interfaces;

    public ClassDiagram() {
        this.classes = new HashMap<>();
        this.interfaces = new HashMap<>();
    }

    public ClassModel getClassModel(String id) {
        return classes.get(id);
    }

    public InterfaceModel getInterfaceModel(String id) {
        return interfaces.get(id);
    }

    public String addClass(ClassModel classModel) {
        String uuid = UUID.randomUUID().toString();

        classes.put(uuid, classModel);

        return uuid;
    }

    public String addInterface(InterfaceModel interfaceModel) {
        String uuid = UUID.randomUUID().toString();

        interfaces.put(uuid, interfaceModel);

        return uuid;
    }

    public void addExtendsRelation(String superTypeId, String extendingTypeId) {
        Connectable superType = findElement(superTypeId);
        Connectable extendingType = findElement(extendingTypeId);

        if (superType != null && extendingType != null) {
            extendingType.addExtendsRelation(superType);
        }
    }

    public boolean addAssociationRelation(String startId, String endId, String identifier) {
        Connectable start = findElement(startId);
        Connectable end = findElement(endId);

        if (start != null && end != null) {
            return start.addAssociation(identifier, end);
        }

        return false;
    }

    public<T> T findElement(String id) {

        if (classes.containsKey(id)) {
            return (T) classes.get(id);
        } else if (interfaces.containsKey(id)) {
            return (T) interfaces.get(id);
        }

        return null;
    }

    public void addImplementsRelation(String interfaceId, String classId) {
        if (classes.containsKey(classId) && interfaces.containsKey(interfaceId)) {
            InterfaceModel interfaceModel = interfaces.get(interfaceId);
            ClassModel classModel = classes.get(classId);

            classModel.addInterface(interfaceModel);
        }
    }

    public Iterator<CodeElement> iterator() {
        Collection<CodeElement> codeElements = new ArrayList<>();

        codeElements.addAll(classes.values());
        codeElements.addAll(interfaces.values());

        return new ClassModelIterator(codeElements);
    }

    public void updateRandom() {
        classes.values().stream().findFirst().ifPresent(classModel -> classModel.setName("I was updated"));
    }

    private static class ClassModelIterator implements Iterator<CodeElement> {

        private final List<CodeElement> codeElements;
        private int currentElement;

        public ClassModelIterator(Collection<CodeElement> codeElements) {
            this.codeElements = new ArrayList<>(codeElements);
            this.currentElement = 0;
        }

        @Override
        public boolean hasNext() {
            return currentElement < codeElements.size();
        }

        @Override
        public CodeElement next() {
            int temp = currentElement;

            currentElement++;

            return codeElements.get(temp);
        }
    }
}
