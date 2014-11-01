

package freeh.kitsune.stages;

import freeh.kitsune.Game;
import freeh.kitsune.GameBundle;
import freeh.kitsune.GameInfo;
import freeh.kitsune.audios.Musics;
import freeh.kitsune.dances.Dances;
import freeh.kitsune.items.Items;
import freeh.kitsune.maps.Maps;
import freeh.kitsune.model.preset.PresetModels;
import freeh.kitsune.poses.Poses;
import un.api.character.Chars;
import un.api.image.Images;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.api.tree.Node;
import un.engine.opengl.GLProcessContext;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WSpace;
import un.api.io.IOException;
import un.science.geometry.Extent;
import un.science.math.Maths;
import un.api.path.Path;
import un.system.path.Paths;

/**
 *
 */
public class StartingStage extends Stage{
    
    public StartingStage() {
    }
     
    public void install(final Game game){
        super.install(game);
        
        game.getGamePhases().setAlphaEnable(true);
        game.getFrame().setUndecorated(true);
        
        final FormLayout layout = new FormLayout();
        layout.setColumnSize(1, 200);
        layout.setColumnSize(2, 60);
        
        final WContainer container = new WContainer(layout);
        
        container.getStyle().getSelfRule().setProperties(
                new Chars("margin              : [15,15,15,15]\n"
                        + "background          : none\n"
                        + "border-margin       : [14,14,14,14]\n"
                        + "border-radius       : [30,30,30,30]\n"
                        + "border-fill-paint   : colorfill(#000000BB)\n"
                        + "border-brush        : plainbrush(1,'round')\n"
                        + "border-brush-paint  : colorfill($color-main-3)"));
        game.getUI().setVisible(container, false);
        
        
        
        final WLabel image = new WLabel();
        try{
            final Path folder = Paths.resolve(GameInfo.PATH_RESOURCE+"/starters");
            final Node[] nodes = folder.getChildren();
            final int index = Maths.clip((int)(Math.random()*nodes.length),0,nodes.length);
            final Path path = (Path)nodes[index];
            image.setImage(Images.read(path));
        }catch(IOException ex){
            Game.LOGGER.warning(ex);
        }
        
        container.addChild(image, new FormConstraint(0, 0, 1, 7));
        container.addChild(new WSpace(new Extent(2)),new FormConstraint(1, 5));
        
        new Thread(){
            public void run() {
                int y = -1;
                
                final WLabel audioNumber = new WLabel(GameBundle.get(new Chars("start.buildmusiclist")));
                container.addChild(audioNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+Musics.getAll().getSize())),new FormConstraint(2, y));
                audioNumber.setText(GameBundle.get(new Chars("start.music")));
                
                final WLabel mapNumber = new WLabel(GameBundle.get(new Chars("start.buildmaplist")));
                container.addChild(mapNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+Maps.getAll().getSize())),new FormConstraint(2, y));
                mapNumber.setText(GameBundle.get(new Chars("start.map")));
                
                final WLabel itemNumber = new WLabel(GameBundle.get(new Chars("start.builditemlist")));
                container.addChild(itemNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+Items.getItems().length)),new FormConstraint(2, y));
                itemNumber.setText(GameBundle.get(new Chars("start.item")));
                
                final WLabel danceNumber = new WLabel(GameBundle.get(new Chars("start.builddancelist")));
                container.addChild(danceNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+Dances.getAll().getSize())),new FormConstraint(2, y));
                danceNumber.setText(GameBundle.get(new Chars("start.dance")));
                
                final WLabel poseNumber = new WLabel(GameBundle.get(new Chars("start.buildposelist")));
                container.addChild(poseNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+Poses.getAll().getSize())),new FormConstraint(2, y));
                poseNumber.setText(GameBundle.get(new Chars("start.pose")));
                
                final WLabel modelNumber = new WLabel(GameBundle.get(new Chars("start.buildmodellist")));
                container.addChild(modelNumber,new FormConstraint(1, ++y));
                container.addChild(new WLabel(new Chars(""+PresetModels.getAll().getSize())),new FormConstraint(2, y));
                modelNumber.setText(GameBundle.get(new Chars("start.model")));
                
                try {
                    sleep(1500);
                } catch (InterruptedException ex) {
                    Game.LOGGER.warning(ex);
                }
                
                game.getFrame().setUndecorated(false);
                game.setStage(new MenuStage());
            }
        }.start();
        
    }

    public void dispose(GLProcessContext context) {
        game.getUI().setNoneVisible();
        super.dispose(context);
    }
    
}
