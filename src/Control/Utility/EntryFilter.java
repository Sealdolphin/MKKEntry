package Control.Utility;

import Control.Entry;

import javax.swing.filechooser.FileFilter;
import java.io.File;

import static Control.Entry.Member.*;
import static Control.Entry.Member.M_ENTERED;

public class EntryFilter extends FileFilter implements ExportFilter{

    private static String[] validExtensions = {
            "txt",
            "csv",
            "log"
    };

    public enum FilterType {
        DEFAULT,
        TOMBOLA
    }

    public static final String separator = ",";

    public static FilterType parseFilterType(String type){
        switch (type){
            default:
            case "Alapméretezett":
                return FilterType.DEFAULT;
            case "Tombola":
                return FilterType.TOMBOLA;
        }
    }

    public static String[] filterTypes = {"Alapméretezett","Tombola"};


    @Override
    public boolean accept(File f) {
        if(f.isDirectory()){
            return true;
        }

        String extension = getExtension(f);
        if(extension != null){
            for (String ext :
                    validExtensions) {
                if (extension.equals(ext)){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "MKK Beléptetés tábla";
    }

    private String getExtension(File file){
        String filename = file.getName();
        String ext = null;

        int index = filename.lastIndexOf('.');
        if(index > 0 && index < filename.length() - 1)
            ext = filename.substring(index + 1).toLowerCase();

        return ext;
    }

    @Override
    public String applyFilter(Entry entry) {

        return String.valueOf(entry.getValue(M_UID.ordinal())) +
                separator +
                entry.getValue(M_NAME.ordinal()) +
                separator +
                entry.getValue(M_ENTRY.ordinal()) +
                separator +
                entry.getValue(M_LEAVE.ordinal()) +
                separator +
                entry.getValue(M_ENTERED.ordinal());
    }

}
