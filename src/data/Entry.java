package data;

import control.modifier.Discount;
import control.modifier.TicketType;
import control.utility.file.EntryFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static control.Application.uh;
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

    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("id",elementData[ID.ordinal()]);
        obj.put("entry",elementData[ENTER_DATE.ordinal()]);
        obj.put("leave",elementData[LEAVE_DATE.ordinal()]);
        return obj;
    }

    @Deprecated
    public boolean hasInvalidID() {
        try {
            int id = Integer.parseInt(get(ID.ordinal()));
            return (id > 9000);
        } catch (NumberFormatException ex){
            System.out.println("ENTRY: ID is invalid: " + get(ID.ordinal()));
            System.out.println("ENTRY: NumberFormatException: " + ex.getMessage());
            return true;
        }
    }

    @Deprecated
    public void Reset() {
        set(ENTERED.ordinal(),null);
        set(ENTER_DATE.ordinal(),null);
        set(LEAVE_DATE.ordinal(),null);
        discountList.clear();
    }

    /**
     * Enum for the vector data
     */
    enum DataColumn {
        ID(0,"ID"),
        NAME(1,"NÉV"),
        TYPE(2,"TÍPUS"),
        PRICE(3,"HOZZÁJÁRULÁS"),
        DISCOUNTS(4,"KEDVEZMÉNYEK"),
        ENTERED(5,"BELÉPETT"),
        ENTER_DATE(6,"BELÉPÉS"),
        LEAVE_DATE(7,"KILÉPÉS");
        private final int column;
        private final String name;
        DataColumn(int col, String name){column = col; this.name = name;}

        public String getName() { return name; }
    }

    /**
     * Parses an array of string to an Entry using the current profile for details.
     * This function assumes that the array of strings are ordered correctly.
     * @param vector the array of strings
     * @param profile the current parsing profile
     * @return a new parsed Entry with the correct attributes
     * @throws IOException if parsing fails
     */
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

        add(PRICE.column,((Integer)getAllFees()).toString());
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
    public void Enter() throws IOException {
        if (isEntered()) throw new IOException(uh.getUIStr("ERR","DUPLICATE"));
        set(ENTER_DATE.column,LocalDateTime.now().format(formatter));
        set(LEAVE_DATE.column,null);
    }

    /**
     * Makes the guest leave, creating / overwriting the time of leave
     */
    public void Leave() throws IOException {
        if(!isEntered()) throw new IOException(uh.getUIStr("ERR","NO_MATCH"));
        set(LEAVE_DATE.column,LocalDateTime.now().format(formatter));
    }

    public String applyFilter(EntryFilter filter){
        StringBuilder w = new StringBuilder();
        if(filter.indeces != null)
            for (int i = 0; i < filter.indeces.length; i++) {
                w.append(get(filter.indeces[i])).append(EntryFilter.separator);
            }
        else {
            w.append(get(ID.ordinal())).append(EntryFilter.separator).append(get(NAME.ordinal())).append(EntryFilter.separator).append(get(TYPE.ordinal())).append(EntryFilter.separator)
                    .append(get(ENTER_DATE.ordinal())).append(EntryFilter.separator).append(get(LEAVE_DATE.ordinal())).append(EntryFilter.separator);
            for(Discount discount : discountList)
                w.append(discount.getMeta()).append(EntryFilter.separator);
        }
        return w.toString();
    }
}
