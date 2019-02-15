package control.utility.file;



public class EntryFilter extends ExtensionFilter {

    private String name;

    public EntryFilter(String name, int[] indeces) {
        super(new String[]{"csv","txt","log"}, "MKK Beléptetési tábla");
        this.name = name;
        this.indeces = indeces;
    }

    @Override
    public String toString(){
        return name;
    }

    private static final String separator = ",";

    public String[] parseEntry(String line){
        return line.split(separator);
    }

    private int[] indeces;

    public String writeData(String s, int index) {
        if(indeces == null) return "";
        for (int indece : indeces) {
            if (indece == index) return s;
        }
        return "";
    }
}
