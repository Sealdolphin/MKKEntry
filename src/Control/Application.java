package Control;

import Window.MainWindow;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    private MainWindow view;
    //private AppData model;
    private EntryController controller;
    private EventHandler eventHandler;
    public static UIHandler ui;

    public enum Relative {
        CENTER,
        TOP,
        BOTTOM,
        RIGHT,
        LEFT
    }

    public static void main(String[] args) {
        JSONObject optionsJSON;

        //Setting up L&F and loading options
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JSONParser parser = new JSONParser();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("options.json")));
            optionsJSON = (JSONObject) parser.parse(br);
            //Loading options
            //ui = new UIHandler();
            //ui.refreshOptions(optionsJSON);

        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            // handle exception
            String errorMsg = "Nem található a rendszer által használt kinézet.\n" +
                    "Az alkalmazás ezért a Java kinézetet fogja használni.";
            JOptionPane.showMessageDialog(null,errorMsg,"Üzenet",JOptionPane.PLAIN_MESSAGE);
        } catch (ParseException | IOException e) {
            String errorMsg = "Nem tudtam betölteni a beállításokat a 'options.json' fáljból.\n" +
                    "Az alkalmazás ezért nem tud elindulni.\n" +
                    "Részletek:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null,errorMsg,"Hiba",JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Starting point of the application
        MainWindow window = new MainWindow();
        setRelativeLocationOnScreen(window, Relative.CENTER);
        window.setVisible(true);

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
}
