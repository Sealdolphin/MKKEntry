package view.main.panel.utility;

import javax.swing.*;

/**
 * Wraps a JComponent in a label handling the two together.
 * You can also include this in different layouts using {@link #createParallelLayout(GroupLayout)} or {@link #createSequentialLayout(GroupLayout)}
 * @param <T> the type of the wrapped JComponent. Must be extending {@link JComponent}
 */
public class LabeledComponent<T extends JComponent> {

    private final JLabel label;
    private final T component;

    public LabeledComponent(String name, T component) {
        this.label = new JLabel(name);
        this.component = component;
    }

    /**
     * Returns a parallel layout group created by the group layout. This way you can lay out the
     * components horizontally.
     * @param layout the parent {@link GroupLayout}
     * @return A Parallel group with the wrapped component and it's label
     */
    public GroupLayout.ParallelGroup createParallelLayout(GroupLayout layout) {
        return layout.createParallelGroup()
                .addComponent(label, GroupLayout.Alignment.CENTER)
                .addComponent(component);
    }


    /**
     * Returns a sequential layout group created by the group layout. This way you can lay out the
     * components vertically.
     * @param layout the parent {@link GroupLayout}
     * @return A Sequential group with the wrapped component and it's label
     */
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
