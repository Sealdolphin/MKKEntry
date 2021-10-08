package control.modifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class ModifierWizardEditor<T extends Modifier> extends MouseAdapter {

    private Window parent;

    ModifierWizardEditor(Window parent){
        this.parent = parent;
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
                JDialog wizard = selectedType.getModifierWizard(parent);
                wizard.setVisible(true);
            }
        }
    }

    public void removeFrom(List<T> objectList, T selectedValue){
        objectList.remove(selectedValue);
    }

    public abstract void createNew(List<T> objectList);
}
