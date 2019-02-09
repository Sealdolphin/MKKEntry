package data;


import java.io.IOException;

public interface DataModel<T> {

    T getDataByIndex(int index);

    T getDataById(String id);

    T getSelectedData();

    int getSelectedIndex();

    void setSelection(T data);

    int getDataSize();

    void addData(T data) throws IOException;

    void removeData(T data);

    void replaceData(T oldData, T newData) throws IOException;
}
