package iss.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;
/**
 * Class that initializes the toolbar with necessary components for the ISSGUIFrame
 * @author Basanta Sharma
 *
 */
public class ISSGUIToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;

	// references to the back and forward button on the frame toolbar;
	private JButton back, forward;

	//the singleton toolBar instance 
	private static ISSGUIToolBar toolBar;

	/**
	 * States whether the back button is enabled or not.
	 * @return true if back button is enabled, false otherwise
	 */
	public boolean isBackEnabled () {
		return back.isEnabled();
	}

	/**
	 * Sets the enabled state of the back button to the given boolean value
	 * @param enable the boolean value for the back button
	 */
	public void setBackEnabled (boolean enable) {
		back.setEnabled(enable);
	}

	/**
	 * States whether the forward button is enabled or not.
	 * @return true if forward button is enabled, false otherwise
	 */
	public boolean isForwardEnabled () {
		return forward.isEnabled();
	}

	/**
	 * Sets the enabled state of the forward button to the given boolean value
	 * @param enable the boolean value for the forward button
	 */
	public void setForwardEnabled (boolean enable) {
		forward.setEnabled(enable);
	}

	/**
	 * Private constructor which is called only once
	 */
	private ISSGUIToolBar () {
		super();
		toolBar= this;
		setLayout(new BorderLayout());
		buildToolBar();
	}

	/**
	 * Function which returns the toolbar reference
	 * @return
	 */
	public static ISSGUIToolBar getToolBar () {
		if (toolBar == null)
			toolBar= new ISSGUIToolBar();
		return toolBar;
	}

	private JPanel makeSearchPane() {
		JPanel searchContainer= new JPanel();
		JTextField searchPane= new JTextField();
		//searchPane.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED));
		searchPane.setPreferredSize(new Dimension(200,30));
		searchPane.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				JTextField pane= (JTextField)e.getSource();
				if (pane.getText().equals("")) {
					pane.setText("Search ISS");
				}
				pane.setEnabled(false);
			}
			@Override
			public void focusGained(FocusEvent e) {
				JTextField pane= (JTextField)e.getSource();
				pane.setEnabled(true);
				if (pane.getText().equals("Search ISS")) {
					pane.setText("");
				}
			}
		});
		searchPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTextField pane= (JTextField)e.getSource();
				pane.requestFocus();
			}
		});
		searchPane.setFocusCycleRoot(false);
		searchPane.setDisabledTextColor(Color.DARK_GRAY);
		searchContainer.add(searchPane);
		ImageIcon findIcon= new ImageIcon("Resources" + File.separator + "buttonicons" + File.separator + "find");
		findIcon= new ImageIcon(findIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		JButton searchBut= new JButton (findIcon);
		searchBut.addActionListener(new ImplementedActionListeners.AL_SearchISS());
		searchBut.setFocusable(false);
		searchBut.setBorderPainted(false);
		searchContainer.add(searchBut);
		return searchContainer;
	}

	/**
	 * Function which builds the ToolBar
	 */
	private void buildToolBar () {

		Image image= ISSIcons.buttonIcons.get("back").getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		toolBar.back= new JButton (new ImageIcon (image));
		image= ISSIcons.buttonIcons.get("forward").getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		toolBar.forward= new JButton (new ImageIcon (image));
		toolBar.back.setBorderPainted(false);
		Border emptyBorder= BorderFactory.createEmptyBorder();
		toolBar.back.setBorder(emptyBorder);
		toolBar.back.setEnabled(false);
		toolBar.back.setFocusable(false);
		toolBar.back.addActionListener(new ImplementedActionListeners.AL_Backward());
		toolBar.forward.setBorderPainted(false);
		toolBar.forward.setBorder(emptyBorder);
		toolBar.forward.setEnabled(false);
		toolBar.forward.addActionListener(new ImplementedActionListeners.AL_Forward());
		toolBar.forward.setFocusable(false);

		JToolBar navigatorTB= new JToolBar();
		navigatorTB.add(toolBar.back);
		navigatorTB.add(toolBar.forward);
		navigatorTB.setOpaque(false);
		navigatorTB.setFloatable(false);

		toolBar.add(navigatorTB,BorderLayout.WEST);
		toolBar.add(makeSearchPane(),BorderLayout.EAST);
		toolBar.setOpaque(false);
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.revalidate();
	}
}
