package view.main.panel;

import control.NewAppController;
import control.modifier.TicketType;
import data.AppData;
import data.Entry;

import javax.swing.*;
import java.io.IOException;


public abstract class AbstractPanel extends JPanel {

    public static int FONT_SIZE_MEDIUM = 15;
    public static int PADDING_DEFAULT = 13;
    public static int TEXT_PANEL_DEFAULT_WIDTH = 32;

    public static int ICON_SIZE_DEFAULT = 24;
    public static String DEFAULT_FONT = "Arial";

    /**
     * TEMP
     */
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        AppData model = new AppData();

        for (int i = 1000; i < 4000; i++) {
            model.addData(new Entry(String.valueOf(i), "name" + i, TicketType.emptyType()));
        }

        AbstractPanel panel = new MainPanel(model, new NewAppController());
        //AbstractPanel panel = new TopPanel(model, new RecordPanel(model));
        panel.initializeLayout();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }

    protected GroupLayout layout;

    public AbstractPanel() {
        layout = new GroupLayout(this);
        setLayout(layout);
    }

    public abstract void initializeLayout();

    public abstract void refreshPanel();

    protected void setSingleComponentLayout(JComponent component) {
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(component));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(component));
    }

}
