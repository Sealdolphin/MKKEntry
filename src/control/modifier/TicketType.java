package control.modifier;


import data.EntryProfile;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class TicketType implements Serializable, Modifier {

    private String name;
    private int price;
    private boolean hasFee;

    /**
     * Private constructor
     * Can initialize a Ticket type from a generated string (or JSON Object)
     * For common use see parseTicketTypeFromJson
     * @param name the name of the TicketType
     * @param price the price of the TicketType
     * @param fee whether it matters to the financial statistics
     */
    private TicketType(String name, int price, boolean fee){
        this.name = name;
        this.price = price;
        this.hasFee = fee;
    }

    public TicketType(TicketType other){
        this(other.name,other.price,other.hasFee);
    }

    /**
     * Perses a JSONObject and creates a TicketType
     * The required attributes are:
     * name : String
     * price : Integer
     * fee : Boolean
     * @param jsonObject the JSON object to be parsed
     * @return a valid TicketType
     */
    public static TicketType parseTicketTypeFromJson(JSONObject jsonObject, String profileName) {
        String name = "undefined";
        int price = 0;
        boolean fee = false;
        try {
            name = jsonObject.get("name").toString();
            price = Integer.parseInt(jsonObject.get("price").toString());
            fee = Boolean.parseBoolean(jsonObject.get("fee").toString());
        } catch (NumberFormatException num) {
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profileName +
                    ":\nA(z) '" + name +
                    "' jegytípushoz csatolt ár formátuma hibás.\n" +
                    "Az importálás nem sikerült, az alap beállítás lesz alkalmazva.","Hiba",ERROR_MESSAGE);
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profileName+ ":\nA(z) '" + name +
                    "' jegytípus importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other.toString(),"Hiba",ERROR_MESSAGE);
        }

        return new TicketType(name,price,fee);
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public ModifierDialog getTypeWizard(Window parent) {
        return new TicketTypeWizard(parent);
    }

    public static class TicketTypeListener extends ModifierWizardEditor<TicketType>{
        public TicketTypeListener(Window parent) {
            super(parent);
        }

        @Override
        public void removeFrom(List<TicketType> objectList, TicketType selectedValue) {
            objectList.remove(selectedValue);
        }

        @Override
        public void createNew(List<TicketType> objectList) {
            TicketType newType = new TicketType("",0,false);
            ModifierDialog wizard = newType.getTypeWizard(null);
            int result = wizard.open();
            if(result == 0){
                objectList.add(newType);
            }
        }

    }

    private class TicketTypeWizard extends ModifierDialog {

        private JTextField tfName = new JTextField(name);
        private JSpinner spPrice = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1));
        private JCheckBox cbHasFee = new JCheckBox("A jegytípust számon tartódik a kasszában is");

        TicketTypeWizard(Window parent) {
            super(parent,"Jegytípus szerkesztése");
            body.setLayout(new GridBagLayout());

            //Set values
            spPrice.setValue(price);
            cbHasFee.setSelected(hasFee);

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
                hasFee = cbHasFee.isSelected();
                result = 0;
                //Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Not a valid ticketType!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
