package control;

import data.util.ReadingFlag;
import view.main.interactive.ReadFlagListener;

public interface EntryCodeReader {

    void setReadingFlag(ReadingFlag newFlag);

    void readEntryCode(String code);    // TODO: read raw entry code

    void receiveBarCode(String barcode);

    void addReadingFlagListener(ReadFlagListener listener);

}
