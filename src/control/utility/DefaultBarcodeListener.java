package control.utility;

import javax.swing.*;
import java.util.function.Consumer;

public class DefaultBarcodeListener implements BarcodeListener {

    private Consumer<String> action;
    private JFrame msg;

    public DefaultBarcodeListener(Consumer<String> action){
        this.action = action;

        msg = new JFrame();
        msg.add(new JLabel("OLVASS"));
        msg.setUndecorated(true);

        msg.pack();
        msg.setResizable(false);
        msg.setVisible(true);
    }

    @Override
    public void readBarCode(String barCode) {
        action.accept(barCode);
        msg.setVisible(false);
    }

}
