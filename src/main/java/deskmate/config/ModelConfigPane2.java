package deskmate.config;

import deskmate.Config;
import deskmate.View;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.scenegraph.SceneNode;
import un.engine.ui.component.path.PathView;
import un.engine.ui.component.path.WPreviewView;
import un.engine.ui.ievent.ActionEvent;
import un.engine.ui.layout.FormConstraint;
import un.engine.ui.layout.FormLayout;
import un.engine.ui.layout.GridLayout;
import un.engine.ui.model.ColumnModel;
import un.engine.ui.model.DefaultColumnModel;
import un.engine.ui.model.DefaultTreeColumnModel;
import un.engine.ui.model.DefaultTreeModel;
import un.engine.ui.model.EmptyTreeTableModel;
import un.engine.ui.model.ObjectPresenter;
import un.engine.ui.model.TreeModel;
import un.engine.ui.model.TreeTableModel;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.WTreeTable;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;
import un.system.path.Path;
import un.system.path.VirtualFolder;

/**
 * Model config pane.
 */
public class ModelConfigPane2 extends WContainer {
    
    private final View view;
    private final WTreeTable tree = new WTreeTable();
    private final WPreviewView ptree = new WPreviewView();
    private final WButton refreshPreview = new WButton(new Chars("Reset thumbnails"), new EventListener() {

        @Override
        public void receiveEvent(Class eventClass, Event event) {
            WPreviewView.deleteThumbnails(View.DATAPATH);
        }
    });

    private final EventListener viewListener = new EventListener() {

        @Override
        public void receiveEvent(Class eventClass, Event event) {
            PropertyEvent pe = (PropertyEvent) event;
            if (View.PROPERTY_MODEL.equals(pe.getPropertyName())) {
                update();
            }
        }
    };

    public ModelConfigPane2(final View view) {
        getStyle().getSelfRule().setProperty(new Chars("margin"), new Chars("[6,6,6,6]"));
        this.view = view;
        this.view.addEventListener(PropertyEvent.class, viewListener);
        
        ptree.setBlockSize(new Extent(200, 200));
        ptree.setCacheThumbs(true);
        ptree.setThumbFormat(new Chars("bmp"));
        setLayout(new FormLayout());
        ((FormLayout)getLayout()).setRowSize(0, FormLayout.SIZE_EXPAND);
        ((FormLayout)getLayout()).setColumnSize(1, FormLayout.SIZE_EXPAND);
        addChild(ptree, new FormConstraint(0,0,2,1));
        addChild(refreshPreview, new FormConstraint(0,1,1,1));
        
        
        ptree.addEventListener(PropertyEvent.class, new EventListener() {
            public void receiveEvent(Class eventClass, Event event) {
                if( ((PropertyEvent)event).getPropertyName().equals(PathView.PROPERTY_SELECTED_PATHS) ){
                    Path[] p = ptree.getSelectedPath();
                    if(p != null && p.length>0){
                        view.changeModel(p[0]);
                    }
                }
            }
        });
        
        update();
    }

    private void update() {

        //update the tree
        if (view.currentModel != null) {
            DefaultTreeColumnModel treeCol = new DefaultTreeColumnModel(null,(Chars)null,new NamePresenter());
            TreeModel tm = new DefaultTreeModel(view.currentModel);
            tree.setModel(new TreeTableModel(tm, new ColumnModel[]{treeCol,new ColModel()}));
        } else {
            tree.setModel(new EmptyTreeTableModel());
        }

        //update the block view
        final Path p = new VirtualFolder("models");
        p.addChildren(view.allModels);
        ptree.setViewRoot(p);
        
    }

    private class ColModel extends DefaultColumnModel {

        private final VisiblePresenter presenter = new VisiblePresenter();

        public ColModel() {
            super();
        }

        @Override
        public Object getElement(Object candidate) {
            return candidate;
        }

        @Override
        public ObjectPresenter getPresenter(Object candidate) {
            return presenter;
        }

    }

    private class NamePresenter implements ObjectPresenter {

        @Override
        public Widget createWidget(Object candidate) {
            if (candidate instanceof Mesh) {
                final Mesh mesh = (Mesh) candidate;
                return new WLabel(mesh.getName());
            } else if (candidate instanceof SceneNode) {
                return new WLabel(new Chars("Node"));
            }
            return new WLabel(new Chars("?"));
        }

    }

    private static class VisiblePresenter implements ObjectPresenter {

        public Widget createWidget(final Object candidate) {
            if (candidate instanceof GLNode) {
                final WButton button = new WButton(new Chars("V"));
                button.addEventListener(ActionEvent.class, new EventListener() {
                    public void receiveEvent(Class eventClass, Event event) {
                        final GLNode node = (GLNode) candidate;
                        node.setVisible(!node.isVisible());
                        button.setText(((GLNode) candidate).isVisible() ? new Chars("V") : new Chars("H"));
                    }
                });
                return button;
            } else {
                return new WSpace(new Extent(2));
            }
        }

    }

}
