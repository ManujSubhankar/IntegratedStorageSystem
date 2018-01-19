package iss.gui;

import iss.FileType;
import iss.ISSFile;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * This class contains all the attributes which defines the state of the DropBox as a whole
 * and methods to be accessed from different other classes to operate on these attributes
 * @author Basanta Sharma
 */
public class ISSState {
	
	// Path where the program is currently running on
	static String projectPath= System.getProperty("user.dir") + File.separator;/* + "bin" + File.separator;*/
	
	// Panel currently in view
	static ViewPanel panelInView;
	
	// System clipboard reference
	static Clipboard sysClipboard= Toolkit.getDefaultToolkit().getSystemClipboard();
	
	//booleans used by menus
	static boolean isClipboardValid= false, isCut= false;
	
	/**
	 * Function which does initial setup for the Frame to show up...
	 */
	public static void init() {
		File f= new File (projectPath);
		if (!f.exists())
			projectPath= System.getProperty("user.dir") + File.separator;
		
		clearNativeFolder (".ISSClipboard" + File.separator);
		clearNativeFolder (".ISStemp" + File.separator);
		
		sysClipboard.addFlavorListener(new ClipboardFlavorListener());
	}
	
	/**
	 * Function which clear the native folder with given path
	 * used for initial clearing of .ISSClipboard and .ISStemp folders used by the application
	 * @param path
	 */
	static void clearNativeFolder (String path) {
		File file= new File (path);
		if (file.exists()) {
			File[] contents= file.listFiles();
			for (File f: contents) {
				f.delete();
			}
		}
		else file.mkdir();
	}
	
	/**
	 * The hack which refreshes the panel currently in view, if any changes are there
	 */
	public static void refreshPanelInView () {
		Navigator navigator= panelInView.getNavigator();
		String wd= navigator.getCurrentPath();
		if (wd.equals(Navigator.SEARCH)) {
			panelInView.showSearchResults();
			return;
		}
		ArrayList<ISSFile> contents= null;
		try {
			contents= BackEnd.getContents(navigator.getCurrentPath());
		} catch (Exception e1) {
			navigator.back();
			refreshPanelInView();
		}
		
		panelInView.refresh(contents);
	}
	
	/**
	 * Method to customize the scrollpane used by the panel showing all file icons
	 * @param c the component with which the customized scrollpane should be built with 
	 * @return the customized scrollpane object
	 */
	public static JScrollPane getCustomScroll (JComponent c) {
		JScrollPane sp= new JScrollPane(c);

		JScrollBar sb= sp.getVerticalScrollBar();
		sb.setUnitIncrement(100);
		sb.setBackground (Color.decode("#F95505"));
		return sp;
	}
	
	/**
	 * Method that finds out unique new name for the file/folder to be created.
	 * @param dirName true if you require new folder name, false if you require file name
	 * @return
	 */
	static String getNewName(boolean dirName) {
		Component[] coms= ISSState.panelInView.getComponents();
		String name= dirName? "New Folder" : "New File";
		if (coms == null || name == null) return null;
		int slNo= 1;
		for (int i= 0; i < coms.length; i++) {
			IconButton but= (IconButton)coms[i];
			if (dirName && !but.getRepresentingFile().fileType.equals(FileType.Directory))
				break;
			if (!dirName && but.getRepresentingFile().fileType.equals(FileType.Directory))
				continue;
			if (but.getRepresentingFile().name.equals(name)) {
				if (slNo != 1)
					name= name.substring(0, name.length() - 1);
				name+= slNo;
				slNo++;
			}
		}
		return name;
	}
}

/**
 * 
 * @author Basanta Sharma
 *
 */
class RefreshThread extends Thread {
	private static ArrayList<IconButton> prevToDelete;
	boolean toContinue;
	private ArrayList<IconButton> buts;
	private String parentDir;
	private ArrayList<String> parentDirs;
	private boolean singleParent= true;
	
	public void setTocontinue(boolean toContinue) {
		this.toContinue = toContinue;
	}
	
	public String getParentDir() {
		return parentDir;
	}
	
	public RefreshThread (ArrayList<IconButton> buts, String parentDir) {
		toContinue= true;
		this.buts= buts;
		this.parentDir= parentDir;
	}
	
	public RefreshThread (ArrayList<IconButton> buts, ArrayList<String> parentDirs) {
		toContinue= true;
		this.buts= buts;
		this.parentDirs= parentDirs;
		singleParent= false;
	}
	
	private void deletePreviousFiles () {
		if (prevToDelete == null) return;
		File f= null;
		ISSFile file= null;
		String path= ISSState.projectPath + ".ISStemp" + File.separator;
		for (IconButton but: prevToDelete) {
			file= but.getRepresentingFile();
			f= new File (path + file.fileID + "_" + file.name);
			if (f.exists())
				f.delete();
		}
		prevToDelete= null;
	}
	
	public void run () {

		deletePreviousFiles();
		
		for (int i= 0; i < buts.size(); i++) {
			IconButton but= buts.get(i);
			if (!toContinue) break;
			if (singleParent)
				ISSIcons.getTrueIcon(parentDir, but.getRepresentingFile());
			else
				ISSIcons.getTrueIcon(parentDirs.get(i), but.getRepresentingFile());
			but.validateIconImage();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
		finally {
			prevToDelete= buts;
		}
	}
}

/**
 * Class Implementing FlavorListener for the System Clipboard
 * @author Basanta Sharma
 *
 */
class ClipboardFlavorListener implements FlavorListener {
	@Override
	public void flavorsChanged(FlavorEvent e) {
		ISSState.isClipboardValid= false;
		//Clear the Clipboard of previous items if any
		ISSState.clearNativeFolder(ISSState.projectPath + ".ISSClipboard/");
	}
}
