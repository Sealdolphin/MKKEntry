package Control.Utility;

import Control.Entry;

import static Control.Entry.Member.*;

public class DefaultFilter extends ExportFilter {
    @Override
    public String applyFilter(Entry entry) {

        return String.valueOf(entry.getValue(M_UID.ordinal())) +
                separator +
                entry.getValue(M_NAME.ordinal()) +
                separator +
                entry.getValue(M_ENTRY.ordinal()) +
                separator +
                entry.getValue(M_LEAVE.ordinal()) +
                separator +
                entry.getValue(M_ENTERED.ordinal());
    }
}
