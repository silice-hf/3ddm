

package freeh.kitsune.ui;

import freeh.kitsune.Game;
import un.api.array.Arrays;
import un.api.character.Chars;
import un.api.character.Language;
import un.engine.opengl.widget.WGLPlane;
import un.engine.ui.ievent.KeyEvent;
import un.engine.ui.ievent.MouseEvent;
import un.api.layout.BorderConstraint;
import un.api.layout.BorderLayout;
import un.api.layout.FormConstraint;
import un.api.layout.FormLayout;
import un.engine.ui.style.WidgetStyles;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;

/**
 *
 */
public class UI {
    
    private final Game game;
    private final UIPhases pipeline;
    private final WGLPlane glPlan;
    private final WContainer plan;
    
    private final MainMenu uiMenu;
    private final ControlsConfigurator uiControl;
    private final GraphicsConfigurator uiConfig;
    private final WCredits uiCredit;
    private final WLoading uiLoading;
    private final WContainer centered;

    public UI(final Game game) {
        this.game = game;
        this.pipeline = game.getUIPhases();
        glPlan = new WGLPlane(10, 10, pipeline.getCamera());
        glPlan.setFitToFrame(true);
        glPlan.setPickable(false);
        
        plan = glPlan.getContainer();
        plan.setLayout(new BorderLayout());
        uiMenu = new MainMenu(game);
        uiControl = new ControlsConfigurator();
        uiConfig = new GraphicsConfigurator(game);
        uiCredit = new WCredits();
        uiLoading = new WLoading();
        
        final FormLayout layout = new FormLayout();
        layout.setColumnSize(0, FormLayout.SIZE_EXPAND);
        layout.setColumnSize(2, FormLayout.SIZE_EXPAND);
        layout.setRowSize(0, FormLayout.SIZE_EXPAND);
        layout.setRowSize(2, FormLayout.SIZE_EXPAND);
        centered = new WContainer(layout);
        centered.getStyle().getSelfRule().setProperties(new Chars(
            "background : none\n"));
        centered.addChild(new WSpace(new Extent(2)), new FormConstraint(0, 0));
        centered.addChild(new WSpace(new Extent(2)), new FormConstraint(2, 2));
        
        
        plan.getStyle().getSelfRule().setProperty(Widget.STYLE_PROP_BACKGROUND, WidgetStyles.NONE);
        plan.setLanguage(game.getLanguage(), true);
        game.getFrame().addEventListener(MouseEvent.class, plan);
        game.getFrame().addEventListener(KeyEvent.class, plan);
        
        setMenuVisible();
    }
    
    public void setLanguage(Language language){
        plan.setLanguage(language, true);
    }
    
    public boolean isMenuVisible(){
        return isVisible(uiMenu);
    }
    
    public boolean isConfigVisible(){
        return isVisible(uiConfig);
    }
    
    public boolean isControlVisible(){
        return isVisible(uiControl);
    }
    
    public boolean isCreditVisible(){
        return isVisible(uiCredit);
    }
    
    public boolean isLoadingVisible(){
        return isVisible(uiLoading);
    }
    
    public boolean isVisible(Widget widget){
        return Arrays.contains(plan.getChildren(),widget) ||
               Arrays.contains(centered.getChildren(),widget);
    }
    
    public void setMenuVisible() {
        setVisible(uiMenu, null);
    }
    
    public void setControlVisible() {
        setVisible(uiMenu, uiControl);
    }
    
    public void setConfigVisible() {
        setVisible(uiMenu, uiConfig);
    }
    
    public void setCreditVisible() {
        setVisible(uiMenu, uiCredit);
    }
    
    public void setLoadingVisible(){
        //update loading image
        uiLoading.updateLoader();        
        setVisible(null, uiLoading);
    }
    
    public void setNoneVisible(){
        plan.removeChildren();
        centered.removeChildren();
    }
        
    public void setVisible(Widget menu, Widget center){
        setNoneVisible();
        if(menu!=null){
            plan.addChild(menu,BorderConstraint.RIGHT);
        }
        if(center!=null){
            centered.addChild(center, new FormConstraint(1, 1));
            plan.addChild(centered, BorderConstraint.CENTER);
        }
    }
    
    public void setVisible(Widget center, boolean fullspace){
        setNoneVisible();
        if(center!=null){
            plan.removeChild(centered);
            if(fullspace){
                plan.addChild(center, BorderConstraint.CENTER);
            }else{
                centered.addChild(center, new FormConstraint(1, 1));
                plan.addChild(centered, BorderConstraint.CENTER);
            }
        }
    }
    
}
