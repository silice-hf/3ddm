

package freeh.kitsune.ui;

import freeh.kitsune.Game;
import freeh.kitsune.GameBundle;
import freeh.kitsune.MetaObject;
import freeh.kitsune.dances.Dances;
import freeh.kitsune.maps.Maps;
import freeh.kitsune.model.preset.PresetModels;
import freeh.kitsune.poses.Poses;
import freeh.kitsune.toys.Toys;
import java.awt.Desktop;
import un.api.character.Chars;
import un.api.collection.Sequence;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.api.layout.GridLayout;
import un.engine.ui.model.CheckGroup;
import un.engine.ui.model.ColumnModel;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.ObjectPresenter;
import un.engine.ui.model.TableModel;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.WSwitch;
import un.engine.ui.widget.WTable;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;
import un.system.jvm.file.FilePath;
import un.system.path.Path;

/**
 *
 */
public class WCredits extends WContainer{

    private final WSwitch maps = new WSwitch(GameBundle.get(new Chars("credits.maps")), null, new EventListener() {
        public void receiveEvent(Event event) {
            setCenter(createCreditPane(Maps.getAll()));
        }
    });  
    private final WSwitch models = new WSwitch(GameBundle.get(new Chars("credits.models")), null, new EventListener() {
        public void receiveEvent(Event event) {
            setCenter(createCreditPane(PresetModels.getAll()));
        }
    });
    private final WSwitch clothes = new WSwitch(GameBundle.get(new Chars("credits.clothes")), null, new EventListener() {
        public void receiveEvent(Event event) {
            //setCenter(createCreditPane(Clothes.getAll()));
        }
    });  
    private final WSwitch toys = new WSwitch(GameBundle.get(new Chars("credits.toys")), null, new EventListener() {
        public void receiveEvent(Event event) {
            setCenter(createCreditPane(Toys.getAll()));
        }
    });  
    private final WSwitch motions = new WSwitch(GameBundle.get(new Chars("credits.motions")), null, new EventListener() {
        public void receiveEvent(Event event) {
            setCenter(createCreditPane(Dances.getAll()));
        }
    });  
    private final WSwitch poses = new WSwitch(GameBundle.get(new Chars("credits.poses")), null, new EventListener() {
        public void receiveEvent(Event event) {
            setCenter(createCreditPane(Poses.getAll()));
        }
    });  
    
    
    public WCredits() {
        super(new BorderLayout());
        getStyle().getSelfRule().setProperties(
                new Chars("margin              : [15,15,15,15]\n"
                        + "background          : none\n"
                        + "border-margin       : [14,14,14,14]\n"
                        + "border-radius       : [30,30,30,30]\n"
                        + "border-fill-paint   : $env-background\n"
                        + "border-brush        : plainbrush(1,'round')\n"
                        + "border-brush-paint  : colorfill($color-main-3)"));
        
        final WContainer top = new WContainer(new GridLayout(1, -1));
        top.getStyle().getSelfRule().setProperties(new Chars(
                "margin     : [5,5,5,5]\n"+
                "background : none"));
        addChild(top, BorderConstraint.TOP);
        maps.setId(new Chars("state"));
        models.setId(new Chars("state"));
        clothes.setId(new Chars("state"));
        toys.setId(new Chars("state"));
        motions.setId(new Chars("state"));
        poses.setId(new Chars("state"));
        top.addChild(maps);
        top.addChild(models);
        top.addChild(clothes);
        top.addChild(toys);
        top.addChild(motions);
        top.addChild(poses);
        
        final CheckGroup group = new CheckGroup();
        group.add(maps);
        group.add(models);
        group.add(clothes);
        group.add(toys);
        group.add(motions);
        group.add(poses);
        
    }
        
    private WContainer center = null;
    private void setCenter(WContainer center){
        if(this.center!=null){
            removeChild(this.center);
        }
        this.center = center;
        if(this.center!=null){
            addChild(this.center, BorderConstraint.CENTER);
        }
        
    }
    
    private WContainer createCreditPane(Sequence elements){
                
        final TableModel tableModel = new TableModel(new DefaultRowModel(elements), new ColumnModel[]{
            new TextColumnModel()
        });
        
        final WTable table = new WTable(tableModel);
        table.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        table.setBestExtent(new Extent(600, 600));
        table.setRowHeight(108);
        
        return table;
    }
        
    private static class TextColumnModel extends DefaultColumnModel{

        public TextColumnModel() {
            super((Chars)null,new ObjectPresenter() {
                @Override
                public Widget createWidget(Object candidate) {
                    if(candidate instanceof MetaObject){
                        final MetaObject obj = (MetaObject) candidate;
                        final WContainer pane = new WContainer(new FormLayout());
                        ((FormLayout)pane.getLayout()).setColumnSize(1, FormLayout.SIZE_EXPAND);
                        pane.getStyle().getSelfRule().setProperties(
                        new Chars("margin              : [4,4,4,4]\n"
                                + "background          : none\n"
                                + "border-margin       : [3,30,30,30]\n"
                                + "border-radius       : [0,0,0,0]\n"
                                + "border-fill-paint   : $env-background\n"
                                + "border-brush        : plainbrush(1,'round')\n"
                                + "border-brush-paint  : colorfill($color-main-5)"));
                        final WLabel lblPreview = new WLabel(null,obj.getPreview());
                        final WLabel lblTitle = new WLabel(obj.getTitle());
                        final WLabel lblAuthor = new WLabel(new Chars("Author: ").concat(obj.getCredits().getName()));
                        final WLabel lblUrl = new WLabel(new Chars("URL: ").concat(obj.getCredits().getUrl()));
                        final WButton readmeButton = new WButton(new Chars("README"),null,new EventListener() {
                            public void receiveEvent(Event event) {
                                final Path readme = obj.getCredits().getReadme();
                                try {
                                    Desktop.getDesktop().open(((FilePath)readme).getFile());
                                } catch (Exception ex) {
                                    //catch exception, illegalArgumentException if file does not exist
                                    Game.LOGGER.warning(new Chars(ex.getMessage()),ex);
                                }
                            }
                        });
                        readmeButton.setEnable(obj.getCredits().getReadme()!=null);
                        pane.addChild(lblPreview, new FormConstraint(0, 0, 1, 3));
                        pane.addChild(lblTitle, new FormConstraint(1, 0));
                        pane.addChild(lblAuthor, new FormConstraint(1, 1));
                        pane.addChild(lblUrl, new FormConstraint(1, 2));
                        pane.addChild(readmeButton, new FormConstraint(2, 1));
                        return pane;
                    }else{
                        return new WSpace(new Extent(1, 1));
                    }
                }
            });
        }
    
    }
    
}
