package control.modifier;

import control.wizard.WizardEditor;
import data.entryprofile.EntryProfile;
import view.main.panel.wizard.WizardPage;

public class EntryProfileEditor extends WizardEditor<EntryProfile> {



    public EntryProfileEditor(EntryProfile data, WizardPage<EntryProfile> view) {
        super(data, view);
    }

    @Override
    protected EntryProfile cacheEditData() {
//        return new EntryProfile(data);
        return data;
    }

    @Override
    protected EntryProfile loadBackEditCache() {
        // Do nothing
        return data;
    }

    /**
     * TODO: create setters for EntryProfile (including Entryprofile.ProfileSettings)
     */
}
