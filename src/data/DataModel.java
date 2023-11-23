package data;


import javax.swing.*;
import java.io.IOException;

public interface DataModel<T> extends ListModel<T> {

    T getElementById(String id);

    T getSelectedData();

    int getSelectedIndex();

    void setSelection(T data);

    void addData(T data) throws IOException;

    void removeData(T data);

    void updateSelection(T data);

    @Deprecated
    void replaceData(T oldData, T newData) throws IOException;
}
