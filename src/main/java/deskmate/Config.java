package deskmate;

import un.api.collection.Sequence;
import un.api.tree.Node;
import un.science.encoding.IOException;
import un.science.encoding.color.Color;
import un.system.path.Path;

/**
 * general config
 */
public class Config {

    public static float BLOOM_RATIO = 0.1f;
    public static int TOON_NBSHADE = 3;
    public static float BORDER_WIDTH = 0.001f;
    public static Color BORDER_COLOR = Color.BLACK;
    
    public static void search(Sequence models, Sequence animations, Sequence audios) throws IOException {
        search(View.DATAPATH, models, animations, audios);
    }

    public static void search(Path candidate, Sequence models, Sequence animations, Sequence audios) throws IOException {
        if (!candidate.isContainer()) {
            String name = candidate.getName().toLowerCase();
            //select only mmd and xna files
            if (name.endsWith(".pmd") || name.endsWith(".pmx") || name.endsWith(".mesh.ascii")
                    || name.endsWith(".xps") || name.endsWith(".mesh")) {
                models.add(candidate);
            } else if (name.endsWith(".vmd")) {
                animations.add(candidate);
            } else if (name.endsWith(".wav")) {
                audios.add(candidate);
            }
        } else {
            for (Node child : candidate.getChildren()) {
                search((Path) child, models, animations, audios);
            }
        }
    }

}
