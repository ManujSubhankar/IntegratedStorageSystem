package iss.user;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class UserServer 
{
	static UserGateway im;
	public static void main(String asrs[]) throws SocketException
	{
		if (im == null)
			try {
				im= new UserGateway();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		NetworkInterface i= NetworkInterface.getByName("eth0");
		  String host= i.getInterfaceAddresses().get(1).getAddress().toString();
		  if(host.startsWith("/"))
				host= host.substring(1);
	  try
	  {
	    System.setProperty("java.rmi.server.hostname", host);
	    Naming.rebind("UserSer",im);
	  }catch(Exception e){System.out.println("EX : " + e);} 
	}
}
