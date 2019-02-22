package control.utility.network;

import control.utility.BarcodeListener;

import java.io.IOException;
import java.net.ServerSocket;

public class BasicNetworkServer implements NetworkMessenger{

    private BasicNetworkMessenger client;
    private ServerSocket server;

    public BasicNetworkServer(int port, BarcodeListener listener) throws IOException {
        server = new ServerSocket(port);
        new Thread(() -> {
            try {
                client = new BasicNetworkMessenger(server.accept(),listener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void sendMessage(String msg) throws IOException {
        if(client != null)
            client.sendMessage(msg);
    }

    @Override
    public void receiveMessage() throws IOException {
        if(client != null)
            client.receiveMessage();
    }
}
