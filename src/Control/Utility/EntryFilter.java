//package Control.Utility;
//
//import Control.Entry;
//
//import javax.swing.filechooser.FileFilter;
//import java.io.File;
//
//import static Control.Entry.Member.*;
//import static Control.Entry.Member.M_ENTERED;
//
//public class EntryFilter extends ExtensionFilter implements ExportFilter{
//
//    public EntryFilter() {
//        super(new String[]{"csv","txt","log"}, "MKK Beléptetési tábla");
//    }
//
//    public enum FilterType {
//        DEFAULT,
//        TOMBOLA
//    }
//
//    public static String[] filterTypes = {"Alapméretezett","Tombola"};
//
//    public static final String separator = ",";
//
//    public static FilterType parseFilterType(String type){
//        switch (type){
//            default:
//            case "Alapméretezett":
//                return FilterType.DEFAULT;
//            case "Tombola":
//                return FilterType.TOMBOLA;
//        }
//    }
//
//    @Override
//    public String applyFilter(Entry entry) {
//
//        return String.valueOf(entry.getValue(M_UID.ordinal())) +
//                separator +
//                entry.getValue(M_NAME.ordinal()) +
//                separator +
//                entry.getValue(M_TYPE.ordinal()) +
//                separator +
//                entry.getValue(M_ENTRY.ordinal()) +
//                separator +
//                entry.getValue(M_LEAVE.ordinal()) +
//                separator +
//                entry.getValue(M_ENTERED.ordinal());
//    }
//
//}
