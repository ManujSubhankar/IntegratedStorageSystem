package iss.gui;

import iss.FileType;
import iss.ISSFile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
/**
 * The panel which acts as a button with file icon and name
 * used to display a file in the file view panel of DropBoxFrame
 * @author Basanta Sharma
 *
 */
public class IconButton extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private boolean selected= false, started= false;
	private Border empty, focused;
	private IconLabelPane namePane;
	private Image image;
	private JPanel imagePanel;
	private ISSFile representingFile;
	private MouseListener mlistener;
	private MouseMotionListener mmlistener;
	
	/**
	 * Method to get the empty border for IconButton instance
	 * @return
	 */
	public Border getEmptyBorder() {
		return empty;
	}

	/**
	 * Method to get the focused border for IconButton instance
	 * @return
	 */
	public Border getFocusedBorder() {
		return focused;
	}
	
	/**
	 * Method which returns the representing file for the given IconButton
	 * @return the ISSFile object which is represented by the IconButton 
	 */
	public ISSFile getRepresentingFile () {
		return representingFile;
	}
	
	/**
	 * sets the representing file component
	 * @param representingFile
	 */
	public void setRepresentingFile(ISSFile representingFile) {
		this.representingFile = representingFile;
	}
	
	/**
	 * Constructor to customize the icon button
	 * @param rfile the ISSFile object for which the IconButton need to be created
	 */
	public IconButton (ISSFile rfile) {
		super();
		representingFile= rfile;
		//setPreferredSize(new Dimension(Preferences.getIconSize().width, Preferences.getIconSize().height + 50));
		setLayout(new BorderLayout());
		setBorder(empty= BorderFactory.createEmptyBorder());
		focused= BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		setOpaque (false);
		if (rfile.fileType == FileType.Directory)
			image= ISSIcons.getImageForFolder(rfile);
		else
			image= ISSIcons.getImageForFile(rfile);
		
		imagePanel= new ImagePanel ();
		imagePanel.setPreferredSize(Preferences.getIconSize());
		namePane= new IconLabelPane(rfile.name);
		add(imagePanel,BorderLayout.CENTER);
		add(namePane,BorderLayout.SOUTH);
		mlistener= new ImplementedMouseListeners.ML_IconButton();
		mmlistener= new ImplementedMouseMotionListeners.MML_IconButton();
		setMListeners();
	}
	
	/**
	 * Sets the new size of the icon
	 * @param w
	 * @param h
	 */
	/*public void updateSize (double w, double h) {
		setPreferredSize(new Dimension ((int)w, (int)h));
		image= image.getScaledInstance((int)(w * 0.8), (int)(h * 0.8), Image.SCALE_SMOOTH);
	}*/
	
	/**
	 * Sets the LF of the icon when pointer is placed above an icon
	 */
	public void setFocusedLF() {
		namePane.setBackground(IconLabelPane.focusedBG);
		namePane.setForeground(IconLabelPane.focusedFG);
		namePane.setSelectionColor(IconLabelPane.focusedBG);
		setBorder(focused);
		namePane.setOpaque(true);
	}
	
	/**
	 * Sets the default LF of the icon
	 */
	public void setDefaultLF() {
		namePane.setForeground(IconLabelPane.defaultFG);
		namePane.setSelectionColor(Color.white);
		setBorder(empty);
		namePane.setOpaque(false);
	}
	
	/**
	 * Sets the LF when the icon is selected
	 */
	public void setSelectedLF() {
		namePane.setBackground(IconLabelPane.selectedBG);
		namePane.setForeground(IconLabelPane.selectedFG);
		namePane.setSelectionColor(IconLabelPane.selectedBG);
		namePane.setOpaque(true);
	}
	
	/**
	 * Sets the selected state of the icon
	 * @param state the desired state
	 */
	public void setSelected(boolean state) {
		selected= state;
		if (state)
			setSelectedLF();
	}
	
	/**
	 * Tells whether the calling icon is selected or not
	 * @return true if icon is selected, false otherwise
	 */
	public boolean isSelected () {
		return selected;
	}
	
	/**
	 * Function which returns the Name of the file represented by the icon
	 */
	public String getName () {
		return representingFile == null? "" : representingFile.name;
	}
	
	public void removeMListeners () {
		imagePanel.removeMouseListener(mlistener);
		imagePanel.removeMouseMotionListener(mmlistener);
		namePane.removeMouseListener(mlistener);
		namePane.removeMouseMotionListener(mmlistener);
	}
	
	public void setMListeners () {
		imagePanel.addMouseListener(mlistener);
		imagePanel.addMouseMotionListener(mmlistener);
		namePane.addMouseListener(mlistener);
		namePane.addMouseMotionListener(mmlistener);
	}
	
	/**
	 * This methods makes the calling IconButton instance ready for rename operation
	 */
	public void setForRename () {
		namePane.setEditable(true);
		namePane.grabFocus();
		removeMListeners();
		namePane.setBackground(Color.white);
		namePane.setForeground(Color.BLACK);
		namePane.setSelectionColor(IconLabelPane.selectedBG);
		namePane.setOpaque(false);
		namePane.selectAll();
		namePane.setRenameBorder();
		namePane.addKeyListener();
		ISSState.panelInView.setRenamingIcon(this);
	}
	
	/**
	 * This method sets the usual characteristics of an icon after rename
	 */
	public void resetAfterRename (boolean toChange) {
		setMListeners();
		namePane.select(0, 0);
		namePane.setNullBorder();
		namePane.setEditable(false);
		namePane.setFocusable(false);
		namePane.setForeground(IconLabelPane.defaultFG);
		namePane.setOpaque(false);
		namePane.removeKeyListener();
		if (toChange) {
			BackEnd.renameISSFile(ISSState.panelInView.getWorkingDirectory() + namePane.getPrevName(),
					namePane.getText());
			namePane.setRenameOk();
		}
		else
			namePane.setOldName();
		representingFile.name= namePane.getText();
		validateIconImage();
		ISSState.panelInView.setRenamingIcon(null);
	}
	
	/**
	 * opens a ISS file
	 */
	private void openFile() {
		if (ISSState.panelInView.isSearchResultShowing()) {
			BackEnd.openFileFromISS(ISSState.panelInView.getSearchPathForFile(representingFile), representingFile);
		}
		else
			BackEnd.openFileFromISS(ISSState.panelInView.getWorkingDirectory(), representingFile);
	}
	
	/**
	 * opens a ISS directory
	 */
	private void openDirectory () {
		if (ISSState.panelInView.isSearchResultShowing()) {
			String wd= ISSState.panelInView.getSearchPathForFile(representingFile);
			Navigator navigator= ISSState.panelInView.getNavigator();
			if (!navigator.forward(wd + "/" + representingFile.name)) return;
			else  {
				ISSState.refreshPanelInView();
				ISSState.panelInView.setSearchResultShowing(false);
			}
			//BackEnd.openFileFromISS(ISSState.panelInView.getSearchPathForFile(representingFile), representingFile);
		}
		else {
			ISSState.panelInView.getNavigator().forward(representingFile.name);
			ISSState.refreshPanelInView ();
		}
	}
	
	/**
	 * Opens the calling file represented by the calling IconButton instance using openfile or openfolder methods.
	 */
	public void open () {
		if (representingFile.fileType.equals(FileType.Directory))
			openDirectory();
		else {
			new Thread ("openingFile") {
				public void run() {
					openFile();
				}
			}.start();
		}
	}
	
	/**
	 * selects/deselects the calling file icon based on current selected property.
	 * if not selected, selects, deselects otherwise
	 */
	void select (MouseEvent e) {
		if (!e.isControlDown() && !e.isPopupTrigger()) {
			ISSState.panelInView.clearSelection();
		}
		ArrayList<IconButton> selectedIcons= ISSState.panelInView.getSelectedIcons();
		if (isSelected() && !e.isPopupTrigger()) {
			setFocusedLF();
			setSelected(false);
			selectedIcons.remove(this);
		} else if (!isSelected()) {
			setSelected(true);
			selectedIcons.add(this);
		}
	}
	
	/**
	 * Validates the image of the icon after rename according to the filename
	 */
	public void validateIconImage () {
		if (representingFile.fileType == FileType.Directory)
			image= ISSIcons.getImageForFolder(representingFile);
		else
			image= ISSIcons.getImageForFile(representingFile);
		revalidate();
		repaint();
	}

	/**
	 * overridden paint method to resize the JPanel containing filename while editing the name
	 */
	public void paint (Graphics g) {
		super.paint(g);
		if (!started) {
			setPreferredSize(new Dimension(Preferences.getIconSize().width,
					Preferences.getIconSize().height + namePane.getSize().height));
		}
	}
	
	/**
	 * Internal class for the panel that conatins icon image.
	 * @author Basanta Sharma
	 *
	 */
	class ImagePanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
	
		public void paint (Graphics g) {
			Graphics2D g2= (Graphics2D) g;
			int h= image.getHeight(this);
			int w= image.getWidth(this);
			if (w == -1 || h == -1) {
				g2.drawImage(image, 0, 0, this);
			}
			else {
				g2.drawImage(image, (getWidth() - w)/2, (getHeight() - h)/2, this);
			}
		}
	}
}
