package view.main.panel.wizard.entryprofile;

import view.main.panel.wizard.WizardEditPanel;

import javax.swing.*;

public class EntryProfileCardPanel extends JTabbedPane {

    // FIXME: EntryProfilePanel needs models!!!!
    private EntryProfileMainPanel settingsTab = new EntryProfileMainPanel();

    public EntryProfileCardPanel(JPanel barcodeView, JPanel ticketTypeView, JPanel discountView) {
        settingsTab.initializeLayout();

        addTab("Beállítások", settingsTab);
        addTab("Vonalkódok", barcodeView);
        addTab("Kedvezmények", discountView);
        addTab("Jegytípusok", ticketTypeView);
    }

    public void addTab(String title, WizardEditPanel<?> editPanel) {
        addTab(title, editPanel);
    }

}
