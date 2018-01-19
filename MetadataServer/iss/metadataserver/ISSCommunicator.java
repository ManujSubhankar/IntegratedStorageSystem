package iss.metadataserver;
import iss.FileType;
import iss.ISSFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

@SuppressWarnings("serial")
public class ISSCommunicator extends UnicastRemoteObject implements ISSApi
{
	ISSTree tree;
	ISSStat stat; 
	HashMap<String,ISSNode> waiting= new HashMap<String,ISSNode>();
	
	public ISSCommunicator() throws RemoteException 
	{
		super();
		stat= new ISSStat();
		stat= ISSStat.ISSStartup();
		tree= stat.tree;
		
	}
	
	public void addHost(String name, byte[] macAdd,long size)
	{
		stat.addHost(name, macAdd, size);
		stat.Shutdown();
	}
	
	/**
	 * Traverse the tree and returns the children of the given path. If the path is a leaf node
	 * then the function returns its parents children.
	 * @param path of the directory 
	 * @return ArrayList of file which are under the directory(path). 
	 * @author Manuj Subhankar Sahoo   
	 */
	public ArrayList<ISSFile> getContent(String path) throws RemoteException, RuntimeException
	{	
		ISSNode node;
		if(path == null || path.equals(""))
			return tree.head.getFiles();
		else
			node= tree.Traverse(path);
		if(!node.getFile().fileType.equals(FileType.Directory))
			return node.getParent().getFiles();
		
		return node.getFiles();
	}
	
	/**
	 * @param path of a file
	 * @return The ISSFile object which represent the given path.
	 * @author Manuj Subhankar Sahoo 
	 */
	public ISSFile getFile(String path) throws RemoteException, RuntimeException
	{
		try
		{
			if(path == null)
				return tree.head.getFile();
			if(path.equals(""))
				return tree.head.getFile();
			ISSFile ans= tree.Traverse(path).getFile();
			return ans.clone();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("In metadata sever : in getFile : " + e.getMessage());}
	}
	
	/**
	 * Finds a suitable host for file according to the size,
	 * create a ISSNode with the given name and size and adds it to the given path.
	 * @param path of the directory where the file is to be created
	 * @param name of the file which is to be stored.
	 * @param size of the file which is to be stored.
	 * @return A String array where the first string is the file ID and the rest are the host where the file is to be stored.
	 * @author Manuj Subhankar Sahoo
	 */
	
	public String[] storeFile(String path, String name, long size) throws RemoteException, RuntimeException
	{
		if(name.contains("/"))
			throw new RuntimeException(name + "cannot be kept as a file name.");
		if(name.equals("") || name.split("\\s+").length == 0)
			throw new RuntimeException(name + "cannot be kept as a file name.");
		try 
		{
			ISSNode node= tree.Traverse(path);
			if(node.getFile().fileType.equals(FileType.RegularFile))
				throw new RuntimeException("The destination path is a file");
			if(node.containsFile(name))
				throw new RuntimeException("File already exits");
			String[] ans= new String[3];
			stat.hostFinder(ans, size);
			ans[0]= stat.fileIdGenerator(ans[1]);	//get the fileID 
			ISSNode child= new ISSNode(name,ans[0],ans[1],size);	//create a node
			child.addFileDestinationAddress(ans[2]);  // Add the mirror host
			child.setFileType(FileType.RegularFile);
			child.setParent(node);
			putInWaiting(child);
			Thread th= new Thread(new Runnable() {
				public void run() {
					synchronized(stat)
					{
						stat.Shutdown();
					}
				}
			});
			th.start();
			return ans;			//return the host and the file id to the client
		}catch(Exception e){throw new RuntimeException("In metadata sever : in storeFile : " + e.getMessage());}
	}
	
	public ArrayList<ISSFile> searchFile(String path, String query)
	{
		ArrayList<ISSFile> ans= new ArrayList<ISSFile>();
		tree.search(query, path, ans, tree.Traverse(path));
		return ans;
	}
	
	/**
	 * 
	 * @param node
	 */
	private void putInWaiting(ISSNode node)
	{
		waiting.put(node.getFileId(), node);
	}
	
	/**
	 * @param fileId of the file which in the waiting list 
	 * @return ISSFile object representing the the fileId
	 */
	public ISSFile getFromWaiting(String fileId)
	{
		ISSNode ans= waiting.get(fileId);
		if(ans == null)
			throw new RuntimeException(fileId + " file doest not exists");
		return ans.getFile();
	}
	
	public void deleteFromWaiting(String fileId)
	{
		ISSNode ans= waiting.remove(fileId);
		if(ans == null)
			throw new RuntimeException(fileId + " file doest not exists");
		ISSFile file= ans.getFile();
		stat.hostSpaceUpdater(file.destIpAddress.get(0), file.size);
		stat.hostSpaceUpdater(file.destIpAddress.get(1), file.size);
	}
	
