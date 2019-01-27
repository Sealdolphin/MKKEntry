package control;

import control.modifier.Discount;
import control.modifier.TicketType;

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

    public static String[] columnNames = {"ID","NÉV","TÍPUS","KEDVEZMÉNYEK","BELÉPETT","BELÉPÉS","KILÉPÉS"};

    public Boolean isEntered(){
        return (get(5) != null && get(6) == null);
    }

    private Entry(String id, String name, TicketType type, List<Discount> discounts, String enter, String leave){
        setSize(7);
        add(0,id);
        add(1,name);
        add(2,type.toString());
        ticketType = type;
        if(discounts != null){
            add(3,discounts.toString());
            discountList = discounts;
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

//
//    private static List<Entry> parseEntryImportFile(File file, EntryProfile profile) throws ParseException{
//        List<Entry> importList = new ArrayList<>();
//        int lineNumber = 0;
//        try {
//            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//
//            boolean eof = false;
//            while (!eof){
//                try {
//                    lineNumber++;
//                    String line = fileReader.readLine();
//                    if (line == null) {
//                        eof = true;
//                        continue;
//                    }
//                    importList.add(createEntryFromString(line, profile, lineNumber));
//                } catch (ParseException ex){
//                    Object[] options = new Object[]{"Következő","Megszakítás"};
//                    int result = JOptionPane.showOptionDialog(new JFrame(),
//                            uh.getUIStr("ERR","IMPORT_PARSE_FAIL") +"\n" +
//                                    uh.getUIStr("ERR","POSITION") + ": " + ex.getErrorOffset() + "\n" +
//                                    uh.getUIStr("ERR","DETAILS") + ":\n" + ex.getMessage(),
//                            uh.getUIStr("ERR","HEADER"),JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE,null,options,options[0]);
//                    System.out.println(ex.getMessage());
//                    if(result == JOptionPane.NO_OPTION) throw new ParseException(uh.getUIStr("ERR","USER_ABORT"),lineNumber);
//                }
//            }
//
//        } catch (FileNotFoundException fnf) {
//            throw new ParseException(uh.getUIStr("ERR","FILE_MISSING"),lineNumber);
//        }  catch (IOException io) {
//            throw new ParseException(uh.getUIStr("ERR","IO_FAIL") + ":\n" + io.getMessage(),lineNumber);
//        }
//
//        if(importList.isEmpty()){
//            JOptionPane.showMessageDialog(new JFrame(),
//                    uh.getUIStr("ERR","FILE_EMPTY"),
//                    uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
//        }
//
//
//        return importList;
//    }
//
//    /**
//     * Creates a new Entry class from an input string
//     * The input string must follow the default Filter format which is:
//     * 0: UID - required,
//     * 1: NAME - required,
//     * 2: TYPE_NAME - required,
//     * 3: ENTRY_DATE - optional,
//     * 4: LEAVE_DATE - optional
//     * @param entryString the input string
//     * @param profile the currently active profile
//     * @param offset the line where the input is found in a file
//     * @return a new Entry with correct attributes
//     * @throws ParseException if the parsing of the string fails
//     */
//    private static Entry createEntryFromString(String entryString, EntryProfile profile, int offset) throws ParseException{
//        String[] props = entryString.split(separator);
//        String uid, name,enter = null ,leave = null;
//        boolean entered = false;
//        TicketType type;
//
//        //Throw it if the array is empty
//        if(props.length < 1) throw new ParseException("A rekord sérült, vagy hibás",offset);
//
//        try {
//            uid = profile.validateCode(props[0]);
//        } catch (IOException e) {
//            throw new ParseException(e.getMessage(),offset);
//        }
//
//        //Looking for required fields
//        //Alert if does not meet the number of fields required
//        if(props.length < 3) {
//            throw new ParseException("A rekordból hiányoznak argumentumok",offset);
//            //TODO: needs implementing
//            //fillDefault = fillOptionIsDefault(offset);
//        }
//
//        name = props[1];
//        type = profile.identifyTicketType(props[2]);
//
//        //Looking for optional fields
//        //Setting ENTRY date (optional)
//        if(props.length > 3) {
//            enter = props[3];
//            entered = true;
//        }
//        //Setting leave date (optional)
//        if(props.length > 4) {
//            leave = props[4];
//            entered = false;
//        }
//
//        return new Entry(uid,name,type,null,null,null);
//    }


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
    void Leave(){
        set(6,LocalDateTime.now().format(formatter));
    }
//
//    public Entry(TicketType type){
//        uniqueId = "UNDEFINED";
//        entered = false;
//        entryStamp = LocalDateTime.now().format(formatter);
//        leaveStamp = LocalDateTime.now().format(formatter);
//        name = "PlaceholderText";
//        ticketType = type;
//    }
//
//    static String[] getColumnNames(){
//        return new String[]{"ID","NÉV","JEGYTÍPUS","BELÉPETT","BELÉPÉS","KILÉPÉS"};
//    }
//
//    public Object getValue(int columnIndex) {
//        Member propertyColumn = Member.values()[columnIndex];
//        switch (propertyColumn){
//            default: return null;
//            case M_UID: return uniqueId;
//            case M_NAME: return name;
//            case M_TYPE: return ticketType.getName();
//            case M_ENTERED: return entered;
//            case M_ENTRY: return entryStamp;
//            case M_LEAVE: return leaveStamp;
//        }
//    }
//
}
