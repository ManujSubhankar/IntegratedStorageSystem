package iss.client;
import iss.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import iss.metadataserver.*;
public class Testing {
	public static void main(String[] args) throws RemoteException{
			Client c= new Client();
			try {
				c.getConnected("192.168.34.113");
			} catch (MalformedURLException | NotBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Client.ConnectMetaServer.storeFile("/", "murali", 1234);
			ArrayList<ISSFile> list = null;
			try {
				list = c.getContent("/");
				for(int i= 0;i < list.size();i++)
					System.out.println(list.get(i).destIpAddress + " : " + list.get(i).name);
			} catch (ClientApiException | RuntimeException e) {
				System.out.println("centralserver: " + e.getMessage());
			}
			//Client.updateDeletelistStatus();
			//ISSFile f= c.getFile("/ch/sai");
			//System.out.println("getfile: " + f.fileID);
			ArrayList<ISSFile> c1= new ArrayList<ISSFile>();
			//c1.add(list.get(0));
			//c1.add(list.get(3));
			//Client.ConnectMetaServer.deleteFile("/otherstuff");
			ArrayList<File> local= new ArrayList<File>();
			local.add(new File("/home/mca2/MCAStuff"));
			//local.add(new File("/home/mca2/otherstuff"));
			//local.add(new File("/home/bca3/ch"));
			//local.add(new File("/home/bca3/4)UNIVERSITY PICS"));
			//c.getFromISSToLocal("/", list, "/home/mca2/localstore");
			//c.deleteFromISS("/", list);
			c.storeFromLocalToISS(local, "/");
			//c.storeISStoISS("/", c1, "/4)UNIVERSITY PICS");
			/*try {
				c.createFolder("/", "Basanta");
			} catch (RuntimeException | ClientApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//System.out.println("size: " + c.ConnectMetaServer.getTotalSpace());
	}
}
