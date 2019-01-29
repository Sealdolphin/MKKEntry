package data;

import control.EntryProfile;
import view.StartupDialog;

import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileData implements DataModel<EntryProfile>, Serializable {

    private List<EntryProfile> profiles;
    private EntryProfile activeProfile;

    public ProfileData() throws Exception {
        profiles = new ArrayList<>();
        StartupDialog helper = new StartupDialog(profiles);
        activeProfile = helper.getProfile();
        System.out.println("Profiles loaded: " + profiles);
        System.out.println("Active profile: " + activeProfile);
    }

    @Override
    public EntryProfile getDataByIndex(int index) {
        return profiles.get(index);
    }

    @Override
    public EntryProfile getDataById(String id) {
        return profiles.stream().filter(p -> p.toString().equals(id)).findAny().orElse(null);
    }

    @Override
    public EntryProfile getSelectedData() {
        return activeProfile;
    }

    @Override
    public int getSelectedIndex() {
        return profiles.indexOf(activeProfile);
    }

    @Override
    public void setSelection(EntryProfile data) {
        activeProfile = data;
    }

    @Override
    public int getDataSize() {
        return profiles.size();
    }

    @Override
    public void addData(EntryProfile data) {
        profiles.add(data);
    }

    @Override
    public void removeData(EntryProfile data) {
        profiles.remove(data);
    }



    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(profiles.toArray());
        out.writeObject(activeProfile);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        Object[] objs = (Object[]) in.readObject();
        profiles = new ArrayList<>();
        for (Object profileObj : objs) {
            profiles.add((EntryProfile) profileObj);
        }
        activeProfile = (EntryProfile) in.readObject();

        System.out.println("Profiles loaded: " + profiles);
        System.out.println("Active profile: " + activeProfile);
    }
}
