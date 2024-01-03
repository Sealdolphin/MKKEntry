package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import view.main.panel.wizard.barcode.BarcodePanel;

public class BarcodeEditor extends WizardEditor<Barcode> {

    public BarcodeEditor(Barcode data, BarcodePanel view) {
        super(data, view);
    }

    public void setBarcodeName(String name) {
        cachedEdit.setName(name);
    }

    public void setBarcodeDescription(String description) {
        cachedEdit.setDescription(description);
    }

    public void setBarcodeMetadata(String metadata) {
        cachedEdit.setMetaData(metadata);
    }

    public void setBarcodeImagePath(String imagePath) {
        cachedEdit.setImagePath(imagePath);
    }

    @Override
    protected Barcode cacheEditData() {
        return new Barcode(data);
    }

    @Override
    protected Barcode loadBackEditCache() {
        data.setName(cachedEdit.getName());
        data.setDescription(cachedEdit.getDescription());
        data.setMetaData(cachedEdit.getMetaData());
        data.setImagePath(cachedEdit.getImagePath());
        return data;
    }
}
