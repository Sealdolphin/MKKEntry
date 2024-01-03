package view.main.panel.mainwindow;

import view.main.panel.AbstractPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class BarcodeReaderConfigPanel extends AbstractPanel {

    private final JLabel lbTitle;
    private final JLabel lbStatus;
    private final JComboBox<String> comboPortList;
    private final JButton btnRefresh;

    {
        btnRefresh = new JButton("↻");
        lbTitle = new JLabel("Vonalkód olvasó:");
        lbStatus = new JLabel("✅");
        comboPortList = new JComboBox<>();
    }

    public BarcodeReaderConfigPanel() {
        btnRefresh.addActionListener(this::refreshPorts);
        setupPortSelector(Collections.emptyList());
    }

    private void refreshPorts(ActionEvent refreshAction) {
        if (!refreshAction.getSource().equals(btnRefresh)) {
            return;
        }
    }

    private void setupPortSelector(List<String> choices) {
        comboPortList.setModel(new DefaultComboBoxModel<>(choices.toArray(new String[0])));
    }

    @Override
    public void initializeLayout() {
        // Vertical layout
        GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup();
        parallelGroup
                .addComponent(lbStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(comboPortList)
                .addComponent(btnRefresh);
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(lbTitle)
                        .addGroup(parallelGroup)
        );

        // Horizontal layout
        GroupLayout.SequentialGroup topLine = layout.createSequentialGroup();
        GroupLayout.SequentialGroup bottomLine = layout.createSequentialGroup();
        topLine.addComponent(lbTitle);
        bottomLine
                .addComponent(lbStatus)
                .addComponent(comboPortList)
                .addComponent(btnRefresh);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(topLine)
                        .addGroup(bottomLine)
        );
    }

}
