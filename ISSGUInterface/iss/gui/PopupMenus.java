package iss.gui;

import iss.ISSFile.CompareParameter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

class PopupMenus {
	// reference holding the popup menu for the icon panel
	private static JPopupMenu panelPopup;
	
	// reference holding the popup menu for the file icons
	private static JPopupMenu fileIconPopup;
	
	// reference holding the popup menu for the folder icons
	private static JPopupMenu folderIconPopup;
	
	/**
	 * makes the class uninstanciable
	 */
	private PopupMenus () {}
	
	/**
	 * Static method that returns popup menu for panel showing file/folder icons
	 * @return popup menu for icon panel
	 */
	public static JPopupMenu getPanelPopup () {
		if (panelPopup == null)
			buildPanelPopup();
		return panelPopup;
	}
	
	/**
	 * Static Method that returns popup menu for file icons
	 * @return popup menu for file icons
	 */
	public static JPopupMenu getFileIconPopup () {
		if (fileIconPopup == null)
			buildFileIconPopup();
		return fileIconPopup;
	}
	
	/**
	 * Static Method that returns popup menu for folder icons
	 * @return popup menu for folder Icons
	 */
	public static JPopupMenu getFolderIconPopup () {
		if (folderIconPopup == null)
			buildFolderIconPopup();
		return folderIconPopup;
	}
	
