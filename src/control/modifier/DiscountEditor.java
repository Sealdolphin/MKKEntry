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

    public void setBarcodeOptions(BarcodeModel options) {
        if (getView() instanceof DiscountPanel discountPanel) {
            discountPanel.updateBarcodeOptions(options);
        }
    }
}
