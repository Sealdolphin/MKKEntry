package control;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class UIHandler {

    public static String uiVersion = "v1.4";

    void refreshOptions(JSONObject options) throws IOException {
        if(!options.get("version").toString().equals(uiVersion))
            throw new IOException("Version mismatch. Correct API version: " + uiVersion);

        JSONObject map = options.getJSONObject("ui");
        for (String key : map.keySet() ) {
            uiStrings.put(key, map.getString(key));
        }
        map = options.getJSONObject("error");
        for (String key : map.keySet()) {
            uiErrors.put(key, map.getString(key));
        }
        map = options.getJSONObject("msg");
        for (String key : map.keySet()) {
            uiMsg.put(key, map.getString(key));
        }

    }

    public String getUIStr(String map,String key){
        return switch (map) {
            default -> uiStrings.get(key);
            case "ERR" -> uiErrors.get(key);
            case "MSG" -> uiMsg.get(key);
        };
    }

    private final HashMap<String,String> uiStrings = new HashMap<>();
    private final HashMap<String,String> uiErrors = new HashMap<>();
    private final HashMap<String,String> uiMsg = new HashMap<>();

}
