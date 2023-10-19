package control;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class UIHandler {

    public static String uiVersion = "v1.4";

    void refreshOptions(JSONObject options) throws IOException {
        if(!options.get("version").toString().equals(uiVersion))
            throw new IOException("Version mismatch. Correct API version: " + uiVersion);

        JSONObject map = (JSONObject) options.get("ui");
        for (Object key : map.keySet()) {
            uiStrings.put(key.toString(),map.get(key).toString());
        }
        map = (JSONObject) options.get("error");
        for (Object key : map.keySet()) {
            uiErrors.put(key.toString(),map.get(key).toString());
        }
        map = (JSONObject) options.get("msg");
        for (Object key : map.keySet()) {
            uiMsg.put(key.toString(),map.get(key).toString());
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
