package view.validation;

import view.validation.listener.DocumentChangeValidationChecker;
import view.validation.listener.KeyTypeValidationChecker;
import view.validation.listener.SelectionValidationChecker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
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
        component.addKeyListener(new KeyTypeValidationChecker(vComponent));
        component.addPropertyChangeListener(new DocumentChangeValidationChecker(vComponent));
        if (component instanceof ItemSelectable selectable) {
            selectable.addItemListener(new SelectionValidationChecker(vComponent));
        }


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

    public static Border getDefaultBorder(Class<?> clazz) {
        String type = clazz.getSimpleName().substring(1);
        return UIManager.getLookAndFeelDefaults().getBorder(type + ".border");
    }

    public List<JLabel> getErrors() {
        return components.stream().map(ValidatedComponent::getErrorLabel).toList();
    }

}
