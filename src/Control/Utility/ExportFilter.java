package Control.Utility;

import Control.Entry;

public abstract class ExportFilter {
    public static final char separator = ',';

    public abstract String applyFilter(Entry entry) ;

}
