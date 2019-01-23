package view.main;

import control.EntryController;

import javax.swing.*;

class Header extends JPanel {

    Header(EntryController controller) {
        JLabel lbProfile = new JLabel("Profil: " + controller.getProfileName());
        add(lbProfile);
        JButton btnChangeProfile = new JButton("Válassz");
        btnChangeProfile.addActionListener(event -> lbProfile.setText(controller.changeProfile()));
        add(btnChangeProfile);
    }
}
