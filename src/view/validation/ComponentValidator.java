package view.validation;

import view.validation.listener.DocumentChangeValidationChecker;
import view.validation.listener.KeyTypeValidationChecker;
import view.validation.listener.SelectionValidationChecker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class ComponentValidator {

    private final HashMap<JComponent, ValidatedComponent> components;

    private static final Border invalidBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED), BorderFactory.createEmptyBorder(0,2,0,2));

    public ComponentValidator() {
        components = new HashMap<>();
    }

    public void addComponent(JComponent component, Supplier<Boolean> validationMethod, String errorMsg) {
        components.putIfAbsent(component, new ValidatedComponent(component));
        ValidatedComponent vComponent = components.get(component);
        vComponent.addValidation(validationMethod, errorMsg);
        component.addKeyListener(new KeyTypeValidationChecker(vComponent));
        component.addPropertyChangeListener(new DocumentChangeValidationChecker(vComponent));
        if (component instanceof ItemSelectable selectable) {
            selectable.addItemListener(new SelectionValidationChecker(vComponent));
        }


    }
    public boolean validate() {
        return components.values().stream().map(this::validateComponent).reduce(true, this::evaluateValidation);
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
        components.values().forEach(component -> {
            component.reset();
            setComponentBorder(component, !component.isInvalid());
        });

    }

    public static Border getDefaultBorder(Class<?> clazz) {
        String type = clazz.getSimpleName().substring(1);
        return UIManager.getLookAndFeelDefaults().getBorder(type + ".border");
    }

    public List<JLabel> getErrors() {
        return components.values().stream().map(ValidatedComponent::getErrorLabels).flatMap(Collection::stream).toList();
    }

}
