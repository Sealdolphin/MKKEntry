package Window;


import Control.EventHandler;

import javax.swing.*;

/**
 * An abstract class responsible for creating the Application's menu bar.
 * @author Márk Mihalovits
 */
abstract class MainMenu {
    /**
     * Creates a default menu bar with an event handler
     * @param handler the event handler handling the action events
     * @return a default menu bar
     */
    static JMenuBar createMenu(EventHandler handler){
        JMenuBar menuBar = new JMenuBar();
        //Assembling Menus
        menuBar.add(createFileMenu(handler));
        menuBar.add(createEditMenu(handler));
        menuBar.add(createChartsMenu(handler));
        menuBar.add(createProfileMenu(handler));

        return menuBar;
    }

    /**
     * Creates the FILE menu for the menu bar
     * @param handler the event handler handling the action events
     * @return the FILE menu
     */
    private static JMenu createFileMenu(EventHandler handler){
        JMenu fileMenu = new JMenu("Fájl");

        JMenuItem miNew = new JMenuItem("Új Lista");
        miNew.addActionListener(e -> handler.renewState());

        JMenuItem miOpen = new JMenuItem("Állás Betöltése");
        miOpen.addActionListener(e -> handler.loadState());

        JMenuItem miSave = new JMenuItem("Mentés");
        miSave.addActionListener(e -> handler.save(false));

        JMenuItem miSaveAs = new JMenuItem("Mentés Másként");
        miSaveAs.addActionListener(e -> handler.save(true));

        JMenuItem miImport = new JMenuItem("Lista Importálása");
        miImport.addActionListener(e -> handler.importEntries());

        JMenuItem miExport = new JMenuItem("Lista Exportálása");
        miExport.addActionListener(e -> handler.exportEntries());

        fileMenu.add(miNew);
        fileMenu.add(miOpen);
        fileMenu.addSeparator();
        fileMenu.add(miSave);
        fileMenu.add(miSaveAs);
        fileMenu.addSeparator();
        fileMenu.add(miImport);
        fileMenu.add(miExport);

        return fileMenu;
    }

    /**
     * Creates the PROFILE menu for the menu bar
     * @param handler the event handler handling the action events
     * @return the PROFILE menu
     */
    private static JMenu createProfileMenu(EventHandler handler) {
        JMenu menuProfiles = new JMenu("Profilok");

        JMenuItem miChangeProfile = new JMenuItem("Profil váltása");
        miChangeProfile.addActionListener(e -> handler.changeProfile());
        JMenuItem miEditProfile = new JMenuItem("Profil szerkesztése");
        miEditProfile.addActionListener(e -> handler.editProfile());

        menuProfiles.add(new JMenuItem("Új profil"));
        menuProfiles.add(miChangeProfile);
        menuProfiles.add(miEditProfile);

        return menuProfiles;
    }

    /**
     * Creates the CHARTS menu for the menu bar
     * TODO: waiting for implementation
     * @param handler the event handler handling the action events
     * @return the CHARTS menu
     */
    private static JMenu createChartsMenu(EventHandler handler){
        return new JMenu("Statisztikák");
    }

    /**
     * Creates the EDIT menu for the menu bar
     * TODO: waiting for implementation
     * @param handler the event handler handling the action events
     * @return the EDIT menu
     */
    private static JMenu createEditMenu(EventHandler handler){
        return new JMenu("Szerkesztés");
    }
}
