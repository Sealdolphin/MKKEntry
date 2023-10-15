package view.main.panel.utility;

import javax.swing.*;

public class LabeledComponent<T extends JComponent> {

    private final JLabel label;
    private final T component;

    public LabeledComponent(String name, T component) {
        this.label = new JLabel(name);
        this.component = component;
    }

    public GroupLayout.ParallelGroup createParallelLayout(GroupLayout layout) {
        return layout.createParallelGroup()
                .addComponent(label, GroupLayout.Alignment.CENTER)
                .addComponent(component);
    }

    public GroupLayout.SequentialGroup createSequentialLayout(GroupLayout layout) {
        return layout.createSequentialGroup()
                .addComponent(label)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(component);
    }

    public JLabel getLabel() {
        return label;
    }

    public T getComponent() {
        return component;
    }

}
