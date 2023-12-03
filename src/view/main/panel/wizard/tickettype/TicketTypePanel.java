package view.main.panel.wizard.tickettype;

import control.modifier.TicketTypeEditor;
import control.wizard.WizardEditor;
import data.modifier.TicketType;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.WizardPage;
import view.validation.ComponentValidator;

import javax.swing.*;

import static data.modifier.TicketType.DEFAULT_COLOR;

public class TicketTypePanel extends AbstractPanel implements WizardPage<TicketType> {

    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JSpinner> compPrice;
    private final JCheckBox cbStatisticsEnabled;
    private final JColorChooser ccBackground;

    public TicketTypePanel() {
        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compPrice = new LabeledComponent<>("Ár:", new JSpinner(new SpinnerNumberModel(0,0,Short.MAX_VALUE,1)));
        cbStatisticsEnabled = new JCheckBox("A jegytípus beleszámít a kassza statisztikába");
        ccBackground = new JColorChooser(DEFAULT_COLOR);
    }

    @Override
    public void refreshPage(TicketType model) {
        compName.getComponent().setText(model.getName());
        compPrice.getComponent().setValue(model.getPrice());
        cbStatisticsEnabled.setSelected(model.isStatisticsEnabled());
        ccBackground.setColor(model.getBackgroundColor());
    }

    @Override
    public void saveData(WizardEditor<TicketType> controller) {
        if (controller instanceof TicketTypeEditor editor) {
            editor.setTicketTypeName(compName.getComponent().getText());
            editor.setTicketTypePrice((Integer) compPrice.getComponent().getValue());
            editor.setTicketTypeStatisticsEnabled(cbStatisticsEnabled.isEnabled());
            editor.setTicketTypeColor(ccBackground.getColor());
        }
    }

    @Override
    public void setupValidation(ComponentValidator validator) {
        validator.addComponent(compName.getComponent(), this::isNameValid, "Név nem lehet üres");
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(compName.createSequentialLayout(layout))
                        .addGroup(compPrice.createSequentialLayout(layout))
                        .addComponent(cbStatisticsEnabled)
                        .addComponent(ccBackground)
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(compName.createParallelLayout(layout))
                        .addGroup(compPrice.createParallelLayout(layout))
                        .addComponent(cbStatisticsEnabled)
                        .addComponent(ccBackground)
        );

        layout.linkSize(compName.getComponent(), compPrice.getComponent());
    }

    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }
}
