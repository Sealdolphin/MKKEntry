package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import data.modifier.Discount;
import data.wizard.BarcodeModel;
import view.main.panel.wizard.WizardPage;
import view.main.panel.wizard.discount.DiscountPanel;

public class DiscountEditor extends WizardEditor<Discount> {

    public DiscountEditor(Discount data, WizardPage<Discount> view) {
        super(data, view);
    }

    @Override
    protected Discount cacheEditData() {
        return new Discount(data);
    }

    @Override
    protected Discount loadBackEditCache() {
        data.setName(cachedEdit.getName());
        data.setBarcode(cachedEdit.getBarcode());
        data.setDiscount(cachedEdit.getDiscount());
        data.setIconPath(cachedEdit.getIconPath());
        data.setFree(cachedEdit.isFree());
        return data;
    }

    public void setDiscountName(String name) {
        cachedEdit.setName(name);
    }

    public void setDiscountIconPath(String iconPath) {
        cachedEdit.setIconPath(iconPath);
    }

    public void setDiscountIsFree(boolean free) {
        cachedEdit.setFree(free);
    }

    public void setDiscountBarcode(Barcode barcode) {
        cachedEdit.setBarcode(barcode);
    }

    public void setDiscountValue(int discount) {
        cachedEdit.setDiscount(discount);
    }

    public void setBarcodeOptions(BarcodeModel options) {
        if (getWizardPage() instanceof DiscountPanel discountPanel) {
            discountPanel.updateBarcodeOptions(options);
        }
    }
}
