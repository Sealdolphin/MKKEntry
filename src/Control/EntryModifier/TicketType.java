package Control.EntryModifier;

import org.json.simple.JSONObject;

public class TicketType {

    private String name;
    private int price;
    private boolean hasFee;

    private TicketType(String name, int price, boolean fee){
        this.name = name;
        this.price = price;
        this.hasFee = fee;
    }

    public static TicketType parseTicketTypeFromJson(JSONObject jsonObject) {
        String name = jsonObject.get("name").toString();
        int price = Integer.parseInt(jsonObject.get("price").toString());
        boolean fee = Boolean.parseBoolean(jsonObject.get("fee").toString());

        return new TicketType(name,price,fee);
    }


    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }


}
