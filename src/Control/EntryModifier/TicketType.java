package Control.EntryModifier;

public class TicketType {

    private String name;
    private int price;

    public TicketType(String name, int price){
        this.name = name;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
