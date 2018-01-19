package iss.metadataserver;
import java.io.Serializable;

@SuppressWarnings("serial")
public class HostInfo implements Serializable
{
	String hostName;
	byte[] macAdd;
	long totalSpace, availableSpace;
	
	HostInfo(String host,byte[] macAdd, long total)
	{
		this.macAdd= macAdd;
		hostName= host;
		totalSpace= total;
		availableSpace= total;
	}
	
	public HostInfo clone()
	{
		HostInfo ans= new HostInfo(hostName, macAdd, totalSpace);
		ans.availableSpace= availableSpace;
		return ans;
	}
}
