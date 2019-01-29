package view;

import control.modifier.Discount;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountRenderer implements TableCellRenderer {

    private List<ImageIcon> iconList = new ArrayList<>();
    private JList<Discount> discountJList;

    private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    DiscountRenderer(List<Discount> discounts){
        for (Discount discount: discounts) {
            iconList.add(new ImageIcon(discount.getIcon()));
        }
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {



        return null;
    }
}
