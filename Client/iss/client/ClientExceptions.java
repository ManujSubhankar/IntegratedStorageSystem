package iss.client;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClientExceptions {
	/**
	 * 
	 */
	public static int showErrorDialog (String message) {
		JOptionPane pane= new JOptionPane(message);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
		JPanel buttonPane= null;
		Component[] comps= pane.getComponents();
		for (Component comp : comps) {
			if (comp.getClass().toString().contains("javax.swing.JPanel"))
				buttonPane= (JPanel) comp;
		}
		JButton but1= (JButton)(buttonPane.getComponent(0));
		but1.setText("Skip");
		JButton but2= (JButton)(buttonPane.getComponent(1));
		but2.setText("Skip All");
		JDialog dlg= pane.createDialog("Error Message");
		dlg.setVisible(true);
		Object val= pane.getValue();
		int choice= 0;
		try {
			choice= (int) val;
		} catch (NullPointerException e){
			return 0;
		}
		if (choice == JOptionPane.YES_OPTION) return 1;
		if (choice == JOptionPane.NO_OPTION) return 2;
		return 0;
	}

	/**
	 * 
	 */
	public static int showBackgroundOption (String message) {
		JOptionPane pane= new JOptionPane(message);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptionType(JOptionPane.YES_NO_OPTION);
		JDialog dlg= pane.createDialog("Run In Background");
		dlg.setVisible(true);
		Object val= pane.getValue();
		int choice= 0;
		try {
			choice= (int) val;
		} catch (NullPointerException e){
			return 0;
		}
		if (choice == JOptionPane.YES_OPTION) return 1;
		if (choice == JOptionPane.NO_OPTION) return 2;
		return 0;
	}
	
	public static UserInteraction WaitingListToUserConfirm(){
		int userChoice= showBackgroundOption ("This process may take some time destination system is not running.\n"
				+ "Do you want to run task background.");
		if(userChoice == 0)
			Thread.currentThread().stop();
		else if(userChoice == 1)
			return UserInteraction.RunBackGround;
		else if(userChoice == 2)
			Thread.currentThread().stop();
		return null;
	}
	
	public static boolean defaultException(String Msg,Status st){
		int userChoice= showErrorDialog(Msg);
		if(userChoice == 0){
			st.Usedsize= st.Totalsize;
			Thread.currentThread().stop();
		}
		else if(userChoice == 1)
			return false;
		else if(userChoice == 2)
			return true;
		return true;
	}
	
	public static boolean defaultException1(String Msg){
		int userChoice= showErrorDialog(Msg);
		if(userChoice == 0){
			Thread.currentThread().stop();
		}
		else if(userChoice == 1)
			return false;
		else if(userChoice == 2)
			return true;
		return true;
	}
}
