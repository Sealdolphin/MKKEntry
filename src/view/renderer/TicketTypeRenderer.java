package view.renderer;

import control.modifier.TicketType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TicketTypeRenderer extends DefaultTableCellRenderer {

    private TicketType ticketType;
    public TicketTypeRenderer(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {


        Component origin = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            origin.setBackground(ticketType.getBgColor());
        }
        return origin;
    }

}
