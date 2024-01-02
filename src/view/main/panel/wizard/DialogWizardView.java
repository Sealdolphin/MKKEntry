package view.main.panel.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialogWizardView extends JPanel {
    private final JDialog editDialog;

    public DialogWizardView(DataListView<?> view, WizardEditPanel<?> wizardEditPanel, String title) {
        setLayout(new BorderLayout());
        add(view.createListPanel(true), BorderLayout.CENTER);

        this.editDialog = new JDialog();
        createEditDialog(wizardEditPanel, title);

        view.setListDoubleClickListener(new EditDialogCreator());
    }

    private void createEditDialog(WizardEditPanel<?> wizardEditPanel, String title) {
        editDialog.setTitle(title);
        editDialog.setMinimumSize(new Dimension(640, 300));
        editDialog.add(wizardEditPanel);
        editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }

    public void showDialog() {
        editDialog.pack();
        editDialog.setLocationRelativeTo(null);
        editDialog.setVisible(true);
    }

    public void closeDialog() {
        editDialog.dispose();
    }

    private class EditDialogCreator extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList<?>) {
                if (mouseEvent.getClickCount() == 2) {
                    showDialog();
                }
            }
        }
    }
}
