package data;

import view.StartupDialog;

import javax.swing.event.ListDataListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static control.Application.uh;

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
    public EntryProfile getElementAt(int index) {
        return profiles.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {

    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }

    @Override
    public EntryProfile getElementById(String id) {
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
    public int getSize() {
        return profiles.size();
    }

    @Override
    public void addData(EntryProfile data) throws IOException
    {
        if(profiles.stream().filter(p -> p.toString().equals(data.toString())).findAny().orElse(null) != null) throw new IOException(uh.getUIStr("ERR","PROFILE_CONFLICT"));
        profiles.add(data);
    }

    @Override
    public void removeData(EntryProfile data) {
        profiles.remove(data);
    }

    @Override
    public void updateSelection(EntryProfile data) {
        // Not implemented
    }

    @Override
    public void replaceData(EntryProfile oldData, EntryProfile newData) throws IOException {
        if(!oldData.toString().equals(newData.toString())){
            addData(newData);
            removeData(oldData);
        } else {
            removeData(oldData);
            addData(newData);
        }
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
