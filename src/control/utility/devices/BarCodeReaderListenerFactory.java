package control.utility.devices;

import com.fazecast.jSerialComm.SerialPort;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class BarCodeReaderListenerFactory {

    private static HashMap<SerialPort, BarcodeReader> deviceMap = new HashMap<>();

    /**
     * Listens on a serial port
     * @param readingPort the specified serial port (not necessarily active)
     */
    public static void connectSerialPort(SerialPort readingPort) {
//        System.out.println("Added main object to port: " + readingPort.getSystemPortName());
        BarcodeReader reader = new BarcodeReader();
        deviceMap.put(readingPort, reader);
        readingPort.addDataListener(reader);  //Add the reader object to the serial port
    }

    /**
     * Generates a reader device, which listens to all the ports
     * @param action the action it executes after reading
     * @param message the message the dialog shows to the user
     * @param visible if it's true then a dialog is shown with the message
     */
    public static void generateReader(Consumer<String> action, String message, boolean visible) {
        ReaderDevice device = new ReaderDevice(action,message,visible);
        deviceMap.forEach((port, reader) -> reader.addListener(device));

    }
}
