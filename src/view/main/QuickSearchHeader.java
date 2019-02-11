package view.main;

import data.AppData;

import javax.swing.*;
import javax.swing.table.TableRowSorter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

class QuickSearchHeader extends JPanel {

    QuickSearchHeader(AppData model, JTable entryView) {

        add(new JLabel("Gyorskeresés: "));
        JTextField tfSearch = new JTextField(32);
        add(tfSearch);
        TableRowSorter<AppData> sorter = new TableRowSorter<>(model);
        entryView.setRowSorter(sorter);

        JButton btnFilters = new JButton("Szűrők");

        JPopupMenu menuFilters = new JPopupMenu();
        for (int i = 0; i < entryView.getColumnCount(); i++) {
            menuFilters.add(new JCheckBoxMenuItem(entryView.getColumnName(i)));
        }

        btnFilters.addActionListener(e -> menuFilters.show(btnFilters,btnFilters.getWidth() / 2, btnFilters.getHeight() / 2));

        add(btnFilters);

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                RowFilter<AppData, Object> rf;
                //If current expression doesn't parse, don't update.
                try {
                    Integer[] columns = Arrays.stream(menuFilters.getSubElements())
                            .filter(me -> ((JCheckBoxMenuItem) me).getState())
                            .map(s -> Arrays.asList(menuFilters.getSubElements()).indexOf(s))
                            .toArray(Integer[]::new);
                    int[] indices = new int[columns.length];
                    for(int i = 0; i < indices.length; i++){
                        indices[i] = columns[i];
                    }

                    rf = RowFilter.regexFilter("(?i)" + tfSearch.getText(),indices);
                } catch (java.util.regex.PatternSyntaxException ex) {
                    return;
                }
                sorter.setRowFilter(rf);
            }
        });
    }
}