	/**
	 * Add the node which is mapped with the fileId to the tree
	 * @param fileId of the file which is stored successfully. 
	 */
	public boolean confirmFile(String fileId) throws RemoteException, RuntimeException
	{
		ISSNode node= waiting.remove(fileId);
		if(node == null)
			throw new RuntimeException(fileId + " file doesn't exists in the waiting list");
		boolean ans;
		synchronized(tree)
		{
			ans= tree.addNode(node.getParent(), node);	
		}
		if(ans)
			return ans;
		else
			deleteFromWaiting(fileId);
		return ans;
	}
	
	/**
	 * Delete the file represented by the path.
	 * @param path of the file which is to be deleted.
	 */
	public ArrayList<String[]> deleteFile(String path) throws RemoteException, RuntimeException
	{
		try 
		{
			if(path.equals("/"))
				throw new RuntimeException("/ cannot be deleted.");
			ISSNode node= tree.Traverse(path);
			ArrayList<String[]> ans= new ArrayList<String[]>();
			synchronized(tree)
			{
				tree.deleteNode(node,ans);		//delete the node from the tree
			}
			return ans;
		}catch(Exception e){
			throw new RuntimeException("In metadata sever : in deleteFile : " + e.getMessage());}
	}
	
	/**
	 * Create a directory under the given path(parent) with the given name.
	 * @param parent is the directory under which the folder is to be created.
	 * @param name of the folder which is to be created. 
	 */
	public void createFolder(String parent, String name) throws RemoteException, RuntimeException
	{
		if(name.contains("/"))
			throw new RuntimeException(name + "cannot be kept as a file name.");
		if(name.equals("") || name.split("\\s+").length == 0)
			throw new RuntimeException(name + "cannot be kept as a file name.");
		ISSNode node= tree.Traverse(parent);
		if(node.containsFile(name))
			throw new RuntimeException("Folder already exits");
		ISSNode child= new ISSNode(name, "0", "null", 0); //create node of folder type
		child.setFileType(FileType.Directory);
		child.setParent(node);
		synchronized(tree)
		{
			tree.addNode(tree.Traverse(parent), child);		//add the created node to the tree 
		}
		stat.Shutdown();
	}
	
	
	
	/**
	 * Move the file represented by source path to the destination path.
	 * @param destPath the directory to which the source file is to be moved
	 * @param srcPath the file of directory which is to be moved 
	 */
	public void moveFile(String destPath, String srcPath) throws RemoteException, RuntimeException
	{
		if(destPath.startsWith(srcPath))
			throw new RuntimeException("Move is not possible. Destination is the child of sorce");
		synchronized(tree)
		{
			ISSNode node= tree.Traverse(srcPath);
			tree.removeChild(node);
			ISSNode parent= tree.Traverse(destPath);
			if(!parent.getFile().fileType.equals(FileType.Directory))
				throw new RuntimeException("The destination path is a File not a Folder.");
			if(parent.containsFile(node.getFile().getName()))
				throw new RuntimeException("File with same name already exits");
			node.setParent(parent);
			parent.addChildren(node);
			tree.changeSize(parent, node.getFile().size);
		}
	}
	
	/**
	 * Rename the a given file represented by the path to the given name
	 * @param path of the file which is to be renamed.
	 * @param name to which the filename is to be changed.
	 */
	public void renameFile(String path,String name) throws RemoteException, RuntimeException
	{
		if(name.equals("") || name.split("\\s+").length == 0)
			throw new RuntimeException(name + "cannot be kept as a file name.");
		if(name.contains("/"))
			throw new RuntimeException("File can't be renamed to " + name);
		ISSNode node= null;
		node= tree.Traverse(path);
		if(node.containsFile(name))
			throw new RuntimeException("File already exits");
	
		synchronized(tree)
		{
			node.setName(name);
		}
	}
	
	/**
	 * Returns the Machine Address of the hostName host
	 * @param hostName is the host address whose mac address is to be returned
	 * @return the machine address 
	 */
	public byte[] getMacAdd(String hostName)
	{
		return stat.pool.getMacAdd(hostName);
	}
	
	public boolean fileSizeUpdater(String path, long size) throws RemoteException, RuntimeException
	{
		ISSNode node= tree.Traverse(path);
		long change= node.getFile().size - size;
		try 
		{
		
			stat.hostSpaceUpdater(node.getFileDestinations().get(0), size);
			stat.hostSpaceUpdater(node.getFileDestinations().get(1), size);
		}catch(Exception e){return false;}
		synchronized(tree)
		{
			tree.changeSize(node, size);
		}
		return true;
	}
	
	/**
	 * @return total space parent in the cloud
	 */
	public long getTotalSpace()
	{
		return stat.pool.getTotalSpace()/2;
	}
	
	/**
	 * @return available space in the cloud
	 */
	public long getAvialableSpace()
	{
		return stat.pool.getAvialableSpace()/2;
	}
	
	public static void main(String[] args) 
	{
		String[] str= "".split("\\s+");
		
		System.out.println(str.length);
		/*ISSCommunicator com= null;
		try 
		{
			com = new ISSCommunicator();
			//System.out.println("tree : " + com.tree);
		} catch (RemoteException e) {System.out.println("EX : " + e);}*/
		
	}

}

