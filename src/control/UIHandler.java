package control;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class UIHandler {

    public static String uiVersion = "v1.3";

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
        switch (map){
            default:
            case "UI":
                return uiStrings.get(key);
            case "ERR":
                return uiErrors.get(key);
            case "MSG":
                return uiMsg.get(key);
        }
    }

    private HashMap<String,String> uiStrings = new HashMap<>();
    private HashMap<String,String> uiErrors = new HashMap<>();
    private HashMap<String,String> uiMsg = new HashMap<>();

}
