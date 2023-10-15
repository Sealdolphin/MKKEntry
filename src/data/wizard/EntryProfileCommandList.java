package data.wizard;

import data.entry.EntryCommand;

import java.util.ArrayList;
import java.util.List;

public class EntryProfileCommandList implements WizardType {

    private final List<EntryCommand> commands;

    public EntryProfileCommandList() {
        commands = new ArrayList<>();
    }

    public void addEntryCommand(EntryCommand command) {
        commands.add(command);
    }


}
