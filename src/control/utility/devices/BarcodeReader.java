package control.utility.devices;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.util.ArrayList;
import java.util.List;

@Deprecated
/**
 * The Reader device
 * Listens for different entries
 * Communicates through Serial port
 */
public class BarcodeReader implements SerialPortMessageListener {

    /**
     * The raw lastReadData read through the serial port in bytes
     */
    @Deprecated
    private ArrayList<Byte> raw_data = new ArrayList<>();
    /**
     * The read lastReadData in a String
     */
    private String lastReadData;

    private final List<BarcodeReaderListener> listeners = new ArrayList<>();

    void addListener(BarcodeReaderListener l){
        listeners.add(l);
    }

    @SuppressWarnings("unused")
    public void removeListener(BarcodeReaderListener l){
        listeners.remove(l);
    }

    /**
     * Implemented from SerialDataListener
     * Sets the receiving mode of the serial port
     * See implementation at https://fazecast.github.io/jSerialComm/javadoc/com/fazecast/jSerialComm/SerialPortDataListener.html
     * @return the code of the desired event
     */
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    /**
     * Implemented from SerialDataListener
     * Reads the incoming bytes from the device
     * See implementation at https://fazecast.github.io/jSerialComm/javadoc/com/fazecast/jSerialComm/SerialPortDataListener.html
     * @param serialPortEvent the event of the device
     */
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        byte[] bytes = serialPortEvent.getReceivedData();

        //Build string from Byte array
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append((char)b);
        }
        lastReadData = builder.toString().trim();
        //Alert all listeners

        for (BarcodeReaderListener controller : listeners) {
            controller.readBarCode(lastReadData);
        }

    }

    /**
     * Returns the last read data of the device
     * @return the last read data
     */
    @Deprecated
    public String getLastReadData(){
        return lastReadData;
    }

    @Override
    public byte[] getMessageDelimiter() {
        return new byte[]{'\n'};
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        return true;
    }
}
