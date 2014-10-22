

package freeh.kitsune.tools;

import un.api.character.Chars;
import un.science.encoding.IOException;
import un.science.geometry.s2d.Circle;
import un.science.geometry.s2d.Geometry2D;
import un.system.path.Path;

/**
 *
 */
public class Weapon extends Tool{

    private static final Chars PATH_SPEED = new Chars("speed");
    private static final Chars PATH_RADIUS = new Chars("radius");
    
    public Weapon(Path path) throws IOException {
        super(path);
    }
    
    public double getSpeed() {
        return getPathValueNumber(PATH_SPEED, 0.2).doubleValue();
    }
    
    public Geometry2D getShape() {
        return new Circle(getPathValueNumber(PATH_RADIUS, 20.0).doubleValue());
    }
    
}
