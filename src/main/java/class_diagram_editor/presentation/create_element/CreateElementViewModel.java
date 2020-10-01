package class_diagram_editor.presentation.create_element;

import de.saxsys.mvvmfx.ViewModel;

public class CreateElementViewModel implements ViewModel {

    private final CreateElementModel createElementModel;

    public CreateElementViewModel(CreateElementModel createElementModel) {
        this.createElementModel = createElementModel;
    }

    public boolean isEditMode() {
        return createElementModel.isEditMode();
    }
}
