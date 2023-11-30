package view.main.panel;

import control.modifier.TicketType;
import control.wizard.BarcodeWizard;
import data.AppData;
import data.Entry;
import data.modifier.Barcode;
import data.wizard.BarcodeModel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


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

        BarcodeModel barcodes = new BarcodeModel();

//        for (int i = 0; i < 30; i++) {
//            barcodes.addData(new Barcode("Test " + i , "TEST_" + i, "test/test" + i + ".jpg", "This the " + i + "th a test"));
//        }
        barcodes.addData(new Barcode("Büfé", "FOOD_SALE", "Barcodes\\foodSale.png", "Hozott sütit, vagy üdítőt"));
        barcodes.addData(new Barcode("Segítő", "HELP_SALE", "Barcodes\\helpSale.png", "Jelentkezett segítőnek"));
        barcodes.addData(new Barcode("Kilépés", "LEAVE_GUEST", "Barcodes\\leaveGuest.png", "Olvass kiléptetéshez!"));
        barcodes.addData(new Barcode("Törlés", "DELETE_GUEST", "Barcodes\\deleteGuest.png", "Olvass törléshez!"));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new BarcodeWizard(barcodes).getView());
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
