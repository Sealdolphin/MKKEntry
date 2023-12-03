package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.TicketType;
import view.main.panel.wizard.WizardPage;

import java.awt.*;

public class TicketTypeEditor extends WizardEditor<TicketType> {
    public TicketTypeEditor(TicketType data, WizardPage<TicketType> view) {
        super(data, view);
    }

    public void setTicketTypeName(String name) {
        data.setName(name);
    }

    public void setTicketTypePrice(int price) {
        data.setPrice(price);
    }

    public void setTicketTypeStatisticsEnabled(boolean statisticsEnabled) {
        data.setStatisticsEnabled(statisticsEnabled);
    }

    public void setTicketTypeColor(Color backgroundColor) {
        data.setBackgroundColor(backgroundColor);
    }
}
