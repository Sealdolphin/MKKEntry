package data;


import javax.swing.*;
import java.io.IOException;

public interface DataModel<T> extends ListModel<T> {

    T getElementById(String id);

    T getSelectedData();

    int getSelectedIndex();

    void setSelection(T data);

    void addData(T data);

    void removeData(T data);

    void updateSelected(T data);

    void deleteSelected();

    ListCellRenderer<T> createRenderer();

    @Deprecated
    void replaceData(T oldData, T newData) throws IOException;
}
