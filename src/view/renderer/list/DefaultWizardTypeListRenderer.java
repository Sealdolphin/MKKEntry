package view.renderer.list;

import data.wizard.WizardType;
import view.renderer.RenderedIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static view.renderer.RenderedIcon.UNDER_EDIT;

public abstract class DefaultWizardTypeListRenderer<T extends WizardType> extends JPanel implements ListCellRenderer<T> {

    protected final JLabel lbName;
    protected final JLabel lbDescription;
    private T typeUnderEdit = null;

    private final RenderedIcon defaultIcon;

    public DefaultWizardTypeListRenderer(RenderedIcon icon) {
        this.defaultIcon = icon;

        setOpaque(true);
        lbName = new JLabel();
        lbDescription = new JLabel();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        lbName.setIcon(defaultIcon.getImage());
        lbName.setFont(new Font("Arial", Font.BOLD, 22));
        lbDescription.setFont(new Font("Arial", Font.PLAIN, 12));

        add(lbName);
        add(lbDescription);
    }

    public void updateRenderer(T typeUnderEdit) {
        this.typeUnderEdit = typeUnderEdit;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            if (value.equals(typeUnderEdit)) {
                lbName.setIcon(UNDER_EDIT.getImage());
                lbName.setText("<Üres>");
                lbDescription.setText("Szerkesztés alatt...");
                setBackground(Color.ORANGE);
                setForeground(list.getSelectionForeground());
            } else {
                lbName.setIcon(defaultIcon.getImage());
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
        } else {
            lbName.setIcon(defaultIcon.getImage());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
