package class_diagram_editor.presentation.create_element;

import class_diagram_editor.diagram.AttributeModel;
import class_diagram_editor.diagram.ClassDiagram;
import class_diagram_editor.diagram.ClassModel;
import class_diagram_editor.diagram.Connectable;
import class_diagram_editor.diagram.InterfaceModel;
import class_diagram_editor.diagram.MethodModel;
import class_diagram_editor.presentation.graph_editor.DiagramElementService;
import class_diagram_editor.presentation.graph_editor.GraphController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Extends the {@link DiagramElementService} to also allow editing of existing elements.
 */
public class CreateElementService extends DiagramElementService {
    private final Connectable editedElement;

    private final boolean isClass;

    private final String id;
    private final String name;
    private ClassModel extendsElement;
    private boolean isAbstract;
    private Collection<InterfaceModel> implementedInterfaces;
    private final Map<String, Connectable> associations;
    private List<AttributeModel> attributes;
    private List<MethodModel> methods;

    /**
     * Creates the base of the {@link CreateElementService} and must be called inside every constructor.
     *
     * @param id the id of the edited element or null.
     * @param connectable the edited element or null.
     * @param isClass whether the edited element is a {@link ClassModel}. False is interpreted as {@link InterfaceModel}.
     */
    private CreateElementService(String id, Connectable connectable, boolean isClass) {
        super();

        this.editedElement = connectable;

        if (connectable != null) {
            this.name = connectable.getName();
            this.associations = connectable.getAssociations();
        } else {
            this.name = "";
            this.associations = new HashMap<>();
        }

        this.id = id;
        this.extendsElement = null;
        this.implementedInterfaces = new ArrayList<>();

        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();

        this.isAbstract = false;
        this.isClass = isClass;
    }

    /**
     * Creates a basic {@link CreateElementService} without any prefilled values.
     */
    public CreateElementService() {
        this(null, null, true);
    }

    /**
     * Creates a new {@link CreateElementService} to edit a {@link ClassModel class}.
     *
     * @param id the id of the {@link ClassModel class} in the {@link ClassDiagram class diagram}.
     * @param classModel the {@link ClassModel class} to edit.
     */
    public CreateElementService(String id, ClassModel classModel) {
        this(id, classModel, true);

        this.implementedInterfaces = classModel.getImplementsInterfaces();
        this.isAbstract = classModel.isAbstract();
        this.extendsElement = classModel.getExtendsClass();
        this.attributes = classModel.getAttributes();
        this.methods = classModel.getMethods();
    }

    /**
     * Creates a new {@link CreateElementService} to edit an {@link InterfaceModel interface}.
     *
     * @param id the id of the {@link InterfaceModel interface} in the {@link ClassDiagram class diagram}.
     * @param interfaceModel the {@link InterfaceModel interface} to edit.
     */
    public CreateElementService(String id, InterfaceModel interfaceModel) {
        this(id, interfaceModel, false);

        this.implementedInterfaces = interfaceModel.getExtendsRelations()
                .stream()
                .map(connectable -> (InterfaceModel) connectable)
                .collect(Collectors.toList());

        this.methods = interfaceModel.getMethods();
    }

    /**
     * Edits the given {@link ClassModel class} in the model and updates the diagram.
     *
     * @param classModel a {@link ClassModel class} who's values are used to update the edited {@link ClassModel class}.
     */
    public void editClass(ClassModel classModel) {
        graphController.clearConnections(id);
        addClassConnections(classModel, id);

        classDiagram.edit(id, classModel);
    }

    /**
     * Edits the given {@link InterfaceModel interface} in the model and updates the diagram.
     *
     * @param interfaceModel a {@link InterfaceModel interface} who's
     *                   values are used to update the edited {@link InterfaceModel interface}.
     */
    public void editInterface(InterfaceModel interfaceModel) {
        graphController.clearConnections(id);
        addInterfaceConnections(interfaceModel, id);

        classDiagram.edit(id, interfaceModel);
    }

    /**
     * @return the currently edited element.
     */
    public Connectable getEditedElement() {
        return editedElement;
    }

    /**
     * @return whether the {@link CreateElementService} was initialized with an element to edit.
     */
    public boolean isEditMode() {
        return editedElement != null;
    }

    /**
     * @return whether the edited element is a {@link ClassModel}.
     */
    public boolean isClass() {
        return isClass;
    }

    /**
     * @return the name of the edited element.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the extends element if the edited element is a {@link ClassModel} or null if not.
     */
    public ClassModel getExtendsElement() {
        return extendsElement;
    }

    /**
     * @return whether the edited element is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * @return the implemented {@link InterfaceModel interfaces} of the edited element.
     */
    public Collection<InterfaceModel> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     * @return the associations of the edited element.
     */
    public Map<String, Connectable> getAssociations() {
        return associations;
    }

    /**
     * @return the {@link AttributeModel attributes} of the edited element.
     */
    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    /**
     * @return the {@link MethodModel methods} of the edited element.
     */
    public List<MethodModel> getMethods() {
        return methods;
    }
}
