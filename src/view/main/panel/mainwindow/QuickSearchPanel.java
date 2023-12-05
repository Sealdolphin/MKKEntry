package view.main.panel.mainwindow;

import data.entry.DataColumn;
import view.main.panel.AbstractPanel;
import view.update.SorterUpdater;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class QuickSearchPanel extends AbstractPanel {

    private final JLabel lbTitle;
    private final JTextField tfSearch;
    private final JButton btnFilter;
    private final JPopupMenu filterMenu;
    private SorterUpdater<TableModel> sorterUpdater;

    public QuickSearchPanel() {
        lbTitle = new JLabel("Gyorskeresés: ");
        tfSearch = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);
        btnFilter = new JButton("Szűrők");

        filterMenu = setupFilters();
        btnFilter.addActionListener(this::showFilterMenu);
        tfSearch.getDocument().addDocumentListener(sorterUpdater);
    }

    public void updateTableSorter(TableRowSorter<TableModel> sorter) {
        sorterUpdater = new SorterUpdater<>(sorter, tfSearch::getText, filterMenu);
    }

    private JPopupMenu setupFilters() {
        JPopupMenu popupMenu = new JPopupMenu();
        if (sorterUpdater != null) {
            Arrays.stream(DataColumn.values()).forEach(column -> popupMenu.add(sorterUpdater.createMenuItem(column.getName())));
        }
        return popupMenu;
    }

    private void showFilterMenu(ActionEvent event) {
        filterMenu.show(btnFilter, btnFilter.getWidth() / 2, btnFilter.getHeight() / 2);
    }

    @Override
    public void initializeLayout() {
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(lbTitle).addComponent(tfSearch).addComponent(btnFilter));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(lbTitle, GroupLayout.Alignment.CENTER).addComponent(tfSearch).addComponent(btnFilter));
    }
}
