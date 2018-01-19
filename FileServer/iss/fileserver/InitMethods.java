package iss.fileserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class InitMethods {
	static String BroadcaseAddress= getBroadcastAddress();
	
	
	public static void keepMeta_dataServerIp(String IPaddress){
		try{
			File f= new File(System.getProperty("user.dir") + File.separator + ".INFOFile");
			if(!f.exists()){
				f.createNewFile();
				FileOutputStream out= new FileOutputStream(f);
				ObjectOutputStream object_out= new ObjectOutputStream(out);
				object_out.writeObject(new String(IPaddress));
				object_out.flush();
				object_out.close();
			}
		}catch(IOException io){
			//Need to think.
		}
	}
	
	public static void keepStorePath(String path){
		File f= new File(System.getProperty("user.dir") + File.separator + ".Storepath");
		try {
			FileOutputStream out= new FileOutputStream(f);
			ObjectOutputStream write= new ObjectOutputStream(out);
			write.writeObject(path);
			write.flush();
			write.close();
		} catch (IOException e) {
			System.err.println("Error while instaling the Fileserver");
		}
		
	}
	
	
	public static void setStorepath(){
		File f= new File(System.getProperty("user.dir") + File.separator + ".Storepath");
		try{
			FileInputStream in= new FileInputStream(f);
			ObjectInputStream read= new ObjectInputStream(in);
			Network.FileStorepath= (String)read.readObject();
			read.close();
			in.close();
		}catch(IOException | ClassNotFoundException e){
			System.err.println("Unautherized changes are made.Contact admin");
		}
	}
	/**
	 * This returns the mac address.
	 * This is only during the installation.
	 * @return
	 */
	public static byte[] getMacAddress(){
		NetworkInterface Ip;
		try{
			 return NetworkInterface.getByName("eth0").getHardwareAddress();
		}catch(Exception e){
			System.out.println("mac address: " + e.getMessage());
		}
		return null;
	}
	
	public static String getMetaServerIpAddress(){
		try{
			File f= new File(System.getProperty("user.dir") + File.separator + ".INFOFile");
			if(f.exists()){
				FileInputStream in= new FileInputStream(f);
				ObjectInputStream ob_in= new ObjectInputStream(in);
				String ip= (String)ob_in.readObject();
				ob_in.close();
				in.close();
				return ip;
			}
		}catch(IOException | ClassNotFoundException io){
			//need to think.
		}
		return null;
	}
	
	
	public static String getBroadcastAddress(){
		NetworkInterface Ip;
		try{
			 return NetworkInterface.getByName("eth0").getInterfaceAddresses().get(1).getBroadcast().toString().split("/")[1];
		}catch(Exception e){
			//ignore.
		}
		return null;
	}
	/**
	 * This updates the Mirror info and also the DeleteListinfo.
	 */
	public static void UpdateStatus(){
		KeepData.ReadMirrorInfo();
		KeepData.WriteMirrorInfo();
		RunBackgroundThreads();
	}
	
	
	/**
	 * Runs the Background threads to resume the remaining copy and delete actions.
	 */
	public static void RunBackgroundThreads(){
		Runnable mirr= new Run_Background();
		Thread Mirr_thread= new Thread(mirr);
		Mirr_thread.start();
	}

}
