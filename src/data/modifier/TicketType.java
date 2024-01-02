package data.modifier;


import control.modifier.Modifier;
import control.modifier.ModifierDialog;
import control.modifier.ModifierWizardEditor;
import control.modifier.TicketTypeEditor;
import control.wizard.WizardEditor;
import data.wizard.WizardType;
import org.json.JSONObject;
import view.main.panel.wizard.tickettype.TicketTypePanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class TicketType implements Serializable, Modifier, WizardType {

    private final UUID id = UUID.randomUUID();
    public static final Color DEFAULT_COLOR = new Color(255, 255, 255);
    private String name;
    private int price;
    private boolean statisticsEnabled;

    private Color backgroundColor;

    public TicketType() {}

    /**
     * Private constructor
     * Can initialize a Ticket type from a generated string (or JSON Object)
     * For common use see parseTicketTypeFromJson
     * @param name the name of the TicketType
     * @param price the price of the TicketType
     * @param fee whether it matters to the financial statistics
     */
    @Deprecated
    public TicketType(String name, int price, boolean fee, Color bgColor){
        this.name = name;
        this.price = price;
        this.statisticsEnabled = fee;
        this.backgroundColor = bgColor;
    }

    public TicketType(TicketType other){
        this(other.name,other.price,other.statisticsEnabled, other.backgroundColor);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Perses a JSONObject and creates a TicketType
     * The required attributes are:
     * name : String
     * price : Integer
     * fee : Boolean
     * @param ticketObject the JSON object to be parsed
     * @return a valid TicketType
     */
    public static TicketType parseTicketTypeFromJson(JSONObject ticketObject) {
        TicketType ticketType = new TicketType();
        try {
            ticketType.setName(ticketObject.getString("name"));
            ticketType.setPrice(ticketObject.getInt("price"));
            ticketType.setStatisticsEnabled(ticketObject.optBoolean("statistics", false));
            JSONObject colorObject = ticketObject.optJSONObject("color", new JSONObject());
            ticketType.setBackgroundColor(new Color(
                    colorObject.optInt("r", DEFAULT_COLOR.getRed()),
                    colorObject.optInt("g", DEFAULT_COLOR.getGreen()),
                    colorObject.optInt("b", DEFAULT_COLOR.getBlue())
            ));
        } catch (NumberFormatException num) {
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),
                    "Hiba :\nA(z) '" + ticketType.getName() +
                    "' jegytípushoz csatolt ár formátuma hibás.\n" +
                    "Az importálás nem sikerült, az alap beállítás lesz alkalmazva.","Hiba",ERROR_MESSAGE);
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),"Hiba :\nA(z) '" + ticketType.getName() +
                    "' jegytípus importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other,"Hiba",ERROR_MESSAGE);
        }

        return ticketType;
    }

    @Override
    public boolean equals(Object other){
        if(other == null) return false;
        if(other == this) return true;
        if(!other.getClass().equals(TicketType.class)) return false;
        TicketType otherType = (TicketType) other;
        return otherType.name.equals(name);
    }

    @Override
    public String toString(){
        return name;
    }

    @Deprecated
    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new TicketTypeWizard(parent);
    }

    @Deprecated
    @Override
    public boolean validate() {
        return name != null && !name.isEmpty();
    }

    @Deprecated
    public int getFees() {
        return statisticsEnabled ? price : 0;
    }

    @Deprecated
    public boolean hasFee() {
        return statisticsEnabled;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public WizardEditor<TicketType> createWizard() {
        return new TicketTypeEditor(this, new TicketTypePanel());
    }

    @Deprecated
    public static class TicketTypeListener extends ModifierWizardEditor<TicketType> {
        public TicketTypeListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<TicketType> objectList) {
            TicketType newType = new TicketType("",0,false,DEFAULT_COLOR);
            ModifierDialog wizard = newType.getModifierWizard(null);
            int result = wizard.open();
            if(result == 0){
                objectList.add(newType);
            }
        }

    }

    @Deprecated
    private class TicketTypeWizard extends ModifierDialog {

        private final JTextField tfName = new JTextField(name);
        private final JSpinner spPrice = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1));
        private final JCheckBox cbHasFee = new JCheckBox("A jegytípust számon tartódik a kasszában is");

        TicketTypeWizard(Window parent) {
            super(parent,"Jegytípus szerkesztése");
            body.setLayout(new GridBagLayout());

            //Set values
            spPrice.setValue(price);
            cbHasFee.setSelected(statisticsEnabled);

            body.add(new JLabel("Név: "),setConstraints(0,0,1,1));
            body.add(new JLabel("Ár: "),setConstraints(0,1,1,1));
            body.add(cbHasFee,setConstraints(0,2,2,1));

            body.add(tfName,setConstraints(1,0,1,1));
            body.add(spPrice,setConstraints(1,1,1,1));

            btnSave.addActionListener(e -> saveType());

            finishDialog(parent);
        }

        private void saveType(){
            if(tfName.getText().length() > 0){
                name = tfName.getText();
                price = Integer.parseInt(spPrice.getValue().toString());
                statisticsEnabled = cbHasFee.isSelected();
                result = 0;
                //Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Not a valid ticketType!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
