package view.validation.listener;

import view.validation.ValidatedComponent;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static view.validation.ComponentValidator.getDefaultBorder;

public class SelectionValidationChecker implements ItemListener {

    private final ValidatedComponent vComponent;

    public SelectionValidationChecker(ValidatedComponent vComponent) {
        this.vComponent = vComponent;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof JComponent innerComponent && vComponent.isInvalid()) {
            vComponent.reset();
            innerComponent.setBorder(getDefaultBorder(innerComponent.getClass()));
        }
    }
}
