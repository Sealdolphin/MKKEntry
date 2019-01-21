package Control;

import Data.AppData;
import Window.MainWindow;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Application {

    private static final String configFileName = "config.mkk";
    private MainWindow view;
    private AppData model;
    private EntryController controller;
    private EventHandler eventHandler;
    public static UIHandler ui;

    public enum ScreenLocation {
        CENTER,
        TOP,
        BOTTOM,
        RIGHT,
        LEFT
    }

    /**
     * Starting point of application
     * It sets up the basic options and starts the program
     * @param args the program arguments
     */
    public static void main(String[] args) {
        //Loading UI HANDLER
        //UI Handler is essential for the program to run.
        //It contains the static string messages.
        try {
            // Parsing ui Handler from file
            JSONParser parser = new JSONParser();
            BufferedReader optionsReader = new BufferedReader(new InputStreamReader(new FileInputStream("ui.json")));
            JSONObject optionsJSON = (JSONObject) parser.parse(optionsReader);
            //Loading options
            ui = new UIHandler();
            ui.refreshOptions(optionsJSON);

            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ParseException | IOException e) {
            String errorMsg = "Nem tudtam betölteni a beállításokat a 'ui.json' fáljból.\n" +
                    "Az alkalmazás ezért nem tud elindulni.\n" +
                    "Részletek:\n" + e.toString();
            JOptionPane.showMessageDialog(null,errorMsg,"Hiba",JOptionPane.ERROR_MESSAGE);
            return;
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            String errorMsg = ui.getUIStr("ERR","L&F_ERR");
            JOptionPane.showMessageDialog(null,errorMsg,ui.getUIStr("MSG","WARNING"),JOptionPane.PLAIN_MESSAGE);
        }


        try {
            Application app = new Application();
            app.start();
        } catch (Exception appException) {
            appException.printStackTrace();
            JOptionPane.showMessageDialog(null,appException.getMessage(),ui.getUIStr("ERR","HEADER"),JOptionPane.ERROR_MESSAGE);
        }

    }

    private Application() throws Exception {
        try {
            //Starting application
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFileName));
            model = (AppData) ois.readObject();
            ois.close();
        } catch (Exception loadException){
            loadException.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    ui.getUIStr("ERR","START") + "\n" + loadException.getMessage(),
                    ui.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
            model = new AppData();
        }
        view = new MainWindow(model);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(configFileName));
                    oos.writeObject(model);
                    oos.close();
                    System.out.println("Settings saved.");
                } catch (IOException io) {
                    io.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            ui.getUIStr("ERR","CLOSING") + "\n" + io.getMessage(),
                            ui.getUIStr("ERR","HEADER"),JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void start() {
        view.setTitle(ui.getUIStr("UI","WINDOW_TITLE"));
        view.setMinimumSize(new Dimension(640,480));
        setRelativeLocationOnScreen(view, ScreenLocation.CENTER);
        view.pack();
        view.setVisible(true);
    }

    private static void setRelativeLocationOnScreen(Component c, ScreenLocation location){
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
