package iss.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import iss.client.Network.MakeList;
import iss.client.Network.MakegetFileList;
import iss.client.Network.MyBoolean;
import iss.metadataserver.*;
import iss.*;

/**
 * Some methods in this class returns reference to the running thread.
 * So the programmer using this method wants to wait on running thread can wait till
 * thread exits.
 * Or he can continue if he don't want to wait.
 * @return
 */

public class Client implements Client_GateWay{
	public static ISSApi ConnectMetaServer= null;
	public static String tempFilePath= null;
	public static ArrayList<ArrayList<String[]>> delete_list= new ArrayList<ArrayList<String[]>>();
	public static ArrayList<MakeList> store_list= new ArrayList<MakeList>(); 
	public static ArrayList<MakegetFileList> getFile_list= new ArrayList<MakegetFileList>();
	static ArrayList<UpdateList> update_List= new ArrayList<UpdateList>();
	static ArrayList<MakeList> waiting_list= new ArrayList<MakeList>();
	public Client(){}
	
	/**
	 * This method is for connecting to the meta-data server.
	 * It takes Ipaddress of the meta-data server.
	 * @param IPAddress
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void getConnected(String IPAddress) throws MalformedURLException, RemoteException, NotBoundException{
			ConnectMetaServer= (ISSApi)Naming.lookup("rmi://" + IPAddress + "/ISSSer");
			updateFile();
	}
	
	/**
	 * For getting the tempFilePath to open the file.
	 * @return
	 */
	public static String getTempFilePath() {
		return tempFilePath;
	}

	/**
	 * For setting the Temp File Path for storing the to open.
	 * @param tempFilePath
	 */
	public static void setTempFilePath(String tempFilePath) {
		Client.tempFilePath = tempFilePath;
	}

	/**
	 * Reads the remaining delete list from the deletefile.
	 * And, Creates thread and runs the thread to clear the remaining list.
	 * This has the functionality to RunBackGround Threads to store, receive(or)getFile, delete from actual storage.
	 * @author P. Murali krishna
	 */
	public static void updateDeletelistStatus(){
		KeepData.ReadDeleteData();
		KeepData.WriteDeleteData();
		KeepData.ReadStoreData();
		KeepData.WriteStoreData();
		Runnable store= new RunStoreThread();
		Thread storethread= new Thread(store);
		storethread.start();
		Runnable run= new RunBackGround();
		Thread thread= new Thread(run);
		thread.start();
		Runnable getFile= new Run_getFileThread();
		Thread getFileThread= new Thread(getFile);
		getFileThread.start();
		Runnable clearlist= new ClearWaitingList();
		Thread clear= new Thread(clearlist);
		clear.start();
	}
	
	/**
	 * This method return the ISSFile object which consists of files information.
	 * It takes the path as an argument and returns the list of file under the path if it is a directory.
	 * If the path refering to the file the it returns the file object.
	 * @throws RemoteException and {@link RuntimeException} Internally calls the ClientException class method to display message.
	 * @throws ClientApiException  (If Client is not connected to Meta-data server.
	 * @throws RuntimeException @author P. Murali krishna
	 */
	public ArrayList<ISSFile> getContent(String ISSPath) throws ClientApiException, RemoteException, RuntimeException {
			if(ConnectMetaServer == null)
				throw new ClientApiException("Client is not connected to Meta-data Server");
			else{
				return ConnectMetaServer.getContent(FilesMethods.validatepath(ISSPath));
			}
	}
	
	
	/**
	 * Stores the given File to Integrated Storage System.<br>
	 * This has the functionality even to store the Folder.<br>
	 * Internally Creates the thread to store all the user requested Files or Folders.<br>
	 * <h1>Returns the Status object which has the following functionalities:- </h1><li>
	 * Reference to running thread.<li>
	 * Reference to the size of the total data which is storing the size.<li>
	 * Reference to the copied files size.<br>
	 * <h2>Internally Error information is given by calling ClientException class methods.</h2>
	 * @author P. Murali krishna
	 */
	public Status storeFromLocalToISS(final ArrayList<File> localFileObject,final String desISSPath) {
		final Status status= new Status();
		for(File f: localFileObject)
			status.Totalsize+= FilesMethods.getsize(f);
		
		status.setTransferAck(true); //This is set to after calculating the size of the files which are ready to transffer.
		
		status.thread= new Thread("Store File"){
				public void run(){
					Confirmation userconfirmation= new Confirmation(UserInteraction.skip);
					MyBoolean userConfirmToSkip= new MyBoolean(false);
					for(int i= 0;i < localFileObject.size();i++)
						try{
							FilesMethods.SystemFiles(localFileObject.get(i), FilesMethods.validatepath(desISSPath),userConfirmToSkip,status,userconfirmation);
						}catch(ClientApiException e){
							boolean decision= ClientExceptions.defaultException(e.getMessage(),status);
							if(decision)
								 Thread.currentThread().stop();
							else
								Thread.currentThread().stop();
						}
				}
		};
		status.thread.start();  //start thread to store files.
		return status;
	}
	
