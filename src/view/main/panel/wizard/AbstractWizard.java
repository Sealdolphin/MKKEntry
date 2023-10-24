package view.main.panel.wizard;

import data.wizard.WizardType;
import view.main.panel.AbstractPanel;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class AbstractWizard<T extends WizardType> extends JPanel implements Wizard {

    private static final Font FONT_ERROR = new Font("Arial", Font.BOLD, 14);
    protected T model;
    protected WizardPage<T> wizardPage;
    protected ComponentValidator validator;
    private final JButton btnSave;
    private final JButton btnCancel;

    protected AbstractWizard() {
        this.model = null;
        validator = new ComponentValidator();
        createWizardPage();
        setLayout(new BorderLayout());

        btnSave = new JButton("Mentés");
        btnCancel = new JButton("Mégsem");

        btnSave.addActionListener(this::doSaveEntity);
        btnCancel.addActionListener(this::cancelEditing);

        if (wizardPage instanceof AbstractPanel wizardPanel) {
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

    @Override
    public void cancelEditing(ActionEvent event) {
        wizardPage.refreshPage(model);
    }

    @Override
    public void doSaveEntity(ActionEvent event) {
        if (validator.validate()) {
            this.model = wizardPage.generateWizardType();
            // TODO: alert list of model change!!
        }
    }

    public abstract void createWizardPage();

    @Override
    public JPanel getPanel() {
        return this;
    }
}
