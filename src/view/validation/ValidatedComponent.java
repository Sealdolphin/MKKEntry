package view.validation;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ValidatedComponent {

    private final JComponent component;

    private final HashMap<JLabel, Supplier<Boolean>> validations;

    private boolean invalid = false;

    public ValidatedComponent(JComponent component) {
        this.validations = new HashMap<>();
        this.component = component;
    }

    public void addValidation(Supplier<Boolean> validation, String errorMessage) {
        JLabel lbError = new JLabel(errorMessage);
        lbError.setVisible(false);
        validations.put(lbError, validation);
    }

    public boolean doValidate() {
        boolean valid = validations.entrySet().stream().map(this::evaluateValidation).reduce(true, (base, next) -> base && next);

        invalid = invalid || !valid;

        return !invalid;
    }

    private boolean evaluateValidation(Map.Entry<JLabel, Supplier<Boolean>> validation) {
        JLabel validationLabel = validation.getKey();
        boolean valid = validation.getValue().get();
        validationLabel.setVisible(!valid);
        return valid;
    }

    public void reset() {
        invalid = false;
        validations.keySet().forEach(lb -> lb.setVisible(false));
    }

    public boolean isInvalid() {
        return invalid;
    }

    public JComponent getComponent() {
        return component;
    }

    public Set<JLabel> getErrorLabels() {
        return validations.keySet();
    }
}
