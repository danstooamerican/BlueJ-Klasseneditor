package class_diagram_editor.diagram;

import class_diagram_editor.code_generation.CodeElement;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an UML class diagram.
 */
public class ClassDiagram implements Iterable<CodeElement> {

    private static ClassDiagram instance;

    /**
     * @return the current {@link ClassDiagram class diagram} instance.
     */
    public static ClassDiagram getInstance() {
        if (instance == null) {
            instance = new ClassDiagram();
        }

        return instance;
    }

    private final Map<String, ClassModel> classes;
    private final Map<String, InterfaceModel> interfaces;

    private final ListProperty<Editable<?>> allElements;

    /**
     * Creates a new {@link ClassDiagram}.
     * The constructor is marked public because class diagrams can be generated. The Singleton instance
     * represents the single source of truth for the drawn diagram.
     */
    public ClassDiagram() {
        this.classes = new HashMap<>();
        this.interfaces = new HashMap<>();
        this.allElements = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Replaces the current instance with the given {@link ClassDiagram class diagram}.
     *
     * @param classDiagram the new {@link ClassDiagram class diagram}.
     */
    public void replaceWith(ClassDiagram classDiagram) {
        allElements.clear();

        classes.clear();
        classes.putAll(classDiagram.classes);
        allElements.addAll(classDiagram.classes.values());

        for (Map.Entry<String, ClassModel> entry : classDiagram.classes.entrySet()) {
            entry.getValue().registerForUpdates(() -> {
                allElements.set(allElements.indexOf(entry.getValue()), entry.getValue());
            });
        }

        interfaces.clear();
        interfaces.putAll(classDiagram.interfaces);
        allElements.addAll(classDiagram.interfaces.values());

        for (Map.Entry<String, InterfaceModel> entry : classDiagram.interfaces.entrySet()) {
            entry.getValue().registerForUpdates(() -> {
                allElements.set(allElements.indexOf(entry.getValue()), entry.getValue());
            });
        }
    }

    /**
     * Adds the given {@link ClassModel class} to the {@link ClassDiagram class diagram}.
     *
     * @param classModel the {@link ClassModel class} to add.
     * @return the id of the new element or null if the {@link ClassModel class} already exists.
     */
    public String addClass(ClassModel classModel) {
        Connectable element = findElementByName(classModel.getName());

        if (element != null) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();

        classes.put(uuid, classModel);
        allElements.add(classModel);

        classModel.registerForUpdates(() -> {
            allElements.set(allElements.indexOf(classModel), classModel);
        });

        return uuid;
    }

    /**
     * Adds the given {@link InterfaceModel interface} to the {@link ClassDiagram class diagram}.
     *
     * @param interfaceModel the {@link InterfaceModel interface} to add.
     * @return the id of the new element or null if the {@link InterfaceModel interface} already exists.
     */
    public String addInterface(InterfaceModel interfaceModel) {
        Connectable element = findElementByName(interfaceModel.getName());

        if (element != null) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();

        interfaces.put(uuid, interfaceModel);
        allElements.add(interfaceModel);

        interfaceModel.registerForUpdates(() -> {
            allElements.set(allElements.indexOf(interfaceModel), interfaceModel);
        });

        return uuid;
    }

    /**
     * Deletes all elements which belong to the given ids.
     *
     * @param ids the ids of the elements which are removed.
     */
    public void deleteElements(Collection<String> ids) {
        ids.forEach(id -> {
            Connectable removedElement = findElement(id);

            final ClassModel removedClass = classes.remove(id);
            final InterfaceModel removedInterface = interfaces.remove(id);

            if (removedClass != null) {
                allElements.remove(removedClass);
            }

            if (removedInterface != null) {
                allElements.remove(removedInterface);
            }

            classes.values().forEach(classModel -> {
                classModel.removeReferencesTo(removedElement);
            });

            interfaces.values().forEach(interfaceModel -> {
                interfaceModel.removeReferencesTo(removedElement);
            });
        });
    }

    /**
     * Find the element which belongs to the given id.
     *
     * @param id the id of the element.
     * @param <T> the type of the element. Only {@link ClassModel} and {@link InterfaceModel} are supported.
     * @return the element which belongs to the given id or null if it could not be found.
     */
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

    /**
     * Finds the id of the given {@link Connectable element}.
     *
     * @param connectable the element.
     * @return the id of the given element or null if it could not be found.
     */
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

    /**
     * Creates an extends relation from the start node to the end node.
     *
     * @param extendingTypeId the id of the start node.
     * @param superTypeId the id of the end node.
     */
    public void addExtendsRelation(String extendingTypeId, String superTypeId) {
        Connectable superType = findElement(superTypeId);
        Connectable extendingType = findElement(extendingTypeId);

        if (superType != null && extendingType != null) {
            extendingType.addExtendsRelation(superType);
        }
    }

    /**
     * Deletes an extends relation from the start node to the end node.
     *
     * @param startId the id of the start node.
     * @param endId the id of the end node.
     */
    public void deleteExtendsConnection(String startId, String endId) {
        Connectable start = findElement(startId);
        Connectable end = findElement(endId);

        if (start != null && end != null) {
            start.removeExtendsRelation(end);
        }
    }

    /**
     * Creates an implements relation from the start node to the end node.
     * The start node must be a {@link ClassModel} and the end node must be an {@link InterfaceModel}.
     *
     * @param classId the id of the start node.
     * @param interfaceId the id of the end node.
     */
    public void addImplementsRelation(String classId, String interfaceId) {
        if (classes.containsKey(classId) && interfaces.containsKey(interfaceId)) {
            InterfaceModel interfaceModel = interfaces.get(interfaceId);
            ClassModel classModel = classes.get(classId);

            classModel.addInterface(interfaceModel);
        }
    }

    /**
     * Deletes an implements relation from the start node to the end node.
     *
     * @param startId the id of the start node.
     * @param endId the id of the end node.
     */
    public void deleteImplementsConnection(String startId, String endId) {
        ClassModel start = findElement(startId);
        InterfaceModel end = findElement(endId);

        if (start != null && end != null) {
            start.getImplementsInterfaces().remove(end);
        }
    }

    /**
     * Creates an association from the start node to the end node.
     *
     * @param startId the id of the start node.
     * @param endId the id of the end node.
     * @param identifier the identifier given to the association.
     * @return whether the association was added successfully.
     */
    public boolean addAssociationRelation(String startId, String endId, String identifier) {
        Connectable start = findElement(startId);
        Connectable end = findElement(endId);

        if (start != null && end != null) {
            return start.addAssociation(identifier, end);
        }

        return false;
    }

    /**
     * Deletes an outgoing association with the given identifier from the {@link Connectable element} with the given id.
     *
     * @param startId the id of the element where the association starts.
     * @param identifier the identifier of the association.
     */
    public void deleteAssociationConnection(String startId, String identifier) {
        Connectable start = findElement(startId);

        if (start != null && identifier != null) {
            start.removeAssociation(identifier);
        }
    }

    /**
     * Edits the {@link ClassModel class} with the given id.
     * If the id is not found nothing happens.
     *
     * @param id the id of the {@link ClassModel class}.
     * @param classModel the {@link ClassModel class} which is used to update the corresponding
     *                           {@link ClassModel class} in the class diagram.
     */
    public void edit(String id, ClassModel classModel) {
        ClassModel toEdit = classes.get(id);

        if (toEdit != null) {
            toEdit.edit(classModel);
        }
    }

    /**
     * Edits the {@link InterfaceModel interface} with the given id.
     * If the id is not found nothing happens.
     *
     * @param id the id of the {@link InterfaceModel interface}.
     * @param interfaceModel the {@link InterfaceModel interface} which is used to update the corresponding
 *                           {@link InterfaceModel interface} in the class diagram.
     */
    public void edit(String id, InterfaceModel interfaceModel) {
        InterfaceModel toEdit = interfaces.get(id);

        if (toEdit != null) {
            toEdit.edit(interfaceModel);
        }
    }

    /**
     * Finds the {@link ClassModel class} which belongs to the id.
     *
     * @param id the id of the class.
     * @return the {@link ClassModel class} which belongs to the given id or null if it could not be found.
     */
    public ClassModel getClassModel(String id) {
        return classes.get(id);
    }

    /**
     * Finds the {@link InterfaceModel interface} which belongs to the id.
     *
     * @param id the id of the class.
     * @return the {@link InterfaceModel interface} which belongs to the given id or null if it could not be found.
     */
    public InterfaceModel getInterfaceModel(String id) {
        return interfaces.get(id);
    }

    /**
     * @return all added {@link InterfaceModel interfaces}.
     */
    public Collection<InterfaceModel> getInterfaces() {
        return new ArrayList<>(interfaces.values());
    }

    /**
     * @return all added {@link ClassModel classes}.
     */
    public Collection<ClassModel> getClasses() {
        return new ArrayList<>(classes.values());
    }

    /**
     * @return a list of all elements in currently added to this {@link ClassDiagram class diagram}.
     */
    public ListProperty<Editable<?>> allElementsProperty() {
        return allElements;
    }

    @Override
    public Iterator<CodeElement> iterator() {
        Collection<CodeElement> codeElements = new ArrayList<>();

        codeElements.addAll(classes.values());
        codeElements.addAll(interfaces.values());

        return new ClassModelIterator(codeElements);
    }

    /**
     * Looks if attributes in added {@link ClassModel classes} can be represented by associations and
     * creates the corresponding changes. This does not trigger a diagram update.
     *
     * @return a list of all element ids which were affected.
     */
    public Collection<String> extractAttributesToAssociations() {
        final Map<String, Connectable> diagramElements = buildElementLookup();
        final Collection<String> affectedElements = new ArrayList<>();

        for (ClassModel classModel : classes.values()) {
            final String id = extract(classModel, diagramElements);

            if (id != null) {
                affectedElements.add(id);
            }
        }

        return affectedElements;
    }

    private Map<String, Connectable> buildElementLookup() {
        final Map<String, Connectable> diagramElements = new HashMap<>();

        for (ClassModel classModel : classes.values()) {
            diagramElements.put(classModel.getName(), classModel);
        }

        for (InterfaceModel interfaceModel : interfaces.values()) {
            diagramElements.put(interfaceModel.getName(), interfaceModel);
        }

        return diagramElements;
    }

    private String extract(ClassModel classModel, Map<String, Connectable> diagramElements) {
        Collection<AttributeModel> attributeModelsToRemove = new ArrayList<>();

        for (AttributeModel attributeModel : classModel.getAttributes()) {
            if (diagramElements.containsKey(attributeModel.getType())) {
                attributeModelsToRemove.add(attributeModel);

                classModel.addAssociation(attributeModel.getName(), diagramElements.get(attributeModel.getType()));
            }
        }

        for (AttributeModel toRemove : attributeModelsToRemove) {
            classModel.removeAttribute(toRemove);
        }

        String id = null;
        if (!attributeModelsToRemove.isEmpty()) {
            id = getIdOf(classModel);
            classModel.notifyChange();
        }

        return id;
    }

    /**
     * Creates a new {@link ClassDiagram class diagram} which only contains the elements which belong to the given ids.
     *
     * @param ids the ids of the included elements.
     * @return a new {@link ClassDiagram class diagram} which
     *         only includes a subset of this {@link ClassDiagram class diagram}
     */
    public ClassDiagram getSubDiagram(Collection<String> ids) {
        final ClassDiagram classDiagram = new ClassDiagram();

        for (String id : ids) {
            final Connectable connectable = findElement(id);

            if (connectable instanceof ClassModel) {
                classDiagram.addClass((ClassModel) connectable);
            } else if (connectable instanceof InterfaceModel) {
                classDiagram.addInterface((InterfaceModel) connectable);
            }
        }

        return classDiagram;
    }

    /**
     * Iterator which just iterates over all given {@link CodeElement code elements}.
     */
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
