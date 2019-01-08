package Control;

import Control.EntryModifier.Discount;
import Control.EntryModifier.TicketType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    private String[] defaultCommands;
    private String entryCode;

    /**
     * The Defaults of the command strings
     */
    private static String[] defaults = {"leave","delete"};

    private EntryProfile(){
        types = new ArrayList<>();
        discounts = new ArrayList<>();
    }

    /**
     * Deafult constructor
     * @param jsonProfile the JSONObject of the Profile
     * @return a fully parsed profile
     */
    public static EntryProfile parseProfileFromJson(JSONObject jsonProfile){
        EntryProfile profile = new EntryProfile();
        //Loading discounts
        JSONArray jArray = (JSONArray) jsonProfile.get("discounts");
        for (Object discountObject: jArray) {
            profile.discounts.add(Discount.parseDiscountFromJson((JSONObject)discountObject));
        }
        //Loading Ticket types
        jArray = (JSONArray) jsonProfile.get("tickets");
        for (Object discountObject: jArray) {
            profile.types.add(TicketType.parseTicketTypeFromJson((JSONObject)discountObject));
        }

        //Setting the default ticket type
        String defType = jsonProfile.get("defaultType").toString();
        TicketType.defaultType = profile.types.stream().filter(ticketType -> ticketType.getName().equals(defType)).findAny().orElse(profile.types.get(0));

        //Loading commands
        String[] commands = new String[2];
        commands[0] = ((JSONObject)jsonProfile.get("commands")).get(defaults[0]).toString();
        commands[1] = ((JSONObject)jsonProfile.get("commands")).get(defaults[1]).toString();

        //Loading basic information
        profile.name = jsonProfile.get("name").toString();
        profile.entryCode = jsonProfile.get("entry_code").toString();
        profile.defaultCommands = commands;

        return profile;
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

        for (Discount discount : discounts) {
            try{
                //Adding image and image label
                barCode = ImageIO.read(new File(discount.getImage()));
                JLabel label = new JLabel(discount.getLabel());
                label.setFont(new Font(label.getFont().getName(),Font.PLAIN,20));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setToolTipText(discount.getName());
                panelSide.add(new ImagePanel(barCode));
                panelSide.add(label);
                System.out.println("Loaded discount: " + discount.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(new JFrame(),"Nem találom a vonalkódképet a kedvezményhez\n" +
                        discount.getName() + ": " + discount.getImage(),"Hiba",JOptionPane.ERROR_MESSAGE);
            }
        }

        return panelSide;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets up the controller meta information from the stored profile
     * @param controller the controller to be set up
     */
    void setController(EntryController controller) {
        //Set MetaData for discounts
        List<String> discountMeta = new ArrayList<>();
        for (Discount discount : discounts) {
            discountMeta.add(discount.getMeta());
        }
        controller.setMetaData(entryCode,discountMeta,defaultCommands);
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
