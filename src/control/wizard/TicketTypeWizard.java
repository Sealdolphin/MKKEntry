package control.wizard;

import data.DataModel;
import data.modifier.TicketType;
import data.wizard.TicketTypeModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class TicketTypeWizard extends AbstractWizard<TicketType> {

    public TicketTypeWizard() {
        this(new TicketTypeModel());
    }

    public TicketTypeWizard(DataModel<TicketType> ticketTypes) {
        super(ticketTypes, new TicketType().createWizard());
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof TicketType selection) {
                setSelection(selection);
            }
        }
    }

    @Override
    protected TicketType castSelectedElement(Object selection) {
        if (selection instanceof TicketType selectedTicketType) {
            return selectedTicketType;
        } else {
            return null;
        }
    }

    @Override
    protected TicketType getNewElement() {
        return new TicketType();
    }

}
