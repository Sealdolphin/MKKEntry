package view;

import control.modifier.Transaction;
import data.DataModel;
import data.entry.Entry;
import data.entryprofile.EntryProfile;
import view.renderer.TransactionRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static control.Application.uh;

public class CashPanel extends JPanel {

    private final JButton btnNewTransaction = new JButton("Új tranzakció");
    private final JButton btnDeleteTransaction = new JButton("Törlés");
    private final JButton btnDeleteAllTransactions = new JButton("Összes törlése");
    private final JScrollPane transactionPane;
    private final JLabel lbCashTitle = new JLabel("Kassza állapota:");
    private final JLabel lbTransactions = new JLabel("Eddigi tranzakciók:");
    private final JLabel lbCash = new JLabel(String.format(Locale.GERMAN,"%,d Forint", 0));
    private final GroupLayout layout = new GroupLayout(this);

    public CashPanel(EntryProfile profile, DataModel<Entry> data, Window parent) {
        Transaction.TransactionListener editor = new Transaction.TransactionListener(parent);
        transactionPane = new JScrollPane(createList(profile, data, editor));
        createFonts();
        setupComponents();
    }

    private JList<Transaction> createList(EntryProfile profile, DataModel<Entry> data, Transaction.TransactionListener editor) {
        List<Transaction> transactionModel = new ArrayList<>();
        JList<Transaction> transactions = new JList<>(transactionModel.toArray(new Transaction[0]));
        transactions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactions.setCellRenderer(new TransactionRenderer());
        transactions.addListSelectionListener(e -> btnDeleteTransaction.setEnabled(true));
        recalculateCash(data, transactionModel);

        btnNewTransaction.addActionListener(e -> {
            editor.createNew(transactionModel);
            transactions.setListData(transactionModel.toArray(new Transaction[0]));
            btnDeleteTransaction.setEnabled(false);
            btnDeleteAllTransactions.setEnabled(transactionModel.size() > 0);
            recalculateCash(data, transactionModel);
        });

        btnDeleteTransaction.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(null,"Biztos eltávolítod a következőt a listából: " + transactions.getModel().getElementAt(transactions.getSelectedIndex()) + "?"
                    ,uh.getUIStr("MSG","WARNING"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(res == JOptionPane.YES_OPTION) {
                editor.removeFrom(transactionModel, transactions.getModel().getElementAt(transactions.getSelectedIndex()));
                transactions.setListData(transactionModel.toArray(new Transaction[0]));
            }
            btnDeleteTransaction.setEnabled(false);
            btnDeleteAllTransactions.setEnabled(transactionModel.size() > 0);
            recalculateCash(data, transactionModel);
        });
        btnDeleteTransaction.setEnabled(false);

        btnDeleteAllTransactions.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(null,"Biztos törlöd az összes tranzakciót?"
                    ,uh.getUIStr("MSG","WARNING"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(res == JOptionPane.YES_OPTION) {
                transactionModel.clear();
                transactions.setListData(transactionModel.toArray(new Transaction[0]));
            }
            btnDeleteAllTransactions.setEnabled(transactionModel.size() > 0);
            recalculateCash(data, transactionModel);
        });
        btnDeleteAllTransactions.setEnabled(transactionModel.size() > 0);

        return transactions;
    }

    private void recalculateCash(DataModel<Entry> data, List<Transaction> transactions) {
        int total = 0;
        //Calculate Transactions
        for (Transaction transaction: transactions) {
            total += transaction.getValue();
        }
        //Calculate tickets
        for (int i = 0; i < data.getSize(); i++) {
            total += data.getElementAt(i).getAllFees();
        }
        setCashValue(total);
    }

    private void createFonts() {
        Font fntButton = new Font(Font.SANS_SERIF, Font.PLAIN, 22);
        Font fntCash = new Font(Font.SANS_SERIF, Font.BOLD, 50);
        btnNewTransaction.setFont(fntButton);
        btnDeleteTransaction.setFont(fntButton);
        btnDeleteAllTransactions.setFont(fntButton);
        btnDeleteTransaction.setForeground(new Color(161, 22, 22));
        lbCash.setForeground(new Color(60, 103, 31));
        lbCash.setFont(fntCash);
    }

    public void setCashValue(int value) {
        lbCash.setText(String.format(Locale.GERMAN,"%,d Forint", value));
    }

    private void setupComponents() {
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lbTransactions)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(transactionPane)
                                        .addGroup(
                                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(btnNewTransaction, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnDeleteTransaction, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnDeleteAllTransactions, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        )
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lbCashTitle)
                                        .addComponent(lbCash)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(lbTransactions)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(transactionPane)
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(btnNewTransaction)
                                                        .addComponent(btnDeleteTransaction)
                                                        .addComponent(btnDeleteAllTransactions)
                                        )
                        )
                        .addComponent(lbCashTitle)
                        .addComponent(lbCash)
        );
    }

}
