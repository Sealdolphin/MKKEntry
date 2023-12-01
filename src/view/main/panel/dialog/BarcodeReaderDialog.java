package view.main.panel.dialog;

import control.utility.devices.BarcodeReaderListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class BarcodeReaderDialog extends JDialog {

    private final List<BarcodeReaderListener> barcodeReaderListeners = new ArrayList<>();

    public BarcodeReaderDialog(Window parent, JPanel appWindow, String message) {
        super(parent);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        JLabel lbMessage = new JLabel(message);
        lbMessage.setFont(new Font("Arial", Font.PLAIN,30));

        setTitle("Kódbeolvasás");
        getContentPane().add(lbMessage);

        setUndecorated(true);
        pack();
        setResizable(false);
        setLocationRelativeTo(appWindow);
        setLocation(getX(),getY() + 50);

        addKeyListener(new WindowCloseAdapter());
    }

    public void addBarcodeListener(BarcodeReaderListener listener) {
        barcodeReaderListeners.add(listener);
    }

    public void waitIdle() {
        setAlwaysOnTop(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

    private class WindowCloseAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent event) {
            if(event.getKeyCode() == KeyEvent.VK_ESCAPE){
                barcodeReaderListeners.forEach(l -> l.readBarCode(null));
            }
        }

    }
}
