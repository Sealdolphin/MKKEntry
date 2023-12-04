package data.wizard;

import data.modifier.Discount;
import view.renderer.list.DiscountListRenderer;

import javax.swing.*;
import java.util.List;

public class DiscountModel extends DefaultWizardModel<Discount> {

    private final DiscountListRenderer listRenderer;

    public DiscountModel(List<Discount> dataList) {
        super(dataList);
        listRenderer = new DiscountListRenderer();
    }

    @Override
    public Discount getElementById(String id) {
        return dataList.stream()
                .filter(discount -> discount.getMeta().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ListCellRenderer<Discount> createListRenderer() {
        return listRenderer;
    }

    @Override
    protected void updateRenderers(Discount discount) {
        listRenderer.updateRenderer(discount);
    }
}
