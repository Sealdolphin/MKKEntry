package Control.Utility;

import Control.Entry;

import static Control.Entry.Member.M_ENTERED;
import static Control.Entry.Member.M_UID;

public class TombolaFilter implements ExportFilter {

    @Override
    public String applyFilter(Entry entry) {
        if(entry.getValue(M_ENTERED.ordinal()).equals(true))
            return String.valueOf(entry.getValue(M_UID.ordinal()));

        return null;
    }
}
