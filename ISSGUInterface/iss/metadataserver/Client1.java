package iss.metadataserver;
import java.rmi.*;

public class Client1
{
	public static void main(String[] args)
	{
		try
	    {
	      ISSApi in= (ISSApi) Naming.lookup("rmi://" + "192.168.34.114" + "/ISSSer");
	      in.moveFile("/","/4)UNIVERSITY PICS/asd");
	    }catch(Exception e) {System.out.println("Ex : " + e);};
	}
}