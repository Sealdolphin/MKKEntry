package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import data.modifier.Discount;
import view.main.panel.wizard.WizardPage;

public class DiscountEditor extends WizardEditor<Discount> {

    public DiscountEditor(Discount data, WizardPage<Discount> view) {
        super(data, view);
    }

    public void setDiscountName(String name) {
        data.setName(name);
    }

    public void setDiscountIconPath(String iconPath) {
        data.setIconPath(iconPath);
    }

    public void setDiscountIsFree(boolean free) {
        data.setFree(free);
    }

    public void setDiscountBarcode(Barcode barcode) {
        data.setBarcode(barcode);
    }

    public void setDiscountValue(int discount) {
        data.setDiscount(discount);
    }
}
