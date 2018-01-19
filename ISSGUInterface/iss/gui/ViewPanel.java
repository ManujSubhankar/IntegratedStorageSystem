package iss.gui;

import iss.FileType;
import iss.ISSFile;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ViewPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	//flag to say whether the shown icons are search results or not
	private boolean searchResult= false;

	//The search word typed
	private String searchString= null;

	//The search result files as ISSFiles
	private ArrayList<ISSFile> searchFiles;

	//The corresponding path of the search result files
	private ArrayList<String> searchPaths;

	//reference to the icon that is currently in focus by the pointer
	private IconButton focusedIcon= null;

	//ArrayList containing all selected icons' references
	private ArrayList<IconButton> selectedIcons= new ArrayList<IconButton> ();

	//reference to the icon being renamed
	private IconButton renamingIcon;

	//The navigator object that keeps the record of navigation for this panel
	private Navigator navigator= new Navigator();

	//The compare parameter for all the ISSFile items in the panel
	private ISSFile.CompareParameter compareParameter= ISSFile.CompareParameter.NAME;

	//Starting and ending points of selection by mouse dragging
	private Point2D startPos, endPos;

	//The rectangle object that selects the icons while dragging
	private Rectangle2D selector= new Rectangle2D.Double();

	//Thread reference used by refresh() function to provide content-image as icon for image files.
	private static RefreshThread refreshThread;
	
	//For use in refresh
	private ArrayList<String> refreshParents= new ArrayList<String>();

	//getters and setters

	public boolean isSearchResultShowing() {
		return searchResult;
	}

	public void setSearchResultShowing(boolean searchResult) {
		this.searchResult = searchResult;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public void setFocusedIcon(IconButton focusedIcon) {
		this.focusedIcon = focusedIcon;
	}

	public void setStartPos(Point2D startPos) {
		this.startPos = startPos;
	}

	public void setEndPos(Point2D endPos) {
		this.endPos = endPos;
	}

	public IconButton getFocusedIcon() {
		return focusedIcon;
	}

	public String getWorkingDirectory() {
		return navigator.getCurrentPath();
	}

	public Navigator getNavigator() {
		return navigator;
	}

	public ISSFile.CompareParameter getCompareParameter() {
		return compareParameter;
	}

	public Point2D getStartPos() {
		return startPos;
	}

	public Point2D getEndPos() {
		return endPos;
	}

	public Rectangle2D getSelector() {
		return selector;
	}

	public IconButton getRenamingIcon () {
		return renamingIcon;
	}

	public void setRenamingIcon (IconButton but) {
		renamingIcon= but;
	}

	/**
	 * States whether this panel has any icons in renaming phase
	 * @return true if it has an Icon in renaming phase, false otherwise
	 */
	public boolean hasRenamingIcon () {
		return renamingIcon != null;
	}

	/**
	 * @return the arraylist of all the selected icons as IconButton
	 */
	public ArrayList<IconButton> getSelectedIcons () {
		return selectedIcons;
	}

	/**
	 * @return the arraylist of all the selected files as ISSFile
	 */
	public ArrayList<ISSFile> getSelectedFiles () {
		ArrayList<ISSFile> ISSFiles= new ArrayList<ISSFile> ();
		for (IconButton but: selectedIcons) {
			ISSFiles.add(but.getRepresentingFile());
		}
		return ISSFiles;
	}

	/**
	 * sets the new compare parameter to all the ISSFile objects of the component.
	 * @param par the new parameter for comparing two ISSFile objects
	 */
	public void updateCompareParameter (ISSFile.CompareParameter par) {
		compareParameter= par;
		int contentnum= getComponentCount();
		for (int i= 0; i < contentnum; i++) {
			((IconButton)getComponent(i)).getRepresentingFile().setParameter(par);
		}
		ISSState.refreshPanelInView();
	}

	/**
	 * Clears the selection for the panel
	 */
	synchronized public void clearSelection () {
		int size= selectedIcons.size();
		IconButton but;
		for (int i= 0; i < size; i++) {
			but= selectedIcons.get(0);
			but.setSelected(false);
			but.setDefaultLF();
			selectedIcons.remove(0);
		}
	}

	/**
	 * This methods sets the state of navigation buttons of frame to the state required by the panel
	 */
	public void matchToNavigator () {
		ISSGUIToolBar.getToolBar().setBackEnabled(navigator.isBackEnabled());
		ISSGUIToolBar.getToolBar().setForwardEnabled(navigator.isForwardEnabled());
	}

	/**
	 * Constructor that customizes the panel with contents from the cloud path workingDir
	 * @param workingDir the path whose files need to be shown in the new panel
	 */
	public ViewPanel (String workingDir) {
		super();
		//workingDirectory= workingDir;
		setBackground(Color.white);
		setLayout(new FlowLayout(FlowLayout.LEFT, Preferences.getHorizontalGap(), Preferences.getVerticalGap()));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setAutoscrolls(true);

		MouseListener listener= new ImplementedMouseListeners.ML_ViewPanel();
		addMouseListener(listener);
		addMouseMotionListener((MouseMotionListener)listener);
		addKeyListener(new ImplementedKeyListeners.KL_ViewPanel());
		matchToNavigator();

		ISSState.panelInView= this;
		navigator.setNewPath(workingDir);
		ISSState.refreshPanelInView();
	}

	/**
	 * Show the given files as Icons in the panel.
	 * It does not create new IconFile object for those files which are already being shown in the panel.
	 * @param newFiles the sorted array of new file objects to be added
	 */
	public ArrayList<IconButton> conjunct (ISSFile[] newFiles) {
		ArrayList<IconButton> toRefresh= new ArrayList<IconButton> ();
		if (searchResult) refreshParents= new ArrayList<String>();
		if (newFiles.length == 0) {
			removeAll();
			return toRefresh;
		}

		int i= 0, j= 0, removed= 0;
		Component[] coms= getComponents();

		if (coms.length > 0) {

			IconButton but= ((IconButton)coms[0]);

			for (; i < newFiles.length && j < coms.length;) {

				but= (IconButton)coms[j];

				//compare already existing with new one
				int compare= but.getRepresentingFile().compareTo(newFiles[i]);

				//if new one is lesser insert it and go back to outer for loop
				if (compare > 0) {
					IconButton toadd= new IconButton(newFiles[i]);
					add(toadd, i+j-removed);
					if (newFiles[i].fileType != FileType.Directory) {
						String ext= ISSIcons.getExtentionForFilename(newFiles[i].name);
						if (ext.equals("png") || ext.equals("jpg") || ext.equals("gif") || ext.equals("jpeg") )
							if (!ISSIcons.trueIcons.containsKey(newFiles[i].fileID + "_" + newFiles[i].name)) {
								toRefresh.add(toadd);
								if (searchResult)
									refreshParents.add(getSearchPathForFile(toadd.getRepresentingFile()));
							}
					}
					i++;
					continue;
				}

				//if both are equal, set the new file object as the representing file object for the icon button
				//go back to outer loop
				if (compare == 0) {
					but.setRepresentingFile(newFiles[i]);
					i++; j++; removed++;
					continue;
				}

				//existing is not present in the new files
				remove(coms[j]);
				if (selectedIcons.contains(coms[j]))
					selectedIcons.remove(coms[j]);
				removed++;
				j++;
			}
		}

		//add the remaining
		for (; i < newFiles.length; i++) {
			IconButton toadd= new IconButton(newFiles[i]);
			add(toadd);
			if (newFiles[i].fileType != FileType.Directory) {
				String ext= ISSIcons.getExtentionForFilename(newFiles[i].name);
				if (ext.equals("png") || ext.equals("jpg") || ext.equals("gif") || ext.equals("jpeg") )
					if (!ISSIcons.trueIcons.containsKey(newFiles[i].fileID + "_" + newFiles[i].name)) {
						toRefresh.add(toadd);
						if (searchResult)
							refreshParents.add(getSearchPathForFile(toadd.getRepresentingFile()));
					}
			}
		}

		//remove extra ones
		for (; j < coms.length; j++) {
			remove (coms[j]);
			if (selectedIcons.contains(coms[j]))
				selectedIcons.remove(coms[j]);
		}

		//returns icons to refresh
		return toRefresh;
	}

	/**
	 * Function to refresh this panel with new contents.
	 * Also handles the refreshing the icons to be displayed running a back-ground thread.
	 * The thread fetches the content-image as icon for image files.
	 */
	void refresh (ArrayList<ISSFile> contents) {
		if (refreshThread != null && refreshThread.isAlive()) {
			refreshThread.interrupt();
			refreshThread.setTocontinue(false);
		}
		refreshThread= null;

		//set the compare parameter to all files and sort them
		for (int i= 0; i < contents.size(); i++)
			contents.get(i).setParameter(getCompareParameter());
		ISSFile[] arr= new ISSFile[contents.size()];
		arr= contents.toArray(arr);
		Arrays.sort(arr);

		//hack to conjunct the IconButtons
		ArrayList<IconButton> toRefresh= ISSState.panelInView.conjunct(arr);

		revalidate();
		repaint();

		if (toRefresh.size() > 0) {
			if (searchResult)
				refreshThread= new RefreshThread (toRefresh,refreshParents);
			else
				refreshThread= new RefreshThread (toRefresh,navigator.getCurrentPath());
			refreshThread.start();
		}
	}

	/**
	 * Function that shows the search results of the given string in the contents of current working folder.
	 * The result is displayed in the panel.
	 */
	void showSearchResults () {
		//Performing the search
		searchFiles= BackEnd.searchISS(navigator.getParentPath(), searchString);
		//Separating the path from the ISSFile objects and putting them into array list searchFiles
		searchPaths= new ArrayList<String> ();
		for (ISSFile file : searchFiles) {
			String path= "/";
			int ind= file.name.lastIndexOf("/");
			if (ind > 0)
				path= file.name.substring(0, ind);
			searchPaths.add(path);
			file.name= file.name.substring(ind+1);
		}
		//Showing the search results
		searchResult= true;
		refresh(searchFiles);
	}

	/**
	 * For setting the name of the tab
	 * @param name
	 */
	public void setTabName (String name) {
		JTabbedPane tab= ISSGUInterface.frame.getTabs();
		TabComponent tabcom= (TabComponent) tab.getTabComponentAt(tab.getSelectedIndex());
		tabcom.setTabName(name);
	}

	/**
	 * The overridden paint method that sets the height of the panel correctly for the scrollPane<li>
	 * ScrollPane was not doing it automatically and had to be done manually
	 */
	public void paint (Graphics g) {
		super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		int childrenCount= getComponentCount();
		if (childrenCount > 0) {
			FlowLayout layout= (FlowLayout) getLayout();
			Insets ins= getInsets();
			Component zero= getComponent(0);
			int clmCount= (getWidth() - ins.left - ins.right) / (zero.getWidth() + layout.getHgap());
			int height= ins.top + ins.bottom;
			int i= 0, max= 0, h= 0;
			for (i= 0; i + clmCount < childrenCount; i+= clmCount);
			height= getComponent(i).getY();
			for (;i < childrenCount; i++) {
				h= getComponent(i).getHeight();
				if (h > max) max= h;
			}
			height+= max + layout.getVgap();
			setPreferredSize(new Dimension (0, height));
			Dimension d= new Dimension(ins.left + ins.right + zero.getWidth(), ins.top + ins.bottom  + zero.getHeight());
			setMinimumSize(d);
		} else  {
			String msg= null;
			if (searchResult)
				msg= "No match found";
			else msg= "No contents available.";
			g2.setColor(Color.magenta);
			FontMetrics m= g.getFontMetrics();
			int w= m.stringWidth(msg);
			int x= (this.getWidth() - w) / 2;
			g2.drawString(msg, x, 50);
			setPreferredSize(new Dimension ((int) getParent().getPreferredSize().getWidth(),100));
		}
		if (startPos != null && endPos != null) {
			g2.draw(selector);
		}
	}

	/**
	 * Returns the path of the File in the search results
	 * @param file whose path is to be given
	 * @return the path of the file
	 */
	String getSearchPathForFile(ISSFile file) {
		for (int i= 0; i < searchFiles.size(); i++)
			if (file.equals(searchFiles.get(i)))
				return searchPaths.get(i);
		return null;
	}
}