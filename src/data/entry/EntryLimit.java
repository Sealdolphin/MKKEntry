package data.entry;

public enum EntryLimit {
    ONCE("Egyszeri"),
    NO_LIMIT("Korlátlan"),
    CUSTOM("Egyéni");

    private final String name;

    EntryLimit(String name){this.name = name;}
    @Override
    public String toString(){ return name; }
}