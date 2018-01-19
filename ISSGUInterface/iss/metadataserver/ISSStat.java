package iss.metadataserver;
import java.io.*;
import java.net.*;
import java.util.*;

@SuppressWarnings("serial")
public class ISSStat implements Serializable
{
	
	ISSTree tree= new ISSTree(this);
	static String path;
	ArrayList<String> fileIdList= new ArrayList<String>();  //list of file id which are assigned to various files.
	ArrayList<String> freeList= new ArrayList<String>();  //list of file id which are free
	HostPool pool= new HostPool();  //list of hosts where the files are to be stored
	boolean whichfile= false;
	ISSStat()
	{
		path= System.getProperty("user.dir");
	}
	
	/**
	 *  This method reads the status of the file system from files
	 *  @author Manuj Subhankar Sahoo.
	 */
	@SuppressWarnings("resource")
	public static ISSStat ISSStartup()
	{
		File f= new File(path+File.separator+"ISSStat");
		File f1= new File(path+File.separator+"ISSStat1");
		try {
			if(f.lastModified() > f1.lastModified())
			{
				try {
					ObjectInputStream in= new ObjectInputStream(new FileInputStream(f));
					return (ISSStat) in.readObject();	
				}catch (Exception e) 
				{
					//ignore
				}
			}
			ObjectInputStream in= new ObjectInputStream(new FileInputStream(f1));
			return (ISSStat) in.readObject();
		} catch (Exception e1) {}
		return null;
	}
	
	public ISSTree getTree()
	{
		return tree;
	}
	
	
	/**
	 *  This method saves the current status of the file system 
	 *  @author Manuj Subhankar Sahoo.
	 */
	@SuppressWarnings("resource")
	public void Shutdown()
	{
		File f= new File(path+File.separator+"ISSStat");
		File f1= new File(path+File.separator+"ISSStat1");
		try {
			if(whichfile)
			{
				ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(this);
				whichfile= false;
			}
			else
			{
				ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream(f1));
				out.writeObject(this);
				whichfile= true;
			}
		} catch (Exception e) 
		{
			//System.out.println("Shutdown : " + e);
		}
	}
	
	/**
	 * Add new host. This method is called during the installation of ISS
	 *  @author Manuj Subhankar Sahoo.
	 */
	public void addHost(String name, byte[] macAdd,long size)
	{
		synchronized (pool) 
		{
			pool.addHost(name, macAdd, size);
		}
	}
	
	public long getTotalSpace()
	{
		return pool.getTotalSpace()/2;
	}
	
	public long getAvialableSpace()
	{
		return pool.getAvialableSpace()/2;
	}
	
	
	/**
	 * Finds a suitable host for a file of the given size.
	 * It uses worst fit algorithm.
	 * @param size of the file.
	 *  @author Manuj Subhankar Sahoo.
	 */
	public String hostFinder(long size)
	{
		synchronized(pool)
		{	
			return pool.hostFinder(size);
		}
		
	}
	
	public void hostFinder(String[] buff, long size)
	{
		pool.hostFinder(buff, size);
	}
	
	/*public String hostFinder(String host,long size)
	{
		
		synchronized(pool)
		{	
			return pool.hostFinder(host,size);
		}
		
	}*/
	
	public void hostSpaceUpdater(String host, long size)
	{
		synchronized(pool)
		{
			pool.hostSpaceUpdater(host, size);
		} 
	}
	
	/**
	 * Generates a file ID
	 * @param host or ip address of the system where the file is to be stored.
	 * @return a file id
	 */
	public String fileIdGenerator(String host)
	{
		synchronized (fileIdList)
		{
			//if the fileID list has no element and the free list is also empty
			if(fileIdList.size() == 0 && freeList.size() == 0)
			{
				fileIdList.add("1");
				return("1");
			}
			
			//if the free list contains some free file ID's
			synchronized (freeList) 
			{
				if(freeList.size() != 0)
				{
					long ans= Long.parseLong(freeList.get(0));
					int s= fileIdList.size(),i;
					if(s == 0)
						fileIdList.add(freeList.get(0));
					else
					{
						for(i= 0; i < s && Long.parseLong(fileIdList.get(i)) < ans ; i++){}
						fileIdList.add(i, freeList.get(0));
					}
					return(freeList.remove(0));
				}
			}
			
			//if the free list is empty
			long ans= Long.parseLong(fileIdList.get(fileIdList.size() - 1)) + 1;
			fileIdList.add(Long.toString(ans));
			return(Long.toString(ans));
		}
	}
	
	
	/**
	 * Delete a file Id from the file ID List
	 * @param id the file ID 
	 * @author Manuj Subhankar Sahoo.
	 */
	public void deleteFileId(String id)
	{
		synchronized(fileIdList)
		{
			int s= fileIdList.size();
			for(int i= 0; i < s; i++)
			{ 
				if(fileIdList.get(i).equals(id))
				{
					
					synchronized(freeList)
					{
						freeList.add(fileIdList.get(i));
						fileIdList.remove(i);
						break;
					}
				}
			}
		}

	}
	
	
	public static void main(String[] args) throws SocketException 
	{
		
		ISSStat b= new ISSStat();
		byte[] b1= new byte[5];
	//	ISSStat.addHost("192.168.34.113",2*107374182400L);
	//	b.addHost("192.168.34.113",b1,1073741824);
		/*b.addHost("192.168.34.111",b1,1073741824);
		b.addHost("192.168.34.112",b1,1073741824);*/
		b.Shutdown();
		b.Shutdown();
	}

}
