package Window;

import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args) {
        //Setting up L&F
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            // handle exception
            String errorMsg = "Nem található a rendszer által használt kinézet.\n" +
                    "Az alkalmazás ezért a Java kinézetet fogja használni.";
            JOptionPane.showMessageDialog(null,errorMsg,"Üzenet",JOptionPane.PLAIN_MESSAGE);
        }

        //Starting point of the application
        MainWindow window = new MainWindow();
        int x,y;
        x = (int)((Toolkit.getDefaultToolkit().getScreenSize().width - window.getWidth())*.5);
        y = (int)((Toolkit.getDefaultToolkit().getScreenSize().height - window.getHeight())*.5);
        window.setLocation(x,y);
        //setRelativeLocationOnScreen(window, Relative.CENTER);
        window.setVisible(true);

    }

    /*
    STORED FOR FUTURE USE

    public enum Relative {
        CENTER,
        TOP,
        BOTTOM,
        RIGHT,
        LEFT
    }

    private static void setRelativeLocationOnScreen(Component c, Relative location){
        int x,y;
        switch (location){
            default:
            case CENTER:
                x = (int)((Toolkit.getDefaultToolkit().getScreenSize().width - c.getWidth())*.5);
                y = (int)((Toolkit.getDefaultToolkit().getScreenSize().height - c.getHeight())*.5);
                break;
            case TOP:
                x = (int)((Toolkit.getDefaultToolkit().getScreenSize().width - c.getWidth())*.5);
                y = 0;
                break;
            case BOTTOM:
                x = (int)((Toolkit.getDefaultToolkit().getScreenSize().width - c.getWidth())*.5);
                y = Toolkit.getDefaultToolkit().getScreenSize().height - c.getHeight();
                break;
            case LEFT:
                x = 0;
                y = (int)((Toolkit.getDefaultToolkit().getScreenSize().height - c.getHeight())*.5);
                break;
            case RIGHT:
                x = Toolkit.getDefaultToolkit().getScreenSize().width - c.getWidth();
                y = (int)((Toolkit.getDefaultToolkit().getScreenSize().height - c.getHeight())*.5);
                break;
        }
        c.setLocation(x,y);
    }
    */

}
