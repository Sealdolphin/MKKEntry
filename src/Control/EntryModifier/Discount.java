package Control.EntryModifier;

import org.json.simple.JSONObject;

public class Discount {
    private String name;
    private String imagePath;
    private String label;
    private String metaData;
    private int discount;

    private Discount(String name, String imagePath, String label, String meta, int price){
        this.name = name;
        this.imagePath = imagePath;
        this.label = label;
        metaData = meta;
        int discount = price;
    }

    public static Discount parseDiscountFromJson(JSONObject jsonObject) {
        String name = jsonObject.get("name").toString();
        String image = jsonObject.get("imgPath").toString();
        String label = jsonObject.get("label").toString();
        String meta = jsonObject.get("meta").toString();
        int price = Integer.parseInt(jsonObject.get("discount").toString());
        return new Discount(name,image,label,meta,price);
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
