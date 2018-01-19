package iss.fileserver;
import iss.metadataserver.*;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class FileServerStart {
	public static ISSApi ConnectToMetaserver= null;
	/**
	 *This method connects to meta-data server by taking the IPaddress of it.
	 * @param IpAddress
	 */
	public void connect(String IPAddress){
		try {
			System.out.println("Ip address: " + IPAddress);
			ConnectToMetaserver= (ISSApi)Naming.lookup("rmi://" + IPAddress + "/ISSSer");
		} catch (MalformedURLException | RemoteException | NotBoundException | RuntimeException e) {
			JOptionPane.showMessageDialog(null,e.getMessage(),"Error While connecting to Central Server", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	public static void main(String[] args){
		Scanner command= null;
		System.out.println("******FileServer Instalation Process******");
		System.out.print("Enter the META-DATA server IPaddress# ");
		command= new Scanner(System.in);
		String Ipaddress=command.nextLine();
		InitMethods.keepMeta_dataServerIp(Ipaddress);
		System.out.println("Meta-data server Ipaddress given by the user: " + InitMethods.getMetaServerIpAddress());
		new FileServerStart().connect(InitMethods.getMetaServerIpAddress());
		System.out.print("Enter the storage path# ");
		command= new Scanner(System.in);
		String path=command.nextLine();
		File f= new File(path);
		if(!f.exists())
			f.mkdir();
		InitMethods.keepStorePath(f.getAbsolutePath()+File.separator);
		System.out.println("Enter the amount of storage# ");
		command= new Scanner(System.in);
		long size= command.nextLong();
		try {
			ConnectToMetaserver.addHost(Network.Ipaddress, InitMethods.getMacAddress(), size);
			System.out.println("Instalation Process is succesfully completed");
		} catch (RemoteException | RuntimeException e) {
			System.err.println("Error while adding the node: " + e.getMessage());
		}
	}
	
}
