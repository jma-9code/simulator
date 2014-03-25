package com.mxgraph.examples.swing.editor;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import fr.ensicaen.gui_simulator.gui.panel.ConsolePanel;
import fr.ensicaen.gui_simulator.gui.panel.RightSplitPane;

public class BasicGraphEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6561623072112577140L;

	/**
	 * Adds required resources for i18n
	 */
	static {
		try {
			mxResources.add("com/mxgraph/examples/swing/resources/editor");
		}
		catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * 
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * 
	 */
	protected JTabbedPane libraryPane;

	protected RightSplitPane vRightSplit;

	/**
	 * 
	 */
	protected mxUndoManager undoManager;

	/**
	 * 
	 */
	protected String appTitle;

	/**
	 * 
	 */
	protected JLabel statusLabel;

	/**
	 * 
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified
	 */
	protected boolean modified = false;

	/**
	 * 
	 */
	protected mxRubberband rubberband;

	/**
	 * 
	 */
	protected mxKeyboardHandler keyboardHandler;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
		}
	};

	/**
	 * 
	 */
	protected mxIEventListener changeTracker = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {

			setModified(true);
		}
	};

	private ConsolePanel consolePane;

	/**
	 * 
	 */
	public BasicGraphEditor(String appTitle, mxGraphComponent component) {
		// Stores and updates the frame title
		this.appTitle = appTitle;

		// Stores a reference to the graph and creates the command history
		graphComponent = component;
		final mxGraph graph = graphComponent.getGraph();
		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		// Keeps the selection in sync with the command history
		mxIEventListener undoHandler = new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
		graphOutline = new mxGraphOutline(graphComponent);
		graphOutline.setTripleBuffered(false);

		// Creates the library pane that contains the tabs with the palettes
		libraryPane = new JTabbedPane();

		// creates simulator & console panel
		vRightSplit = new RightSplitPane(this);
		consolePane = new ConsolePanel(this);

		// Creates the inner split pane that contains the library with the
		// palettes and the graph outline on the left side of the window
		JSplitPane vLeftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPane, graphOutline);
		vLeftSplit.setDividerLocation(320);
		vLeftSplit.setResizeWeight(1);
		vLeftSplit.setDividerSize(6);
		vLeftSplit.setBorder(null);

		JSplitPane vGraphSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphComponent, consolePane);
		vGraphSplit.setOneTouchExpandable(true);
		vGraphSplit.setDividerLocation(320);
		vGraphSplit.setResizeWeight(1);
		vGraphSplit.setDividerSize(6);
		vGraphSplit.setBorder(null);

		JSplitPane hSplit1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vLeftSplit, vGraphSplit);
		// outer.setOneTouchExpandable(true);
		hSplit1.setDividerLocation(200);
		hSplit1.setDividerSize(6);
		hSplit1.setBorder(null);

		JSplitPane hSplit2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hSplit1, vRightSplit);
		hSplit2.setOneTouchExpandable(true);
		hSplit2.setDividerLocation(575);
		hSplit2.setDividerSize(6);
		hSplit2.setResizeWeight(1);
		hSplit2.setBorder(null);

		// Creates the status bar
		JPanel statusBar = createStatusBar();

		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		add(hSplit2, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		installToolBar();

		// Installs rubberband selection and handling for some special
		// keystrokes such as F2, Control-C, -V, X, A etc.
		installHandlers();
		installListeners();
		updateTitle();
	}

	/**
	 * 
	 */
	protected mxUndoManager createUndoManager() {
		return new mxUndoManager();
	}

	/**
	 * 
	 */
	protected void installHandlers() {
		rubberband = new mxRubberband(graphComponent);
		keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}

	/**
	 * 
	 */
	protected void installToolBar() {
		add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
	}

	/**
	 * 
	 */
	protected JPanel createStatusBar() {
		JPanel bottomBar = new JPanel();
		bottomBar.setLayout(new BorderLayout());

		statusLabel = new JLabel(mxResources.get("ready"));
		statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		bottomBar.add(statusLabel, BorderLayout.WEST);

		JLabel memLabel = new JLabel("Loading...") {
			long timeout = 0;

			@Override
			protected void paintComponent(Graphics g) {
				if (System.currentTimeMillis() > timeout) {
					timeout = System.currentTimeMillis() + 5000;
					long using = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
					long total = Runtime.getRuntime().totalMemory() / 1000000;
					setText(using + "/" + total + "M");
				}

				super.paintComponent(g);
			}
		};

		memLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		bottomBar.add(memLabel, BorderLayout.EAST);

		return bottomBar;
	}

	/**
	 * 
	 */
	protected void installRepaintListener() {
		graphComponent.getGraph().addListener(mxEvent.REPAINT, new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				String buffer = (graphComponent.getTripleBuffer() != null) ? "" : " (unbuffered)";
				mxRectangle dirty = (mxRectangle) evt.getProperty("region");

				if (dirty == null) {
					status("Repaint all" + buffer);
				}
				else {
					status("Repaint: x=" + (int) (dirty.getX()) + " y=" + (int) (dirty.getY()) + " w="
							+ (int) (dirty.getWidth()) + " h=" + (int) (dirty.getHeight()) + buffer);
				}
			}
		});
	}

	/**
	 * 
	 */
	public EditorPalette insertPalette(String title) {
		final EditorPalette palette = new EditorPalette();
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		libraryPane.add(title, scrollPane);

		// Updates the widths of the palettes if the container size changes
		libraryPane.addComponentListener(new ComponentAdapter() {
			/**
			 * 
			 */
			public void componentResized(ComponentEvent e) {
				int w = scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
	}

	/**
	 * 
	 */
	protected void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			graphComponent.zoomIn();
		}
		else {
			graphComponent.zoomOut();
		}

		status(mxResources.get("scale") + ": " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%");
	}

	/**
	 * 
	 */
	protected void showOutlinePopupMenu(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(mxResources.get("magnifyPage"));
		item.setSelected(graphOutline.isFitPage());

		item.addActionListener(new ActionListener() {
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				graphOutline.setFitPage(!graphOutline.isFitPage());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(mxResources.get("showLabels"));
		item2.setSelected(graphOutline.isDrawLabels());

		item2.addActionListener(new ActionListener() {
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(mxResources.get("buffering"));
		item3.setSelected(graphOutline.isTripleBuffered());

		item3.addActionListener(new ActionListener() {
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
				graphOutline.repaint();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(item);
		menu.add(item2);
		menu.add(item3);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	/**
	 * 
	 */
	protected void showGraphPopupMenu(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
		menu.show(graphComponent, pt.x, pt.y);
		e.consume();
	}

	/**
	 * 
	 */
	protected void mouseLocationChanged(MouseEvent e) {
		status(e.getX() + ", " + e.getY());
	}

	/**
	 * 
	 */
	protected void installListeners() {
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener() {
			/**
			 * 
			 */
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
					BasicGraphEditor.this.mouseWheelMoved(e);
				}
			}

		};

		// Handles mouse wheel events in the outline and graph component
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the outline
		graphOutline.addMouseListener(new MouseAdapter() {

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on
				// mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showOutlinePopupMenu(e);
				}
			}

		});

		// Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on
				// mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showGraphPopupMenu(e);
				}
			}

		});

		// Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseMotionListener#mouseDragged(java.
			 * awt.event .MouseEvent)
			 */
			public void mouseDragged(MouseEvent e) {
				mouseLocationChanged(e);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt
			 * .event. MouseEvent)
			 */
			public void mouseMoved(MouseEvent e) {
				mouseDragged(e);
			}

		});
	}

	/**
	 * 
	 */
	public void setCurrentFile(File file) {
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file) {
			updateTitle();
		}
	}

	/**
	 * 
	 */
	public File getCurrentFile() {
		return currentFile;
	}

	/**
	 * 
	 * @param modified
	 */
	public void setModified(boolean modified) {
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified) {
			updateTitle();
		}
	}

	/**
	 * 
	 * @return whether or not the current graph has been modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	/**
	 * 
	 */
	public mxGraphOutline getGraphOutline() {
		return graphOutline;
	}

	/**
	 * 
	 */
	public JTabbedPane getLibraryPane() {
		return libraryPane;
	}

	/**
	 * 
	 */
	public mxUndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name
	 */
	public Action bind(String name, final Action action) {
		return bind(name, action, null);
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name and icon
	 */
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl) {
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				BasicGraphEditor.class.getResource(iconUrl)) : null) {
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

		return newAction;
	}

	/**
	 * 
	 * @param msg
	 */
	public void status(String msg) {
		statusLabel.setText(msg);
		statusLabel.getParent().repaint();
	}

	/**
	 * 
	 */
	public void updateTitle() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			String title = (currentFile != null) ? currentFile.getAbsolutePath() : mxResources.get("newDiagram");

			if (modified) {
				title += "*";
			}

			frame.setTitle(title + " - " + appTitle);
		}
	}

	/**
	 * 
	 */
	public void about() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			EditorAboutFrame about = new EditorAboutFrame(frame);
			about.setModal(true);

			// Centers inside the application frame
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	/**
	 * 
	 */
	public void exit() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			frame.dispose();
		}
	}

	/**
	 * 
	 */
	public void setLookAndFeel(String clazz) {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			try {
				UIManager.setLookAndFeel(clazz);
				SwingUtilities.updateComponentTreeUI(frame);

				// Needs to assign the key bindings again
				keyboardHandler = new EditorKeyboardHandler(graphComponent);
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public JFrame createFrame(JMenuBar menuBar) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setSize(870, 640);

		// Updates the frame title
		updateTitle();

		return frame;
	}

	/**
	 * Creates an action that executes the specified layout.
	 * 
	 * @param key
	 *            Key to be used for getting the label from mxResources and also
	 *            to create the layout instance for the commercial graph editor
	 *            example.
	 * @return an action that executes the specified layout
	 */
	@SuppressWarnings("serial")
	public Action graphLayout(final String key, boolean animate) {
		final mxIGraphLayout layout = createLayout(key, animate);

		if (layout != null) {
			return new AbstractAction(mxResources.get(key)) {
				public void actionPerformed(ActionEvent e) {
					final mxGraph graph = graphComponent.getGraph();
					Object cell = graph.getSelectionCell();

					if (cell == null || graph.getModel().getChildCount(cell) == 0) {
						cell = graph.getDefaultParent();
					}

					graph.getModel().beginUpdate();
					try {
						long t0 = System.currentTimeMillis();
						layout.execute(cell);
						status("Layout: " + (System.currentTimeMillis() - t0) + " ms");
					}
					finally {
						mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

						morph.addListener(mxEvent.DONE, new mxIEventListener() {

							public void invoke(Object sender, mxEventObject evt) {
								graph.getModel().endUpdate();
							}

						});

						morph.startAnimation();
					}

				}

			};
		}
		else {
			return new AbstractAction(mxResources.get(key)) {

				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(graphComponent, mxResources.get("noLayout"));
				}

			};
		}
	}

	/**
	 * Creates a layout instance for the given identifier.
	 */
	protected mxIGraphLayout createLayout(String ident, boolean animate) {
		mxIGraphLayout layout = null;

		if (ident != null) {
			mxGraph graph = graphComponent.getGraph();

			if (ident.equals("verticalHierarchical")) {
				layout = new mxHierarchicalLayout(graph);
			}
			else if (ident.equals("horizontalHierarchical")) {
				layout = new mxHierarchicalLayout(graph, JLabel.WEST);
			}
			else if (ident.equals("verticalTree")) {
				layout = new mxCompactTreeLayout(graph, false);
			}
			else if (ident.equals("horizontalTree")) {
				layout = new mxCompactTreeLayout(graph, true);
			}
			else if (ident.equals("parallelEdges")) {
				layout = new mxParallelEdgeLayout(graph);
			}
			else if (ident.equals("placeEdgeLabels")) {
				layout = new mxEdgeLabelLayout(graph);
			}
			else if (ident.equals("organicLayout")) {
				layout = new mxOrganicLayout(graph);
			}
			if (ident.equals("verticalPartition")) {
				layout = new mxPartitionLayout(graph, false) {
					/**
					 * Overrides the empty implementation to return the size of
					 * the graph control.
					 */
					public mxRectangle getContainerSize() {
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalPartition")) {
				layout = new mxPartitionLayout(graph, true) {
					/**
					 * Overrides the empty implementation to return the size of
					 * the graph control.
					 */
					public mxRectangle getContainerSize() {
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("verticalStack")) {
				layout = new mxStackLayout(graph, false) {
					/**
					 * Overrides the empty implementation to return the size of
					 * the graph control.
					 */
					public mxRectangle getContainerSize() {
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalStack")) {
				layout = new mxStackLayout(graph, true) {
					/**
					 * Overrides the empty implementation to return the size of
					 * the graph control.
					 */
					public mxRectangle getContainerSize() {
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("circleLayout")) {
				layout = new mxCircleLayout(graph);
			}
		}

		return layout;
	}

	/**
	 * Rafraichissement de l'UI
	 */
	public void refreshAll() {

		vRightSplit.getSimulatorPanel().refresh();
	}

	/**
	 * Mise à zéro de l'UI
	 */
	public void resetAll() {
		consolePane.reset();
	}

}
