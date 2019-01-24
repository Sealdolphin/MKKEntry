//package Control;
//
//import Control.EntryModifier.TicketType;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//
///**
// * The class representing an entry (a guest entering) to the event
// */
//public class Entry {
//
//    public enum Member{
//        M_UID,
//        M_NAME,
//        M_TYPE,
//        M_ENTERED,
//        M_ENTRY,
//        M_LEAVE
//    }
//
//    /**
//     * A unique ID
//     */
//    private final String uniqueId;
//
//    /**
//     * A Time formatter for the proper time format
//     */
//    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
//
//    /**
//     * The name of the guest
//     */
//    private String name;
//
//    private TicketType ticketType;
//
//    /**
//     * Indicator for the guest to check whether they are entered yet
//     */
//    private boolean entered = false;
//
//    /**
//     * The Time of entering
//     */
//    private String entryStamp;
//
//    /**
//     * The Time of leave
//     */
//    private String leaveStamp;
//
//    /**
//     * Creates a new guest with a unique ID number
//     * @param id the unique number
//     */
//    public Entry(String id, TicketType type){
//        uniqueId = id;
//        name = "Külsős Belépő";
//        ticketType = type;
//    }
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
//        return new Entry(uid,type,name,enter,leave,entered);
//    }
//
//    public Entry(String id,TicketType type, String name, String entry, String leave, boolean is_entered){
//        uniqueId = id;
//        this.name = name;
//        ticketType = type;
//        entered = is_entered;
//        entryStamp = entry;
//        leaveStamp = leave;
//    }
//
//    /**
//     * Enters the guest, creating / overwriting the time of entry
//     */
//    void Enter(){
//        entered = true;
//        entryStamp = LocalDateTime.now().format(formatter);
//        leaveStamp = null;
//    }
//
//    /**
//     * Makes the guest leave, creating / overwriting the time of leave
//     */
//    void Leave(){
//        entered = false;
//        leaveStamp = LocalDateTime.now().format(formatter);
//    }
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
//}
