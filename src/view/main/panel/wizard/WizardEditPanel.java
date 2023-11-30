package view.main.panel.wizard;

import control.wizard.AbstractWizard;
import control.wizard.Wizard;
import data.wizard.WizardType;
import view.main.panel.AbstractPanel;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.awt.*;

public class WizardEditPanel<T extends WizardType> extends JPanel {

    private static final Font FONT_ERROR = new Font("Arial", Font.BOLD, 14);
    private final ComponentValidator validator = new ComponentValidator();
    private final JButton btnSave;
    private final JButton btnCancel;

    public WizardEditPanel(Wizard wizard, WizardPage<T> editPage) {
        editPage.setupValidation(validator);

        setLayout(new BorderLayout());

        btnSave = new JButton("Mentés");
        btnSave.setActionCommand(String.valueOf(AbstractWizard.WizardCommands.UPDATE));
        btnSave.addActionListener(wizard::handleUserAction);

        btnCancel = new JButton("Mégsem");
        btnCancel.setActionCommand(String.valueOf(AbstractWizard.WizardCommands.CANCEL));
        btnCancel.addActionListener(wizard::handleUserAction);

        if (editPage instanceof AbstractPanel wizardPanel) {
            wizardPanel.initializeLayout();

            add(wizardPanel, BorderLayout.NORTH);
            add(new JScrollPane(createValidationPanel()), BorderLayout.CENTER);
            add(createBottomButtonPanel(), BorderLayout.PAGE_END);
        }
    }

    private Box createValidationPanel() {
        Box validationPanel = Box.createVerticalBox();
        validator.getErrors().forEach(error -> {
            decorateErrorLabel(error);
            validationPanel.add(error);
            validationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        });
        return validationPanel;
    }

    private void decorateErrorLabel(JLabel errorLabel) {
        Icon icon = UIManager.getIcon("OptionPane.errorIcon");
        errorLabel.setIcon(icon);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        errorLabel.setFont(FONT_ERROR);
        errorLabel.setForeground(Color.RED);
        errorLabel.setBackground(Color.PINK);
        errorLabel.setOpaque(true);
        errorLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, icon.getIconHeight()));
    }

    private Box createBottomButtonPanel() {
        Box panel = Box.createHorizontalBox();
        panel.add(Box.createGlue());
        panel.add(btnSave);
        panel.add(Box.createGlue());
        panel.add(btnCancel);
        panel.add(Box.createGlue());
        return panel;
    }
}
