package control.wizard;

import data.DataModel;
import data.entryprofile.EntryProfile;

import javax.swing.event.ListSelectionEvent;

public class EntryProfileWizard extends DefaultDataListWizard<EntryProfile> {


    public EntryProfileWizard(DataModel<EntryProfile> dataList) {
        super(dataList, new EntryProfile().createWizard());
    }

    @Override
    protected EntryProfile getNewElement() {
        return new EntryProfile();
    }

    @Override
    public void selectElement(ListSelectionEvent event) {

    }
}
