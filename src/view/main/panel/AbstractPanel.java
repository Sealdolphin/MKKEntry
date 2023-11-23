package view.main.panel;

import control.modifier.TicketType;
import data.AppData;
import data.Entry;
import data.modifier.Barcode;
import view.main.panel.wizard.barcode.BarcodeWizard;
import view.main.panel.wizard.entryprofile.DataListView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractPanel extends JPanel {

    public static int FONT_SIZE_MEDIUM = 15;
    public static int TEXT_PANEL_DEFAULT_WIDTH = 32;

    public static int ICON_SIZE_DEFAULT = 24;
    public static String DEFAULT_FONT = "Arial";

    /**
     * TEMP
     * TODO: delete this!!
     */
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        AppData model = new AppData();

        for (int i = 1000; i < 2000; i++) {
            model.addData(new Entry(String.valueOf(i), "name" + i, TicketType.emptyType()));
        }

        List<Barcode> barcodes = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            barcodes.add(new Barcode("Test " + i , "TEST_" + i, "test/test" + i + ".jpg", "This the " + i + "th a test"));
        }

        //JPanel panel = new BarcodeWizard(new Barcode("Test", "TEST", "test/test.jpg", "This is a test")); //new MainPanel(model, new NewAppController());
        //panel.initializeLayout();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new DataListView<>(new Barcode[]{
                new Barcode("Test", "TEST", "test/test.jpg", "This is a test"),
                new Barcode("Test 2", "TEST_2", "test/test2.jpg", "This is an another test"),
        }, new BarcodeWizard()));
        frame.pack();
        frame.setMinimumSize(new Dimension(640,480));
        frame.setVisible(true);
    }

    protected GroupLayout layout;

    public AbstractPanel(boolean autoGaps, boolean containerGaps) {
        layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(autoGaps);
        layout.setAutoCreateContainerGaps(containerGaps);
    }

    public AbstractPanel() {
        this(true, true);
    }

    public abstract void initializeLayout();

    protected void setSingleComponentLayout(JComponent component) {
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(component));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(component));
    }

}
