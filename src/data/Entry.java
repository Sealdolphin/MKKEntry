package data;

import control.modifier.Discount;
import control.modifier.TicketType;
import control.utility.file.EntryFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static data.Entry.DataColumn.*;

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

    public int getAllFees() {
        int f = ticketType.getFees();
        for(Discount d : discountList){
            f -= d.getPrice();
        }
        return get(ENTER_DATE.ordinal()) != null ? f : 0;
    }

    /**
     * Enum for the vector data
     */
    enum DataColumn {
        ID(0,"ID"),
        NAME(1,"NÉV"),
        TYPE(2,"TÍPUS"),
        DISCOUNTS(3,"KEDVEZMÉNYEK"),
        ENTERED(4,"BELÉPETT"),
        ENTER_DATE(5,"BELÉPÉS"),
        LEAVE_DATE(6,"KILÉPÉS");
        private final int column;
        private final String name;
        DataColumn(int col, String name){column = col; this.name = name;}

        public String getName() { return name; }
    }

    public static Entry importEntry(String[] vector, EntryProfile profile) throws IOException {
        if(vector.length <= TYPE.column) throw new IOException("Kicsi");
        //Parsing string vector fields in order:
        Entry imported = new Entry(vector[ID.column], vector[NAME.column],
                profile.identifyTicketType(vector[TYPE.column]));
        int lastIndex = TYPE.column + 1;
        //Registring entry / leave dates if any
        try {
            if(vector.length > lastIndex){
                formatter.parse(vector[lastIndex]);
                imported.set(ENTER_DATE.column,vector[lastIndex++]);}
            if(vector.length > lastIndex){
                formatter.parse(vector[lastIndex]);
                imported.set(LEAVE_DATE.column,vector[lastIndex++]);
            }
        } catch (DateTimeParseException ignored){ }
        //Registrating discounts if any
        for (int i = lastIndex; i < vector.length; i++) {
            imported.applyDiscount(profile.identifyDiscountMeta(vector[i]));
        }
        return imported;
    }

    Boolean isEntered(){
        return (get(ENTER_DATE.column) != null && get(LEAVE_DATE.column) == null);
    }

    public void applyDiscount(Discount discount){
        if(discount == null) return;
        Discount disCopy = discountList.stream().filter(d -> d.equals(discount)).findAny().orElse(null);
        if(disCopy != null && discountList.contains(disCopy)) {
            discountList.remove(disCopy);
            System.out.println("[ENTRY "+ get(ID.column) +"]: Discount removed (" + disCopy.toString() + ")");
        } else {
            discountList.add(discount);
            System.out.println("[ENTRY "+ get(ID.column) +"]: Discount added (" + discount.toString() + ")");
        }
        set(DISCOUNTS.column,discountList.toString());
    }

    private Entry(String id, String name, TicketType type, List<Discount> discounts, String enter, String leave){
        setSize(DataColumn.values().length);
        add(ID.column,id);
        add(NAME.column,name);
        add(TYPE.column,type.toString());
        ticketType = type;
        if(discounts != null){
            discountList = discounts;
            add(DISCOUNTS.column,discountList.toString());
        }
        add(ENTER_DATE.column,enter);
        add(LEAVE_DATE.column,leave);
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
        set(ENTER_DATE.column,LocalDateTime.now().format(formatter));
        set(LEAVE_DATE.column,null);
    }

    /**
     * Makes the guest leave, creating / overwriting the time of leave
     */
    public void Leave(){
        set(LEAVE_DATE.column,LocalDateTime.now().format(formatter));
    }

    public String applyFilter(EntryFilter filter){
        StringBuilder w = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            w.append(filter.writeData(get(i), i));
        }
        return w.toString();
    }
}
