package view.renderer.list;

import data.entryprofile.EntryProfile;

import javax.swing.*;
import java.awt.*;

import static view.renderer.RenderedIcon.ENTRY_PROFILE;

public class EntryProfileListRenderer extends DefaultWizardTypeListRenderer<EntryProfile> {

    public EntryProfileListRenderer() {
        super(ENTRY_PROFILE);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends EntryProfile> list, EntryProfile profile, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(profile.getProfileName());
        lbDescription.setText(profile.getId());

        return super.getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
    }
}
