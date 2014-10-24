

package freeh.kitsune.toys;

import freeh.kitsune.GameInfo;
import un.api.collection.ArraySequence;
import un.api.collection.Sequence;
import un.api.tree.Node;
import un.science.encoding.IOException;
import un.system.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class Toys {
    
    private static Sequence ALL = null;
    
    private Toys(){}
    
    public static synchronized Sequence getAll(){
        if(ALL==null){
            final Path path = Paths.resolve(GameInfo.PATH_TOYS);
            ALL = new ArraySequence();
            for(Node n : path.getChildren()){
                final Path p = (Path) n;
                try{
                    if(p.isContainer()){
                        ALL.add(new Toy(p));
                    }
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
        return ALL;
    }
    
    public static Toy getRandomToy(){
        final Sequence candidates = getAll();
        if(candidates.isEmpty()) return null;
        final int index = (int) (Math.random() * (candidates.getSize()- 1));
        return (Toy) candidates.get(index);
    }
    
}
