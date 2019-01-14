package Control.Utility;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFilter extends FileFilter {

    private String[] validExtensions;
    private String description;

    public ExtensionFilter(String[] extensions, String description){
        validExtensions = extensions;
        this.description = description;
    }

    private String getExtension(File file){
        String filename = file.getName();
        String ext = null;

        int index = filename.lastIndexOf('.');
        if(index > 0 && index < filename.length() - 1)
            ext = filename.substring(index + 1).toLowerCase();

        return ext;
    }

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

    @Override
    public String getDescription() {
        return description;
    }

}
