package data.entryprofile;

import org.json.JSONObject;

public class ProfileSettings {

    private boolean newIdGeneratedUponEntry;

    private boolean discardingDuplicatesOnImport;

    private boolean discardingUnknownOnImport;

    private boolean nameRequired;

    public ProfileSettings() {}

    public ProfileSettings(ProfileSettings other) {
        this.nameRequired = other.nameRequired;
        this.discardingDuplicatesOnImport = other.discardingDuplicatesOnImport;
        this.discardingUnknownOnImport = other.discardingUnknownOnImport;
        this.newIdGeneratedUponEntry = other.newIdGeneratedUponEntry;
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

    public static ProfileSettings parseProfileSettingsFromJSON(JSONObject settingsJson) {
        ProfileSettings settings = new ProfileSettings();

        settings.setNameRequired(settingsJson.optBoolean("nameRequired", true));
        settings.setDiscardingDuplicatesOnImport(settingsJson.optBoolean("discardDuplicates", true));
        settings.setDiscardingUnknownOnImport(settingsJson.optBoolean("discardUnknown", true));
        settings.setNewIdGeneratedUponEntry(settingsJson.optBoolean("generateNewId", false));

        return settings;
    }
}
