package Control.Utility;

import Control.Entry;

public interface ExportFilter {

    char separator = '.';

    String applyFilter(Entry entry);

}
