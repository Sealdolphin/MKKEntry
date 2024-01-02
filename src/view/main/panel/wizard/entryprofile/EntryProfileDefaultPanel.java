package view.main.panel.wizard.entryprofile;

import data.entryprofile.EntryProfile;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Stream;

public class EntryProfileDefaultPanel extends AbstractPanel implements EntryProfilePagePart {

    private final LabeledComponent<JTextField> compTitle;
    private final LabeledComponent<JTextField> compProfileMask;
    private final LabeledComponent<JTextField> compEditMask;

    private final JCheckBox checkEnableEditMask;

    public EntryProfileDefaultPanel() {
        //TODO: use ui.json!!!!!!!!!!!!!
        compTitle = new LabeledComponent<>("Profil neve:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compProfileMask = new LabeledComponent<>("Beléptetési maszk:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        //TODO: confirm that this feature is needed...
        compEditMask = new LabeledComponent<>("Új belépést generáló maszk:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));

        checkEnableEditMask = new JCheckBox("Új ID kiosztása belépéskor");
        checkEnableEditMask.addActionListener(this::toggleEditMask);
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(compTitle.createSequentialLayout(layout))
                        .addGroup(compProfileMask.createSequentialLayout(layout))
                        .addGroup(compEditMask.createSequentialLayout(layout))
                        .addComponent(checkEnableEditMask)
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(compTitle.createParallelLayout(layout))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(compProfileMask.createParallelLayout(layout))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(compEditMask.createParallelLayout(layout))
                        .addComponent(checkEnableEditMask)
        );

        layout.linkSize(Stream.of(compTitle, compProfileMask, compEditMask).map(LabeledComponent::getComponent).toArray(JComponent[]::new));
    }

    private void toggleEditMask(ActionEvent event) {
        compEditMask.getComponent().setEnabled(checkEnableEditMask.isSelected());
    }

    @Override
    public void updateView(EntryProfile model) {
        compTitle.getComponent().setText(model.getProfileName());
        compProfileMask.getComponent().setText(model.getProfileMask());
        compEditMask.getComponent().setText(model.getProfileMaskForEntry());
        checkEnableEditMask.setSelected(model.getProfileSettings().isNewIdGeneratedUponEntry());
        toggleEditMask(new ActionEvent(this, 0, ""));
    }
}
