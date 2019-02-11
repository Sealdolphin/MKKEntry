package control.utility.file;


import data.Entry;

public class EntryFilter extends ExtensionFilter {

    public EntryFilter() {
        super(new String[]{"csv","txt","log"}, "MKK Beléptetési tábla");
    }

    private static final String separator = ",";

    public String applyFilter(Entry data){

        return "";
    }

    public String[] parseEntry(String line){
        return line.split(separator);
    }

}
