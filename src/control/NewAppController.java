package control;

import data.util.ReadingFlag;
import view.main.interactive.ReadFlagListener;

import java.util.ArrayList;
import java.util.List;

public class NewAppController implements EntryCodeReader {
    
    private ProgramStateListener operationsController;
    private ReadingFlag readingFlag = ReadingFlag.FL_DEFAULT;

    private final List<ReadFlagListener> flagListeners = new ArrayList<>();

    public NewAppController() {

    }

    @Override
    public void setReadingFlag(ReadingFlag newFlag) {
        readingFlag = newFlag;
        flagListeners.forEach(l -> l.readingFlagChanged(newFlag));
    }

    @Override
    public void readEntryCode(String code) {

    }

    @Override
    public void receiveBarCode(String barcode) {

    }

    @Override
    public void addReadingFlagListener(ReadFlagListener listener) {
        flagListeners.add(listener);
    }
}
