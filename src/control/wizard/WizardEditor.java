package control.wizard;

import data.wizard.WizardType;
import view.main.panel.wizard.WizardPage;

public abstract class WizardEditor<T extends WizardType> {

    protected T data;
    protected T cachedEdit;
    private final WizardPage<T> view;

    public WizardEditor(T data, WizardPage<T> view) {
        this.data = data;
        this.view = view;
        if (data != null) {
            this.cachedEdit = cacheEditData();
        }
    }

    public final WizardPage<T> getWizardPage() {
        return view;
    }

    public void updateView() {
        view.getWizardEditPanel().setVisible(data != null);
        if (cachedEdit != null) {
            view.refreshPage(cachedEdit);
        }
    }

    public void updateSelection(T data) {
        this.data = data;
        if (data != null) {
            this.cachedEdit = cacheEditData();
        }
        updateView();
    }

    public T doSaveData() {
        view.saveData(this);
        return cachedEdit;
    }

    protected abstract T cacheEditData();

    protected abstract T loadBackEditCache();
}
