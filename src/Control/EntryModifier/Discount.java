package Control.EntryModifier;

public class Discount {
    private String name;
    private String imagePath;
    private String label;
    private String metaData;
    private int discount;

    public Discount(String name, String imagePath, String label, String meta, int price){
        this.name = name;
        this.imagePath = imagePath;
        this.label = label;
        metaData = meta;
        int discount = price;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return imagePath;
    }

    public String getLabel() {
        return label;
    }

    public String getMeta() {
        return metaData;
    }
}
