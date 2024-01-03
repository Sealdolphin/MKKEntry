package control.modifier;

import java.awt.*;

/**
 * Use ${@link data.wizard.WizardType} instead
 */
@Deprecated
public interface Modifier {

    ModifierDialog getModifierWizard(Window parent);

    boolean validate();
}
