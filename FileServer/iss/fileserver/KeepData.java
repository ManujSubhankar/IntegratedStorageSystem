package iss.fileserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class KeepData {

	KeepData(){}



	public synchronized static void WriteMirrorInfo(){
		try {
			ObjectOutputStream mirr_ob_out= new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + ".MirrorInfo"));
			mirr_ob_out.writeObject(FileServermain.Mirr_list);
			mirr_ob_out.flush();
		} catch (IOException e) {
			//Ignore.
		}
	}


	@SuppressWarnings("unchecked")
	public static void ReadMirrorInfo(){
		try {
			File f= new File(System.getProperty("user.dir") + File.separator + ".DeleteInfo");
			if(f.exists()){
				ObjectInputStream del_ob_in= new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + File.separator + ".MirrorInfo"));
				ArrayList<String[]> list=  (ArrayList<String[]>)del_ob_in.readObject();
				FileServermain.Mirr_list= list;
			}else
				WriteMirrorInfo();
			
		} catch (ClassNotFoundException | IOException e) {
			//Ignore.
		}
	}


	public synchronized static void WriteDeleteInfo(){
		try{
			ObjectOutputStream del_ob_out= new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + ".DeleteInfo"));
			del_ob_out.writeObject(FileServermain.delete_List);
			del_ob_out.flush();
		}catch(IOException io){
			//Need to do.
		}
	}


	@SuppressWarnings("unchecked")
	public static void ReadDeleteInfo(){
		try {
			File f= new File(System.getProperty("user.dir") + File.separator + ".DeleteInfo");
			if(f.exists()){
				ObjectInputStream del_ob_in= new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + File.separator + ".DeleteInfo"));
				ArrayList<ArrayList<String[]>> list=  (ArrayList<ArrayList<String[]>>)del_ob_in.readObject();
				FileServermain.delete_List= list;
			}else
				WriteDeleteInfo();
			
		} catch (ClassNotFoundException | IOException e) {
			//Ignore.
		}


	}
}
