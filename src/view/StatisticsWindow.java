package view;

import data.DataModel;
import data.EntryProfile;
import data.entry.Entry;

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

        int allPeople = data.getSize();
        int peopleEntered = 0, peopleLeft = 0;
        for (int i = 0; i < allPeople; i++) {
            Entry e = data.getElementAt(i);
            if(e.hasEntered()){
                peopleEntered++;
            }
            if(e.hasLeft())
                peopleLeft++;
        }

        JLabel lbTitle = new JLabel("Létszám:");
        JLabel lbIn = new JLabel("Benn vannak: " + (peopleEntered - peopleLeft));
        JLabel lbEntered = new JLabel("Beléptek: " + peopleEntered);
        JLabel lbLeft = new JLabel("Kiléptek: " + peopleLeft);
        JLabel lbTotal = new JLabel("Összesen: " + allPeople);
        if(allPeople == 0) allPeople = 1;   //Avoid dividing by Zero
        JLabel lbEnterRate = new JLabel("Megjelenési arány: " + (100*peopleEntered/allPeople) + "%");

        lbTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        panel.add(lbTitle);
        for (JLabel label: new JLabel[]{lbIn, lbEntered, lbLeft, lbTotal, lbEnterRate}) {
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            panel.add(label);
        }

        return panel;
    }
}
