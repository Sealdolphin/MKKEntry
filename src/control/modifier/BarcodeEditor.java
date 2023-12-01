package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import view.main.panel.wizard.barcode.BarcodePanel;

public class BarcodeEditor extends WizardEditor<Barcode> {

    public BarcodeEditor(Barcode data, BarcodePanel view) {
        super(data, view);
    }

    public void setBarcodeName(String name) {
        if (data != null) {
            data.setName(name);
        }
    }

    public void setBarcodeDescription(String description) {
        data.setDescription(description);
    }

    public void setBarcodeMetadata(String metadata) {
        data.setMetaData(metadata);
    }

    public void setBarcodeImagePath(String imagePath) {
        data.setImagePath(imagePath);
    }
}
