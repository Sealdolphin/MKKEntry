package view.main;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JFrame {

    private JProgressBar progress;

    public LoadingScreen(int tasks){
        progress = new JProgressBar(0,tasks);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        add(progress);

        progress.setString("Betöltés...");
        progress.setStringPainted(true);

        setMinimumSize(new Dimension(320,70));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public void setProgress(String msg){
        progress.setString(msg);
        progress.setValue(progress.getValue() + 1);
        revalidate();
        if(progress.getValue() == progress.getMaximum()) dispose();
    }

    public void close(){
        dispose();
    }

}
