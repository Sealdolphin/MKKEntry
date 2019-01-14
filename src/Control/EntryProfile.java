package Control;

import Control.EntryModifier.Discount;
import Control.EntryModifier.TicketType;
import Window.Wizard.ImagePanel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Window.Main.ui;

import static javax.swing.BoxLayout.PAGE_AXIS;

public class EntryProfile {
    private String name;

    private List<TicketType> types;
    private List<Discount> discounts;

    private String[] defaultCommands;
    private CodeRestraints entryCode;

    /**
     * The Defaults of the command strings
     */
    private static String[] defaults = {"leave","delete"};

    public void modifyDiscount(int index,Discount discount){
        if(index > 0) {
            discounts.remove(index);
            discounts.add(index, discount);
        } else discounts.add(discount);

        //Revalide sidemenu etc...
    }

    private EntryProfile(JSONObject codeRestraints) throws Exception {
        types = new ArrayList<>();
        discounts = new ArrayList<>();
        entryCode = new CodeRestraints(
                codeRestraints.get("start"),
                codeRestraints.get("regex")
        );
    }

    /**
     * Deafult constructor
     * @param jsonProfile the JSONObject of the Profile
     * @return a fully parsed profile
     */
    public static EntryProfile parseProfileFromJson(JSONObject jsonProfile) throws Exception {
        //Loading basic information
        JSONObject jsonCode = (JSONObject) jsonProfile.get("entry_code");
        EntryProfile profile = new EntryProfile(jsonCode);
        profile.name = jsonProfile.get("name").toString();

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

        //Add the label first
        JLabel lbHeader = new JLabel(ui.getUIStr("UI","DISCOUNT_BTN"));
        lbHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSide.add(lbHeader);

        for (Discount discount : discounts) {
            //Adding image and image label
            panelSide.add(discount.getDiscountPanel());
        }

        return panelSide;
    }

    public String getName() {
        return name;
    }

    TicketType identifyTicketType(String name){
        return types.stream().filter(type -> type.getName().equals(name)).findAny().orElse(TicketType.defaultType);
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
        controller.setMetaData(entryCode.start,discountMeta,defaultCommands);
    }

    String validateCode(String code) throws IOException{
        String validID;
        //Removing code start part
        validID = code.replace(entryCode.start,"").toUpperCase().trim();
        //Checking constraints
        if(!validID.matches(entryCode.pattern)) throw new IOException("A kód nem követi a megadott mintát");

        return validID;
    }

    private class CodeRestraints {
        final String start;
        final String pattern;
        private CodeRestraints(Object start, Object pattern) throws Exception {
            if(start == null || pattern == null) throw new Exception();
            this.start = start.toString();
            this.pattern = pattern.toString();
        }
    }
    

}
