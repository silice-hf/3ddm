
package deskmate;

import un.api.collection.Dictionary;
import un.engine.ui.io.WCSParser;
import un.engine.ui.style.SystemStyle;
import un.engine.ui.style.WStyleClass;
import un.science.encoding.IOException;
import un.system.path.Paths;

/**
 * Starts the application
 */
public class DeskMateRunner {

    public static void main(String[] args) throws IOException {

        //change global application theme
        //copied from unlicense-edit3d, nice dark theme
        try{
            final WStyleClass clazz = SystemStyle.INSTANCE.getClass(SystemStyle.SYSTEM_CLASS);
            final Dictionary properties = WCSParser.readProperties(Paths.resolve("mod>/style/dark-theme.wcs"));
            clazz.setProperties(properties);
        }catch(IOException ex){
            throw new RuntimeException("Failed to load default style "+ex.getMessage(),ex);
        }
        new DeskMateFrame();

    }

}
