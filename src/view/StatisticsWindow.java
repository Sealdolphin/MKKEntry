package view;

import data.DataModel;
import data.Entry;

import javax.swing.*;

public class StatisticsWindow extends JFrame {

    public StatisticsWindow(DataModel<Entry> data){
        setTitle("Statisztikák");
        setLayout(new BoxLayout(this.getContentPane(),BoxLayout.PAGE_AXIS));

        int ppl = data.getDataSize();
        int entry = 0, leave = 0, cash = 0;
        for (int i = 0; i < ppl; i++) {
            Entry e = data.getDataByIndex(i);
            if(e.get(5) != null){
                entry++;
                if(e.get(6) != null)
                    leave++;
            }
            cash += e.getAllFees();
        }

        add(new JLabel("Létszám:"));
        add(new JLabel("Benn vannak: " + (entry - leave)));
        add(new JLabel("Beléptek: " + entry));
        add(new JLabel("Kiléptek: " + leave));
        add(new JLabel("Összesen: " + ppl));
        if(ppl == 0) ppl = 1;
        add(new JLabel("Megjelenési arány: " + (100*entry/ppl) + "%"));
        add(new JLabel("Kassza: "+ cash +" Ft"));

        pack();
        setLocationRelativeTo(null);

    }

}
