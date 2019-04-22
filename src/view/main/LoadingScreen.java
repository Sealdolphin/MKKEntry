package view.main;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LoadingScreen extends JFrame {

    private JProgressBar progress;
    private String interruptMsg = "The task was interrupted.";

    public LoadingScreen(){
        progress = new JProgressBar(0,1);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage("Icons"+ File.separator+"mkkMini.png"));
        setTitle("Kérem várjon");

        setLayout(new BorderLayout());
        add(progress);

        progress.setString("Betöltés...");
        progress.setStringPainted(true);

        setMinimumSize(new Dimension(320,70));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public void setInterruptMessage(String message){
        interruptMsg = message;
    }

    public void interrupt(){
        JOptionPane.showMessageDialog(null,interruptMsg,"Hiba",JOptionPane.ERROR_MESSAGE);
        dispose();
    }

    public void setTasks(int max){
        if(max <= 0)
            progress.setIndeterminate(true);
        else {
            progress.setIndeterminate(false);
            progress.setMaximum(max);
        }
    }

    public void setProgress(String msg){
        progress.setString(msg);
        progress.setValue(progress.getValue() + 1);
        revalidate();
        if(progress.getValue() >= progress.getMaximum()) dispose();
    }

    public void done(String dialogMessage) {
        if(dialogMessage != null)
            JOptionPane.showMessageDialog(null,dialogMessage,"Kész",JOptionPane.INFORMATION_MESSAGE);
        progress.setValue(progress.getMaximum());
        dispose();
    }
}
