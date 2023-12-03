package view.main.panel;

import control.modifier.TicketType;
import data.AppData;
import data.Entry;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;


public abstract class AbstractPanel extends JPanel {

    public static int FONT_SIZE_MEDIUM = 15;
    public static int TEXT_PANEL_DEFAULT_WIDTH = 32;

    public static int ICON_SIZE_DEFAULT = 24;
    public static String DEFAULT_FONT = "Arial";

    public static final Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.BLACK);

    /**
     * TEMP
     * TODO: delete this!!
     */
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.getLookAndFeelDefaults().put("ImagePanel.border", DEFAULT_BORDER);

        AppData model = new AppData();

        for (int i = 1000; i < 2000; i++) {
            model.addData(new Entry(String.valueOf(i), "name" + i, TicketType.emptyType()));
        }

//        List<Barcode> barcodeList = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            barcodes.addData(new Barcode("Test " + i , "TEST_" + i, "test/test" + i + ".jpg", "This the " + i + "th a test"));
//        }
//        barcodeList.add(new Barcode("Büfé", "FOOD_SALE", "Barcodes\\foodSale.png", "Hozott sütit, vagy üdítőt"));
//        barcodeList.add(new Barcode("Segítő", "HELP_SALE", "Barcodes\\helpSale.png", "Jelentkezett segítőnek"));
//        barcodeList.add(new Barcode("Kilépés", "LEAVE_GUEST", "Barcodes\\leaveGuest.png", "Olvass kiléptetéshez!"));
//        barcodeList.add(new Barcode("Törlés", "DELETE_GUEST", "Barcodes\\deleteGuest.png", "Olvass törléshez!"));

//        BarcodeModel barcodes = new BarcodeModel(barcodeList);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.add();
        frame.pack();
        frame.setMinimumSize(new Dimension(640,480));
        frame.setLocationRelativeTo(null);
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
