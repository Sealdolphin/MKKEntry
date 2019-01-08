package Window;

import Control.EntryController;

/**
 * An interface to listen to the reading state change
 * @author Márk Mihalovits
 */
public interface ReadingFlagListener {
    /**
     * Event happening when the reading flag has changed
     * @param flag the flag's current status
     */
    void flagChange(EntryController.readCodeFlag flag);
}