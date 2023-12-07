package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.TicketType;
import view.main.panel.wizard.WizardPage;

import java.awt.*;

public class TicketTypeEditor extends WizardEditor<TicketType> {
    public TicketTypeEditor(TicketType data, WizardPage<TicketType> view) {
        super(data, view);
    }

    @Override
    protected TicketType cacheEditData() {
        return new TicketType(data);
    }

    @Override
    protected TicketType loadBackEditCache() {
        data.setName(cachedEdit.getName());
        data.setPrice(cachedEdit.getPrice());
        data.setBackgroundColor(cachedEdit.getBackgroundColor());
        data.setStatisticsEnabled(cachedEdit.isStatisticsEnabled());
        return data;
    }

    public void setTicketTypeName(String name) {
        cachedEdit.setName(name);
    }

    public void setTicketTypePrice(int price) {
        cachedEdit.setPrice(price);
    }

    public void setTicketTypeStatisticsEnabled(boolean statisticsEnabled) {
        cachedEdit.setStatisticsEnabled(statisticsEnabled);
    }

    public void setTicketTypeColor(Color backgroundColor) {
        cachedEdit.setBackgroundColor(backgroundColor);
    }
}
