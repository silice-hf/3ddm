package deskmate;

import deskmate.util.XNASkeletonToVMD;
import un.api.collection.Collection;
import un.api.collection.Iterator;
import un.engine.opengl.mesh.MultipartMesh;
import un.engine.opengl.physic.SkeletonAnimation;
import un.storage.model3d.Model3DStore;
import un.storage.model3d.Model3Ds;
import un.system.path.Path;

/**
 * Load models and animation functions
 */
public class DataLoader {

    public static MultipartMesh loadModel(Path modelPath) {
        MultipartMesh mesh = null;

        try {
            Model3DStore store = Model3Ds.read(modelPath);
            Collection elements = store.getElements();
            Iterator ite = elements.createIterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (obj instanceof MultipartMesh) {
                    mesh = (MultipartMesh) obj;
                    
                    //change xna models skeleton to adapth vmd animations
                    //does not work correctly yet :'(
//                    final String name = modelPath.toURI().toLowerCase();
//                    if (name.endsWith(".mesh.ascii") || name.endsWith(".xps") || name.endsWith(".mesh")) {
//                        XNASkeletonToVMD.adapt(mesh.getSkeleton());
//                    }
                    
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mesh;
    }

    public static SkeletonAnimation loadAnimation(Path animationPath) {
        SkeletonAnimation animation = null;

        try {
            Model3DStore store = Model3Ds.read(animationPath);
            Collection elements = store.getElements();
            Iterator ite = elements.createIterator();
            while (ite.hasNext()) {
                Object obj = ite.next();
                if (obj instanceof SkeletonAnimation) {
                    animation = (SkeletonAnimation) obj;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return animation;
    }

}
