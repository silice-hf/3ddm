package deskmate;

import java.util.List;
import un.api.tree.Node;
import un.science.encoding.IOException;
import un.system.path.Path;
import un.system.path.Paths;

/**
 * general config
 */
public class Config {

    public static void search(List models, List animations) throws IOException {
        search(Paths.resolve("file>./data"), models, animations);
    }

    public static void search(Path candidate, List models, List animations) throws IOException {
        if (!candidate.isContainer()) {
            String name = candidate.getName().toLowerCase();
            //select only mmd and xna files
            if (name.endsWith(".pmd") || name.endsWith(".pmx") || name.endsWith(".mesh.ascii")
                    || name.endsWith(".xps") || name.endsWith(".mesh")) {
                models.add(candidate);
            }else if (name.endsWith(".vmd")) {
                animations.add(candidate);
            }
        } else {
            for (Node child : candidate.getChildren()) {
                search((Path) child, models,animations);
            }
        }
    }

}
