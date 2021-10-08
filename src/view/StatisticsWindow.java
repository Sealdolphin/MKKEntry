package view;

import data.DataModel;
import data.Entry;
import data.EntryProfile;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;

public class StatisticsWindow extends JFrame {

    public StatisticsWindow(EntryProfile profile, DataModel<Entry> data){
        setTitle("Statisztikák");

        setLayout(new BorderLayout());
        CashPanel cashPanel = new CashPanel(profile, data, this);

        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.addTab("Általános",createMainPanel(data));
        tabPanel.addTab("Kassza", cashPanel);
        tabPanel.addTab("Grafikonok",new JPanel());

        tabPanel.addChangeListener(e -> {
            if (tabPanel.getSelectedIndex() == 1){
                if(!profile.getPassword().equals("")) {
                    String pw = JOptionPane.showInputDialog(null, "Jelszó", "Kassza jelszó", JOptionPane.INFORMATION_MESSAGE);
                    if (pw == null || !pw.equals(profile.getPassword()))
                        tabPanel.setSelectedIndex(0);
                }
            }
        });

        add(tabPanel,CENTER);
        setMinimumSize(new Dimension(400, 300));

        pack();
        setLocationRelativeTo(null);

    }

    private JPanel createMainPanel(DataModel<Entry> data){
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

        int ppl = data.getDataSize();
        int entry = 0, leave = 0;
        for (int i = 0; i < ppl; i++) {
            Entry e = data.getDataByIndex(i);
            if(e.get(5) != null){
                entry++;
                if(e.get(6) != null)
                    leave++;
            }
        }

        panel.add(new JLabel("Létszám:"));
        panel.add(new JLabel("Benn vannak: " + (entry - leave)));
        panel.add(new JLabel("Beléptek: " + entry));
        panel.add(new JLabel("Kiléptek: " + leave));
        panel.add(new JLabel("Összesen: " + ppl));
        if(ppl == 0) ppl = 1;
        panel.add(new JLabel("Megjelenési arány: " + (100*entry/ppl) + "%"));

        return panel;
    }
}
