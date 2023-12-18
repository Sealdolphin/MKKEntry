package view.main.panel.wizard;

import data.DataModel;

public interface ListUpdateListener<T> {

    void listUpdated(DataModel<T> model);

}
