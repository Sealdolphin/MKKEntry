package view;

import control.modifier.Discount;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * A renderer class for displaying the Discounts on a record
 */
public class DiscountRenderer extends JPanel implements TableCellRenderer {

    /**
     * The size of the discount icon in pixels
     */
    private final int iconSize;

    /**
     * Default constructor
     * Loads settings and icon size
     * @param size the icon's size
     */
    public DiscountRenderer(int size) {
        iconSize = size;
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        setOpaque(true);
    }

    /**
     * Inherited from TableCellRenderer
     * Returns the JPanel containing the discount labels
     * @param table the table calling the event
     * @param value the value of the cell: the discount list
     * @param isSelected true if the rendered cell is selected
     * @param hasFocus true if the rendered cell has focus
     * @param row the row of the rendered cell
     * @param column the column of the rendered cell
     * @return a JPanel containing the Discount labels
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List discounts = (List) value;
        removeAll();
        for (Object disObj: discounts) {
            Discount discount = (Discount) disObj;
            add(new DiscountLabel(discount.getName(),discount.getIcon(),isSelected));
            add(Box.createRigidArea(new Dimension(10,0)));
        }
        //Rendering selection
        if(isSelected){
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else setBackground(UIManager.getColor("Table.background"));
        return this;
    }

    /**
     * A custom label for displaying discounts
     */
    private class DiscountLabel extends JLabel {

        DiscountLabel(String label, String iconPath, boolean isSelected){
            //Creating JLabel with accurate icon
            super(label,new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(iconSize,iconSize,Image.SCALE_SMOOTH)),JLabel.CENTER);
            //Set opaque for rendering background
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
    }
}
