package control.wizard;

import data.DataModel;
import data.entryprofile.EntryProfile;
import view.main.panel.wizard.entryprofile.EntryProfileMainPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EntryProfileWizard extends AbstractWizard<EntryProfile> {

    public EntryProfileWizard(DataModel<EntryProfile> dataList) {
        super(dataList, new EntryProfile().createWizard());
        view.setListDoubleClickListener(new EditDialogCreator());
    }

    private void openEditDialog() {
        JDialog editDialog = new JDialog();
        JTabbedPane panel = createEditPanel(selectionEditor.data);
        editDialog.setTitle("Belépőprofil szerkesztése");
        editDialog.setMinimumSize(new Dimension(640, 300));
        editDialog.add(panel);
        editDialog.pack();
        editDialog.setLocationRelativeTo(null);
        editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        editDialog.setVisible(true);
    }

    private JTabbedPane createEditPanel(EntryProfile selection) {
        BarcodeWizard barcodeWizard = new BarcodeWizard(selection.createBarcodeModel());
        DiscountWizard discountWizard = new DiscountWizard(selection.createDiscountModel());
        TicketTypeWizard ticketTypeWizard = new TicketTypeWizard(selection.createTicketTypeModel());
        EntryProfileMainPanel settingsPanel = new EntryProfileMainPanel();

        discountWizard.updateBarcodeOptions(selection.createBarcodeModel());
        settingsPanel.initializeLayout();
        barcodeWizard.addListUpdateListener(discountWizard);

        JTabbedPane cardPanel = new JTabbedPane();
        cardPanel.addTab("Beállítások", settingsPanel);
        cardPanel.addTab("Vonalkódok", barcodeWizard.getView());
        cardPanel.addTab("Kedvezmények", discountWizard.getView());
        cardPanel.addTab("Jegytípusok", ticketTypeWizard.getView());

        return cardPanel;
    }

    @Override
    protected EntryProfile getNewElement() {
        return new EntryProfile();
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof EntryProfile selection) {
                setSelection(selection);
            }
            if (list.getSelectedValue() == null) {
                setSelection(null);
            }
        }
    }

    @Override
    public JPanel getView() {
        return view.createListPanel();
    }

    private class EditDialogCreator extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList<?>) {
                if (mouseEvent.getClickCount() == 2) {
                    openEditDialog();
                }
            }
        }
    }
}
