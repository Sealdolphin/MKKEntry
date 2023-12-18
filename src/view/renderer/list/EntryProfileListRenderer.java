package view.renderer.list;

import data.entryprofile.EntryProfile;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

import static view.renderer.RenderedIcon.ENTRY_PROFILE;

public class EntryProfileListRenderer extends DefaultWizardTypeListRenderer<EntryProfile> {

    public EntryProfileListRenderer() {
        super(ENTRY_PROFILE);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends EntryProfile> list, EntryProfile profile, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(profile.getProfileName());
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyLocalizedPattern("yyyy. MMMM dd. (E) h:m:s a");
        lbDescription.setText("Létrehozva: " + formatter.format(profile.getCreatedAt()));

        return super.getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
    }
}
