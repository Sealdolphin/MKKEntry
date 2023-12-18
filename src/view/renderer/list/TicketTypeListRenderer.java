package view.renderer.list;

import data.modifier.TicketType;

import javax.swing.*;
import java.awt.*;

import static view.renderer.RenderedIcon.TICKET_TYPE;

public class TicketTypeListRenderer extends DefaultWizardTypeListRenderer<TicketType> {


    public TicketTypeListRenderer() {
        super(TICKET_TYPE);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends TicketType> list, TicketType ticketType, int index, boolean isSelected, boolean cellHasFocus) {
        if (ticketType == null) {
            lbName.setText("Válassz egyet...");
            lbDescription.setText(null);
        } else {
            lbName.setText(ticketType.getName());
            lbDescription.setText(String.format("Ár: %d", ticketType.getPrice()));
        }

        return super.getListCellRendererComponent(list, ticketType, index, isSelected, cellHasFocus);
    }
}
