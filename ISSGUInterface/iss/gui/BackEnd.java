package iss.gui;

import iss.client.Client;
import iss.client.ClientApiException;
import iss.*;
import iss.client.Status;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.Timer;

import iss.gui.ImplementedActionListeners.AL_Timer;

public class BackEnd {
	
	// Connection to the back end...
	static Client conn= null;
	
	/**
	 * Method that establishes the connection with the back end
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public static void connect (String ip) throws MalformedURLException, RemoteException, NotBoundException {
		conn= new Client();
		conn.getConnected(ip);
		Client.setTempFilePath(ISSState.projectPath + File.separator + ".ISStemp" + File.separator);
		Client.updateDeletelistStatus();
	}
	
	public static ArrayList<ISSFile> getContents (String path) throws RemoteException, ClientApiException, RuntimeException {
		return conn.getContent(path);
	}
	
	static void copyISStoISS(final String cloudSource, final ArrayList<ISSFile> files, final String cloudDes) {
		new Thread ("callingCopyFile") {
			public void run () {
				AL_Timer timerAL= new ImplementedActionListeners.AL_Timer();
				Timer t= new Timer (1000, timerAL);
				Status st= null;
				try {
					st= conn.storeISStoISS(cloudSource, files, cloudDes);
				} catch (RuntimeException e1) {
					System.out.println ("Copy from cloud to cloud error");
					e1.printStackTrace();
				}
				ISSState.refreshPanelInView();
				t.start();
				
				String message= "Copying items from Cloud: " + cloudSource + " to Cloud: " + cloudDes;
				ISSProgressBar.show(st, "Copy", message);
				
				timerAL.setFinished(true);
				ISSState.refreshPanelInView();
				
			}
		}.start();
	}
	
	static void downloadFromISS (String parentDir, ArrayList<ISSFile> files, String localDestination) {
		new Thread ("callingCopyToLocal") {
			public void run () {
				Status st = null;
				st = conn.getFromISSToLocal (parentDir,files,localDestination);
				String message= "Downloading files from ISS: " + parentDir + " to " + localDestination;
				ISSProgressBar.show(st, "Download", message);
			}
		}.start();
	}

	static void copyISSToLocal(final ArrayList<ISSFile> files) {
		new Thread ("callingCopyToLocal") {
			public void run () {
				ISSFile fpath= files.get(0);
				String parentDir= fpath.getName();
				files.remove(0);
				Status th = null;
				th = conn.getFromISSToLocal (parentDir,files,ISSState.projectPath + ".ISSClipboard");
				try {
					th.getThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				ArrayList<File> nativefiles= new ArrayList<File> ();
				for (ISSFile f: files) {
					nativefiles.add(new File(ISSState.projectPath + ".ISSClipboard/" + f.name));
				}
				files.add(0,fpath);
				FileTransferable tfiles= new FileTransferable (nativefiles, files);
				ISSState.sysClipboard.setContents(tfiles, null);

				ISSState.isClipboardValid= false;
				
			}
		}.start();
	}

	static void createFolderInISS(final String cloudPath, final String folderName){
		new Thread ("callingCreateFolder") {
			public void run () {
				Thread th= null;
				th= conn.createFolder(cloudPath, folderName);
				try {
					th.join();
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				ISSState.refreshPanelInView();
				Component[] coms= ISSState.panelInView.getComponents();
				for (Component com: coms) {
					IconButton but= (IconButton) com;
					if (but.getRepresentingFile().name.equals(folderName)) {
						but.setForRename();
						break;
					}
				}
				
			}
		}.start();
	}

	static void deleteFileInISS(final String cloudSource, final ArrayList<ISSFile> files) {
		new Thread ("callingDeleteFile") {
			public void run () {
				AL_Timer timerAL= new ImplementedActionListeners.AL_Timer();
				Timer t= new Timer (100,timerAL);
				Status th= null;
				th= conn.deleteFromISS(cloudSource, files);
				ISSState.refreshPanelInView();
				t.start();
				try {
					th.getThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				timerAL.setFinished(true);
			}
		}.start();
	}

	static void moveFileInISS(final String cloudSource, final ArrayList<ISSFile> files, final String cloudDes) {

		new Thread ("callingMoveFile") {
			public void run () {
				AL_Timer timerAL= new ImplementedActionListeners.AL_Timer();
				Timer t= new Timer (1000, timerAL);
				Thread st= null;
				try {
					st= conn.moveFile(cloudSource, files, cloudDes);
				} catch (RuntimeException | ClientApiException e1) {
					System.out.println ("move from cloud to cloud error");
					e1.printStackTrace();
				}
				ISSState.refreshPanelInView();
				t.start();
				
				try {
					st.join();
				} catch (InterruptedException e) {
					//Ignore
				}
				//String message= "Moving files from ISS: " + cloudSource + " to ISS: " + cloudDes;
				//ISSProgressBar.show(st,"Move",message);
				
				timerAL.setFinished(true);
				ISSState.refreshPanelInView();
			}
		}.start();
	}

	static void openFileFromISS(final String cloudParent, final ISSFile file) {

		new Thread ("callingOpenFile") {
			public void run () {
				File toOpen= new File(ISSState.projectPath + ".ISStemp/" + file.fileID + "_" + file.name);
				//if (!toOpen.exists()) {
				Thread th = null;
				th = conn.openFile(file,cloudParent);
				try {
					th.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				//}
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(toOpen);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else System.out.println ("Desktop Api is not supported in this system");
			}
		}.start();
	}

	static void renameISSFile(final String oldPath, final String newName) {

		new Thread ("callingRenameFile") {
			public void run () {
				try {
					conn.renameFile(oldPath, newName);
				} catch (RemoteException | RuntimeException e) {
				}
				ISSState.refreshPanelInView();
			}
		}.start();
	}

	static void storeFilesInISS(final ArrayList<File> pfiles, final String cloudDestination) {

		new Thread ("callingStoreFiles") {
			public void run () {
				AL_Timer timerAL= new ImplementedActionListeners.AL_Timer();
				Timer t= new Timer (1000, timerAL);
				Status th= null;
				try {
					th= conn.storeFromLocalToISS(pfiles, cloudDestination);
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				}
				t.start();
				
				String localSource= pfiles.get(0).getParent();
				String message= "Copying item(s) from " + localSource + " to ISS directory:" + cloudDestination;
				ISSProgressBar.show(th,"Upload",message);
				
				timerAL.setFinished(true);
				ISSState.refreshPanelInView();
				
			}
		}.start();
	}
	
	static void createFileInISS(ArrayList<File> files) {
		Status th;
		try {
			th= conn.storeFromLocalToISS(files, ISSState.panelInView.getWorkingDirectory());
		} catch (RuntimeException e1) {
			System.out.println ("Cannot make new file in cloud");
			return;
		}
		try {
			th.getThread().join();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} finally {
			files.get(0).delete();
		}
	}

	static boolean isExistingInISS(String path) {
		try {
			conn.getISSFile(path);
		} catch (RemoteException | RuntimeException e) {
			return false;
		}
		return true;
	}

	static Thread openISSFile(ISSFile file, String parentDir) {
		return conn.openFile(file,parentDir);
	}
	
	static ArrayList<ISSFile> searchISS (String searchDir, String searchStr) {
		try {
			return conn.searchFile(searchDir, searchStr);
		} catch (RemoteException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

class ISSProgressBar {
	private JProgressBar bar;
	private JOptionPane pane;
	private JDialog dialog;
	private JTextPane messagePanel;
	private boolean shown= false;
	
	private ISSProgressBar () {
		bar= new JProgressBar();
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(500,20));
		
		messagePanel= new JTextPane();
		messagePanel.setEditable(false);
		messagePanel.setFocusable(false);
		messagePanel.setForeground(Color.blue);
		messagePanel.setBackground(null);
		
		JPanel progressPanel= new JPanel();
		progressPanel.setLayout(new BorderLayout());
		JPanel messageContainer= new JPanel();
		messageContainer.add(messagePanel);
		progressPanel.add(messageContainer, BorderLayout.NORTH);
		progressPanel.add(new JPanel(), BorderLayout.WEST);
		progressPanel.add(new JPanel(), BorderLayout.EAST);
		progressPanel.add(new JPanel(), BorderLayout.SOUTH);
		progressPanel.add(bar);
		
		pane= new JOptionPane(progressPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.CANCEL_OPTION);
	}
	
	@SuppressWarnings("deprecation")
	static void show (Status st, String title, String message) {
		ISSProgressBar instance= new ISSProgressBar();
		instance.bar.setIndeterminate(true);
		Thread dialogThread= new Thread("dialogThread") {
			public void run() {
				instance.dialog.setVisible(true);
				//instance.shown= true;
				instance.dialog.setVisible(false);
			}
		};
		while(!st.getTransferAck());
		instance.bar.setIndeterminate(false);
		instance.bar.setMaximum((int) (st.Totalsize / 1024));
		instance.messagePanel.setText(message);
		instance.dialog= instance.pane.createDialog(title);
		instance.dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialogThread.start();
		long usedsiz= st.Usedsize;
		while(!instance.shown || instance.dialog.isVisible()) {
			if (st.Usedsize > usedsiz) {
				usedsiz= st.Usedsize;
				instance.bar.setValue((int)(usedsiz / 1024));
			}
			if (st.Totalsize == st.Usedsize)
				instance.dialog.setVisible(false);
		}
		//If cancel was pressed
		if(instance.pane.getValue() instanceof Integer) {
			int option= (int)instance.pane.getValue();
			if (option == JOptionPane.CANCEL_OPTION) {
				st.getThread().stop();
			}
		}
	}
	
}

