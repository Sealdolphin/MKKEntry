package view.main.panel;

import control.wizard.EntryProfileWizard;
import data.entry.AppData;
import data.entry.Entry;
import data.modifier.TicketType;
import data.wizard.EntryProfileModel;

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
//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        UIManager.getLookAndFeelDefaults().put("ImagePanel.border", DEFAULT_BORDER);

        AppData model = new AppData();

        for (int i = 1000; i < 2000; i++) {
            model.addData(new Entry(String.valueOf(i), "name" + i, new TicketType()));
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new EntryProfileWizard(new EntryProfileModel()).getView());
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
