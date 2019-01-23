package control.utility;

import control.ProgramStateListener;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Reader device
 * Listens for different entries
 * Communicates through Serial port
 */
public class BarcodeReader implements SerialPortDataListener {

    /**
     * The raw lastReadData read through the serial port in bytes
     */
    private ArrayList<Byte> raw_data = new ArrayList<>();
    /**
     * The read lastReadData in a String
     */
    private String lastReadData;

    private List<ProgramStateListener> controllers = new ArrayList<>();

    public void addListener(ProgramStateListener l){
        controllers.add(l);
    }


    @SuppressWarnings("unused")
    public void removeListener(ProgramStateListener l){
        controllers.remove(l);
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
        byte bytes[] = serialPortEvent.getReceivedData();
        Byte byteObjs[] = new Byte[bytes.length];
        int i = 0;
        for (byte b : bytes)
            byteObjs[i++] = b;

        Collections.addAll(raw_data,byteObjs);

        checkForFlush();
    }

    /**
     * Checks the read lastReadData if it is at the end
     * If it is, then sets the last lastReadData to the read bytes (as a String)
     */
    private void checkForFlush() {
        //Check for Line Endings (Character Code = 10)
        /*
            The ASCII character code for line ending (line feed = LF)
        */
        int LINE_FEED = 10;
        if(raw_data.get(raw_data.size() - 1) == LINE_FEED){
            StringBuilder builder = new StringBuilder();
            for (Byte b :
                    raw_data) {
                byte cb = b;
                builder.append((char)cb);
            }
            lastReadData = builder.toString().trim();

            //Flush the stream
            raw_data.clear();

            //Alert all controllers
            for (ProgramStateListener controller :
                    controllers) {
                controller.readBarCode(lastReadData);
            }
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
}
