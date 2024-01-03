package view.validation.listener;

import view.validation.ValidatedComponent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static view.validation.ComponentValidator.getDefaultBorder;

public class DocumentChangeValidationChecker implements PropertyChangeListener, DocumentListener {

    private final ValidatedComponent vComponent;

    public DocumentChangeValidationChecker(ValidatedComponent vComponent) {
        this.vComponent = vComponent;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() instanceof JTextComponent textComponent) {
            textComponent.getDocument().putProperty("owner", textComponent);
            textComponent.getDocument().addDocumentListener(this);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        resetComponent(event);
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        resetComponent(event);
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        resetComponent(event);
    }

    private void resetComponent(DocumentEvent event) {
        if (event.getDocument().getProperty("owner") instanceof JComponent innerComponent && vComponent.isInvalid()) {
            vComponent.reset();
            innerComponent.setBorder(getDefaultBorder(innerComponent.getClass()));
        }
    }
}