	/**
	 * Stores the File from the local system To Integrated Storage System.<br>
	 * It takes the InputStream object and read data from it and creates a file by the name and writes to the file.<br>
	 * It creates thread store the File to the ISS so the current activity can go on.<br>
	 * @param InputStream (The File data will be read)
	 * @param desISSPath (Where the file need to be store in ISS)
	 * @param name (File name)
	 * @param size (Size of the File)
	 * @return Thread 
	 */
	public Thread storeFromLocalToISSWeb(final InputStream input,final String desISSPath,final String name,final long size){
		Thread thread= new Thread("Upload thread"){
			public void run(){
				try {
					String[] Fileinfo= Client.ConnectMetaServer.storeFile(FilesMethods.validatepath(desISSPath), name, size);
					Network.sendFromBrowser(input, Fileinfo);
				} catch (RemoteException | FileServerException | ClientApiException | ClassNotFoundException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		};
		thread.start();
		return thread;
	}
	
	/**
	 * This is for the copying the file or Directory from the cloud to Local system.
	 * @param parentCloudPath
	 * @param ISSfileobject
	 * @param desLocalPath
	 * @return Thread
	 */
	public Status getFromISSToLocal(final String parentISSPath,final ArrayList<ISSFile> ISSfileobject, final String desLocalPath) {
		final Status status= new Status();
		
		for(int i= 0; i < ISSfileobject.size();i++)
			status.Totalsize+= ISSfileobject.get(i).size;
		
		status.setTransferAck(true);
		
		status.thread= new Thread("Get to local system"){
			public void run(){
				Confirmation confirm= new Confirmation(UserInteraction.skip);
				MyBoolean userChoiceToSkip= new MyBoolean(false);
				for(int i= 0;i < ISSfileobject.size();i++){
					try{
						FilesMethods.cloudFiles(FilesMethods.validatepath(parentISSPath) + "/" + ISSfileobject.get(i).name,ISSfileobject.get(i), FilesMethods.validatepath(desLocalPath),userChoiceToSkip,status,confirm);
					}catch(ClientApiException e){
						ClientExceptions.defaultException("Error in getting the files from ISS: " + e.getMessage(),status);
					}
				}
			}
		};
		status.thread.start();
		return status;
	}
	
	/**
	 * @param output
	 * @param cloudPath
	 * @return
	 * @throws ClientApiException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileServerException 
	 */
	public Socket getISSToLocalWeb(final ISSFile ISSFileObject)throws RemoteException, RuntimeException, IOException,
	ClientApiException, ClassNotFoundException, FileServerException{
			return Network.Download(ISSFileObject); //This is to write data to output stream of the socket.
	}

	
	/**
	 * This method is for copying files or Directory from cloud to cloud.
	 * @param srcParentPath
	 * @param ISSfileobject
	 * @param desCloudpath
	 * @return Thread.
	 */
	public Status storeISStoISS(final String srcParentISSPath,final ArrayList<ISSFile> ISSfileobject, final String desISSPath) {
		final Status status= new Status();

		for(int i= 0;i < ISSfileobject.size();i++)
			status.Totalsize+= ISSfileobject.get(i).size;

		status.setTransferAck(true);
		
		status.thread= new Thread("Cloud to Cloud"){
			public void run(){
				MyBoolean skipflag= new MyBoolean(false);
				MyBoolean T_skipflag= new MyBoolean(false);
				for(ISSFile list: ISSfileobject){
					try{
						FilesMethods.getFileListFromISS(FilesMethods.validatepath(srcParentISSPath) + "/" + list.name, list, FilesMethods.validatepath(desISSPath),skipflag,status);
					}catch(ClientApiException e){
						ClientExceptions.defaultException(e.getMessage(),status);
					}
				}
			}
		};
		status.thread.start();
		return status;
	}
	
	
	

	/**
	 * This method is for deleting file from the Cloud.
	 * @param srcCloudpath
	 * @param ISSfileobject
	 * @return Thread
	 */
	public Status deleteFromISS(final String srcParentISSpath,final ArrayList<ISSFile> ISSfileobject) {
		final Status status= new Status();
		status.setTotalsize(ISSfileobject.size()); //This is set the total size.
		status.setTransferAck(true);
		
		status.thread= new Thread("Deleting file"){
			public void run(){
				for(int i= 0;i < ISSfileobject.size();i++){
					try {
						ArrayList<String[]> list= ConnectMetaServer.deleteFile(FilesMethods.validatepath(srcParentISSpath)+"/"+ISSfileobject.get(i).name);
						performTransfer.performDeleteAction(list);
					} catch (RemoteException | RuntimeException e) {
						ClientExceptions.defaultException("Error while deleting From Meta-data Server: " + e.getMessage(),status);
					} catch (Exception e){
						ClientExceptions.defaultException("Error While deleting: " + e.getMessage(),status);
					}
				}
			}
		};
		status.thread.start();
		return status;
	}
	
	/**
	 * This method is for deleting file from the Cloud.
	 * @param srcCloudpath
	 * @param ISSfileobject
	 * @return Thread
	 *//*
	public Status deleteFromISS(final String srcParentISSpath,final ArrayList<ISSFile> ISSfileobject) {
		final Status status= new Status();
		status.setTotalsize(ISSfileobject.size()); //This is set the total size.
		status.setTransferAck(true);
		
		status.thread= new Thread("Deleting file"){
			public void run(){
				for(int i= 0;i < ISSfileobject.size();i++){
					try {
						ArrayList<String[]> list= ConnectMetaServer.deleteFile(FilesMethods.validatepath(srcParentISSpath)+"/"+ISSfileobject.get(i).name);
						performTransfer.performDeleteAction(list);
					} catch (RemoteException | RuntimeException e) {
						throw new RuntimeException(e.getMessage());
					} catch (Exception e){
						throw new RuntimeException(e.getMessage());
					}
				}
			}
		};
		status.thread.start();
		return status;
	}*/
	
	/**
	 * This method is for opening the file.
	 * By using this method the programmmer can copy the file to the temperary path.
	 * And he can display the copied file to the user.
	 * @param srcCloudPath
	 * @param ISSfileobject
	 * @return Thread
	 */
	public Thread openFile(final ISSFile ISSfileobject, final String ISSpath) {
		Thread thread= new Thread("open File"){
			public void run(){
				File f= new File(tempFilePath);
				if(!f.exists())
					f.mkdir();
				FilesMethods.openfile(tempFilePath, ISSfileobject, ISSpath);
			}
		};
		thread.start();
		return thread;
	}
	
	
	
	/**
	 * This method is for creating a Directory in Cloud.
	 * @param cloudDirPath
	 * @param Dirname
	 * @return Thread
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 * @throws ClientApiException 
	 */
	public Thread createFolder(final String srcISSPath, final String Dirname) {
		Thread thread= new Thread("Create folder"){
			public void run(){
				try {
					ConnectMetaServer.createFolder(FilesMethods.validatepath(srcISSPath), Dirname);
				} catch (RemoteException | RuntimeException
						| ClientApiException e) {
					ClientExceptions.defaultException1(e.getMessage());
				}
			}
			};
			thread.start();
			return thread;
	}
	
	
	/**
	 * This method is for moving file or Directory from one position to another in cloud.
	 * @param destpath
	 * @param parentsource
	 * @param sourceobject
	 * @return Thread
	 * @throws ClientApiException 
	 */
	public Thread moveFile(final String parentsrcISSPath,final ArrayList<ISSFile> sourceobject,final String destISSPath) throws ClientApiException , RuntimeException{
		if(destISSPath.equals(""))
			throw new ClientApiException("Path not found");
		Thread thread= new Thread("Move File"){
			public void run(){
				int sourcepathlength= sourceobject.size();
				for(int i= 0;i < sourcepathlength;i++){
							try {
								ConnectMetaServer.moveFile(FilesMethods.validatepath(destISSPath),FilesMethods.validatepath(parentsrcISSPath)+"/"+sourceobject.get(i).name);
							} catch(RemoteException | RuntimeException e){
								throw new RuntimeException("Error While moving File: " + e.getMessage());
							}catch (Exception e) {
								throw new RuntimeException("Error While moving File: " + e.getMessage());
							}
				}
			}
		};
		thread.start();
		return thread;
	}
	
	/**
	 * This for updating the existing file.
	 * Suppose any changes are made then the existing file sholud be updated.
	 * @param Cloudpath
	 * @param tempfilepath
	 * @return Thread
	 */
	public static void updateFile() {
		Thread thread= new Thread("Update"){
			public void run(){
				while (true) {
					synchronized (update_List) {
						for(UpdateList l : update_List)
						{
							File f= new File(l.update_List[0]);
							if(f.lastModified() > Long.valueOf(l.update_List[2])) {
								FilesMethods.updatefile(f,l.update_List[1],l.object);
								l.update_List[2]= Long.toString(f.lastModified());
							}
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						//ignore...
					}
				}
			}
		};
		thread.start();
	}
	
	
	/**
	 * This method return the ISSFile object by the given valide path.
	 * @param path
	 * @return Thread
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 */
	public ISSFile getISSFile(String srcISSPath) throws RemoteException, RuntimeException {
			return ConnectMetaServer.getFile(srcISSPath);
	}

	
	/**
	 * This method is for renaming the exiting file name with new file name.
	 * @param path
	 * @param name
	 * @return Thread
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 */
	public void renameFile(String ISSFilepath, String newname) throws RemoteException, RuntimeException {
			ConnectMetaServer.renameFile(ISSFilepath, newname);
	}

	public ArrayList<ISSFile> searchFile(String path, String query)
			throws RemoteException, RuntimeException {
		return ConnectMetaServer.searchFile(path, query);
	}
}
