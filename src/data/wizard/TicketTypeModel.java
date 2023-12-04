package data.wizard;

import data.modifier.TicketType;
import view.renderer.list.TicketTypeListRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeModel extends DefaultWizardModel<TicketType> {

    private final TicketTypeListRenderer listRenderer;

    public TicketTypeModel() {
        this(new ArrayList<>());
    }

    public TicketTypeModel(List<TicketType> dataList) {
        super(dataList);
        this.listRenderer = new TicketTypeListRenderer();
    }

    @Override
    public ListCellRenderer<TicketType> createListRenderer() {
        return listRenderer;
    }

    @Override
    protected void updateRenderers(TicketType ticketType) {
        listRenderer.updateRenderer(ticketType);
    }
}
