package iss.fileserver;
import iss.metadataserver.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import iss.*;
public class Run_Background implements Runnable {
	public void run() {
		while (true) {
			synchronized (FileServermain.Mirr_list){
				for(int i= 0; (i < 10) && (!FileServermain.Mirr_list.isEmpty());i++){
					try {
						File f= new File(Fileserver.FileStorepath+FileServermain.Mirr_list.get(0)[0]); //If file exits then only send.
						if(f.exists()){
							ISSTask action= null;
							Socket s= new Socket(FileServermain.Mirr_list.get(0)[1],1234);
							ObjectOutputStream object= new ObjectOutputStream(s.getOutputStream()); //This send ack to Mirr_Fileserver.
							if(FileServermain.Mirr_list.get(0)[2].equals("1"))
								action= ISSTask.Receive;
							else if(FileServermain.Mirr_list.get(0)[2].equals("2"))
								action= ISSTask.Update;
							object.writeObject(new RecognizeObject(FileServermain.Mirr_list.get(0)[0],action,null));
							object.flush();
							if((action == ISSTask.Receive) || (action == ISSTask.Update)){
								if(f.exists())
									Network.sendingfile(s, f);
							}
							object.close();
							FileServermain.Mirr_list.remove(0);
							KeepData.WriteMirrorInfo();
						}
					}catch (ConnectException con){
					}catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Run_BackGroundToDelete implements Runnable{

	public void run() {
		while (true) {
			synchronized (FileServermain.delete_List){
				for(int i= 0;(i < 10) && (!FileServermain.delete_List.isEmpty());i++){
					try {
						Socket client= new Socket(FileServermain.delete_List.get(0).get(0)[1],1234);
						ObjectOutputStream out= new ObjectOutputStream(client.getOutputStream());
						out.writeObject(new RecognizeObject(null,ISSTask.Delete,FileServermain.delete_List.get(0)));  //deletelist.get(i)[0] fileId,[1] is Ipaddress.
						out.flush();
						out.close();
						client.close();
						FileServermain.delete_List.remove(0);
						KeepData.WriteDeleteInfo();
					}catch (IOException e) {
						//Ignore....
					}
				}
			}
			try {
				Thread.sleep(10000); 
			} catch (InterruptedException e) {
				//ignore...
			}
		}
	}
}
