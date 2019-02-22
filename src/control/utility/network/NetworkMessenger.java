package control.utility.network;

import java.io.IOException;

public interface NetworkMessenger {
    void sendMessage(String msg) throws IOException;
    void receiveMessage() throws IOException;
}
