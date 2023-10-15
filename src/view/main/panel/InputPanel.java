package view.main.panel;

import control.EntryCodeReader;
import data.DataModel;
import data.util.ReadingFlag;
import view.main.interactive.SelectableComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class InputPanel extends AbstractPanel {

    private final JButton btnDelete;
    private final JButton btnLeave;
    private final JButton btnSendCode;
    private final JButton btnClearSelection;

    private final JCheckBox checkBoxCode;

    private final JTextField tfInputCode;

    private final DataModel<?> model;
    private final EntryCodeReader codeReader;

    private final List<SelectableComponent> selectableComponents = new ArrayList<>();

    public InputPanel(DataModel<?> model, EntryCodeReader codeReader) {
        this.model = model;
        this.codeReader = codeReader;

        // FIXME: import UI json!!
        btnDelete = new JButton("Töröl");
        btnLeave = new JButton("Kiléptet");
        btnSendCode = new JButton("Olvas");
        btnClearSelection = new JButton("Kiválasztást megszüntet");
        checkBoxCode = new JCheckBox("Belépőkód küldése");
        tfInputCode = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);
        btnDelete.setForeground(Color.RED);
        btnDelete.setBackground(new Color(0xff6666));

        btnClearSelection.addActionListener(this::clearSelectionAction);
        btnSendCode.addActionListener(this::sendCodeAction);
        btnLeave.addActionListener(this::setLeaveAction);
        btnDelete.addActionListener(this::deleteAction);
    }

    public void addSelectableComponent(SelectableComponent component) {
        selectableComponents.add(component);
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(btnDelete)
                        .addComponent(btnLeave)
                        .addComponent(tfInputCode)
                        .addComponent(checkBoxCode)
                        .addComponent(btnSendCode)
                        .addComponent(btnClearSelection)
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addComponent(btnDelete)
                        .addComponent(btnLeave)
                        .addComponent(tfInputCode)
                        .addComponent(checkBoxCode)
                        .addComponent(btnSendCode)
                        .addComponent(btnClearSelection)
        );
    }

    private void sendCodeAction(ActionEvent event) {
        if(checkBoxCode.isSelected())
            codeReader.readEntryCode(tfInputCode.getText());
        else codeReader.receiveBarCode(tfInputCode.getText());
    }

    private void clearSelectionAction(ActionEvent event) {
        model.setSelection(null);
        selectableComponents.forEach(SelectableComponent::clearSelection);
    }

    private void setLeaveAction(ActionEvent event) {
        codeReader.setReadingFlag(ReadingFlag.FL_IS_LEAVING);
    }

    private void deleteAction(ActionEvent event) {
        codeReader.setReadingFlag(ReadingFlag.FL_IS_DELETE);
    }
}
