package view.main.panel;

import control.wizard.DiscountWizard;
import control.wizard.EntryProfileWizard;
import data.entry.AppData;
import data.entry.Entry;
import data.entryprofile.EntryProfile;
import data.modifier.Barcode;
import data.modifier.Discount;
import data.modifier.TicketType;
import data.wizard.BarcodeModel;
import data.wizard.DiscountModel;
import data.wizard.EntryProfileModel;
import data.wizard.TicketTypeModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        UIManager.getLookAndFeelDefaults().put("ImagePanel.border", DEFAULT_BORDER);

        AppData model = new AppData();

        for (int i = 1000; i < 2000; i++) {
            model.addData(new Entry(String.valueOf(i), "name" + i, new TicketType()));
        }

        List<Barcode> barcodeList = new ArrayList<>();

        Barcode codeHelper = new Barcode("Segítő", "HELPER", "Barcodes\\helpSale.png", "Jelentkezett segítőnek");
        Barcode codeFood = new Barcode("Büfé", "FOOD", "Barcodes\\foodSale.png", "Hozott sütit / üdítőt");
        Barcode codeSupport = new Barcode("Támogató", "SUPPORT", "Barcodes\\vipTicket.png", "Támogató <3");
        Barcode codeRandom = new Barcode("Random", "FOOD", "Barcodes\\vipTicket.png", "Valami...");

        barcodeList.add(codeHelper);
        barcodeList.add(codeFood);
        barcodeList.add(codeSupport);
        barcodeList.add(codeRandom);

        List<Discount> discountList = new ArrayList<>();

        discountList.add(new Discount("Segítő",codeHelper,"Icons\\DIS_HELP.png",500,false));
        discountList.add(new Discount("Büfé",codeFood,"Icons\\DIS_PIE.png",500,false));
        discountList.add(new Discount("Támogató",codeSupport,"Icons\\DIS_MONEY.png",0,false));

        DiscountModel discounts = new DiscountModel(discountList);

        DiscountWizard dWizard = new DiscountWizard(discounts);
//        dWizard.updateBarcodeOptions(new BarcodeModel(barcodeList));

        List<TicketType> ticketTypes = new ArrayList<>();
        ticketTypes.add(new TicketType("TT1", 1000, false, TicketType.DEFAULT_COLOR));
        ticketTypes.add(new TicketType("TT4", 10, false, TicketType.DEFAULT_COLOR));
        ticketTypes.add(new TicketType("TT3", 3000, false, TicketType.DEFAULT_COLOR));
        ticketTypes.add(new TicketType("TT4", 4000, false, TicketType.DEFAULT_COLOR));

        EntryProfile szuretiBal = new EntryProfile("MKK Szüreti bál 2023");
        szuretiBal.updateBarcodes(new BarcodeModel(barcodeList));
        szuretiBal.updateTicketTypes(new TicketTypeModel(ticketTypes));
        szuretiBal.updateDiscounts(new DiscountModel(discountList));

        List<EntryProfile> profiles = List.of(
                szuretiBal,
                new EntryProfile("MKK Katalin bál 2023"),
                new EntryProfile("MKK Farsangi bál 2024")
        );


        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new EntryProfileWizard(new EntryProfileModel(profiles)).getView());
//        frame.add(new TicketTypeWizard(new TicketTypeModel(ticketTypes)).getView());
//        frame.add(dWizard.getView());
        frame.setMinimumSize(new Dimension(300,480));
        frame.pack();
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
