package iss.metadataserver;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class HostPool implements Serializable
{
	private long totalSpace, avialableSpace;
	private ArrayList<HostInfo> hostList= new ArrayList<HostInfo>();
	
	public long getTotalSpace() {
		return totalSpace;
	}

	public long getAvialableSpace() {
		return avialableSpace;
	}
	
	public ArrayList<HostInfo> getHostList()
	{
		return (ArrayList<HostInfo>) hostList.clone();
	}

	@SuppressWarnings("unchecked")
	public HostPool clone(HostPool p)
	{
		HostPool ans= new HostPool();
		ans.avialableSpace= avialableSpace;
		ans.totalSpace= totalSpace;
		ans.hostList= (ArrayList<HostInfo>) hostList.clone();
		return ans;
	}
	
	public byte[] getMacAdd(String hostName)
	{
		byte[] ans= null;
		for(int i= 0; i < hostList.size(); i++)
		{
			if(hostList.get(i).hostName.equals(hostName))
			{
				ans= hostList.get(i).macAdd;
				break;
			}
		}
		return ans;
	}
	
	private synchronized void shotList(HostInfo host)
	{
		int i, s= hostList.size();
		for(i= 0; i < s; i++)
			if(hostList.get(i).availableSpace <= host.availableSpace) break;
		if(i < s)
			hostList.add(i, host);
		else
			hostList.add(host);
		//System.out.println(hostList.get(0).hostName);
	}
	
	/**
	 * Add new host. This method is called during the installation of BSFS
	 *  @author Manuj Subhankar Sahoo.
	 */
	public synchronized void addHost(String name,byte[] macAdd, long size)
	{
		HostInfo temp= new HostInfo(name, macAdd, size);
		int i, s= hostList.size();
		for(i= 0; i < s; i++)
			if(hostList.get(i).hostName.equals(name)) break;
		if(i < s)
		{
			hostList.get(i).totalSpace+= size;
			hostList.get(i).availableSpace+= size;
			totalSpace+= size;
			avialableSpace+= size;
			return;
		}
		shotList(temp);
		totalSpace+= temp.totalSpace;
		avialableSpace+= temp.availableSpace;
	}
	
	public synchronized void modifyHostSpace(String host, long newSize)
	{
		synchronized(hostList)
		{
			int s= hostList.size();
			for(int i= 0; i < s; i++)
			{
				if(hostList.get(i).hostName.equals(host))
				{
					HostInfo temp= hostList.get(i);
					if(temp.totalSpace - temp.availableSpace > newSize)
						throw new RuntimeException("Host cannot be resized. The data present in the host is more than the new size.");
					else
					{
						avialableSpace= newSize - temp.totalSpace;
						totalSpace+= newSize - temp.totalSpace;
						temp.availableSpace= newSize - (temp.totalSpace - temp.availableSpace);
						temp.totalSpace= newSize;
						
					}	
					break;
				}
			}
		}
	}
	
	public synchronized void deleteHost(String host)
	{
		synchronized(hostList)
		{
			int s= hostList.size();
			int i=	0;
			for(; i < s; i++)
			{
				if(hostList.get(i).hostName.equals(host))
				{
					HostInfo temp= hostList.get(i);
					if(temp.totalSpace != temp.availableSpace)
						throw new RuntimeException("Host cannot be deleted. Some data are present.");
					else
					{
						temp= hostList.remove(i);
						totalSpace-= temp.totalSpace;
						avialableSpace-= temp.availableSpace;
					}
					break;
				}
			}
			if(s == i)
				throw new RuntimeException("No such host present");
		}
				
	}
	
	public synchronized void forcedDeleteHost(String host)
	{
		synchronized(hostList)
		{
			int s= hostList.size();
			int i= 0;
			for(; i < s; i++)
			{
				if(hostList.get(i).hostName.equals(host))
				{
					HostInfo temp= hostList.remove(i);
					totalSpace-= temp.totalSpace;
					avialableSpace-= temp.availableSpace;
					break;
				}
			}
			if(s == i)
				throw new RuntimeException("No such host present");
		}
				
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	/*public synchronized HostInfo removeHost(String name)
	{
		int i,s= hostList.size();
		for(i= 0; i < s; i++)
			if(hostList.get(i).hostName.equals(name)) break;
		if(i < s)
		{
			HostInfo ans= hostList.remove(i);
			totalSpace-= ans.totalSpace;
			avialableSpace-= ans.availableSpace;
			return ans;
		}
		else
			throw new RuntimeException("No such host present");
	}*/
	
	/**
	 * Finds a suitable host for a file of the given size.
	 * It uses worst fit algorithm.
	 * @param size of the file.
	 *  @author Manuj Subhankar Sahoo.
	 */
	public synchronized String hostFinder(long size)
	{
		HostInfo ans= null;
		int s= hostList.size();
		if(s == 0)
			throw new RuntimeException("No hosts present.");
		if(hostList.get(0).availableSpace < size)
			throw new RuntimeException("No hosts have " + size + " space. Max size avialable" + hostList.get(1).availableSpace);
		
		ans= hostList.remove(0);
		ans.availableSpace-= size;
		avialableSpace-= size;
		shotList(ans);
		
		//System.out.println("The host is : " + ans.hostName);
		return ans.hostName;
	}
	
	private synchronized void hostFinderSupporter(long size)
	{
		HostInfo ans;
		HostInfo ans1;
		ans= hostList.remove(0);
		ans1= hostList.remove(0);
		ans.availableSpace-= size;
		avialableSpace-= size;
		shotList(ans);
		ans1.availableSpace-= size;
		avialableSpace-= size;
		shotList(ans1);
	}
	
	public synchronized void hostFinder(String[] buff, final long size)
	{
		int s= hostList.size();
		if(s == 0)
			throw new RuntimeException("No hosts present.");
		if(s == 1)
			throw new RuntimeException("No host for Morroring.");
		if(hostList.get(1).availableSpace < size)
			throw new RuntimeException("No hosts have " + size + " space. Max size avialable" + hostList.get(1).availableSpace);
		buff[1]= hostList.get(0).hostName;
		buff[2]= hostList.get(1).hostName;
		Thread th= new Thread(new Runnable() {
			public void run() {
				synchronized(this)
				{
					hostFinderSupporter(size);
				}
			}
		});
		th.start();
	}
	
	/*public synchronized String hostFinder(String host, long size)
	{
		int s= hostList.size();
		if(s <= 1)
			throw new RuntimeException("Only one host is present.");
		HostInfo h= null;
		String ans;
		for(int i= 0; i< s; i++)
		{
			if(hostList.get(i).hostName.endsWith(host))
			{
				h= removeHost(host);
				break;
			}
		}
		if(h == null)
			throw new RuntimeException(host + " host is not present.");
		ans= hostFinder(size);
		shotList(h);
		return ans;
	}*/
	
	
	public synchronized void hostSpaceUpdater(String host, final long size)
	{
		
		int s= hostList.size();
		int i= 0;
		for(; i < s; i++)
		{
			if(hostList.get(i).hostName.equals(host))
			{
				if(hostList.get(i).availableSpace + size < 0)
					throw new RuntimeException("Not enough space in the host.");
				HostInfo ans; 
				ans= hostList.remove(i);
				ans.availableSpace+= size;
				avialableSpace+= size;
				shotList(ans);
				break;
			}
		}
		if(i == s)
			throw new RuntimeException(host + " is not present.");
	}

}
