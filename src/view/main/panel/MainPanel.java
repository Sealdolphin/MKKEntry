package view.main.panel;

import control.EntryCodeReader;
import data.AppData;

import javax.swing.*;

public class MainPanel extends AbstractPanel {

    private final TopPanel topPanel;
    private final RecordPanel recordPanel;
    private final InputPanel inputPanel;
    private final InfoPanel infoPanel;

    public MainPanel(AppData model, EntryCodeReader reader) {
        recordPanel = new RecordPanel(model);
        topPanel = new TopPanel(model, recordPanel);
        inputPanel = new InputPanel(model, reader);
        infoPanel = new InfoPanel();

        inputPanel.addSelectableComponent(recordPanel);
        reader.addReadingFlagListener(infoPanel);
    }

    @Override
    public void initializeLayout() {
        topPanel.initializeLayout();
        recordPanel.initializeLayout();
        inputPanel.initializeLayout();
        infoPanel.initializeLayout();

        // Horizontal Layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(topPanel)
                        .addComponent(recordPanel)
                        .addComponent(inputPanel)
                        .addComponent(infoPanel)
        );

        //Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(topPanel)
                        .addComponent(recordPanel)
                        .addComponent(inputPanel)
                        .addComponent(infoPanel)
        );

        layout.linkSize(SwingConstants.VERTICAL, inputPanel, infoPanel);
    }

    @Override
    public void refreshPanel() {

    }
}
