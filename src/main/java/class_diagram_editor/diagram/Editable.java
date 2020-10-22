package class_diagram_editor.diagram;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents objects which can be updated and notifies listeners whenever the object is edited.
 */
public abstract class Editable<T> {

    private final Collection<Runnable> callbacks;

    /**
     * Creates a new {@link Editable}.
     */
    public Editable() {
        callbacks = new ArrayList<>();
    }

    /**
     * Notifies all listeners of the {@code hasUpdatedProperty} that the element has changed.
     */
    public void notifyChange() {
        for (Runnable runnable : callbacks) {
            runnable.run();
        }
    }

    /**
     * Registers the callback to be called when {@code edit(T editable)} is called.
     * The same callback can only be added once.
     *
     * @param callback the callback to be called on edit.
     */
    public void registerForUpdates(Runnable callback) {
        Objects.requireNonNull(callback, "callback cannot be null");

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    /**
     * Replaces the associations, extends relations, and methods with
     * the corresponding values of the given {@link T editable}.
     *
     * @param editable the {@link T editable} containing the edit information.
     */
    public final void edit(@NonNull T editable) {
        Objects.requireNonNull(editable, "editable cannot be null");

        performEdit(editable);
        notifyChange();
    }

    /**
     * Method stub of the template pattern which is called in {@code edit(T editable}.
     *
     * @param editable the object containing the edit information.
     */
    protected abstract void performEdit(@NonNull T editable);

}
