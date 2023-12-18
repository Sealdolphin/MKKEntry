package data.wizard;

import data.DataModel;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultWizardModel<T extends WizardType> implements DataModel<T> {

    protected final List<T> dataList;
    private T selection = null;
    private T editCache = null;
    private final List<ListDataListener> listeners;

    public DefaultWizardModel(List<T> dataList) {
        this.dataList = removeDuplicates(dataList);
        this.listeners = new ArrayList<>();
    }

    @Override
    public T getElementById(String id) {
        return dataList.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public T getSelectedItem() {
        return selection;
    }

    @Override
    public int getSelectedIndex() {
        if (selection != null) {
            return dataList.indexOf(selection);
        }
        return -1;
    }

    @Override
    public void setSelectedItem(Object data) {
        T typedData = castModelData(data);
        selection = typedData;
        refreshEditCache(typedData, false);
    }

    @Override
    public void addData(T data) {
        dataList.add(data);
        int addedIdx = dataList.size() - 1;
        refreshEditCache(data, true);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, addedIdx, addedIdx)));

    }

    @Override
    public void removeData(T data) {
        int removeIdx = dataList.indexOf(data);
        dataList.remove(data);
        refreshEditCache(data, true);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, removeIdx, removeIdx)));

    }

    @Override
    public void updateSelected(T data) {
        if (selection != null) {
            refreshEditCache(null, false);
            int selectedIdx = getSelectedIndex();
            dataList.remove(selection);
            dataList.add(selectedIdx, data);
            selection = data;
            listeners.forEach(l -> l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, selectedIdx, selectedIdx)));
        }
    }

    @Override
    public void deleteSelected() {
        if (selection != null) {
            removeData(selection);
        }
        setSelectedItem(null);
    }

    @Override
    public void replaceData(T oldData, T newData) {
        // Not implemented
    }

    @Override
    public int getSize() {
        return dataList.size();
    }

    @Override
    public T getElementAt(int index) {
        return dataList.get(index);
    }

    @Override
    public boolean isUnique(T other, T self) {
        return dataList.stream()
                .filter(data -> !data.getId().equals(self.getId()))
                .noneMatch(data -> data.equals(other));
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    protected abstract void updateRenderers(T type);

    protected abstract T castModelData(Object data);

    /**
     * Updates the Edit cache. This is a value that changes based on the signaling data
     * If the model have been changed, the cache must be updated (if it's a new data then it's updated, if the
     * data has been deleted, it's cleared). If the selection has been changed, the cached data must be deleted.
     * If the cached data has been saved, the cache must be cleared.
     * @param data the value that sent the cache signal
     * @param dataChanged if the model has been changed
     */
    private void refreshEditCache(T data, boolean dataChanged) {
        if (editCache == null) {
            if (dataChanged) {
                editCache = data;
            }
        } else if (data == null) {
            editCache = null;
        } else if (editCache != data) {
            removeData(editCache);
        } else if (dataChanged) {
            editCache = null;
        }

        updateRenderers(editCache);
    }

    private static <L extends WizardType> List<L> removeDuplicates(List<L> list) {
        List<L> purgedList = new ArrayList<>();

        for (L listItem : list) {
            if (!purgedList.contains(listItem)) {
                purgedList.add(listItem);
            } else {
                System.out.printf("WARNING: (%s - %s) duplicate list item has been removed!%n", listItem.getClass().getSimpleName(), listItem.getId());
            }
        }

        return purgedList;
    }
}
