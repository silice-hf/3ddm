
package deskmate;

import un.engine.ui.io.WCSParser;
import un.engine.ui.style.SystemStyle;
import un.science.encoding.IOException;
import un.system.path.Paths;

/**
 * Starts the application
 */
public class DeskMateRunner {

    public static void main(String[] args) throws IOException {

        //change global application theme
        //copied from unlicense-edit3d, nice dark theme
        try {
            SystemStyle.INSTANCE.getRules().addAll(WCSParser.readStyle(Paths.resolve("mod>/style/dark-theme.wcs")).getRules());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load default style "+ex.getMessage(),ex);
        }
        new DeskMateFrame();

    }

}
