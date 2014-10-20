

package freeh.kitsune.stages.deskmate;

import freeh.kitsune.GameBundle;
import freeh.kitsune.GameProperties;
import freeh.kitsune.audios.MusicSelector;
import freeh.kitsune.maps.MapSelector;
import freeh.kitsune.model.ModelSelector;
import freeh.kitsune.model.clothes.ClotheStateSelector;
import freeh.kitsune.model.dances.DanceSelector;
import freeh.kitsune.model.poses.PoseSelector;
import freeh.kitsune.ui.ActionLayer;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyBinding;
import un.api.layout.BorderConstraint;
import un.engine.ui.widget.WButton;
import un.science.geometry.Extent;

/**
 *
 */
public class DeskMateMenu extends ActionLayer {

    private final DeskMateStage stage;
    
    private final WButton nextModel = new WButton(GameBundle.get(new Chars("deskmate.nextmodel")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.randomModel();
        }
    });
    private final WButton nextMotion = new WButton(GameBundle.get(new Chars("deskmate.nextdance")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.randomDance();
        }
    });
    private final WButton nextPose = new WButton(GameBundle.get(new Chars("deskmate.nextpose")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.randomPose();
        }
    });
    private final WButton nextMusic = new WButton(GameBundle.get(new Chars("deskmate.nextmusic")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.randomMusic();
        }
    });
    private final WButton showBorder = new WButton(GameBundle.get(new Chars("deskmate.showborder")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.getGame().getFrame().setUndecorated(!stage.getGame().getFrame().isUndecorated());
        }
    });
    private final WButton showGrid = new WButton(GameBundle.get(new Chars("deskmate.showgrid")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.setGridVisible(!stage.isGridActive());
        }
    });
    private final WButton showReflect = new WButton(GameBundle.get(new Chars("deskmate.showreflect")), null, new EventListener() {
        @Override
        public void receiveEvent(Event event) {
            stage.setReflectVisible(!stage.isReflectActive());
        }
    });
    
    private final ClotheStateSelector clothePane = new ClotheStateSelector();
    private final ModelSelector modelPane = new ModelSelector();
    private final DanceSelector dancePane = new DanceSelector();   
    private final PoseSelector posePane = new PoseSelector();
    private final MusicSelector musicPane = new MusicSelector();
    private final MapSelector mapPane = new MapSelector();
    
    private final WButton clotheButton = new WButton(null,GameProperties.ICON_CLOTHE_ON,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(clothePane, BorderConstraint.CENTER);
        }
    });
    private final WButton modelButton = new WButton(null,GameProperties.ICON_MODEL,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(modelPane, BorderConstraint.CENTER);
        }
    });
    private final WButton danceButton = new WButton(null,GameProperties.ICON_MOTION,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(dancePane, BorderConstraint.CENTER);
        }
    });
    private final WButton poseButton = new WButton(null,GameProperties.ICON_POSE,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(posePane, BorderConstraint.CENTER);
        }
    });
    private final WButton musicButton = new WButton(null,GameProperties.ICON_MUSIC,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(musicPane, BorderConstraint.CENTER);
        }
    });
    private final WButton stageButton = new WButton(null,GameProperties.ICON_STAGE,new EventListener() {
        public void receiveEvent(Event event) {
            switchVisible(mapPane, BorderConstraint.LEFT);
        }
    });
    
    public DeskMateMenu(final DeskMateStage stage) {
        super(false);
        this.stage = stage;
                
        clothePane.setBestExtent(new Extent(400, 500));
        modelPane.setBestExtent(new Extent(550, 550));
        dancePane.setBestExtent(new Extent(400, 500));
        posePane.setBestExtent(new Extent(400, 500));
        musicPane.setBestExtent(new Extent(400, 500));
        mapPane.setBestExtent(new Extent(400, 500));
        
        PropertyBinding.bidirectional(stage, ModelSelector.PROPERTY_MODEL, clothePane, ModelSelector.PROPERTY_MODEL);
        PropertyBinding.bidirectional(stage, ModelSelector.PROPERTY_MODEL, modelPane, ModelSelector.PROPERTY_MODEL);
        PropertyBinding.bidirectional(stage, DanceSelector.PROPERTY_DANCE, dancePane, DanceSelector.PROPERTY_DANCE);
        PropertyBinding.bidirectional(stage, PoseSelector.PROPERTY_POSE, posePane, PoseSelector.PROPERTY_POSE);
        PropertyBinding.bidirectional(stage, MusicSelector.PROPERTY_MUSIC, musicPane, MusicSelector.PROPERTY_MUSIC);
        PropertyBinding.bidirectional(stage, MapSelector.PROPERTY_MAP, mapPane, MapSelector.PROPERTY_MAP);
         
        addLeftAction(clotheButton);
        addLeftAction(modelButton);
        addLeftAction(danceButton);
        addLeftAction(poseButton);
        addLeftAction(musicButton);
        addLeftAction(stageButton);
        
        addRightAction(nextModel);
        addRightAction(nextMotion);
        addRightAction(nextPose);
        addRightAction(nextMusic);
        addRightAction(showBorder);
        addRightAction(showGrid);
        addRightAction(showReflect);
                
    }

}
