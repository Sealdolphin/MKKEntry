package view.validation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ComponentValidator {

    private final List<ValidatedComponent> components;

    private static final Border invalidBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED), BorderFactory.createEmptyBorder(0,2,0,2));

    public ComponentValidator() {
        components = new ArrayList<>();
    }

    public void addComponent(JComponent component, Supplier<Boolean> validationMethod, String errorMsg) {
        ValidatedComponent vComponent = new ValidatedComponent(component, validationMethod, errorMsg);
        components.add(vComponent);
        component.addKeyListener(new ValidationAdapter(vComponent));
    }
    public boolean validate() {
        return components.stream().map(this::validateComponent).reduce(true, this::evaluateValidation);
    }

    private boolean evaluateValidation(boolean prev, boolean next) {
        return prev && next;
    }

    public boolean validateComponent(ValidatedComponent component) {
        boolean valid = component.doValidate();
        setComponentBorder(component, valid);
        return valid;
    }

    private void setComponentBorder(ValidatedComponent component, boolean isValid) {
        JComponent innerComponent = component.getComponent();
        if (isValid) {
            innerComponent.setBorder(getDefaultBorder(innerComponent.getClass()));
        } else {
            innerComponent.setBorder(invalidBorder);
        }

    }

    public void resetAll() {
        components.forEach(component -> {
            component.reset();
            setComponentBorder(component, !component.isInvalid());
        });

    }

    private Border getDefaultBorder(Class<?> clazz) {
        String type = clazz.getSimpleName().substring(1);
        return UIManager.getLookAndFeelDefaults().getBorder(type + ".border");
    }

    public List<JLabel> getErrors() {
        return components.stream().map(ValidatedComponent::getErrorLabel).toList();
    }

    private class ValidationAdapter extends KeyAdapter {

        private final ValidatedComponent vComponent;

        public ValidationAdapter(ValidatedComponent component) {
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

}
