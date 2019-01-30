package data;

import control.modifier.Discount;
import control.modifier.TicketType;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Az egyes vendég-rekordok tulajdonságai.
 * A tulajdonságok egy String alapú Vector.
 * A Vector tartalma:
 * 1. ID: a rekord egyedi azonosítója
 * 2. Name: a rekordhoz tartozó név
 * 3. Type: a rekordhoz tartozó jegytípus (TicketType -> toString())
 * 4. Discounts: a levont kedvezmények listája (List -> Array.toString())
 * 5. Entered: Belépett-e a rekord => enter != null && leave == null
 * 6. Enter: a belépés időpontja
 * 7. Leave: a kilépés időpontja
 */
public class Entry extends Vector<String> {


    private TicketType ticketType;
    private List<Discount> discountList = new ArrayList<>();

    static String[] columnNames = {"ID","NÉV","TÍPUS","KEDVEZMÉNYEK","BELÉPETT","BELÉPÉS","KILÉPÉS"};
    public enum Column{

    }

    Boolean isEntered(){
        return (get(5) != null && get(6) == null);
    }

    public void applyDiscount(Discount discount){
        if(discount == null) return;
        Discount disCopy = discountList.stream().filter(d -> d.equals(discount)).findAny().orElse(null);
        if(disCopy != null && discountList.contains(disCopy)) {
            discountList.remove(disCopy);
            System.out.println("[ENTRY "+ get(0) +"]: Discount removed (" + disCopy.toString() + ")");
        } else {
            discountList.add(discount);
            System.out.println("[ENTRY "+ get(0) +"]: Discount added (" + discount.toString() + ")");
        }
        set(3,discountList.toString());
    }

    private Entry(String id, String name, TicketType type, List<Discount> discounts, String enter, String leave){
        setSize(7);
        add(0,id);
        add(1,name);
        add(2,type.toString());
        ticketType = type;
        if(discounts != null){
            discountList = discounts;
            add(3,discountList.toString());
        }
        add(5,enter);
        add(6,leave);
    }

    /**
     * A Time formatter for the proper time format
     */
    static private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

    public Entry(String id, String name, TicketType type){
        this(id,name,type,null,null,null);
    }

    List<Discount> getDiscounts(){
        return discountList;
    }

    /**
     * Enters the guest, creating / overwriting the time of entry
     */
    public void Enter(){
        set(5,LocalDateTime.now().format(formatter));
        set(6,null);
    }

    /**
     * Makes the guest leave, creating / overwriting the time of leave
     */
    public void Leave(){
        set(6,LocalDateTime.now().format(formatter));
    }


}
