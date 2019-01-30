package control.utility.file;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * FileDialog-hoz készített egyéni személyreszabható kiterjesztés-filter.
 */
public class ExtensionFilter extends FileFilter {

    /**
     * A fájltípus (csoport) elfogadott kiterjesztése(i)
     */
    private String[] validExtensions;
    /**
     * A fájltípushoz (csoporthoz) tartozó leírás
     */
    private String description;

    /**
     * Alapméretezett konstruktor.
     * Létrehozza az egyéni filtert
     * @param extensions az elfogadott kiterjesztések tömbje
     * @param description a fájlcsoport leírása
     */
    public ExtensionFilter(String[] extensions, String description){
        validExtensions = extensions;
        this.description = description;
    }

    /**
     * Egy adott fájl kiterjesztését ellenőrzi
     * @param file a fájl
     * @return a fájl kiterjesztése (utolsó . utáni karakterek)
     */
    private String getExtension(File file){
        String filename = file.getName();
        String ext = null;

        int index = filename.lastIndexOf('.');
        if(index > 0 && index < filename.length() - 1)
            ext = filename.substring(index + 1).toLowerCase();

        return ext;
    }

    /**
     * Inherited from FileFilter
     * @param f the file in question
     * @return true if the extension is an element of the valid extensions false otherwise
     */
    @Override
    public boolean accept(File f) {
        if(f.isDirectory()){
            return true;
        }

        String extension = getExtension(f);
        if(extension != null){
            for (String ext : validExtensions) {
                if (extension.equals(ext)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Inherited from FileFilter
     * @return the description of the filter
     */
    @Override
    public String getDescription() {
        return description;
    }

}
