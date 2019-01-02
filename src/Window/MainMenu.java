package Window;


import Control.EventHandler;

import javax.swing.*;

abstract class MainMenu {
    static JMenuBar createMenu(EventHandler handler){
        JMenuBar menuBar = new JMenuBar();
        //Assembling Menus
        menuBar.add(createFileMenu(handler));
        menuBar.add(createEditMenu(handler));
        menuBar.add(createChartsMenu(handler));

        return menuBar;
    }

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

    private static JMenu createChartsMenu(EventHandler handler){
        return new JMenu("Statisztikák");
    }

    private static JMenu createEditMenu(EventHandler handler){
        return new JMenu("Szerkesztés");
    }
}
