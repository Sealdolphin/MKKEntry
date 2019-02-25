package control.utility.network;

import control.AppController;
import data.Entry;
import data.EntryProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;

public class NetworkController {

    private boolean onlineMode;
    private EntryProfile profile;
    private NetworkMessenger socketMessenger;
    private AppController controller;

    public NetworkController(EntryProfile parsingProfile, AppController controller, String host, int port) throws IOException {
        profile = parsingProfile;
        this.controller = controller;
        socketMessenger = new BasicNetworkMessenger(new Socket(host,port));
        onlineMode = true;
        //Starting receiving thread
        Thread receiveThread = new Thread(() -> {
            while (onlineMode) {
                try {
                    receiveDataUpdate(socketMessenger.receiveMessage());
                } catch (IOException e) {
                    System.out.println("ERROR: Couldn't receive socket message!");
                    System.out.println("DETAILS: " + e.getMessage());
                    onlineMode = false;
                }
            }
        });
        receiveThread.start();
    }

//    public void close(){
//        onlineMode = false;
//    }

    private void receiveDataUpdate(String entryData) {
        try {
            StringBuilder vectorBuilder = new StringBuilder();
            JSONObject entryObject = (JSONObject) new JSONParser().parse(entryData);
            String id = (String) entryObject.get("id");
            vectorBuilder.append(id).append(",");
            vectorBuilder.append((String) entryObject.get("entry")).append(",");
            vectorBuilder.append((String) entryObject.get("leave")).append(",");
            JSONArray discounts = (JSONArray) entryObject.get("discounts");
            for (Object discountObject : discounts) {
                vectorBuilder.append(discountObject).append(",");
            }
            String[] vector = vectorBuilder.toString().split(",");
            Entry newData = Entry.importEntry(vector,profile);
            controller.updateEntry(id,newData);

        } catch (ParseException e) {
            System.out.println("ERROR: Couldn't parse socket message!");
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't create Entry from parsed data!");
            System.out.println("DETAILS: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void updateData(Entry data) throws IOException {
        JSONObject refreshMsg = new JSONObject();
        refreshMsg.put("command","update");
        refreshMsg.put("data",data.getJsonObject());
        socketMessenger.sendMessage(refreshMsg.toString());
    }

}
