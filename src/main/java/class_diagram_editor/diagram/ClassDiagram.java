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

    private static ClassDiagram instance;

    public static ClassDiagram getInstance() {
        if (instance == null) {
            instance = new ClassDiagram();
        }

        return instance;
    }

    private final Map<String, ClassModel> classes;
    private final Map<String, InterfaceModel> interfaces;

    private ClassDiagram() {
        this.classes = new HashMap<>();
        this.interfaces = new HashMap<>();
    }

    public ClassModel getClassModel(String id) {
        return classes.get(id);
    }

    public InterfaceModel getInterfaceModel(String id) {
        return interfaces.get(id);
    }

    public Collection<InterfaceModel> getInterfaces() {
        return new ArrayList<>(interfaces.values());
    }

    public Collection<ClassModel> getClasses() {
        return new ArrayList<>(classes.values());
    }

    public String addClass(ClassModel classModel) {
        Connectable element = findElementByName(classModel.getName());

        if (element != null) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();

        classes.put(uuid, classModel);

        return uuid;
    }

    public String addInterface(InterfaceModel interfaceModel) {
        Connectable element = findElementByName(interfaceModel.getName());

        if (element != null) {
            return null;
        }

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

    public void deleteElements(Collection<String> ids) {
        ids.forEach(id -> {
            Connectable removedElement = findElement(id);

            classes.remove(id);
            interfaces.remove(id);

            classes.values().forEach(classModel -> {
                classModel.removeReferencesTo(removedElement);
            });

            interfaces.values().forEach(interfaceModel -> {
                interfaceModel.removeReferencesTo(removedElement);
            });
        });
    }

    public<T> T findElement(String id) {
        if (classes.containsKey(id)) {
            return (T) classes.get(id);
        } else if (interfaces.containsKey(id)) {
            return (T) interfaces.get(id);
        }

        return null;
    }

    private<T> T findElementByName(String name) {
        for (ClassModel classModel : classes.values()) {
            if (classModel.getName().equals(name)) {
                return (T) classModel;
            }
        }

        for (InterfaceModel interfaceModel : interfaces.values()) {
            if (interfaceModel.getName().equals(name)) {
                return (T) interfaceModel;
            }
        }

        return null;
    }

    public String getIdOf(Connectable connectable) {
        for (Map.Entry<String, ClassModel> classEntry : classes.entrySet()) {
            if (classEntry.getValue().getName().equals(connectable.getName())) {
                return classEntry.getKey();
            }
        }

        for (Map.Entry<String, InterfaceModel> interfaceEntry : interfaces.entrySet()) {
            if (interfaceEntry.getValue().getName().equals(connectable.getName())) {
                return interfaceEntry.getKey();
            }
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

    public void deleteExtendsConnection(String startId, String endId) {
        Connectable start = findElement(startId);
        Connectable end = findElement(endId);

        if (start != null && end != null) {
            start.removeExtendsRelation(end);
        }
    }

    public void deleteImplementsConnection(String startId, String endId) {
        ClassModel start = findElement(startId);
        InterfaceModel end = findElement(endId);

        if (start != null && end != null) {
            start.getImplementsInterfaces().remove(end);
        }
    }

    public void deleteAssociationConnection(String startId, String identifier) {
        Connectable start = findElement(startId);

        if (start != null && identifier != null) {
            start.removeAssociation(identifier);
        }
    }

    public void edit(String id, ClassModel classModel) {
        ClassModel toEdit = classes.get(id);

        if (toEdit != null) {
            toEdit.edit(classModel);
        }
    }

    public void edit(String id, InterfaceModel interfaceModel) {
        InterfaceModel toEdit = interfaces.get(id);

        if (toEdit != null) {
            toEdit.edit(interfaceModel);
        }
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