	/**
	 * Private Method for initializing popup menu for panel
	 */
	private static void buildPanelPopup () {
		panelPopup= new JPopupMenu();
		panelPopup.setBorder(BorderFactory.createEtchedBorder());
		
		JMenuItem pmi_refresh= new JMenuItem("Refresh");
		pmi_refresh.addActionListener(new ImplementedActionListeners.AL_Refresh());
		JMenuItem pmi_paste= new JMenuItem ("Paste");
		pmi_paste.addActionListener(new ImplementedActionListeners.AL_Paste());
		JMenuItem pmi_newFile= new JMenuItem ("Create New File");
		pmi_newFile.addActionListener(new ImplementedActionListeners.AL_NewFile());
		JMenuItem pmi_newFolder= new JMenuItem ("Create New Folder");
		pmi_newFolder.addActionListener(new ImplementedActionListeners.AL_NewFolder());
		JMenuItem pmi_selectAll= new JMenuItem ("Select All");
		pmi_selectAll.addActionListener(new ImplementedActionListeners.AL_SelectAll());
		JMenuItem pmi_properties= new JMenuItem ("Properties");
		pmi_properties.setEnabled(false);
		JMenu pm_arrangeItems= new JMenu("Arrange Items");
			JMenuItem pmi_byName= new JRadioButtonMenuItem ("By Name");
			pmi_byName.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.NAME));
			JMenuItem pmi_bySize= new JRadioButtonMenuItem ("By Size");
			pmi_bySize.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.SIZE));
			JMenuItem pmi_byType= new JRadioButtonMenuItem ("By File Type");
			pmi_byType.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.TYPE));
			JMenuItem pmi_byMDate= new JRadioButtonMenuItem ("By Modification Date");
			pmi_byMDate.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.MODIFICATION));
			
			ButtonGroup arrangeGroup= new ButtonGroup();
			arrangeGroup.add(pmi_byName);
			pmi_byName.setSelected(true);
			arrangeGroup.add(pmi_bySize);
			arrangeGroup.add(pmi_byType);
			arrangeGroup.add(pmi_byMDate);
			
			JMenuItem pmi_ascending= new JRadioButtonMenuItem ("Ascending Order");
			pmi_ascending.setEnabled(false);
			JMenuItem pmi_descending= new JRadioButtonMenuItem ("Descending Order");
			pmi_descending.setEnabled(false);
			
			ButtonGroup orderGroup= new ButtonGroup ();
			orderGroup.add(pmi_ascending);
			pmi_ascending.setSelected(true);
			orderGroup.add(pmi_descending);
			
			pm_arrangeItems.add(pmi_byName);
			pm_arrangeItems.add(pmi_bySize);
			pm_arrangeItems.add(pmi_byType);
			pm_arrangeItems.add(pmi_byMDate);
			pm_arrangeItems.addSeparator();
			pm_arrangeItems.add(pmi_ascending);
			pm_arrangeItems.add(pmi_descending);
			
		panelPopup.add(pmi_newFile);
		panelPopup.add(pmi_newFolder);
		panelPopup.addSeparator();
		panelPopup.add(pm_arrangeItems);
		panelPopup.add(pmi_selectAll);
		panelPopup.addSeparator();
		panelPopup.add(pmi_paste);
		panelPopup.addSeparator();
		panelPopup.add(pmi_refresh);
		panelPopup.addSeparator();
		panelPopup.add(pmi_properties);
	}

	/**
	 * Private method for initializing popup menu for file icons
	 */
	private static void buildFileIconPopup () {
		fileIconPopup= new JPopupMenu();
		fileIconPopup.setBorder(BorderFactory.createEtchedBorder());
		
		JMenuItem pmi_open= new JMenuItem ("Open");
		pmi_open.addActionListener(new ImplementedActionListeners.AL_Open());
		JMenuItem pmi_cut= new JMenuItem ("Cut");
		pmi_cut.addActionListener(new ImplementedActionListeners.AL_Cut());
		JMenuItem pmi_copy= new JMenuItem ("Copy");
		JMenuItem pmi_download= new JMenuItem ("Download");
		pmi_download.addActionListener(new ImplementedActionListeners.AL_Download());
		pmi_copy.addActionListener(new ImplementedActionListeners.AL_Copy());
		JMenuItem pmi_rename= new JMenuItem ("Rename");
		pmi_rename.addActionListener(new ImplementedActionListeners.AL_Rename());
		JMenuItem pmi_delete= new JMenuItem ("Delete");
		pmi_delete.addActionListener(new ImplementedActionListeners.AL_Delete());
		JMenuItem pmi_properties= new JMenuItem ("Properties");
		pmi_properties.setEnabled(false);
		
		fileIconPopup.add(pmi_open);
		fileIconPopup.addSeparator();
		fileIconPopup.add(pmi_cut);
		fileIconPopup.add(pmi_copy);
		fileIconPopup.add(pmi_download);
		fileIconPopup.addSeparator();
		fileIconPopup.add(pmi_rename);
		fileIconPopup.add(pmi_delete);
		fileIconPopup.addSeparator();
		fileIconPopup.add(pmi_properties);
	}
	
	/**
	 * Private method for initializing popup menu for folder icons
	 */
	private static void buildFolderIconPopup () {
		folderIconPopup= new JPopupMenu();
		folderIconPopup.setBorder(BorderFactory.createEtchedBorder());
		
		JMenuItem pmi_open= new JMenuItem ("Open");
		pmi_open.addActionListener(new ImplementedActionListeners.AL_Open());
		JMenuItem pmi_openInNewTab= new JMenuItem ("Open In New Tab");
		pmi_openInNewTab.setEnabled(false);
		JMenuItem pmi_openInNewWindow= new JMenuItem ("Open In New Window");
		pmi_openInNewWindow.setEnabled(false);
		JMenuItem pmi_cut= new JMenuItem ("Cut");
		pmi_cut.addActionListener(new ImplementedActionListeners.AL_Cut());
		JMenuItem pmi_copy= new JMenuItem ("Copy");
		pmi_copy.addActionListener(new ImplementedActionListeners.AL_Copy());
		JMenuItem pmi_download= new JMenuItem ("Download");
		pmi_download.addActionListener(new ImplementedActionListeners.AL_Download());
		JMenuItem pmi_rename= new JMenuItem ("Rename");
		pmi_rename.addActionListener(new ImplementedActionListeners.AL_Rename());
		JMenuItem pmi_delete= new JMenuItem ("Delete");
		pmi_delete.addActionListener(new ImplementedActionListeners.AL_Delete());
		JMenuItem pmi_properties= new JMenuItem ("Properties");
		pmi_properties.setEnabled(false);
		
		folderIconPopup.add(pmi_open);
		folderIconPopup.add(pmi_openInNewTab);
		folderIconPopup.add(pmi_openInNewWindow);
		folderIconPopup.addSeparator();
		folderIconPopup.add(pmi_cut);
		folderIconPopup.add(pmi_copy);
		folderIconPopup.add(pmi_download);
		folderIconPopup.addSeparator();
		folderIconPopup.add(pmi_rename);
		folderIconPopup.add(pmi_delete);
		folderIconPopup.addSeparator();
		folderIconPopup.add(pmi_properties);
	}

}

