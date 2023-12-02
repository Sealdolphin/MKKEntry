package view.validation.listener;

import view.validation.ValidatedComponent;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static view.validation.ComponentValidator.getDefaultBorder;

public class KeyTypeValidationChecker extends KeyAdapter {

    private final ValidatedComponent vComponent;

    public KeyTypeValidationChecker(ValidatedComponent component) {
        this.vComponent = component;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() instanceof JComponent innerComponent && vComponent.isInvalid()) {
            vComponent.reset();
            innerComponent.setBorder(getDefaultBorder(innerComponent.getClass()));
        }
    }
}