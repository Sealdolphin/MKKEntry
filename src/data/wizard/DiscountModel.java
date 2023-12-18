package data.wizard;

import data.modifier.Discount;
import view.renderer.list.DiscountListRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountModel extends DefaultWizardModel<Discount> {

    private final DiscountListRenderer listRenderer;

    public DiscountModel() {
        this(new ArrayList<>());
    }

    public DiscountModel(List<Discount> dataList) {
        super(dataList);
        listRenderer = new DiscountListRenderer();
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
