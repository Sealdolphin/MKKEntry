package control;

import control.utility.devices.BarCodeReaderListenerFactory;
import data.ProfileData;
import data.entry.AppData;
import data.modifier.Barcode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.main.LoadingScreen;
import view.main.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final String configFileName = "config.mkk";
    private static final String profileFileName = "profiles.mkk";
    private static final String barcodeFileName = "barcodes.mkk";

    public static UIHandler uh;

    private MainWindow view;
    private AppData model;
    private ProfileData profileData;

    private static LoadingScreen loadingScreen;

    /**
     * Starting point of application
     * It sets up the basic options and starts the program
     * @param args the program arguments
     */
    public static void main(String[] args) {

        Application.loadingScreen = new LoadingScreen();
        Application.loadingScreen.setTasks(7);
        Application.loadingScreen.setVisible(true);

        //Loading UI HANDLER
        //UI Handler is essential for the program to run.
        //It contains the static string messages.
        try {
            // Parsing uh Handler from file
            JSONParser parser = new JSONParser();
            BufferedReader optionsReader = new BufferedReader(new InputStreamReader(new FileInputStream("ui.json"), StandardCharsets.UTF_8));
            JSONObject optionsJSON = (JSONObject) parser.parse(optionsReader);

            Application.loadingScreen.setProgress("UI betöltése...");

            //Loading options
            uh = new UIHandler();
            uh.refreshOptions(optionsJSON);

            Application.loadingScreen.setProgress("L&F betöltése...");
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        }
        catch (ParseException | IOException e) {
            Application.loadingScreen.setInterruptMessage("Nem tudtam betölteni a beállításokat a 'uh.json' fáljból.\n" +
                    "Az alkalmazás ezért nem tud elindulni.\n" +
                    "Részletek:\n" + e);
            Application.loadingScreen.interrupt();
            return;
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            String errorMsg = uh.getUIStr("ERR","L&F_ERR");
            JOptionPane.showMessageDialog(null,errorMsg, uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
        }


        try {
            Application app = new Application();
            app.start();
        } catch (Exception appException) {
            appException.printStackTrace();
            Application.loadingScreen.setInterruptMessage(appException.getMessage());
            Application.loadingScreen.interrupt();
        }
    }

    private Application() throws Exception {
        //Read serial ports
        List<String> ports = BarCodeReaderListenerFactory.refreshSerialPorts();
        if(!ports.isEmpty()) {
            BarCodeReaderListenerFactory.connectSerialPort(ports.get(0));
        }

        //Read data
        List<Barcode> barcodeList = new ArrayList<>();
        try {
            Application.loadingScreen.setProgress("Vonalkódok betöltése...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(barcodeFileName));
            Object[] barcodeObjects = (Object[]) ois.readObject();
            for(Object obj : barcodeObjects)
                barcodeList.add((Barcode) obj);
            ois.close();
        } catch (Exception ex){
            JOptionPane.showMessageDialog(
                    null,
                    uh.getUIStr("ERR","START") + "\n" + ex.getMessage(),
                    uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
        }

        try {
            //Starting application
            Application.loadingScreen.setProgress("Profilok betöltése...");
            //Loading profile data
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profileFileName));
            profileData = (ProfileData) ois.readObject();
            ois.close();
        } catch (Exception loadException){
            loadException.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    uh.getUIStr("ERR","START") + "\n" + loadException.getMessage(),
                    uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
            profileData = new ProfileData();
        }

        try {
            Application.loadingScreen.setProgress("Konfigurációk betöltése...");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFileName));
            model = (AppData) ois.readObject();
            ois.close();
        } catch (Exception ex){
            JOptionPane.showMessageDialog(
                    null,
                    uh.getUIStr("ERR","START") + "\n" + ex.getMessage(),
                    uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
            model = new AppData();
        }

        AppController controller = new AppController(model, profileData);

        Image icon = Toolkit.getDefaultToolkit().getImage("Icons"+File.separator+"mkkMini.png");

        view = new MainWindow(model, controller);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(configFileName));
                    oos.writeObject(model);
                    oos.close();
                    System.out.println("Settings saved.");
                    oos = new ObjectOutputStream(new FileOutputStream(profileFileName));
                    oos.writeObject(profileData);
                    oos.close();
                    System.out.println("Profiles saved.");
                    oos = new ObjectOutputStream(new FileOutputStream(barcodeFileName));
                    oos.writeObject(barcodeList.toArray());
                    oos.close();
                    System.out.println("Barcodes saved.");
                } catch (IOException io) {
                    io.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            uh.getUIStr("ERR","CLOSING") + "\n" + io.getMessage(),
                            uh.getUIStr("ERR","HEADER"),JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        view.setIconImage(icon);
        Application.loadingScreen.done(null);
    }

    private void start() {
        view.setTitle(uh.getUIStr("UI","WINDOW_TITLE"));
        view.setMinimumSize(new Dimension(800,600));
        view.setLocationRelativeTo(null);
        view.pack();
        view.setVisible(true);
    }

    @Deprecated
    public static String parseFilePath(String filePath) {
        if (filePath == null) return "";
    	String parsed = filePath.replaceAll("\\t", File.separator);
    	String baseDir = System.getProperty("user.dir")+File.separator;
    	return parsed.substring(baseDir.length());
    }


}
