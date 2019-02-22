package control.utility.network;


import control.ProgramStateListener;
import control.utility.BarcodeListener;

import java.io.*;
import java.net.Socket;

public class BasicNetworkMessenger implements NetworkMessenger {

    private Socket client;
    private BarcodeListener networkListener;

    public BasicNetworkMessenger(Socket clientSocket, BarcodeListener listener){
        client = clientSocket;
        networkListener = listener;
    }

    @Override
    public void sendMessage(String msg) throws IOException {
        new PrintWriter(client.getOutputStream(),true).println(msg);
        System.out.println("SENT TO SERVER: " + msg);
    }

    @Override
    public void receiveMessage() throws IOException {
        String input;
        BufferedReader netReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        while ((input = netReader.readLine()) != null) {
            System.out.println("INPUT FROM CLIENT: " + input);
            networkListener.readBarCode(input);
        }
    }
}
