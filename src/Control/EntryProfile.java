package Control;

import Control.EntryModifier.Discount;
import Control.EntryModifier.TicketType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.BoxLayout.PAGE_AXIS;

public class EntryProfile {
    private String name;
    private List<TicketType> types;
    private List<Discount> discounts;

    public EntryProfile(){
        types = new ArrayList<>();
        discounts = new ArrayList<>();
        //TODO: Custom profiles

        discounts.add(new Discount("Büfé",
                "Barcodes\\foodSale.png",
                "Hozott sütit, vagy üdítőt",
                "FOOD_SALE",500));
        discounts.add(new Discount("Büfé",
                "Barcodes\\foodSale.png",
                "Hozott sütit, vagy üdítőt",
                "FOOD_SALE",500));

    }

    /**
     * Creates a side menu with additional attributes and options
     * @return a JPanel containing the sideBar components
     */
    public JPanel createSideMenu(){
        //Setting up layout
        JPanel panelSide = new JPanel();
        panelSide.setLayout(new BoxLayout(panelSide,PAGE_AXIS));

        //Iterate through discounts
        BufferedImage barCode;

        for (Discount discount :
                discounts) {
            try{
                //Adding image and image label
                barCode = ImageIO.read(new File(discount.getImage()));
                JLabel label = new JLabel(discount.getLabel());
                label.setFont(new Font(label.getFont().getName(),Font.PLAIN,20));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setToolTipText(discount.getName());
                panelSide.add(new ImagePanel(barCode));
                panelSide.add(label);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(new JFrame(),"Nem találom a vonalkódképet a kedvezményhez:" +
                        discount.getName(),"Hiba",JOptionPane.ERROR_MESSAGE);
            }
        }

        return panelSide;
    }

    List<String> getDiscountMeta(){
        List<String> discountMetadata = new ArrayList<>();
        for (Discount discount :
                discounts) {
            discountMetadata.add(discount.getMeta());
        }
        return discountMetadata;
    }

    /**
     * An empty JPanel containing a custom image
     */
    private class ImagePanel extends JPanel {
        private BufferedImage image;

        ImagePanel(BufferedImage image){
            this.image = image;
            //Set layout to fill available space
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            if(image != null) {
                //Adding Empty space to fill out image
                add(Box.createRigidArea(new Dimension(image.getWidth(), image.getHeight())));
                //Setting maximum height not to be infinity
                setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
            }
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(image,0,0,this);
        }

    }

}
