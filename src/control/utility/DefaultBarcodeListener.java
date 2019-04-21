package control.utility;

import javax.swing.*;
import java.util.function.Consumer;

@Deprecated
public class DefaultBarcodeListener implements BarcodeReaderListener {

    private Consumer<String> action;
    private JDialog msg;

    public DefaultBarcodeListener(Consumer<String> action){
        this.action = action;

        msg = new JDialog();
        msg.add(new JLabel("OLVASS"));
        //msg.setUndecorated(true);
        msg.setModal(true);

        msg.pack();
        msg.setResizable(false);
        msg.setLocationRelativeTo(null);
        msg.setVisible(true);
        System.out.println("VISIBLE: " + msg.isVisible());
    }

    @Override
    public void readBarCode(String barCode) {
        System.out.println("CODE: " + barCode);
        action.accept(barCode);
        msg.dispose();
    }

}
