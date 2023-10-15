package data.entry;

public class EntryCommand {

    private final String name;
    private final String humanReadableName;
    private final String code;

    public EntryCommand(String name, String readableName, String code) {
        this.name = name;
        this.humanReadableName = readableName;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return humanReadableName + " (" + name + "): ";
    }
}
