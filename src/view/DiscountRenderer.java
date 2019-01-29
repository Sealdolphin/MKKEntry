package view;

import control.modifier.Discount;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class DiscountRenderer extends JPanel implements TableCellRenderer {

    private final int iconSize;

    public DiscountRenderer(int size) {
        iconSize = size;
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List discounts = (List) value;
        removeAll();
        for (Object disObj: discounts) {
            Discount discount = (Discount) disObj;
            add(new DiscountIcon(discount.getName(),discount.getIcon(),isSelected));
            add(Box.createRigidArea(new Dimension(10,0)));
        }
        //Rendering selection
        if(isSelected){
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else setBackground(UIManager.getColor("Table.background"));
        return this;
    }

    private class DiscountIcon extends JLabel implements ListCellRenderer<JLabel>{

        DiscountIcon(String label, String iconPath,boolean isSelected){
            //Creating JLabel with accurate icon
            super(label,new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(iconSize,iconSize,Image.SCALE_SMOOTH)),JLabel.CENTER);
            setOpaque(true);
            //Aligning text and icon
            setVerticalAlignment(CENTER);
            setHorizontalAlignment(CENTER);
            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);
            //Rendering Selection color
            if(isSelected)
                setBackground(UIManager.getColor("Table.selectionBackground"));
            else setBackground(UIManager.getColor("Table.background"));
            //Set font
            setFont(new Font("Arial",Font.PLAIN,8));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends JLabel> list, JLabel value, int index, boolean isSelected, boolean cellHasFocus) {
            //Set text
            setText(value.getText());
            //Set Icon
            setIcon(value.getIcon());
            return this;
        }
    }
}
