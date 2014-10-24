

package freeh.kitsune.model;

import freeh.kitsune.MetaObject;
import freeh.kitsune.model.clothes.Clothe;
import freeh.kitsune.model.clothes.ClotheState;
import freeh.kitsune.model.preset.PresetModelClothe;
import un.api.collection.ArraySequence;
import un.api.collection.CollectionEvent;
import un.api.collection.Sequence;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.physic.skeleton.Skeleton;
import un.engine.opengl.GLProcessContext;
import un.engine.opengl.phase.picking.PickResetPhase;
import un.engine.opengl.scenegraph.GLNode;
import un.science.encoding.IOException;
import un.system.path.Path;

/**
 *
 */
public abstract class Model extends MetaObject {
    
    protected final Sequence clothes = new ArraySequence();
    
    public Model() {
        clothes.addEventListener(CollectionEvent.class, new EventListener() {
            public void receiveEvent(Event event) {
                clothesChanged((CollectionEvent)event);
            }
        });
    }
    
    public Model(Path metaPath) {
        super(metaPath);
    }
    
    public abstract GLNode getNode();
    
    public abstract Skeleton getSkeleton();
    
    public Sequence getClothes(){
        return clothes;
    }
    
    public synchronized void unload(GLProcessContext context){
        
    }
    
    protected void clothesChanged(CollectionEvent event){
        final int type = event.getType();
        if(type == CollectionEvent.TYPE_ADD){
            for(Object obj : event.getNewElements()){
                ((Clothe)obj).install(this);
            }
        }else if(type == CollectionEvent.TYPE_REMOVE){
            for(Object obj : event.getOldElements()){
                ((Clothe)obj).uninstall(this);
            }
        }else if(type == CollectionEvent.TYPE_REPLACE){
            for(Object obj : event.getNewElements()){
                ((Clothe)obj).install(this);
            }
            for(Object obj : event.getOldElements()){
                ((Clothe)obj).uninstall(this);
            }
        }
    }
    
    /**
     * make all clothe hittable.
     */
    public void makeCLotheHittable(PickResetPhase pickingPhase){
        final Sequence clothes = getClothes();
        for(int i=0,n=clothes.getSize();i<n;i++){
            final PresetModelClothe clothe = (PresetModelClothe) clothes.get(i);
            Models.setHittable(clothe.getMesh(), pickingPhase);
            clothe.setState((ClotheState) clothe.getStates().get(1));
            clothe.setState((ClotheState) clothe.getStates().get(0));
        }
    }
}
