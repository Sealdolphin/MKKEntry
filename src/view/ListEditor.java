package view;

import control.modifier.Modifier;
import control.modifier.ModifierWizardEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;

public class ListEditor<T extends Modifier> extends JDialog {


    public ListEditor(List<T> list, String title, ModifierWizardEditor<T> editor) {

        //Default settings
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle(title);
        setLayout(new BorderLayout());

        //Operation panel
        JPanel panelOper = new JPanel();
        panelOper.setLayout(new BoxLayout(panelOper,BoxLayout.LINE_AXIS));
        //Add buttons to operation panel
        panelOper.add(new JButton("Új"));
        panelOper.add(new JButton("Módosít"));
        panelOper.add(new JButton("Töröl"));
        panelOper.add(Box.createHorizontalGlue());
        panelOper.add(new JButton("OK"));
        panelOper.add(new JButton("Mégse"));

        //List panel
        JList objectJList = new JList<>(list.toArray());
        JScrollPane panelList = new JScrollPane(objectJList);

        //Add listener
        objectJList.addMouseListener(editor);

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
