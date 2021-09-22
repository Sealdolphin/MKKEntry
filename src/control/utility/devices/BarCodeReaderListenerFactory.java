package control.utility.devices;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BarCodeReaderListenerFactory {

    private static final HashMap<SerialPort, BarcodeReader> deviceMap = new HashMap<>();

    public static List<String> refreshSerialPorts() {
        System.out.println("[INFO]: Scanning for ports...");
        return Arrays.stream(SerialPort.getCommPorts()).map(SerialPort::getSystemPortName).collect(Collectors.toList());
    }

    /**
     * Listens on a serial port
     * @param serialPortName the specified serial port (not necessarily active)
     */
    public static void connectSerialPort(String serialPortName) {
        SerialPort readingPort = SerialPort.getCommPort(serialPortName);
        if(readingPort.openPort()) {
            System.out.println("[INFO]: Connected to " + readingPort);
            BarcodeReader reader = new BarcodeReader();
            deviceMap.put(readingPort, reader);
            readingPort.addDataListener(reader);  //Add the reader object to the serial port
        }
    }

    /**
     * Generates a reader device, which listens to all the ports
     * @param action the action it executes after reading
     * @param message the message the dialog shows to the user
     * @param visible if it's true then a dialog is shown with the message
     */
    public static void generateReader(Consumer<String> action, String message, boolean visible) {
        deviceMap.forEach((port, reader) -> reader.addListener(new ReaderDevice(action,message,visible,port)));
        //FIXME: here should open the dialog!!
    }
}
