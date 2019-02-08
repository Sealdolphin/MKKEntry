package control.modifier;

import data.EntryProfile;

import javax.swing.*;
import java.awt.*;

public interface TicketModifier {

    JDialog getTypeWizard(Window parent, EntryProfile profile, int index);

}
