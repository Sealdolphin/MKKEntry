package view.main.panel.mainwindow;

import view.main.panel.AbstractPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class QuickSearchPanel extends AbstractPanel {

    private final JLabel lbTitle;
    private final JTextField tfSearch;
    private final JButton btnFilter;
    private final JPopupMenu filterMenu;

    private final TableRowSorter<TableModel> sorter;
    private final SorterUpdater sorterUpdater;

    public QuickSearchPanel(TableModel model, RecordPanel recordPanel) {
        sorter = new TableRowSorter<>(model);
        sorterUpdater = new SorterUpdater();

        lbTitle = new JLabel("Gyorskeresés: ");
        tfSearch = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);
        btnFilter = new JButton("Szűrők");

        filterMenu = setupFilters(recordPanel);
        btnFilter.addActionListener(this::showFilterMenu);
        tfSearch.getDocument().addDocumentListener(sorterUpdater);

        recordPanel.setRowSorter(sorter);
    }

    private JPopupMenu setupFilters(RecordPanel recordPanel) {
        JPopupMenu popupMenu = new JPopupMenu();
        recordPanel.getTableAttributes().asIterator().forEachRemaining(column -> popupMenu.add(new InteractiveSortingMenuItem(column)));
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

    private class InteractiveSortingMenuItem extends JCheckBoxMenuItem {

        private InteractiveSortingMenuItem(TableColumn column) {
            super(column.getHeaderValue().toString());
            addActionListener(l -> sorterUpdater.updateRegexSorter());
        }

    }

    private class SorterUpdater implements DocumentListener {

        public void updateRegexSorter() {
            List<MenuElement> subElements = Arrays.asList(filterMenu.getSubElements());
            int[] indices = subElements.stream()
                    .filter(element -> element instanceof JCheckBoxMenuItem filter && filter.getState())
                    .mapToInt(subElements::indexOf)
                    .toArray();
            try {
                RowFilter<TableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + tfSearch.getText(), indices);
                sorter.setRowFilter(rowFilter);
            } catch (java.util.regex.PatternSyntaxException ex) {
                System.out.println("WARNING: " + ex.getLocalizedMessage());
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateRegexSorter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateRegexSorter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateRegexSorter();
        }
    }
}
