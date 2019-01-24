package control.utility;


import java.util.List;

public abstract class EntryFilter extends ExtensionFilter {

    EntryFilter() {
        super(new String[]{"csv","txt","log"}, "MKK Beléptetési tábla");
    }

    public static final String separator = ",";

    private List<String> filters;

    public void applyFilter(){

    }

}
