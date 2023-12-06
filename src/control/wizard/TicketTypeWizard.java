package control.wizard;

import data.DataModel;
import data.modifier.TicketType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class TicketTypeWizard extends DefaultDataListWizard<TicketType> {

    public TicketTypeWizard(DataModel<TicketType> ticketTypes) {
        super(ticketTypes, new TicketType().createWizard());
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof TicketType selection) {
                setSelection(selection);
            }
            if (list.getSelectedValue() == null) {
                setSelection(null);
            }
        }
    }

    @Override
    protected TicketType getNewElement() {
        return new TicketType();
    }

}
