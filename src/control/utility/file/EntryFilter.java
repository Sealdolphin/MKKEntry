package control.utility.file;


public class EntryFilter extends ExtensionFilter {

    public EntryFilter() {
        super(new String[]{"csv","txt","log"}, "MKK Beléptetési tábla");
    }

    private static final String separator = ",";

    public void applyFilter(){

    }

    public String[] parseEntry(String line){
        return line.split(separator);
    }

}
