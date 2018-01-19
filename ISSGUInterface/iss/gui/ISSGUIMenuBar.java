package iss.gui;

import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import iss.ISSFile.CompareParameter;
/**
 * Class which customizes the menubar for the DropBoxFrame
 * @author bca3
 *
 */
public class ISSGUIMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private static ISSGUIMenuBar menubar;
	//private ActionListener l_newFolder;
	//private ActionListener l_newFile;
	private ActionListener l_refresh;
	
	/**
	 * private constructor
	 */
	private ISSGUIMenuBar () {
		super();
		menubar= this;
		buildMenu();
	}
	
	/**
	 * Function that actually builds the menu;
	 */
	private void buildMenu () {
		//create items for menu bar
		JMenu m_file= new JMenu ("File");
		m_file.setMnemonic('F');
		JMenu m_edit= new JMenu ("Edit");
		m_edit.setMnemonic('E');
		JMenu m_view= new JMenu ("View");
		m_view.setMnemonic('V');
		JMenu m_help= new JMenu ("Help");
		m_help.setMnemonic('H');
		//add items to menu bar
		menubar.add(m_file);
		menubar.add(m_edit);
		menubar.add(m_view);
		menubar.add(m_help);
		
		//create items for file menu
		JMenuItem mi_newTab= new JMenuItem("New tab");
		mi_newTab.setMnemonic('t');
		mi_newTab.addActionListener(new ImplementedActionListeners.AL_NewTab());
		JMenuItem mi_newWindow= new JMenuItem("New window");
		mi_newWindow.setMnemonic('w');
		mi_newWindow.addActionListener(new ImplementedActionListeners.AL_NewWindow());
		JMenuItem mi_exit= new JMenuItem("Exit");
		mi_exit.setMnemonic('x');
		mi_exit.addActionListener(new ImplementedActionListeners.AL_Exit());
		JMenuItem mi_newFile= new JMenuItem ("Create new File");
		mi_newFile.addActionListener(new ImplementedActionListeners.AL_NewFile());
		JMenuItem mi_newFolder= new JMenuItem ("Create new Folder");
		mi_newFolder.addActionListener(new ImplementedActionListeners.AL_NewFolder());
		JMenuItem mi_openBrowser= new JMenuItem ("Browse BSFS in Browser");
		mi_openBrowser.addActionListener(new ImplementedActionListeners.AL_OpenBrowser());
		//add items to file menu
		m_file.add(mi_newTab);
		m_file.add(mi_newWindow);
		m_file.addSeparator();
		m_file.add(mi_newFile);
		m_file.add(mi_newFolder);
		m_file.addSeparator();
		m_file.add(mi_openBrowser);
		m_file.add(mi_exit);
		
		//create items for edit menu
		JMenuItem mi_undo= new JMenuItem ("Undo");
		mi_undo.setEnabled(false);
		JMenuItem mi_redo= new JMenuItem ("Redo");
		mi_redo.setEnabled(false);
		JMenuItem mi_cut= new JMenuItem ("Cut");
		mi_cut.addActionListener(new ImplementedActionListeners.AL_Cut());
		JMenuItem mi_copy= new JMenuItem ("Copy");
		mi_copy.addActionListener(new ImplementedActionListeners.AL_Copy());
		JMenuItem mi_paste= new JMenuItem ("Paste");
		mi_paste.addActionListener(new ImplementedActionListeners.AL_Paste());
		JMenuItem mi_delete= new JMenuItem ("Delete");
		mi_delete.addActionListener(new ImplementedActionListeners.AL_Delete());
		JMenuItem mi_selectAll= new JMenuItem ("Select All");
		mi_selectAll.addActionListener(new ImplementedActionListeners.AL_SelectAll());
		JMenuItem mi_invert= new JMenuItem ("Invert Selection");
		mi_invert.addActionListener(new ImplementedActionListeners.AL_InvertSelection());
		JMenuItem mi_find= new JMenuItem ("Find/Replace");
		mi_find.setEnabled(false);
		// add items to edit menu
		m_edit.add(mi_undo);
		m_edit.add(mi_redo);
		m_edit.addSeparator();
		m_edit.add(mi_cut);
		m_edit.add(mi_copy);
		m_edit.add(mi_paste);
		m_edit.addSeparator();
		m_edit.add(mi_delete);
		m_edit.add(mi_selectAll);
		m_edit.add(mi_invert);
		m_edit.addSeparator();
		m_edit.add(mi_find);
		
		//create items for view menu
		JMenuItem mi_refresh= new JMenuItem ("Refresh");
		l_refresh= new ImplementedActionListeners.AL_Refresh();
		mi_refresh.addActionListener(l_refresh);
		JMenuItem mi_zoomIn= new JMenuItem ("Zoom in");
		mi_zoomIn.setEnabled(false);
		JMenuItem mi_zoomOut= new JMenuItem ("Zoom out");
		mi_zoomOut.setEnabled(false);
		JMenuItem mi_normal= new JMenuItem ("Normal Size");
		mi_normal.setEnabled(false);
		JMenu m_arrangeItems= new JMenu ("Arrange Items");
			JRadioButtonMenuItem mi_byName= new JRadioButtonMenuItem ("By Name");
			mi_byName.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.NAME));
			mi_byName.setSelected(true);
			JRadioButtonMenuItem mi_bySize= new JRadioButtonMenuItem ("By Size");
			mi_bySize.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.SIZE));
			JRadioButtonMenuItem mi_byType= new JRadioButtonMenuItem ("By Type");
			mi_byType.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.TYPE));
			JRadioButtonMenuItem mi_byMDate= new JRadioButtonMenuItem ("By Modification Date");
			mi_byMDate.addActionListener(new ImplementedActionListeners.AL_ChangeArrangement(CompareParameter.MODIFICATION));
			JRadioButtonMenuItem mi_ascending= new JRadioButtonMenuItem ("Ascending Order");
			mi_ascending.setSelected(true);
			mi_ascending.setEnabled(false);
			JRadioButtonMenuItem mi_descending= new JRadioButtonMenuItem ("Descending Order");
			mi_descending.setEnabled(false);
			ButtonGroup bg_sort= new ButtonGroup ();
			bg_sort.add(mi_byName);
			bg_sort.add(mi_bySize);
			bg_sort.add(mi_byType);
			bg_sort.add(mi_byMDate);
			ButtonGroup bg_order= new ButtonGroup ();
			bg_order.add(mi_ascending);
			bg_order.add(mi_descending);
		JRadioButtonMenuItem mi_icons= new JRadioButtonMenuItem ("Icons");
		mi_icons.setSelected(true);
		mi_icons.setEnabled(false);
		JRadioButtonMenuItem mi_list= new JRadioButtonMenuItem ("List");
		mi_list.setEnabled(false);
		JRadioButtonMenuItem mi_grid= new JRadioButtonMenuItem ("Grid");
		mi_grid.setEnabled(false);
		ButtonGroup bg_view= new ButtonGroup ();
		bg_view.add(mi_icons);
		bg_view.add(mi_list);
		bg_view.add(mi_grid);
		JMenuItem mi_resetView= new JMenuItem ("Reset View");
		mi_resetView.setEnabled(false);
		//add items to view menu
		m_view.add(mi_refresh);
		m_view.addSeparator();
		m_view.add(m_arrangeItems);
			m_arrangeItems.add(mi_byName);
			m_arrangeItems.add(mi_bySize);
			m_arrangeItems.add(mi_byType);
			m_arrangeItems.add(mi_byMDate);
			m_arrangeItems.addSeparator();
			m_arrangeItems.add(mi_ascending);
			m_arrangeItems.add(mi_descending);
		m_view.addSeparator();
		m_view.add(mi_zoomIn);
		m_view.add(mi_zoomOut);
		m_view.add(mi_normal);
		m_view.addSeparator();
		m_view.add(mi_icons);
		m_view.add(mi_list);
		m_view.add(mi_grid);
		m_view.add(mi_resetView);
		
		//create items for help menu
		JMenuItem mi_all= new JMenuItem ("All Topics");
		mi_all.setEnabled(false);
		JMenuItem mi_search= new JMenuItem ("Search for files/folders");
		mi_search.setEnabled(false);
		JMenuItem mi_about= new JMenuItem ("About DropBox");
		mi_about.setEnabled(false);
		//add items to help menu
		m_help.add(mi_all);
		m_help.addSeparator();
		m_help.add(mi_search);
		m_help.addSeparator();
		m_help.add(mi_about);
	}
	
	/**
	 * Function which gives the menubar for the dropbox.
	 * Calling this method many times will give the same reference to the menubar
	 * @return
	 */
	public static ISSGUIMenuBar getMenuBar() {
		if (menubar == null)
			menubar= new ISSGUIMenuBar();
		return menubar;
	}
	
	/**
	 * meant to return the listeners for building the popup menu with same menuitems
	 * @return
	 */
	ActionListener[] getMenuListenersForPanel () {
		ActionListener[] items= new ActionListener[1];
		items[0]= l_refresh;
		return items;
	}
}
