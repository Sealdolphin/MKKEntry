package control.modifier;

import data.EntryProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class ModifierWizardEditor<T extends TicketModifier> extends MouseAdapter{

    private Window parent;
    private EntryProfile profile;

    ModifierWizardEditor(Window parent, EntryProfile profile){
        this.parent = parent;
        this.profile = profile;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        JList list = (JList)evt.getSource();
        //Double click detection
        if(evt.getClickCount() == 2) {
            Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
            //Only items in the list detect clicks
            if (r != null && r.contains(evt.getPoint())) {
                //noinspection unchecked
                T selectedType = (T) list.getSelectedValue();
                JDialog wizard = selectedType.getTypeWizard(parent, profile, list.getSelectedIndex());
                wizard.setVisible(true);
            }
        }
    }

    public abstract void removeFrom(List<T> objectList, T selectedValue);
}
