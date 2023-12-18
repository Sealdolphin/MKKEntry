package data.wizard;

import data.entryprofile.EntryProfile;
import view.renderer.list.EntryProfileListRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EntryProfileModel extends DefaultWizardModel<EntryProfile> {

    private final EntryProfileListRenderer listRenderer;

    public EntryProfileModel() {
        this(new ArrayList<>());
    }

    public EntryProfileModel(List<EntryProfile> dataList) {
        super(dataList);
        listRenderer = new EntryProfileListRenderer();
    }

    @Override
    public ListCellRenderer<EntryProfile> createListRenderer() {
        return listRenderer;
    }

    @Override
    protected void updateRenderers(EntryProfile type) {

    }
}