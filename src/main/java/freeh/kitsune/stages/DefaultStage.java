
package freeh.kitsune.stages;

import com.jogamp.openal.AL;
import freeh.kitsune.audios.Music;
import freeh.kitsune.audios.MusicSelector;
import freeh.kitsune.audios.Musics;
import freeh.kitsune.maps.Map;
import freeh.kitsune.maps.MapSelector;
import freeh.kitsune.maps.Maps;
import freeh.kitsune.model.Model;
import freeh.kitsune.model.ModelSelector;
import freeh.kitsune.model.Models;
import freeh.kitsune.model.dances.Dance;
import freeh.kitsune.model.dances.DanceSelector;
import freeh.kitsune.model.dances.Dances;
import freeh.kitsune.model.poses.Pose;
import freeh.kitsune.model.poses.PoseSelector;
import freeh.kitsune.model.poses.Poses;
import java.nio.ByteBuffer;
import un.api.CObjects;
import un.api.character.Chars;
import un.api.media.AudioStreamMeta;
import un.engine.openal.resource.ALBuffer;
import un.engine.openal.resource.ALSource;
import un.engine.opengl.animation.Animation;
import un.engine.opengl.physic.RelativeSkeletonPose;
import un.engine.opengl.physic.SkeletonPoseResolver;
import un.engine.opengl.physic.Skeletons;
import un.engine.ui.ievent.KeyEvent;

/**
 * Default stage with :
 * - menu
 * - model
 * - map
 * - motion
 * - pose
 * - music
 */
public class DefaultStage extends Stage {
                
    public static final Chars PROPERTY_MAP = MapSelector.PROPERTY_MAP;
    public static final Chars PROPERTY_MODEL = ModelSelector.PROPERTY_MODEL;
    public static final Chars PROPERTY_DANCE = DanceSelector.PROPERTY_DANCE;
    public static final Chars PROPERTY_POSE = PoseSelector.PROPERTY_POSE;
    public static final Chars PROPERTY_MUSIC = MusicSelector.PROPERTY_MUSIC;
    
    protected boolean menuVisible = false;
        
    //Music source
    private final ALSource audioSource = new ALSource();
    
    private Map currentMap;    
    private Model currentModel;    
    private Dance currentDance;    
    private Pose currentPose;    
    private Music currentMusic;
    
    public DefaultStage() {
        
    }
    
    protected void keyEvent(KeyEvent ke) {
        //27 == echap
        if(ke.getCodePoint() == 27 && ke.getType() == KeyEvent.TYPE_PRESS){
            menuVisible = !menuVisible;
            if(menuVisible){
                game.getUI().setMenuVisible();
            }else{
                game.getUI().setNoneVisible();
            }
        }
    }
    
    public void randomMap(){
        setMap(Maps.getRandom());
    }
        
    public void randomModel(){
        setModel(Models.getRandomPreset());
    }
    
    public void randomDance(){
        setDance(Dances.getRandom());
    }
    
    public void randomPose(){
        setPose(Poses.getRandom());
    }
    
    public void randomMusic(){
        setMusic(Musics.getRandom());
    }
    
    
    public Map getMap() {
        return currentMap;
    }

    public synchronized void setMap(Map map){
        if(CObjects.equals(currentMap,map)) return;
        unloadMap();
        currentMap = map;
                
        if(map==null) return;
        
        addChild(currentMap.getNode());
        getEventManager().sendPropertyEvent(this, MapSelector.PROPERTY_MAP, null, currentMap);
    }
    
    public Model getModel() {
        return currentModel;
    }

    public synchronized void setModel(Model model){
        if(CObjects.equals(currentModel,model)) return;
        unloadModel();
        currentModel = model;
                
        if(model==null) return;
        
        addChild(currentModel.getNode());
        currentModel.makeCLotheHittable(game.getGamePhases().getPickingPhase());
        getEventManager().sendPropertyEvent(this, ModelSelector.PROPERTY_MODEL, null, currentModel);
    }
    
    public Dance getDance() {
        return currentDance;
    }

    public synchronized void setDance(Dance dance){
        if(CObjects.equals(currentDance,dance)) return;
        
        stopAnimation();
        currentDance = dance;
        
        if(dance==null) return;
        
        //load new animation
        if(currentModel!=null && currentDance!=null){
            final Animation animation = currentDance.getAnimation();
            Skeletons.mapAnimation(animation, currentModel.getSkeleton(), currentModel.getNode());
            currentModel.getNode().getUpdaters().add(animation);
            animation.play();
        }
        
        getEventManager().sendPropertyEvent(this, DanceSelector.PROPERTY_DANCE, null, currentDance);
    }
    
    public Pose getPose() {
        return currentPose;
    }

    public synchronized void setPose(Pose pose){
        if(CObjects.equals(currentPose,pose)) return;
        
        stopAnimation();
        currentPose = pose;
        
        if(pose==null) return;
        
        //load new pose
        final RelativeSkeletonPose skepose = currentPose.createPose();
        if(currentModel!=null && currentPose!=null){
            SkeletonPoseResolver.resolve1(currentModel.getSkeleton(), skepose);
            currentModel.getSkeleton().solveKinematics();
            currentModel.getSkeleton().updateBindPose();
        }
        
        getEventManager().sendPropertyEvent(this, PoseSelector.PROPERTY_POSE, null, currentPose);
    }
    
    public Music getMusic() {
        return currentMusic;
    }

    public synchronized void setMusic(Music music){
        if(CObjects.equals(currentMusic,music)) return;
        currentMusic = music;
        
        stopAudio();
        
        if(music==null) return;
        
        //load new audio
        final byte[] all = music.createPCMBuffer();
        final AudioStreamMeta meta = music.getAudioStreamMeta();
        if(meta==null) return;

        //buffer which contains audio datas
        final ALBuffer data = new ALBuffer();
        data.setFormat(AL.AL_FORMAT_STEREO16);
        data.setFrequency((int) meta.getSampleRate());
        data.setSize(all.length);
        data.setData(ByteBuffer.wrap(all));
        data.load();

        audioSource.setBuffer(data);
        audioSource.load();
        audioSource.play();
        
        getEventManager().sendPropertyEvent(this, MusicSelector.PROPERTY_MUSIC, null, null);
    }
    
    protected void unloadMap(){
        if (currentMap != null) {
            currentMap.unload(game.getGlcontext());
            currentMap = null;
        }
    }
    
    protected void unloadModel(){
        stopAnimation();
        if (currentModel != null) {
            currentModel.unload(game.getGlcontext());
            currentModel = null;
        }
    }
        
    protected void stopAnimation(){
        if (currentDance != null) {
            currentDance.getAnimation().stop();
            if(currentModel!=null){
                currentModel.getNode().getUpdaters().remove(currentDance.getAnimation());
            }
            currentDance.unload();
            currentDance = null;
        }
    }
    
    protected void stopAudio(){
        if(audioSource.isLoaded()){
            audioSource.stop();
            audioSource.unload();
        }
    }
    
}
