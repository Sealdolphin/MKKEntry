package data.entry;

import control.utility.file.EntryFilter;
import data.entryprofile.EntryProfile;
import data.modifier.Discount;
import data.modifier.TicketType;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static control.Application.uh;
import static data.entry.DataColumn.*;

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

    /**
     * A Time formatter for the proper time format
     */
    static private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

    private TicketType ticketType;
    private List<Discount> discountList;

    public Entry(String id, Entry other) {
        this(id, other.get(NAME.ordinal()), other.ticketType, other.discountList, other.get(ENTER_DATE.ordinal()), other.get(LEAVE_DATE.ordinal()));
    }

    public Entry(String id, String name, TicketType type){
        this(id, name, type, new ArrayList<>(),null,null);
    }

    private Entry(String id, String name, TicketType ticketType, List<Discount> discountList, String enter, String leave){
        setSize(DataColumn.values().length);

        this.ticketType = ticketType;
        this.discountList = discountList;

        int entryPrice = getAllFees();

        set(ID, id);
        set(NAME, name);
        set(TYPE, ticketType.getId());
        set(PRICE, String.valueOf(entryPrice));
        set(DISCOUNTS, this.discountList.toString());
        set(ENTER_DATE, enter);
        set(LEAVE_DATE, leave);
    }

    public int getAllFees() {
        if (!ticketType.isStatisticsEnabled()) {
            return 0;
        }

        int entryPrice = ticketType.getPrice();

        for (Discount discount : discountList) {
            if (discount.isFree()) return 0;
            entryPrice -= discount.getDiscount();
        }

        return entryPrice;
    }

    private void set(DataColumn column, String data) {
        set(column.ordinal(), data);
    }

    public String getID() {
        return get(ID.ordinal());
    }

    /**
     * Parses an array of string to an Entry using the current profile for details.
     * This function assumes that the array of strings is ordered correctly.
     * Correct order: ID, NAME, TYPE, [ENTER_DATE, LEAVE_DATE, DISCOUNT_1, DISCOUNT_2, ...]
     * Example string: "1234, Example Name, Basic, , , FOOD_SALE"
     * @param importVector the array of strings
     * @param profile the current parsing profile
     * @return a new parsed Entry with the correct attributes
     * @throws IOException if parsing fails
     */
    public static Entry importEntry(String[] importVector, EntryProfile profile) throws IOException {
        if(importVector.length <= TYPE.ordinal()){
            throw new IOException("Nincs megadva jegytípus");
        }

        // Parsing string vector fields in order:
        Entry importedEntry = new Entry(
                importVector[ID.ordinal()],
                importVector[NAME.ordinal()],
                profile.identifyTicketType(importVector[TYPE.ordinal()])
        );
        int lastIndex = TYPE.ordinal() + 1;

        // Registring entry / leave dates if any
        try {
            if(importVector.length > lastIndex) {
                formatter.parse(importVector[lastIndex]);
                importedEntry.set(ENTER_DATE, importVector[lastIndex++]);}
            if(importVector.length > lastIndex) {
                formatter.parse(importVector[lastIndex]);
                importedEntry.set(LEAVE_DATE, importVector[lastIndex++]);
            }
        } catch (DateTimeParseException ignored){

        }

        // Registrating discounts if any
        for (int i = lastIndex; i < importVector.length; i++) {
            importedEntry.applyDiscount(profile.identifyDiscount(importVector[i]));
        }

        return importedEntry;
    }

    public void applyDiscount(Discount discount){
        if(discount == null) {
            return;
        }

        if(discountList.contains(discount)) {
            discountList.remove(discount);
            System.out.println("[ENTRY "+ get(ID.ordinal()) +"]: Discount removed (" + discount + ")");
        } else {
            discountList.add(discount);
            System.out.println("[ENTRY "+ get(ID.ordinal()) +"]: Discount added (" + discount + ")");
        }
        set(DISCOUNTS, discountList.toString());
    }

    boolean isEntered(){
        return (get(ENTER_DATE.ordinal()) != null && get(LEAVE_DATE.ordinal()) == null);
    }

    public Boolean hasEntered() {
        return (get(ENTER_DATE.ordinal()) != null);
    }

    public Boolean hasLeft() {
        return (get(LEAVE_DATE.ordinal()) != null);
    }


    @SuppressWarnings("unchecked")
    public JSONObject getJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("id",elementData[ID.ordinal()]);
        obj.put("entry",elementData[ENTER_DATE.ordinal()]);
        obj.put("leave",elementData[LEAVE_DATE.ordinal()]);
        return obj;
    }

    List<Discount> getDiscounts(){
        return discountList;
    }

    /**
     * Enters the guest, creating / overwriting the time of entry
     */
    public void doEnter() throws IOException {
        if (isEntered()) throw new IOException(uh.getUIStr("ERR","DUPLICATE"));
        set(ENTER_DATE, LocalDateTime.now().format(formatter));
        set(LEAVE_DATE, null);
    }

    /**
     * Makes the guest leave, creating / overwriting the time of leave
     */
    public void doLeave() throws IOException {
        if(!isEntered()) throw new IOException(uh.getUIStr("ERR","NO_MATCH"));
        set(LEAVE_DATE, LocalDateTime.now().format(formatter));
    }

    public String applyFilter(EntryFilter filter){
        StringBuilder w = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            w.append(filter.writeData(get(i), i));
            w.append(EntryFilter.separator);
        }
        return w.toString();
    }

    public void copyEntry(Entry other) {
        ticketType = other.ticketType;
        discountList = new ArrayList<>(other.discountList);
        for(int i = 0; i < values().length; i++) {
            set(i, other.get(i));
        }
    }
}
