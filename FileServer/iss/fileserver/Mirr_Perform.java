package iss.fileserver;

import iss.metadataserver.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.RemoteException;
import iss.*;
public class Mirr_Perform {
	
	/**
	 * This method is for making connection to the Mirroring Fileserver.
	 * And transferring file there.
	 * This Mirring Fileserver Ipaddress is decided and given by the Meta-data server.
	 * @author P. Murali krishna
	 */
	public static void Mirr_Copy(String[] file,ISSTask action){
		for(int i= 1; i < file.length;i++){
			try {
				if(!file[i].equals(Network.Ipaddress)){
					Socket s= new Socket(file[i],1234);
					ObjectOutputStream object= new ObjectOutputStream(s.getOutputStream()); //This send ack to Mirr_Fileserver.
					object.writeObject(new RecognizeObject(file[0],action,null));
					object.flush();
					Network.sendingfile(s, new File(Network.FileStorepath+file[0]));
					object.close();
				}
			} catch (ConnectException e) {
				String[] list= new String[3];
				list[0]= file[0];
				list[1]= file[i];
				if(action == ISSTask.Receive)
					list[2]= "1";
				else if(action == ISSTask.Update)
					list[2]= "2";
				FileServermain.Mirr_list.add(list);
				KeepData.WriteMirrorInfo();
				
			} catch (IOException io){
				//Ignore.
			}
		}
	}
}	
