package data;


import javax.swing.*;
import java.io.IOException;

public interface DataModel<T> extends ComboBoxModel<T> {

    T getElementById(String id);

    int getSelectedIndex();

    void addData(T data);

    void removeData(T data);

    void updateSelected(T data);

    void deleteSelected();

    ListCellRenderer<T> createListRenderer();

    @Deprecated
    void replaceData(T oldData, T newData) throws IOException;

    boolean isUnique(T other, T self);
}
