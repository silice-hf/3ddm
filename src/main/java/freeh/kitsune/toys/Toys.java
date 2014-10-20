

package freeh.kitsune.toys;

import freeh.kitsune.GameInfo;
import un.api.collection.ArraySequence;
import un.api.collection.Collections;
import un.api.collection.Sequence;
import un.api.tree.Node;
import un.science.encoding.IOException;
import un.system.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class Toys {
    
    private static Toy[] TOYS = null;
    
    private Toys(){}
    
    public static synchronized Toy[] getToys(){
        if(TOYS==null){
            final Path path = Paths.resolve(GameInfo.PATH_TOYS);
            final Sequence seq = new ArraySequence();
            for(Node n : path.getChildren()){
                final Path p = (Path) n;
                try{
                    if(p.isContainer()){
                        seq.add(new Toy(p));
                    }
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            TOYS = new Toy[seq.getSize()];
            Collections.copy(seq, TOYS, 0);
        }
        return TOYS;
    }
    
    public static Toy getRandomToy(){
        final Toy[] toys = getToys();
        final int modelIndex = (int) (Math.random() * (toys.length- 1));
        return toys[modelIndex];
    }
    
}
