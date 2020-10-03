package class_diagram_editor.presentation.graph_editor;

import de.tesis.dynaware.grapheditor.core.connections.ConnectionCommands;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.model.GraphPackage;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

public class AddConnectionCommand extends CompoundCommand {

    private static final EReference CONNECTIONS = GraphPackage.Literals.GMODEL__CONNECTIONS;

    private static final EReference CONNECTOR_CONNECTIONS = GraphPackage.Literals.GCONNECTOR__CONNECTIONS;

    private static final EAttribute CONNECTION_TYPE = GraphPackage.Literals.GCONNECTION__TYPE;
    private static final EReference SOURCE = GraphPackage.Literals.GCONNECTION__SOURCE;
    private static final EReference TARGET = GraphPackage.Literals.GCONNECTION__TARGET;

    public AddConnectionCommand(final GModel model, final GConnector source, final GConnector target,
                                final String type, final String name) {
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        final GConnection connection = GraphFactory.eINSTANCE.createGConnection();
        connection.setId(name);

        append(AddCommand.create(editingDomain, model, CONNECTIONS, connection));

        if (type != null) {
            append(SetCommand.create(editingDomain, connection, CONNECTION_TYPE, type));
        }

        append(SetCommand.create(editingDomain, connection, SOURCE, source));
        append(SetCommand.create(editingDomain, connection, TARGET, target));
        append(AddCommand.create(editingDomain, source, CONNECTOR_CONNECTIONS, connection));
        append(AddCommand.create(editingDomain, target, CONNECTOR_CONNECTIONS, connection));
    }
}
