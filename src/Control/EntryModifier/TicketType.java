package Control.EntryModifier;


import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TicketType implements Serializable {

    private String name;
    private int price;
    private boolean hasFee;
    private List<Discount> appliedDiscounts = new ArrayList<>();

    public static TicketType parseTicketTypeFromJson(JSONObject discountObject) {
        //TODO: needs implementation
        return new TicketType();
    }

    //
//    /**
//     * Private constructor
//     * Can initialize a Ticket type from a generated string (or JSON Object)
//     * For common use see parseTicketTypeFromJson
//     * @param name the name of the TicketType
//     * @param price the price of the TicketType
//     * @param fee whether it matters to the financial statistics
//     */
//    private TicketType(String name, int price, boolean fee){
//        this.name = name;
//        this.price = price;
//        this.hasFee = fee;
//    }
//
//    /**
//     * Perses a JSONObject and creates a TicketType
//     * The required attributes are:
//     * name : String
//     * price : Integer
//     * fee : Boolean
//     * @param jsonObject the JSON object to be parsed
//     * @return a valid TicketType
//     */
//    public static TicketType parseTicketTypeFromJson(JSONObject jsonObject) {
//        String name = "undefined";
//        int price = 0;
//        boolean fee = false;
//        try {
//            name = jsonObject.get("name").toString();
//            price = Integer.parseInt(jsonObject.get("price").toString());
//            fee = Boolean.parseBoolean(jsonObject.get("fee").toString());
//        } catch (NumberFormatException num) {
//            //Show warning message
//            JOptionPane.showMessageDialog(new JFrame(),"A(z) '" + name +
//                    "' jegytípushoz csatolt ár formátuma hibás.\n" +
//                    "Az importálás nem sikerült, az alap beállítás lesz alkalmazva.","Hiba",ERROR_MESSAGE);
//        } catch (Exception other){
//            //Show warning message
//            JOptionPane.showMessageDialog(new JFrame(),"A(z) '" + name +
//                    "' jegytípus importálása közben hiba történt.\n" +
//                    "Az importálás nem sikerült. Részletek:\n" + other.getMessage(),"Hiba",ERROR_MESSAGE);
//        }
//
//        return new TicketType(name,price,fee);
//    }
//
//
//    /**
//     * It applies or removes a discount.
//     * If a discount with the same name is applied already then it removes it.
//     * Otherwise it applies it.
//     * @param discount the discount to apply
//     */
//    public void applyDiscount(Discount discount){
//        if(appliedDiscounts.contains(discount))
//            appliedDiscounts.remove(discount);
//        else
//            appliedDiscounts.add(discount);
//    }
    @Override
    public String toString(){
        return name;
    }
}
