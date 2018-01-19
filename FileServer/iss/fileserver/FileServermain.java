package iss.fileserver;
import iss.metadataserver.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class FileServermain {
	static ArrayList<String[]> Mirr_list= new ArrayList<String[]>();
	static ArrayList<ArrayList<String[]>> delete_List= new ArrayList<ArrayList<String[]>>();
	public static ISSApi ConnectToMetaserver= null;
	
	/**
	 *This method connects to meta-data server by taking the IPaddress of it.
	 * @param IpAddress
	 */
	public void connect(String IPAddress){
		try {
			ConnectToMetaserver= (ISSApi)Naming.lookup("rmi://" + IPAddress + "/ISSSer");
		} catch (MalformedURLException | RemoteException | NotBoundException | RuntimeException e) {
			JOptionPane.showMessageDialog(null,e.getMessage(),"Error While connecting to Central Server", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	public static void main(String[] args){
		try{
			new FileServermain().connect(InitMethods.getMetaServerIpAddress());
			InitMethods.setStorepath();
			InitMethods.UpdateStatus();
			new NormalLayer().runFileServer();
		}catch(Exception e){
			System.out.println("Reason: " + e.getMessage());
		}
	}
}
