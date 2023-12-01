package control.utility.devices;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import view.main.panel.dialog.BarcodeReaderDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BasicSerialPortReader implements BarcodeReaderListener, SerialPortDataListener {

    private final SerialPort serialPort;

    private final Thread readAction;

    private final BarcodeReaderDialog view;

    private final List<Consumer<String>> barcodeReceivers = new ArrayList<>();

    public BasicSerialPortReader(BarcodeReaderDialog view) {
        serialPort = SerialPort.getCommPorts()[1];
        serialPort.addDataListener(this);
        readAction = new Thread(view::waitIdle);
        this.view = view;
        view.addBarcodeListener(this);
    }

    public void addReceiver(Consumer<String> receiver) {
        barcodeReceivers.add(receiver);
    }

    public void readSerialPort() {
        serialPort.openPort();
        readAction.start();
    }


    @Override
    public void readBarCode(String barCode) {
        System.out.println("Read barcode " + barCode);
        view.dispose();
        barcodeReceivers.forEach(receiver -> receiver.accept(barCode));
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        byte[] newData = new byte[serialPort.bytesAvailable()];
        int numRead = serialPort.readBytes(newData, newData.length);
        System.out.println("Read " + numRead + " bytes.");

        readBarCode(new String(newData));
    }
}
