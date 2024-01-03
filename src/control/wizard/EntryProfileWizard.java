package control.wizard;

import data.DataModel;
import data.entryprofile.EntryProfile;
import org.json.JSONException;
import org.json.JSONObject;
import view.helper.DialogCreator;
import view.main.panel.wizard.DialogWizardView;
import view.main.panel.wizard.WizardEditPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class EntryProfileWizard extends AbstractWizard<EntryProfile> {

    private DialogWizardView dialogView;

    public EntryProfileWizard(DataModel<EntryProfile> dataList) {
        super(dataList, new EntryProfile().createWizard());
    }

    @Override
    protected EntryProfile getNewElement() {
        return new EntryProfile();
    }

    private EntryProfile importElementFromSource(JSONObject object) throws JSONException {
        return EntryProfile.parseProfileFromJson(object);
    }

    public void importElement() {
        DialogCreator.openDialogWithAction(view, DialogCreator.getImportFileChooser(), (fileChooser) -> {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    String content = new String(fis.readAllBytes());
                    JSONObject profileObject = new JSONObject(content);
                    addNewElement(importElementFromSource(profileObject));
                } catch (IOException | JSONException exception) {
                    JOptionPane.showMessageDialog(new JFrame(), "Hiba :\nA profil importálása közben hiba történt.\n" +
                            "Az importálás nem sikerült. Részletek:\n" + exception.getLocalizedMessage(),"Hiba",ERROR_MESSAGE);
                }
            }
        });
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
        WizardEditPanel<EntryProfile> editPanel = new WizardEditPanel<>(this, selectionEditor.getWizardPage(), validator);
        dialogView = new DialogWizardView(view, editPanel, "Belépőprofil szerkesztése");
        return dialogView;
    }

    @Override
    public void handleUserAction(ActionEvent event) {
        switch (WizardCommands.valueOf(event.getActionCommand())) {
            case CANCEL -> {
                selectionEditor.updateView();
                dialogView.closeDialog();
            }
            case UPDATE -> {
                if (updateElement()) {
                    dialogView.closeDialog();
                }
            }
            case ADD -> {
                createNewElement();
                dialogView.showDialog();
            }
            case IMPORT -> importElement();
            // FIXME: implement selection
            default -> super.handleUserAction(event);
        }
    }
}
