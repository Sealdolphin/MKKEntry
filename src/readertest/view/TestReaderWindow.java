package readertest.view;

import com.fazecast.jSerialComm.SerialPort;
import readertest.reader.BarcodeReaderServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class TestReaderWindow extends JFrame {

    private final BarcodeReaderServer server = new BarcodeReaderServer();

    public static void main(String[] args) {
        new TestReaderWindow().setVisible(true);
    }

    public TestReaderWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(200,200));

        JButton btnScan = new JButton("Scan!");
        btnScan.addActionListener(this::doScan);
        add(btnScan);

        setLocationRelativeTo(null);
        pack();
    }

    private void doScan(ActionEvent event) {
        System.out.println(Arrays.toString(SerialPort.getCommPorts()));
        String file = openDialog();
        if (file != null) {
            server.readBarcodes(file, 0);
        }
    }

    private String openDialog() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        System.out.println("Cancelled");
        return null;
    }

}
