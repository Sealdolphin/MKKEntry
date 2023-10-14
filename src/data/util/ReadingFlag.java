package data.util;

import java.awt.*;

/**
 *  For the different reading operations
 */
public enum ReadingFlag {
    FL_IS_LEAVING("leave","Kilépésre vár",new Color(0xffcc00),Color.BLACK),
    FL_IS_DELETE("delete","Törlésre vár",new Color(0xaa0000),Color.WHITE),
    FL_DEFAULT("default","Belépésre vár",new Color(0x00aa00),Color.BLACK),
    FL_GENERATE_NEW("generate", "Belépőkód generálása", Color.BLACK, Color.BLACK);

    private final String flagMeta;
    private final String labelInfo;
    private final Color labelColor;
    private final Color fgColor;

    ReadingFlag(String meta, String info, Color color, Color fg){
        flagMeta = meta;
        labelInfo = info;
        labelColor = color;
        fgColor = fg;
    }

    public String getInfo(){ return labelInfo; }
    public String getMeta(){ return flagMeta; }
    public Color getColor(){ return labelColor; }
    public Color getTextColor(){ return fgColor; }
}