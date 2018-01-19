package iss.gui;


import iss.ISSFile;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Packaging class of all classes implementing AcitonListener
 * @author Basanta Sharma
 *
 */
public class ImplementedActionListeners {

	static class AL_Open implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ISSState.panelInView.getSelectedIcons().size() == 1)
				ISSState.panelInView.getSelectedIcons().get(0).open();
		}
	}

	/**
	 * ActionListener for exit menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Exit implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * ActionListener for New Tab menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_NewTab implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTabbedPane tabs= ISSGUInterface.frame.getTabs();
			JScrollPane scrollPane= ISSGUInterface.frame.getScrollPane();
			if (tabs == null) {

				ISSGUInterface.frame.setTabs(tabs= new JTabbedPane () {
					private static final long serialVersionUID = 1L;
					public void paint (Graphics g) {
						super.paint(g);
						ISSState.panelInView= ((ViewPanel)((JScrollPane)getSelectedComponent()).getViewport().getComponent(0));
						ISSState.panelInView.matchToNavigator();
					}
				});

				tabs.addTab("Tab1", scrollPane);
				tabs.setTabComponentAt(0, new TabComponent ("Tab1", scrollPane));
				ISSGUInterface.frame.remove(scrollPane);
				ISSGUInterface.frame.setScrollPane(scrollPane= null);
				ISSGUInterface.frame.add(tabs);
			}
			int no_of_tabs= tabs.getTabCount();
			JScrollPane sp= ISSState.getCustomScroll(new ViewPanel("/"));
			tabs.addTab("Tab" + (no_of_tabs + 1), sp);
			tabs.setTabComponentAt(no_of_tabs, new TabComponent ("Tab" + (no_of_tabs + 1), (JScrollPane)tabs.getComponentAt(no_of_tabs)));
			tabs.getComponent(no_of_tabs).revalidate();
		}
	}

	/**
	 * ActionListener for Close Tab menu and close button of tab
	 * @author Basanta Sharma
	 *
	 */
	static class AL_CloseTab implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTabbedPane tabs= ISSGUInterface.frame.getTabs();
			JScrollPane scrollPane= ISSGUInterface.frame.getScrollPane();
			int no_of_tabs= tabs.getTabCount();
			if (no_of_tabs == 2) {
				JScrollPane toRemove=((TabComponent)((JButton)e.getSource()).getParent()).getContainingScroll();
				if (tabs.getComponentAt(0) == toRemove) {
					ISSGUInterface.frame.setScrollPane(scrollPane= (JScrollPane)tabs.getComponentAt(1));
				}
				else {
					ISSGUInterface.frame.setScrollPane(scrollPane= (JScrollPane)tabs.getComponentAt(0));
				}
				ISSGUInterface.frame.remove(tabs);
				ISSGUInterface.frame.add(scrollPane);
				ISSGUInterface.frame.setTabs(tabs= null);
				ISSGUInterface.frame.revalidate();
				ISSState.panelInView= (ViewPanel)scrollPane.getViewport().getComponent(0);
				ISSState.panelInView.matchToNavigator();
				return;
			}
			tabs.remove( ((TabComponent)((JButton)e.getSource()).getParent()).getContainingScroll() );
			ISSState.panelInView= (ViewPanel)((JScrollPane) (tabs.getSelectedComponent())).getViewport().getComponent(0);
			ISSState.panelInView.matchToNavigator();
		}
	}

	/**
	 * ActionListener for New Window menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_NewWindow implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Runtime rt= Runtime.getRuntime();
			try {
				String[] cmd= {"java", "iss.gui.ISSGUInterface"};
				Process p= rt.exec(cmd,null,new File (ISSState.projectPath));
				System.out.println (p);
			} catch (IOException e1) {
				System.out.println ("process not found");
			}
		}
	}

	/**
	 * ActionListener for Back button of the toolbar.
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Backward implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ISSState.panelInView.getNavigator().back();
			ISSState.refreshPanelInView();
		}
	}

	/**
	 * ActionListener for Forward button of toolbar
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Forward implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ISSState.panelInView.getNavigator().forward(null);
			ISSState.refreshPanelInView();
		}
	}

	/**
	 * ActionListener for all menuitems of Arrange Items menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_ChangeArrangement implements ActionListener {
		ISSFile.CompareParameter parameter;
		public AL_ChangeArrangement (ISSFile.CompareParameter par) {
			parameter= par;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ISSState.panelInView.getCompareParameter() != parameter)
				ISSState.panelInView.updateCompareParameter(parameter);
		}
	}

	/**
	 * ActionListener for Refresh menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Refresh implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ISSState.refreshPanelInView();
		}
	}

	/**
	 * ActionListner for Cut menuitem
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Cut implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			final ViewPanel panel= ISSState.panelInView;
			final ArrayList<ISSFile> ISSfiles= new ArrayList<ISSFile> ();
			ArrayList<IconButton> selectedIcons= panel.getSelectedIcons();
			for (int i= 0; i < selectedIcons.size(); i++) {
				IconButton but= selectedIcons.get(i);
				ISSfiles.add(but.getRepresentingFile());
			}

			ISSfiles.add(0,new ISSFile (panel.getWorkingDirectory(), null, "", 0));

			ISSTransferable tfiles= new ISSTransferable (ISSfiles);
			ISSState.sysClipboard.setContents(tfiles, null);

			ISSState.isCut= true;
		}
	}

	/**
	 * ActionListener for Copy menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Copy implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ISSState.isClipboardValid= false;
			final ViewPanel panel= ISSState.panelInView;
			ArrayList<File> files= new ArrayList<File> ();
			final ArrayList<ISSFile> ISSfiles= new ArrayList<ISSFile> ();
			ArrayList<IconButton> selectedIcons= panel.getSelectedIcons();
			for (int i= 0; i < selectedIcons.size(); i++) {
				IconButton but= selectedIcons.get(i);
				files.add(new File (ISSState.projectPath + ".ISSClipboard/" + selectedIcons.get(i).getName()));
				ISSfiles.add(but.getRepresentingFile());
			}

			ISSfiles.add(0,new ISSFile (panel.getWorkingDirectory(), null, "", 0));

			ISSTransferable tfiles= new ISSTransferable (ISSfiles);
			ISSState.sysClipboard.setContents(tfiles, null);
		}
	}

	/**
	 * ActionListener for Paste menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Paste implements ActionListener {
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ISSState.sysClipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor) && 
					!ISSState.sysClipboard.isDataFlavorAvailable(ISSTransferable.getISSFileListFlavor()) ) {
				ArrayList<File> files= null;
				try {
					files= (ArrayList<File>) ISSState.sysClipboard.getData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException | IOException e1) {
					e1.printStackTrace();
				}
				
				BackEnd.storeFilesInISS(files,ISSState.panelInView.getWorkingDirectory());

			} else if (ISSState.sysClipboard.isDataFlavorAvailable(ISSTransferable.getISSFileListFlavor())){
				
				ArrayList<ISSFile> files = null;
				try {
					files= (ArrayList<ISSFile>) ISSState.sysClipboard.getData(ISSTransferable.getISSFileListFlavor());
				} catch (UnsupportedFlavorException | IOException e1) {
					e1.printStackTrace();
				}
				
				final String cloudpath= ((ISSFile)files.get(0)).name;
				files.remove(0);
				
				if (ISSState.isCut) {
					BackEnd.moveFileInISS(cloudpath, files, ISSState.panelInView.getWorkingDirectory());
					ISSState.isCut= false;
				}
				else
					BackEnd.copyISStoISS(cloudpath, files, ISSState.panelInView.getWorkingDirectory());
			}
		}
	}

	/**
	 * ActionListener for Delete menu
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Delete implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<IconButton> ibs= ISSState.panelInView.getSelectedIcons();
			final ArrayList<ISSFile> files= new ArrayList<ISSFile> ();
			for (int i= 0; i < ibs.size(); i++)
				files.add(ibs.get(i).getRepresentingFile());
			String msg= "Are you sure you want to delete selected " + files.size() + " items?";
			int choice= JOptionPane.showConfirmDialog(null, msg, "Delete", JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION) return;
			BackEnd.deleteFileInISS(ISSState.panelInView.getWorkingDirectory(), files);
		}
	}

	/**
	 * ActionListener for timers used by paste, delete etc. for refreshing panel
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Timer implements ActionListener {
		boolean finished= false;
		public void setFinished(boolean finished) {
			this.finished = finished;
		}
		public void actionPerformed (ActionEvent e) {
			if (finished)
				((Timer)e.getSource()).stop();
			ISSState.refreshPanelInView();
		}
	}

	/**
	 * ActionListener for New Folder menuitem
	 * @author Basanta Sharma
	 *
	 */
	static class AL_NewFolder implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String newFolderName= ISSState.getNewName(true);
			BackEnd.createFolderInISS(ISSState.panelInView.getWorkingDirectory(), newFolderName);
		}
	}

	/**
	 * ActionListener for Rename menuitem
	 * @author Basanta Sharma
	 *
	 */
	static class AL_Rename implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList <IconButton> selectedIcons= ISSState.panelInView.getSelectedIcons();
			if (selectedIcons.size() == 1) {
				selectedIcons.get(0).setForRename();
			}
		}
	}

	/**
	 * ActionListener for Seclect All menuitem
	 * @author Basanta Sharma
	 *
	 */
	static class AL_SelectAll implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Component[] comps= ISSState.panelInView.getComponents();
			ISSState.panelInView.clearSelection();
			ArrayList<IconButton> selectedIcons= ISSState.panelInView.getSelectedIcons();
			IconButton but= null;
			for (Component comp: comps) {
				but= (IconButton)comp;
				selectedIcons.add(but);
				but.setSelected(true);
			}
		}
	}

	/**
	 * ActionListener for Invert Selection menuitem
	 * @author Basanta Sharma
	 *
	 */
	static class AL_InvertSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Component[] comps= ISSState.panelInView.getComponents();
			ArrayList<IconButton> selectedIcons= ISSState.panelInView.getSelectedIcons();
			IconButton but= null;
			for (Component comp: comps) {
				but= (IconButton)comp;
				if (but.isSelected()) {
					selectedIcons.remove(but);
					but.setSelected(false);
					but.setDefaultLF();
				} else {
					selectedIcons.add(but);
					but.setSelected(true);
				}
			}
		}
	}

	static class AL_OpenBrowser implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().browse(URI.create("http://192.168.34.114:8080/ISS.jsp"));
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Could not open browser");
			}
		}
	}
	
	static class AL_NewFile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Component[] coms= ISSState.panelInView.getComponents();
			String newFileName= ISSState.getNewName(false);
			File file= new File (ISSState.projectPath + ".ISStemp/" + newFileName);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				System.out.println ("Cannot make new file because of exception: " + e1.getMessage());
				return;
			}
			ArrayList<File> files= new ArrayList<File> ();
			files.add(file);
			BackEnd.createFileInISS(files);
			ISSState.refreshPanelInView();
			coms= ISSState.panelInView.getComponents();
			for (Component com: coms) {
				IconButton but= (IconButton) com;
				if (but.getRepresentingFile().name.equals(newFileName)) {
					but.setForRename();
					break;
				}
			}
		}
	}

	/**
	 * ActionListener to download contents of ISS to the local file system
	 * @author bca3
	 *
	 */
	static class AL_Download implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ViewPanel pnl= ISSState.panelInView;
			ArrayList<ISSFile> files= pnl.getSelectedFiles();
			JFileChooser ch= new JFileChooser();
			ch.setDialogType(JFileChooser.CUSTOM_DIALOG);
			ch.setDialogTitle("Select the destination folder for download");
			ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			ch.setMultiSelectionEnabled(false);
			int option= ch.showDialog(null, "Download");
			File f= null;
			if (option == JFileChooser.APPROVE_OPTION) {
				f= ch.getSelectedFile();
				BackEnd.downloadFromISS(pnl.getWorkingDirectory(),files, f.getAbsolutePath());
			}
		}
	}
	
	static class AL_SearchISS implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String searchText= ((JTextField)((JButton)e.getSource()).getParent().getComponent(0)).getText();
			ISSState.panelInView.setSearchString(searchText);
			ISSState.panelInView.getNavigator().forward(Navigator.SEARCH);
			ISSState.panelInView.showSearchResults();
		}
	}
}
