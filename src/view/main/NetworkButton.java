package view.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class NetworkButton extends JButton {

    private boolean online = false;
    private ImageIcon on;
    private ImageIcon off;

    public NetworkButton(){

        off =  new ImageIcon(new ImageIcon("Icons" + File.separator + "offline.png").getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
        on =  new ImageIcon(new ImageIcon("Icons" + File.separator + "online.png").getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
        setIcon(off);
        addActionListener(this::changeState);
    }

    private void changeState(ActionEvent e){
        online = !online;
        if(online){
            setIcon(on);
        } else setIcon(off);
    }

}
