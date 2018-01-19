package iss.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/**
 * This class creates the GUI frame for acessing the virtual cloud
 * @author Basanta Sharma
 *
 */
class ISSGUIFrame extends JFrame {
	
	private static final long serialVersionUID = -2236448023267154184L;
	private JTabbedPane tabs;
	private JScrollPane scrollPane;
	
	public JTabbedPane getTabs() {
		return tabs;
	}

	public void setTabs(JTabbedPane tabs) {
		this.tabs = tabs;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public ISSGUIFrame () {
		
		//create and customize the frame to default
		super();
		Toolkit tk= Toolkit.getDefaultToolkit();
		Dimension dim= tk.getScreenSize();
		dim.width/= 1.5;
		dim.height/= 1.1;
		setSize(dim);
		setTitle("Interface to Integrated Storage System");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			//ignore these exceptions
		}
		setIconImage(ISSIcons.createImageForFile("Resources" + File.separator + "swami_icon.jpg"));
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		//create and add menu bar to the ISSGUIFrame
		setJMenuBar(ISSGUIMenuBar.getMenuBar());
		add(ISSGUIToolBar.getToolBar(),BorderLayout.NORTH);
		
		ViewPanel panel= new ViewPanel ("/");
		scrollPane= ISSState.getCustomScroll(panel);
		add(scrollPane);
		validate();
		//ISSState.popUp= PanelPopup.getPanelPopup();
	}
}