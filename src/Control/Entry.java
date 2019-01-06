package Control;

import Control.EntryModifier.Discount;
import Control.EntryModifier.TicketType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * The class representing an entry (a guest entering) to the event
 */
public class Entry {

    public enum Member{
        M_UID,
        M_NAME,
        M_ENTERED,
        M_ENTRY,
        M_LEAVE
    }

    /**
     * A unique ID
     */
    private final String uniqueId;

    /**
     * A Time formatter for the proper time format
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

    /**
     * The name of the guest
     */
    private String name;

    private boolean foodSale = false;

    private TicketType ticketType;

    /**
     * Indicator for the guest to check whether they are entered yet
     */
    private boolean entered = false;

    /**
     * The Time of entering
     */
    private String entryStamp;

    /**
     * The Time of leave
     */
    private String leaveStamp;

    /**
     * Creates a new guest with a unique ID number
     * @param id the unique number
     */
    public Entry(String id){
        uniqueId = id;
        name = "Külsős Belépő";
    }

    public Entry(String id, String name, String entry, String leave, boolean is_entered){
        uniqueId = id;
        this.name = name;
        entered = is_entered;
        entryStamp = entry;
        leaveStamp = leave;
    }

    /**
     * Enters the guest, creating / overwriting the time of entry
     */
    void Enter(){
        entered = true;
        entryStamp = LocalDateTime.now().format(formatter);
        leaveStamp = null;
    }

    /**
     * Makes the guest leave, creating / overwriting the time of leave
     */
    void Leave(){
        entered = false;
        leaveStamp = LocalDateTime.now().format(formatter);
    }

    public Entry(){
        uniqueId = "DMYID";
        entered = false;
        entryStamp = LocalDateTime.now().format(formatter);
        leaveStamp = LocalDateTime.now().format(formatter);
        name = "PlaceholderText";
    }

    static String[] getColumnNames(){
        return new String[]{"ID","NÉV","BELÉPETT","BELÉPÉS","KILÉPÉS"};
    }

    public Object getValue(int columnIndex) {
        Member propertyColumn = Member.values()[columnIndex];
        switch (propertyColumn){
            default: return null;
            case M_UID: return uniqueId;
            case M_NAME: return name;
            case M_ENTERED: return entered;
            case M_ENTRY: return entryStamp;
            case M_LEAVE: return leaveStamp;
        }
    }

    public void toggleDiscount(Discount discount){

    }
}
