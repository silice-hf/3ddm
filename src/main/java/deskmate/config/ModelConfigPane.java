package deskmate.config;

import deskmate.View;
import un.api.character.Chars;
import un.api.event.Event;
import un.api.event.EventListener;
import un.api.event.PropertyEvent;
import un.engine.opengl.mesh.Mesh;
import un.engine.opengl.scenegraph.GLNode;
import un.engine.scenegraph.SceneNode;
import un.engine.ui.ievent.ActionEvent;
import un.engine.ui.layout.BorderConstraint;
import un.engine.ui.layout.BorderLayout;
import un.engine.ui.layout.GridLayout;
import un.engine.ui.model.AbstractColumnModel;
import un.engine.ui.model.DefaultRowModel;
import un.engine.ui.model.DefaultTreeModel;
import un.engine.ui.model.EmptyTreeTableModel;
import un.engine.ui.model.ObjectPresenter;
import un.engine.ui.model.RowModel;
import un.engine.ui.model.TreeModel;
import un.engine.ui.model.TreeTableModel;
import un.engine.ui.widget.WButton;
import un.engine.ui.widget.WContainer;
import un.engine.ui.widget.WLabel;
import un.engine.ui.widget.WList;
import un.engine.ui.widget.WScrollContainer;
import un.engine.ui.widget.WSpace;
import un.engine.ui.widget.WTreeTable;
import un.engine.ui.widget.Widget;
import un.science.geometry.Extent;
import un.system.path.Path;

/**
 * Model config pane.
 */
public class ModelConfigPane extends WContainer {

    private final View view;
    private final WList models = new WList();
    private final WTreeTable tree = new WTreeTable();

    private final EventListener viewListener = new EventListener() {

        @Override
        public void receiveEvent(Class eventClass, Event event) {
            PropertyEvent pe = (PropertyEvent) event;
            if (View.PROPERTY_MODEL.equals(pe.getPropertyName())) {
                update();
            }
        }
    };

    public ModelConfigPane(final View view) {
        this.view = view;
        this.view.addEventListener(PropertyEvent.class, viewListener);

        setLayout(new GridLayout(1, 2));

        WContainer left = new WContainer(new BorderLayout());
        WContainer right = new WContainer(new BorderLayout());
        addChild(left);
        addChild(right);

        WScrollContainer scrollModels = new WScrollContainer(models);
        models.setModel(new DefaultRowModel(view.allModels));
        left.addChild(scrollModels, BorderConstraint.CENTER);

        WScrollContainer scrollParts = new WScrollContainer(tree);
        right.addChild(scrollParts, BorderConstraint.CENTER);

        models.getModel().addEventListener(RowModel.RowEvent.class, new EventListener() {
            
            @Override
            public void receiveEvent(Class eventClass, Event event) {
                RowModel.RowEvent re = (RowModel.RowEvent) event;
                int[] selected = re.getNewSelection();
                if (selected != null && selected.length > 0) {
                    Path p = (Path) models.getModel().getElement(selected[0]);
                    view.changeModel(p);
                }
            }
        });

        models.setRenderer(new ObjectPresenter() {

            @Override
            public Widget createWidget(Object candidate) {
                Path p = (Path) candidate;
                return new WLabel(new Chars(p.toURI().replaceAll(View.DATAPATH.toURI(), "")));
            }
        });

        update();
    }

    private void update() {
        //update the model list
        if (view.currentModelPath != null) {
            models.getModel().setSelectedIndex(new int[]{view.allModels.search(view.currentModelPath)});
        } else {
            models.getModel().setSelectedIndex(new int[]{});
        }

        //update the tree
        if (view.currentModel != null) {
            TreeModel tm = new DefaultTreeModel(view.currentModel);
            tm.setCellPresenter(new NamePresenter());
            tree.setModel(new TreeTableModel(tm, new ColModel()));
        } else {
            tree.setModel(new EmptyTreeTableModel());
        }

    }

    private class ColModel extends AbstractColumnModel {

        private final VisiblePresenter presenter = new VisiblePresenter();

        public ColModel() {
            super(1);
        }

        @Override
        public Object getElement(Object candidate, int column) {
            return candidate;
        }

        @Override
        public ObjectPresenter getPresenter(Object candidate, int column) {
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
