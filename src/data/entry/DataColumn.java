package data.entry;

public enum DataColumn {
    ID("ID"),
    NAME("NÉV"),
    TYPE("TÍPUS"),
    PRICE("FIZETENDŐ"),
    DISCOUNTS("KEDVEZMÉNYEK"),
    ENTERED("BELÉPETT"),
    ENTER_DATE("BELÉPÉS"),
    LEAVE_DATE("KILÉPÉS");

    private final String name;
    DataColumn(String name){this.name = name;}
    public String getName() { return name; }
}
