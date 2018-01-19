package iss.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import iss.client.Network.MakeList;

public class KeepData {
	
	KeepData(){}
	
	/**
	 * Writes Remaining delete list in to the .DeleteInfo file.
	 * @author P . Murali krishna
	 */
	public synchronized static void WriteDeleteData(){
		try{
			ObjectOutputStream del_ob_out= new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + ".DeleteInfo"));
			del_ob_out.writeObject(Client.delete_list);
			del_ob_out.flush();
		}catch(IOException io){
			//Ignore...
		}
	}


	/**
	 * Reads the remaining delete list to be cleared from the .DeleteInfo file
	 */
	@SuppressWarnings("unchecked")
	public static void ReadDeleteData(){
		try {
			File f= new File(System.getProperty("user.dir") + File.separator + ".DeleteInfo");
			if(f.exists()){
				ObjectInputStream del_ob_in= new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + File.separator + ".DeleteInfo"));
				ArrayList<ArrayList<String[]>> list=  (ArrayList<ArrayList<String[]>>)del_ob_in.readObject();
				Client.delete_list= list;
			}else
				WriteDeleteData();
			
		} catch (ClassNotFoundException | IOException e) {
			//Ignore
		}
	}
	
	/**
	 * Writes Remaining delete list in to the .DeleteInfo file.
	 * @author P . Murali krishna
	 */
	public synchronized static void WriteStoreData(){
		try{
			ObjectOutputStream del_ob_out= new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + ".StoreInfo"));
			del_ob_out.writeObject(Client.store_list);
			del_ob_out.flush();
		}catch(IOException io){
			//Ignore
		}
	}


	/**
	 * Reads the remaining delete list to be cleared from the .DeleteInfo file
	 */
	@SuppressWarnings("unchecked")
	public static void ReadStoreData(){
		try {
			File f= new File(System.getProperty("user.dir") + File.separator + ".StoreInfo");
			if(f.exists()){
				ObjectInputStream del_ob_in= new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + File.separator + ".StoreInfo"));
				ArrayList<MakeList> list=  (ArrayList<MakeList>)del_ob_in.readObject();
				Client.waiting_list= list;
			}else
				WriteStoreData();
			
		} catch (ClassNotFoundException | IOException e) {
			ClientExceptions.defaultException1(e.getMessage());
		}
	}
	
}
