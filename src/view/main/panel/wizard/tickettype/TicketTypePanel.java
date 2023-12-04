package view.main.panel.wizard.tickettype;

import control.modifier.TicketTypeEditor;
import control.wizard.WizardEditor;
import data.modifier.TicketType;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.JColoredPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.WizardPage;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static data.modifier.TicketType.DEFAULT_COLOR;

public class TicketTypePanel extends AbstractPanel implements WizardPage<TicketType> {

    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JSpinner> compPrice;
    private final JCheckBox cbStatisticsEnabled;
    private final JButton btnChooseColor;

    private final JColoredPanel bgColorPanel;

    public TicketTypePanel() {
        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compPrice = new LabeledComponent<>("Ár:", new JSpinner(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1)));
        cbStatisticsEnabled = new JCheckBox("A jegytípus beleszámít a kassza statisztikába");
        btnChooseColor = new JButton("Módosítás");
        bgColorPanel = new JColoredPanel(DEFAULT_COLOR, 50);

        btnChooseColor.addActionListener(this::openColorChooser);
    }

    @Override
    public void refreshPage(TicketType model) {
        compName.getComponent().setText(model.getName());
        compPrice.getComponent().setValue(model.getPrice());
        cbStatisticsEnabled.setSelected(model.isStatisticsEnabled());
        bgColorPanel.setBackgroundColor(model.getBackgroundColor());
    }

    @Override
    public void saveData(WizardEditor<TicketType> controller) {
        if (controller instanceof TicketTypeEditor editor) {
            editor.setTicketTypeName(compName.getComponent().getText());
            editor.setTicketTypePrice(((Number) compPrice.getComponent().getValue()).intValue());
            editor.setTicketTypeStatisticsEnabled(cbStatisticsEnabled.isSelected());
            editor.setTicketTypeColor(bgColorPanel.getBackgroundColor());
        }
    }

    @Override
    public void setupValidation(ComponentValidator validator) {
        validator.addComponent(compName.getComponent(), this::isNameValid, "Név nem lehet üres");
    }

    @Override
    public JComponent getObjectValidationComponent() {
        return compName.getComponent();
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }

    private void openColorChooser(ActionEvent event) {
        if (event.getSource().equals(btnChooseColor)) {
            Color newColor = JColorChooser.showDialog(
                    this,
                    "Válassz egy színt",
                    bgColorPanel.getBackgroundColor(),
                    false
            );
            if (newColor != null) {
                bgColorPanel.setBackgroundColor(newColor);
            }
        }
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(compName.createSequentialLayout(layout))
                        .addGroup(compPrice.createSequentialLayout(layout))
                        .addComponent(cbStatisticsEnabled)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(btnChooseColor)
                                        .addGap(0,0,Short.MAX_VALUE)
                                        .addComponent(bgColorPanel)
                        )
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(compName.createParallelLayout(layout))
                        .addGroup(compPrice.createParallelLayout(layout))
                        .addComponent(cbStatisticsEnabled)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(btnChooseColor)
                                        .addComponent(bgColorPanel)
                        )
        );

        layout.linkSize(compName.getComponent(), compPrice.getComponent());
    }

    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }
}
