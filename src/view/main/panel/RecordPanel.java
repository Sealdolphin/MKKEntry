package view.main.panel;

import control.modifier.Discount;
import data.AppData;
import data.DataModel;
import data.Entry;
import view.main.interactive.SelectableComponent;
import view.renderer.DiscountRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class RecordPanel extends AbstractPanel implements SelectableComponent {

    private final JTable table;
    private final JScrollPane scrollView;
    private final DataModel<Entry> model;

    public RecordPanel(AppData data) {
        super(false, false);
        this.model = data;

        table = new JTable(data);
        scrollView = new JScrollPane(table);

        // Scrolling
        scrollView.setVerticalScrollBar(scrollView.createVerticalScrollBar());
        scrollView.setWheelScrollingEnabled(true);

        // Table
        table.setDefaultRenderer(Discount.class, new DiscountRenderer(ICON_SIZE_DEFAULT));
        table.setRowHeight(ICON_SIZE_DEFAULT * 2);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false); //Selecting a column does not make sense anyway
        table.createDefaultColumnsFromModel();
        table.getSelectionModel().addListSelectionListener(this::selectionHasChanged);
        data.addTableModelListener(this::adaptSelectionToModelChange);
        // FIXME: implement popup menu through something... controller??
        table.addMouseListener(new PopupMenuAdapter(new JPopupMenu()));
    }

    @Override
    public void clearSelection() {
        table.clearSelection();
    }

    public void setRowSorter(TableRowSorter<TableModel> sorter) {
        table.setRowSorter(sorter);
    }

    public Enumeration<TableColumn> getTableAttributes() {
        return table.getColumnModel().getColumns();
    }

    @Override
    public void initializeLayout() {
        setSingleComponentLayout(scrollView);
    }

    private void selectionHasChanged(ListSelectionEvent selectionEvent) {

        if (!selectionEvent.getValueIsAdjusting()) {
            if((table.getSelectedRow() >= 0)){
                model.setSelection(model.getElementAt(table.convertRowIndexToModel(table.getSelectedRow())));
            }
        }
    }

    private void adaptSelectionToModelChange(TableModelEvent tableModelEvent) {
        if(tableModelEvent.getType() == TableModelEvent.UPDATE) {
            int selectedIndex = model.getSelectedIndex();
            try {
                int index = table.convertRowIndexToView(selectedIndex);
                System.out.println("SELECTION: " + model.getSelectedData() + " at index " + selectedIndex + " ("+ index +" in view)");
                table.changeSelection(index, 0, false, false);
            } catch (IndexOutOfBoundsException ex){
                System.out.println("SELECTION cleared (out of bounds)");
            }
        }
    }

    private class PopupMenuAdapter extends MouseAdapter {

        private final JPopupMenu popupMenu;

        private PopupMenuAdapter(JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                table.changeSelection(table.rowAtPoint(e.getPoint()),0,false,false);
            }
        }

    }

}
