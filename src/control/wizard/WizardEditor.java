package control.wizard;

import data.wizard.WizardType;
import view.main.panel.wizard.WizardPage;

public abstract class WizardEditor<T extends WizardType> {

    protected T data;
    private final WizardPage<T> view;

    public WizardEditor(T data, WizardPage<T> view) {
        this.data = data;
        this.view = view;
    }

    public final WizardPage<T> getView() {
        return view;
    }

    public void updateView() {
        view.getWizardEditPanel().setVisible(data != null);
        if (data != null) {
            view.refreshPage(data);
        }
    }

    public void updateSelection(T data) {
        this.data = data;
        updateView();
    }

    public T getSavedData() {
        view.saveData(this);
        return data;
    }
}
