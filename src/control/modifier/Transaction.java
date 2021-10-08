package control.modifier;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transaction implements Serializable, Modifier {
    private final LocalTime timeStamp;
    private String name;
    private int value;

    public Transaction(String name, int value){
        timeStamp = LocalTime.now();
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return timeStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " - " + name + ": " + value;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new TransactionWizard(parent);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean validate() {
        return !name.isEmpty() && value != 0;
    }

    public static class TransactionListener extends ModifierWizardEditor<Transaction> {

        public TransactionListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Transaction> objectList) {
            Transaction newTransaction = new Transaction("", 0);
            ModifierDialog wizard = newTransaction.getModifierWizard(null);
            int result = wizard.open();
            if(result == 0){
                objectList.add(newTransaction);
            }
        }
    }

    class TransactionWizard extends ModifierDialog {

        private final JSpinner spValue = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,5));
        private final JTextField tfName = new JTextField();

        TransactionWizard(Window parent) {
            super(parent, "Új Tranzakció");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            btnSave.addActionListener(e -> saveTransaction());
            GroupLayout layout = new GroupLayout(body);
            layout.setAutoCreateGaps(true);
            body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            body.setLayout(layout);

            JLabel lbSum = new JLabel("Összeg:");
            JLabel lbSign = new JLabel("Aláírja:");
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lbSum).addComponent(lbSign))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(spValue).addComponent(tfName))
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(lbSum).addComponent(spValue))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(lbSign).addComponent(tfName))
            );

            finishDialog(parent);
        }

        private void saveTransaction() {
            int transaction = Integer.parseInt(spValue.getValue().toString());
            if (!tfName.getText().isEmpty() && transaction != 0) {
                name = tfName.getText();
                value = transaction;
                result = 0;
                dispose();
            } else {
                if (transaction == 0) {
                    JOptionPane.showMessageDialog(this,"Nem adhatsz nullát a kasszához!","ERROR",JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,"Adj meg egy nevet!","ERROR",JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }
}
