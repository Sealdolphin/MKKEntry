package data.wizard;

import control.wizard.WizardEditor;
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

    @Override
    public WizardEditor<?> createWizard() {
        return null;
    }

    //TODO: add ticket types here. Piros cipőben gyorsabban lehet futni.


}
