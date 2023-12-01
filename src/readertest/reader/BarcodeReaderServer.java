package readertest.reader;

import ClearImageJNI.*;
import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class BarcodeReaderServer {

    private static final String CLEAR_IMAGE_DLL_PATH = "C:\\Program Files (x86)\\Inlite\\ClearImageSDK\\x64\\ClearImage.dll";

    private final SerialPort serialPort;

    private final ICiServer readerServer;

    public BarcodeReaderServer() {
        readerServer = initClearImage();
        serialPort = SerialPort.getCommPorts()[0];
    }

    private ICiServer initClearImage() {
        try {
            CiServer.loadClearImage(CLEAR_IMAGE_DLL_PATH);
            //  Create ClearImage Server
            CiServer objServer = new CiServer();
            return objServer.getICiServer();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public void readBarcodes(String fileName, int page) {
        ICiBarcodePro reader = null;
        try {
            reader = readerServer.CreateBarcodePro(); // Create and configure barcode reader
            reader.setType(new FBarcodeType(FBarcodeType.cibfCode39, FBarcodeType.cibfCode128));
            reader.getImage().Open(fileName, page); // Open image from an image file
            int idx = reader.Find(0); // Read barcodes
            for (int i = 1; i <= idx; i++) { // Process results
                ICiBarcode barcode = reader.getBarcodes().getItem(i); // getItem is 1-based
                System.out.println(" Barcode + type: " + barcode.getType() + "   Text: \n" + barcode.getText());
                String readData = barcode.getText();
                writeToPort(readData);
            }
        } catch (Exception ex) { // Process exceptions
            System.out.println();
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.getImage().Close(); // Close images and free memory
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void writeToPort(String data) {
        serialPort.openPort();
        if (serialPort.isOpen()) {
            System.out.println("Connected to " + serialPort);
            try (OutputStream out = serialPort.getOutputStream()) {
                Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                writer.write(data);
                writer.flush();
                System.out.println("wrote data");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serialPort.closePort();
    }
}
