package iss.gui;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ISSGUInterface {
	static ISSGUIFrame frame;

	public static void main (String args[]) {
		EventQueue.invokeLater(new Runnable () {
			@SuppressWarnings("resource")
			private String takeIpInput (String message) {
				String ip= null;
				do {
					ip= JOptionPane.showInputDialog(message);
				} while (ip == null || ip.equals(""));
				File f= new File(ISSState.projectPath + ".metaIP");
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e1) {}
				}
				FileOutputStream stream= null;
				try {
					stream= new FileOutputStream(f);
				} catch (FileNotFoundException e) {}
				try {
					stream.write(ip.getBytes());
				} catch (IOException e) {}
				return ip;
			}
			
			private String readIpFromFile () {
				File f= new File(ISSState.projectPath + ".metaIP");
				if (f.isFile()) {
					BufferedReader r= null;
					try {
						r= new BufferedReader(new FileReader (f));
					} catch (FileNotFoundException e) {}
					try {
						return r.readLine();
					} catch (IOException e) {}
				}
				return null;
			}
			
			private String getServerIpAddress () {
				String ip= readIpFromFile();
				if (ip == null) {
					ip= takeIpInput("Enter the IP address of the Metadata Server");
				}
				return ip;
			}

			private void startup () {
				//Connect to the back-end
				try {
					BackEnd.connect(getServerIpAddress());
				} catch (MalformedURLException | RemoteException
						| NotBoundException e) {
					String ip= takeIpInput("Could not connect to Server. Please enter server IP address:");
					try {
						BackEnd.connect(ip);
					} catch (MalformedURLException | RemoteException
							| NotBoundException e1) {
						JOptionPane.showMessageDialog(null, "Could not connect to server. Please make sure that the server is running. Clik to exit");
						System.exit(1);
					}
					return;
				}
				//Load icons
				ISSIcons.loadIcons();
				//Set environment
				ISSState.init();
			}
			@Override
			public void run() {
				startup();
				//Make the frame and show
				frame= new ISSGUIFrame ();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}