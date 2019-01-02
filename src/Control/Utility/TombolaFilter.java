package Control.Utility;

import Control.Entry;

import static Control.Entry.Member.M_UID;

public class TombolaFilter implements ExportFilter {

    @Override
    public String applyFilter(Entry entry) {
        return String.valueOf(entry.getValue(M_UID.ordinal()));
    }
}
