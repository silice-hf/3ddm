

package freeh.kitsune.model;

import un.science.encoding.color.Color;

/**
 *
 */
public class SilhouetteInfo {
    
    public Color color = Color.WHITE;
    public float width = 0.003f;

    public SilhouetteInfo() {
    }
    
    public SilhouetteInfo(Color color, float width) {
        this.color = color;
        this.width = width;
    }
    
    
}
