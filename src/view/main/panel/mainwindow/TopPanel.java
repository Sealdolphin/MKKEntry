package view.main.panel.mainwindow;

import view.main.panel.AbstractPanel;

import javax.swing.*;
import javax.swing.table.TableModel;

public class TopPanel extends AbstractPanel {

    private final JLabel lbProfile;
    private final JButton btnBarcodePanel;
    private final BarcodeReaderConfigPanel barcodeConfigPanel;
    private final QuickSearchPanel searchPanel;

    public TopPanel(TableModel model, RecordPanel entryView) {
        lbProfile = new JLabel("Profil: MKK Szüreti Bál 2032");
        barcodeConfigPanel = new BarcodeReaderConfigPanel();
        btnBarcodePanel = new JButton("Vonalkódok");
        searchPanel = new QuickSearchPanel(model, entryView);
    }

    @Override
    public void initializeLayout() {
        barcodeConfigPanel.initializeLayout();
        searchPanel.initializeLayout();

        // Horizontal layout
        GroupLayout.SequentialGroup topLine = layout.createSequentialGroup();
        topLine.addComponent(lbProfile)
                .addGap(0,0,Short.MAX_VALUE)
                .addComponent(barcodeConfigPanel)
                .addGap(0,0,Short.MAX_VALUE)
                .addComponent(btnBarcodePanel);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(topLine)
                        .addComponent(searchPanel, GroupLayout.Alignment.CENTER)
        );

        // Vertical layout
        GroupLayout.ParallelGroup topLineVertical = layout.createBaselineGroup(false, true);
        topLineVertical.addComponent(lbProfile, GroupLayout.Alignment.LEADING)
                .addComponent(barcodeConfigPanel)
                .addComponent(btnBarcodePanel, GroupLayout.Alignment.TRAILING);
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(topLineVertical)
                        .addComponent(searchPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
        );
    }
}
