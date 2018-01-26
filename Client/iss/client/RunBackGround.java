package iss.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import iss.metadataserver.*;
import iss.client.Network.MakeList;
import iss.client.Network.MakegetFileList;
import iss.*;
public class RunBackGround implements Runnable{
		public void run() {
			while (true) {
					synchronized (Client.delete_list){
						for(int i= 0; (i < 10) && (!Client.delete_list.isEmpty());i++){
							try {
								Socket s= new Socket(Client.delete_list.get(0).get(0)[1],1234);
								ObjectOutputStream WriteRequest= new ObjectOutputStream(s.getOutputStream());
								RequestToFileServer fs_req= new RequestToFileServer(Client.delete_list.get(0),ISSTask.Delete);
								WriteRequest.writeObject(fs_req);
								WriteRequest.flush();
								WriteRequest.close();
								s.close();
								Client.delete_list.remove(Client.delete_list.get(0));
								KeepData.WriteDeleteData();
							}catch (ConnectException con){
								try {
									RemoteMethods.InvokeRemoteBoot(Client.ConnectMetaServer.getMacAdd(Client.delete_list.get(0).get(0)[1]), Network.BroadcastAddress);
								} catch (RemoteException | RuntimeException e) {
									//ignore..
								} 
							}catch (IOException e) {
								//Ignore....
							}
							
						}
					}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//ignore....
				}
			}
		}
}

class RunStoreThread implements Runnable{
	
	public void run() {
		while (true) {
				synchronized (Client.store_list){
					for(int i= 0; (i < 10) && (!Client.store_list.isEmpty());i++){
						try {
							Socket s= new Socket(Client.store_list.get(0).Info.get(0)[1],1234);
							ObjectOutputStream WriteRequest= new ObjectOutputStream(s.getOutputStream());
							RequestToFileServer fs_req= new RequestToFileServer(Client.store_list.get(0).Info,ISSTask.Receive);
							WriteRequest.writeObject(fs_req);
							WriteRequest.flush();
							Network.sendNow( s, Client.store_list.get(0).file, null);
							WriteRequest.close();
							Client.store_list.remove(Client.store_list.get(0));
						}catch (ConnectException con){
							try {
								RemoteMethods.InvokeRemoteBoot(Client.ConnectMetaServer.getMacAdd(Client.store_list.get(0).Info.get(0)[1]), Network.BroadcastAddress);
							} catch (RemoteException | RuntimeException e) {
								//ignore....
							} 
						}catch (IOException e) {
							//ignore...
						} catch (FileServerException | ClientApiException e) {
							//ignore...
						}
					}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				//ignore....
			}
		}
	}
}

class Run_getFileThread implements Runnable{
	
	public void run(){
			while (true) {
					synchronized (Client.getFile_list){
								for(int i= 0; (i < 10) && (!Client.getFile_list.isEmpty());i++){
									try{
										Socket s= new Socket(Client.getFile_list.get(0).getFileInfo.get(0)[1],1234);
										ObjectOutputStream WriteRequest= new ObjectOutputStream(s.getOutputStream());
										RequestToFileServer fs_req= new RequestToFileServer(Client.getFile_list.get(0).getFileInfo,ISSTask.Serve);
										WriteRequest.writeObject(fs_req);
										WriteRequest.flush();
										try {
											File f= new File(Client.getFile_list.get(0).file.getAbsolutePath());
											f.createNewFile();
											Network.receiveNow(s, f, WriteRequest);
										} catch (FileServerException | ClientApiException e) {
											//ignore.
										}
										Client.getFile_list.remove(Client.getFile_list.get(0));
									}catch (ConnectException con){
										try {
											RemoteMethods.InvokeRemoteBoot(Client.ConnectMetaServer.getMacAdd(Client.getFile_list.get(0).getFileInfo.get(0)[1]), Network.BroadcastAddress);
										} catch (RemoteException | RuntimeException e) {
											//ignore....
										} 
									}catch(IOException io){
										//ignore...
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

class ClearWaitingList implements Runnable{
	
	public void run(){
		while(true){
			for(int i= 0; (i < 10) && (!Client.waiting_list.isEmpty());i++){
				try {
					Client.ConnectMetaServer.deleteFromWaiting(Client.waiting_list.get(0).Info.get(0)[0]);
					Client.waiting_list.remove(0);
				} catch (RemoteException | RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				//ignore..
			}
		}
	}
}

