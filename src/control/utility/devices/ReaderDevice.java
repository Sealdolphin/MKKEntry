package control.utility.devices;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ReaderDevice implements BarcodeReaderListener {

    private Consumer<String> action;
    private JDialog dialog;
    private final Thread actionThread;
    private String lastReadCode;
    private final String message;
    private JLabel lbMessage;

    ReaderDevice(Consumer<String> action, String message, boolean isDialog, SerialPort listeningPort){
        this.action = action;
        this.message = message;
        actionThread = new Thread(() -> idle(listeningPort));

        if(isDialog) {
            dialog = new JDialog();
            dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

            lbMessage = new JLabel(message);
            lbMessage.setFont(new Font(lbMessage.getFont().getName(),Font.PLAIN,30));

            dialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        readBarCode(null);
                    }
                }
            });

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public synchronized void windowActivated(WindowEvent e) {
                    actionThread.interrupt();   //This is very ugly
                }
            });

            dialog.setTitle("Kódbeolvasás");
            dialog.getContentPane().add(lbMessage);
            dialog.setUndecorated(true);

            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);

        }

        actionThread.start();
    }

    @Override
    public void readBarCode(String barCode) {
        //Interrupt thread!
        lastReadCode = barCode;
        actionThread.interrupt();
    }

    private synchronized void idle(SerialPort port) {
        if(dialog != null) {
            try {
                dialog.setAlwaysOnTop(true);
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setVisible(true);
                //Here stops the magic
                wait(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Device " + this.hashCode() + " is starting idle thread");
        while (port.isOpen()){
            try {
                wait(1); //Awaiting ready
            } catch (InterruptedException e) {
                System.out.println("[Device "+ this.hashCode() +"]: Input received: " + lastReadCode + " from port " + port.getSystemPortName());
                action.accept(lastReadCode);
                if(dialog != null) {
                    //If dialog was true, then stop working
                    dialog.dispose();
                    break;
                }
            }
        }
    }
}
