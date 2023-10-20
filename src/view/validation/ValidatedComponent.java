package view.validation;

import javax.swing.*;
import java.util.function.Supplier;

public class ValidatedComponent {

    private final JComponent component;
    private final Supplier<Boolean> validation;
    private final JLabel lbError;
    private boolean invalid = false;

    public ValidatedComponent(JComponent component, Supplier<Boolean> validation, String errorMessage) {
        this.component = component;
        this.validation = validation;
        this.lbError = new JLabel(errorMessage);
        lbError.setVisible(false);
    }

    public boolean doValidate() {
        boolean valid = validation.get();
        invalid = !valid;
        lbError.setVisible(invalid);
        return valid;
    }

    public void reset() {
        invalid = false;
        lbError.setVisible(false);
    }

    public boolean isInvalid() {
        return invalid;
    }

    public JComponent getComponent() {
        return component;
    }

    public JLabel getErrorLabel() {
        return lbError;
    }
}
