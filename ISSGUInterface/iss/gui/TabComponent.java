package iss.gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 * Class for customized tab component used by the tabbed pane of DropBox
 * @author Basanta Sharma
 *
 */
public class TabComponent extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//Static reference to the icon used in tab's close button
	private static ImageIcon close_icon;
	
	//The reference to the scrollPane contained by the tab to which this tabComponent is added
	private JScrollPane containingScroll;
	
	//The label for the tab
	private JLabel tabLabel;
	
	//Getters and setters
	public String getTabName() {
		return tabLabel.getText();
	}

	public void setTabName(String tabName) {
		tabLabel.setText(tabName);
		
	}

	/**
	 * Function which returns the reference of the scrollPane viewed in the tab to which this tabComponent is added.
	 * @return
	 */
	public JScrollPane getContainingScroll() {
		return containingScroll;
	}

	public TabComponent (String name, JScrollPane sc) {
		
		if (close_icon == null) {
			Image close_image= ISSIcons.buttonIcons.get("close").getScaledInstance(12, 12, Image.SCALE_SMOOTH);
			close_icon= new ImageIcon (close_image);
		}
		tabLabel= new JLabel (name, JLabel.LEADING);
		add(tabLabel);
		JButton tab_close= new JButton (close_icon);
		tab_close.setPreferredSize(new Dimension(15,15));
		tab_close.setBorderPainted(false);
		tab_close.setContentAreaFilled(false);
		tab_close.addActionListener(new ImplementedActionListeners.AL_CloseTab());
		add(tab_close);
		setOpaque(false);
		containingScroll= sc;
	}
}
