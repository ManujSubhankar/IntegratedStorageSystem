package iss.gui;

import iss.ISSFile;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class ISSIcons {
	
	// HashMaps of icon Images
	static HashMap<String, Image> fileIcons= new HashMap<String, Image> ();
	static HashMap<String, Image> folderIcons= new HashMap<String, Image> ();
	static HashMap<String, Image> buttonIcons= new HashMap<String, Image> ();
	static HashMap<String, Image> trueIcons= new HashMap<String, Image> ();
	
	/**
	 * Method that returns appropriate Image for the given foldername
	 * @param foldername the name of the folder
	 * @return
	 */
	public static Image getImageForFolder (ISSFile file) {
		String foldername= file.name;
		foldername= foldername.toLowerCase();
		if (folderIcons.containsKey(foldername))
			return folderIcons.get(foldername);
		return folderIcons.get("defaultfolder");
	}
	
	/**
	 * Method which returns the cuts off the extension from a filename and returns the same
	 * @param filename
	 * @return
	 */
	public static String getExtentionForFilename(String filename) {
		int ind= filename.lastIndexOf('.');
		return ind == -1 ? "" : filename.substring(ind + 1).toLowerCase();
	}
	
	/**
	 * Method which returns the appropriate image given a file name based upon the extension
	 * @param filename
	 * @return
	 */
	public static Image getImageForFile (ISSFile file) {
		String filename= file.name;
		Image image;
		
		String ext= getExtentionForFilename(filename);
		if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif")) {
			if (trueIcons.containsKey(file.fileID + "_" + filename)) {
				image= trueIcons.get(file.fileID + "_" + filename);
				return image;
			}
		}
		if (ext.endsWith("~"))
			ext= ext.substring(0,ext.length()-1);
		if (ext.equals("")) ext= "default";
		if (fileIcons.containsKey(ext)) {
			image= fileIcons.get(ext);
		}
		else
			image= fileIcons.get("default");
		return image;
	}
	
	static Image createImageForFile (String path) {
		ImageIcon icon= new ImageIcon (path);
		int h= icon.getIconHeight();
		int w= icon.getIconWidth();
		int scaleHeight, scaleWidth;
		if (h > w) {
			scaleHeight= (int) (Preferences.getIconSize().height);
			scaleWidth= (int)(Preferences.getIconSize().width * ((double)w / h));
		} else {
			scaleHeight= (int) (Preferences.getIconSize().height * ((double)h / w));
			scaleWidth= (int) (Preferences.getIconSize().width);
		}
		return icon.getImage().getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
	}
	
	/**
	 * Method which fills the HashMaps of icons with the icon images
	 */
	public static void loadIcons () {
		String path= /*projectPath +*/ "Resources" + File.separator;
		Image image;
		File container= new File(path + "fileicons");
		String[] contents= container.list();
		for (int i= 0; i < contents.length; i++) {
			image= createImageForFile(container.getAbsolutePath() + File.separator + contents[i]);
			fileIcons.put(contents[i], image);
		}
		container= new File(path + "foldericons");
		contents= container.list();
		for (int i= 0; i < contents.length; i++) {
			image= createImageForFile(container.getAbsolutePath() + File.separator + contents[i]);
			folderIcons.put(contents[i], image);
		}
		container= new File(path + "buttonicons");
		contents= container.list();
		for (int i= 0; i < contents.length; i++) {
			image= createImageForFile(container.getAbsolutePath() + File.separator + contents[i]);
			buttonIcons.put(contents[i], image);
		}
	}
	
	/**
	 * Gets the actual image from the image file to show as icon of the file.
	 */
	static void getTrueIcon (String parentDir, ISSFile file) {
		
		Thread th= null;
		try {
			th= BackEnd.openISSFile(file, parentDir);
		} catch (Exception e) {
			return;
		}
		try {
			th.join();
		} catch (InterruptedException e) {
			((RefreshThread)Thread.currentThread()).toContinue= false;
			System.out.println ("Could not get photo");
			return;
		}
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.out.println ("Interrupted");
		}
		
		File f= new File (ISSState.projectPath + ".ISStemp" + File.separator + file.fileID + "_" + file.name);
		f.deleteOnExit();
		
		ISSIcons.trueIcons.put(file.fileID + "_" + file.name, ISSIcons.createImageForFile(f.getAbsolutePath()));
	}

}
