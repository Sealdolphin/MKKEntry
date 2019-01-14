package Window.Wizard;

import Control.EntryModifier.Discount;
import Control.Utility.EntryFilter;
import Control.Utility.ExtensionFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DiscountWizard extends JFrame {

    private ImagePanel panelImg = new ImagePanel(null);

    public static void main(String[] args){
        new DiscountWizard().setVisible(true);
    }

    public DiscountWizard(){
        this(null);
    }

    public DiscountWizard(Discount modify){
        //Set basic window attributes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Kedvezmény beállítása");
        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        //Browse button
        JButton btnBrowse = new JButton("Tallózás");
        btnBrowse.addActionListener(e -> changePicture());

        //Assemble Window
        add(new JLabel("Név"));
        add(new JTextField(32));
        add(new JLabel("META"));
        add(new JTextField(32));
        add(new JLabel("Tooltip"));
        add(new JTextField(32));
        add(new JLabel("Vonalkód képe"));
        add(btnBrowse);
        add(panelImg);
        //Set alignment
        for (Component c: getContentPane().getComponents()) {
            ((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        //Pack
        pack();
        setResizable(false);
    }

    private void changePicture() {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png", "jpg", "jpeg", "bmp", "gif"}, "Minden Képfájl"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png"}, "Portable Network Graphics (.png)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"jpg", "jpeg"}, "Joint Photographic Experts Group (.jpg, .jpeg)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"bmp"}, "Bitmap (.bmp)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"gif"}, "Graphics Interchange Format (.gif)"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                remove(panelImg);
                BufferedImage bi = ImageIO.read(new File(fc.getSelectedFile().getAbsolutePath()));
                panelImg = new ImagePanel(bi);
                panelImg.setAlignmentX(Component.LEFT_ALIGNMENT);
                add(panelImg);
                //Resize window (since the user cannot do it)
                setResizable(true);
                pack();
                setResizable(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
