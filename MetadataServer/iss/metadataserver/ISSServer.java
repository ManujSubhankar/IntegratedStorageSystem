package iss.metadataserver;
import java.net.*;
import java.rmi.*;

public class ISSServer
{
	public static void main(String[] args) throws SocketException
	{
		NetworkInterface i= NetworkInterface.getByName("eth0");
		String host= i.getInterfaceAddresses().get(1).getAddress().toString();
		if(host.startsWith("/"))
			host= host.substring(1);
		try
		{
			ISSCommunicator im= new ISSCommunicator();
			System.setProperty("java.rmi.server.hostname", host);
			Naming.rebind("ISSSer",im);
			new ServerShell(im);
		}catch(Exception e){e.printStackTrace();}
		

	}
}
