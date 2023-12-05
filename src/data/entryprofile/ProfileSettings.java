package data.entryprofile;

public class ProfileSettings {

    private boolean newIdGeneratedUponEntry;

    private boolean discardingDuplicatesOnImport;

    private boolean discardingUnknownOnImport;

    private boolean nameRequired;

    public ProfileSettings() {

    }

    public boolean isNewIdGeneratedUponEntry() {
        return newIdGeneratedUponEntry;
    }

    public void setNewIdGeneratedUponEntry(boolean newIdGeneratedUponEntry) {
        this.newIdGeneratedUponEntry = newIdGeneratedUponEntry;
    }

    public boolean isDiscardingDuplicatesOnImport() {
        return discardingDuplicatesOnImport;
    }

    public void setDiscardingDuplicatesOnImport(boolean discardingDuplicatesOnImport) {
        this.discardingDuplicatesOnImport = discardingDuplicatesOnImport;
    }

    public boolean isDiscardingUnknownOnImport() {
        return discardingUnknownOnImport;
    }

    public void setDiscardingUnknownOnImport(boolean discardingUnknownOnImport) {
        this.discardingUnknownOnImport = discardingUnknownOnImport;
    }

    public boolean isNameRequired() {
        return nameRequired;
    }

    public void setNameRequired(boolean nameRequired) {
        this.nameRequired = nameRequired;
    }
}
