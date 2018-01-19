package iss.metadataserver;
import iss.ISSFile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ISSApi extends Remote
{
	/**
	 * Add the host to the given host to the host list  
	 * @param name is the address of the host which is to be added.
	 * @param macAdd machine address of the host
	 * @param size is the amount of space it is contributing in bytes
	 */
	public void addHost(String name, byte[] macAdd,long size) throws RemoteException, RuntimeException;
	/**
	 * Traverse the tree and returns the children of the given path. If the path is a leaf node
	 * then the function returns its parents children.
	 * @param path of the directory 
	 * @return ArrayList of file which are under the directory(path). 
	 * @author Manuj Subhankar Sahoo   
	 */
	public ArrayList<ISSFile> getContent(String path) throws RemoteException, RuntimeException;
	
	/**
	 * Finds a suitable host for file according to the size,
	 * create a ISSNode with the given name and size and adds it to the given path.
	 * @param path of the directory where the file is to be created
	 * @param name of the file which is to be stored.
	 * @param size of the file which is to be stored.
	 * @return A String array where the first string is the file ID and the rest are the host where the file is to be stored.
	 * @author Manuj Subhankar Sahoo
	 */
	public String[] storeFile(String path, String name, long size) throws RemoteException, RuntimeException;
	
	
	/**
	 * Searches for a query in given path.
	 * @param path in which search will be performed
	 * @param query is the file name
	 * @return ArrayList of ISSFile which contains all the matches
	 */
	public ArrayList<ISSFile> searchFile(String path, String query) throws RemoteException, RuntimeException;
	/**
	 * Add the node which is mapped with the fileId to the tree
	 * @param fileId of the file which is stored successfully. 
	 */
	public boolean confirmFile(String fileId) throws RemoteException, RuntimeException;
	
	/**
	 * This method delete a node which is present in the waiting list
	 * @param fileId of the file.
	 * 
	 */
	public void deleteFromWaiting(String fileId) throws RemoteException, RuntimeException;
	/**
	 * Delete the file represented by the path.
	 * @param path of the file which is to be deleted.
	 */
	public ArrayList<String[]> deleteFile(String path) throws RemoteException, RuntimeException;
	
	/**
	 * Create a directory under the given path(parent) with the given name.
	 * @param parent is the directory under which the folder is to be created.
	 * @param name of the folder which is to be created. 
	 */
	public void createFolder(String parent, String name) throws RemoteException, RuntimeException;
	
	/**
	 * Move the file represented by source path to the destination path.
	 * @param destPath the directory to which the source file is to be moved
	 * @param srcPath the file of directory which is to be moved 
	 */
	public void moveFile(String destPath, String srcPath) throws RemoteException, RuntimeException;
	
	
	public boolean fileSizeUpdater(String path, long size) throws RemoteException, RuntimeException;
	
	/**
	 * @param path of a file
	 * @return The ISSFile object which represent the given path.
	 * @author Manuj Subhankar Sahoo 
	 */
	public ISSFile getFile(String path) throws RemoteException, RuntimeException;
	
	/**
	 * Rename the a given file represented by the path to the given name
	 * @param path of the file which is to be renamed.
	 * @param name to which the filename is to be changed.
	 */
	public void renameFile(String path,String name) throws RemoteException, RuntimeException;
	
	/**
	 * @param fileId of the file which in the waiting list 
	 * @return ISSFile object representing the the fileId
	 */
	public ISSFile getFromWaiting(String fileId) throws RemoteException, RuntimeException;
	
	/**
	 * Returns the Machine Address of the hostName host
	 * @param host address
	 * @return the machine address 
	 */
	public byte[] getMacAdd(String hostName) throws RemoteException, RuntimeException;
	
	/**
	 * @return total space parent in the cloud
	 */
	public long getTotalSpace() throws RemoteException, RuntimeException;
	
	/**
	 * @return available space in the cloud
	 */
	public long getAvialableSpace() throws RemoteException, RuntimeException;
}
