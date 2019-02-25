package control.utility.network;

import java.io.*;
import java.net.Socket;

public class BasicNetworkMessenger implements NetworkMessenger {

    private Socket client;

    BasicNetworkMessenger(Socket clientSocket){
        client = clientSocket;
    }

    @Override
    public void sendMessage(String msg) throws IOException {
        new PrintWriter(client.getOutputStream(),true).println(msg);
        System.out.println("SENT TO SERVER: " + msg);
    }

    @Override
    public String receiveMessage() throws IOException {
        String input = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
        System.out.println("RECEIVED FROM SERVER: " + input);
        return input;
    }
}
