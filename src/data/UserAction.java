package data;

import data.entry.Entry;

public class UserAction {

    private final Entry actualState;
    private final Entry previousState;

    public UserAction(Entry actualState, Entry previousState) {
        System.out.println("Created User Action! old Entry: " + previousState.toString() + " new entry: " + actualState.toString());
        this.actualState = actualState;
        this.previousState = previousState;
    }

    public Entry undo() {
        actualState.copyEntry(previousState);
        return actualState;
    }
}
