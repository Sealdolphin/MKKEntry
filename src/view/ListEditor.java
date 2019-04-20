package view;

import control.modifier.Modifier;
import control.modifier.ModifierWizardEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static control.Application.uh;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;

public class ListEditor<T extends Modifier> extends JDialog {

    public ListEditor(List<T> objectList, String title, ModifierWizardEditor<T> editor) {

        //Default settings
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle(title);
        setLayout(new BorderLayout());

        //Operation panel
        JPanel panelOper = new JPanel();
        panelOper.setLayout(new BoxLayout(panelOper,BoxLayout.LINE_AXIS));

        //List panel
        JList<Object> list = new JList<>(objectList.toArray());
        JScrollPane panelList = new JScrollPane(list);

        //Create buttons
        JButton btnCreate = new JButton("Új");
        JButton btnModify = new JButton("Módosít");
        JButton btnDelete = new JButton("Töröl");

        btnCreate.addActionListener(e -> {
            editor.createNew(objectList);
            list.setListData(objectList.toArray());
        });

        btnModify.addActionListener(e->{
            ((T) list.getModel().getElementAt(list.getSelectedIndex())).getModifierWizard(null).setVisible(true);
        });

        btnDelete.addActionListener(e -> {
            //Ask for removal
            int res = JOptionPane.showConfirmDialog(null,"Biztos eltávolítod a következőt a listából: " + list.getModel().getElementAt(list.getSelectedIndex()) + "?"
                    ,uh.getUIStr("MSG","WARNING"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(res == JOptionPane.YES_OPTION) {
                //They will be called in their respected place no worries :)
                //noinspection unchecked
                editor.removeFrom(objectList, (T) list.getModel().getElementAt(list.getSelectedIndex()));
                list.setListData(objectList.toArray());
            }
        });

        //Add buttons to operation panel
        panelOper.add(btnCreate);
        panelOper.add(btnModify);
        panelOper.add(btnDelete);

        //Add listener
        list.addMouseListener(editor);

        //Assemble panels
        add(panelList,CENTER);
        add(panelOper,SOUTH);

        //Set size and location
        setMinimumSize(new Dimension(300,getMinimumSize().height));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

    }
}
