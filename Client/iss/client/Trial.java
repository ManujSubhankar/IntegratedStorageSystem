package iss.client;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Trial {
	public static void main (String[] args) {
		EventQueue.invokeLater(new Runnable() {
			/**
			 * 
			 */
			public int showErrorDialog (String message) {
				JOptionPane pane= new JOptionPane(message);
				pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
				pane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
				JPanel buttonPane= (JPanel) pane.getComponent(1);
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
			public int showBackgroundOption (String message) {
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
			@Override
			public void run() {
				int choice= showErrorDialog("Message");
				switch (choice) {
				case 0:
					System.out.println ("User closed the dialog using the close button");
					break;
				case 1:
					System.out.println ("Here1");
					break;
				case 2:
					System.out.println ("Here2");
					break;
				default:
						System.out.println ("Here in default");
						break;
				}
			}
		});
	}
}
