package iss.client;

import iss.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Client_GateWay {

	/**
	 * This method is for connecting to the meta-data server.
	 * It takes Ipaddress of the meta-data server.
	 * @param IPAddress
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void getConnected(String IPAddress) throws MalformedURLException, RemoteException, NotBoundException;
	
	/**
	 * This method return the ISSFile object which consists of files information.
	 * It takes the path as an argument and returns the list of file under the path if it is a directory.
	 * If the path refering to the file the it returns the file object.
	 * @throws RemoteException and {@link RuntimeException} Internally calls the ClientException class method to display message.
	 * @author P. Murali krishna
	 * @throws ClientApiException 
	 */
	public ArrayList<ISSFile> getContent(String ISSPath) throws RemoteException, ClientApiException;
	
	
	/**
	 * This method Stores the files or Directories given to the cloud.
	 * @param desCloudPath
	 * @param localSrcPath
	 * @return Status (This is used for the programmer to do some activity on the current prograss.
	 */
	public Status storeFromLocalToISS(final ArrayList<File> localFileObject,final String desISSPath);
	
	
	/**
	 * This is for Uploading file to the Cloud by using the Browser.
	 * OR this takes a Inputstream and reads from it transfer the data to the destination system.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Thread storeFromLocalToISSWeb(final InputStream input,final String desISSPath,final String name,final long size);
	
	/**
	 * This is for the copying the file or Directory from the cloud to Local system.
	 * @param parentCloudPath
	 * @param ISSfileobject
	 * @param desLocalPath
	 * @return Thread
	 */
	public Status getFromISSToLocal(final String parentISSPath,final ArrayList<ISSFile> ISSfileobject,final String desLocalPath);

	/**
	 * This function write the file to the Outputstream.
	 * @param output
	 * @param cloudPath
	 * @param file
	 * @return
	 * @throws RemoteException
	 * @throws RuntimeException
	 * @throws ClientApiException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileServerException 
	 */
	public Socket getISSToLocalWeb(final ISSFile ISSfile)throws RemoteException, RuntimeException, IOException, ClientApiException, ClassNotFoundException, FileServerException;
	
	/**
	 * This method is for copying files or Directory from cloud to cloud.
	 * @param srcParentPath
	 * @param ISSfileobject
	 * @param desCloudpath
	 * @return Status.
	 */
	public Status storeISStoISS(final String srcParentISSPath,final ArrayList<ISSFile> ISSfileobject,final String desISSpath);

	/**
	 * This method is for deleting file from the Cloud.
	 * @param srcCloudpath
	 * @param ISSfileobject
	 * @return Status
	 */
	public Status deleteFromISS(final String srcParentISSpath,final ArrayList<ISSFile> ISSfileobject);

	/**
	 * This method is for opening the file.
	 * By using this method the programmmer can copy the file to the temperary path.
	 * And he can display the copied file to the user.
	 * @param srcCloudPath
	 * @param ISSfileobject
	 * @return Thread
	 */
	public Thread openFile(final ISSFile ISSfileobject, final String ISSpath);
	
	/**
	 * This method is for creating a Directory in Cloud.
	 * @param cloudDirPath
	 * @param Dirname
	 * @return Thread
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 * @throws ClientApiException 
	 */
	public Thread createFolder(final String ISSDirPath,final String Dirname);
	
	/**
	 * This method is for moving file or Directory from one position to another in cloud.
	 * @param destpath
	 * @param parentsource
	 * @param sourceobject
	 * @return Thread
	 * @throws ClientApiException 
	 */
	public Thread moveFile(final String parentISSsource,final ArrayList<ISSFile> sourceobject,final String destISSPath) throws ClientApiException;
	
	
	/**
	 * This method return the ISSFile object by the given valide path.
	 * @param path
	 * @return Thread
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 */
	public ISSFile getISSFile(String ISSPath) throws RemoteException, RuntimeException;
	
	/**
	 * This method is for renaming the exiting file name with new file name.
	 * 
	 * @param path
	 * @param name
	 * @throws RuntimeException 
	 * @throws RemoteException 
	 */
	public void renameFile(final String ISSfilepath,final String newFileName) throws RemoteException, RuntimeException; 
	
	/**
	 * Searches for a query in given path.
	 * @param path in which search will be performed
	 * @param query is the file name
	 * @return ArrayList of ISSFile which contains all the matches
	 */
	public ArrayList<ISSFile> searchFile(String path, String query) throws RemoteException, RuntimeException;
}
