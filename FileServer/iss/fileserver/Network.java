package iss.fileserver;
import iss.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import iss.metadataserver.*;
public class Network {

	private final static byte Process= 1;
	final static byte fileNotFound= 2;
	private final static byte MulformedRequest= 3;
	private final static String Admin=  "please Contact Admin to solve the Problem";
	@SuppressWarnings("unused")
	private final static String FilenotFound= "File not Found..Problem occured in Fileserver or Central server."
			+ Admin;
	private final static String Nodir= "There in no Directory in the specified path given to the fileserver.\n" +
			Admin;
	@SuppressWarnings("unused")
	private final static String Mulformed= "Mulformed request has been made.This will be reported to the Admin.";
	public static String FileStorepath= null;
	final static int BUFFER= 1024000;
	private static HashMap<String, ArrayList<String[]>> filesAccToIp= new HashMap<String, ArrayList<String[]>> ();
	static String Ipaddress= getSystemIpAddress();
	
	/**
	 * This method gets the system Ipaddress.
	 * And sets the System Ipaddress.
	 * Presently not used any where.
	 * @return
	 */
	public static String getSystemIpAddress(){
		NetworkInterface Ip;
		try {
			Ip = NetworkInterface.getByName("eth0");
			Ipaddress= Ip.getInterfaceAddresses().get(1).getAddress().toString().split("/")[1];
		}catch(SocketException socketEx){
			System.err.println("Not able fetch the system IPaddress");
		}
		return Ipaddress;
	}


	/***
	 * This method is for getting referrence of the arranged list according to Ipaddress.
	 * @return
	 */
	public static HashMap<String, ArrayList<String[]>> getFilesAccToIp() {
		return filesAccToIp;
	}

	/**
	 * This for arranging the objects accoring to the fileserver.
	 * @param bsfsfile
	 * @param cloudpath
	 */

	public static void arrangeIPaddresses(String[] storefileinfo){
		String key= storefileinfo[1];
			if (filesAccToIp.containsKey(key)) {
				ArrayList<String[]> list= filesAccToIp.get(key);
				list.add(storefileinfo);
				return;
			}
			ArrayList<String[]> toput= new ArrayList<String[]>();
			toput.add(storefileinfo);
			filesAccToIp.put(key, toput);
	}

	/**
	 * This method is for deleting the files which are present in the different nodes.
	 * It connects to the destination node and sends arraylist(String[]) which will have the files details to delete.
	 * @param list
	 */

	public static void performDeleteAction(ArrayList<String[]> list){
		for(String[] listinfo: list){
			for(int i= 1;i < listinfo.length;i++){  //[0] is id.Remaining Ipaddresses.
				if(listinfo[i].equals(Ipaddress)){
						new File(Network.FileStorepath+listinfo[0]).delete();
				}
				else{
					String[] info= new String[2];
					info[0]= listinfo[0];
					info[1]= listinfo[i];
					Network.arrangeIPaddresses(info);
				}
			}
		}
		ArrayList<String[]> deletelist= null;
		for(int i= 0;i < Network.getFilesAccToIp().keySet().size();i++){   //This gets the all the elements of the key.
			try{
				String Ipaddress= (String) Network.getFilesAccToIp().keySet().toArray()[i];
				deletelist= Network.getFilesAccToIp().get(Ipaddress);
				Socket client= new Socket(Ipaddress,1234);
				ObjectOutputStream out= new ObjectOutputStream(client.getOutputStream());
				out.writeObject(new RecognizeObject(null,ISSTask.Delete,deletelist));  //deletelist.get(i)[0] fileId,[1] is Ipaddress.
				out.flush();
				out.close();
			}catch(ConnectException con){
				FileServermain.delete_List.add(deletelist);
				KeepData.WriteDeleteInfo();
			}
			catch(Exception e){
				//Ignore..
			}
		}
	}

	
	/**
	 * This is for receiving the file from client.
	 * @param con (Socket reference)
	 * @param f (File object source which need to be stored)
	 * @param flag (Flag to check whether requesting for update or normal store)
	 */
	public static void ReceiveFile(Socket con,File f,boolean flag) {
		try{
			f.createNewFile();
			BufferedInputStream r= new BufferedInputStream(con.getInputStream());
			FileOutputStream fr= new FileOutputStream(f);
			byte b[]= new byte[BUFFER];
			int read;
			while(true) {
				read= r.read(b);
				if(read == -1) 
					break;
				fr.write(b, 0, read);
				fr.flush();
			}
			if(flag){
				File rfile= new File(FileStorepath.concat(f.getName().split("p")[1])); /*This is for updating the existing file.*/
				rfile.delete();
				f.renameTo(rfile);
			}
			fr.close();
			r.close();
			con.close();
		}catch(IOException io){
			//System.out.println("Reason in put: " + io.getMessage());
		}
	}


	/**
	 * This method is for receiving the file which is send by the Client.
	 * And this has the functionality of mirror the file by the information given by the meta-data server.
	 * @param socket
	 * @param request
	 * @author P. Murali krishna
	 */
	public static void PerformReceiveAction(Socket socket,String fileID, boolean flag, boolean flag1){
		try{
			if(!new File(FileStorepath).exists()){
				ObjectOutputStream ob= new ObjectOutputStream(socket.getOutputStream());
				ob.writeObject(new ReadData(fileNotFound,Nodir));
				ob.flush();
			}else if(flag1){
				ObjectOutputStream ob= new ObjectOutputStream(socket.getOutputStream());
				ob.writeObject(new ReadData(Process,""));
				ob.flush();
				File f= new File (FileStorepath.concat(fileID));
				ReceiveFile(socket, f, flag);
				ob.close();
			}else{
				File f= new File (FileStorepath.concat(fileID));
				ReceiveFile(socket, f, flag);
			}
		}catch(Exception e){
			//Ignore..
		}
	}

	/**
	 * This method actually transfers the file to the client on request.
	 * @param con
	 * @param f
	 */
	public static void sendingfile(Socket con,File f){
		try {
			BufferedOutputStream forWriting= new BufferedOutputStream(con.getOutputStream());
			FileInputStream rd_file= new FileInputStream(f.getAbsolutePath());
			int read;
			byte[] b= new byte[BUFFER];
			while(true) {
				read= rd_file.read(b);
				if(read == -1) 
					break;
				forWriting.write(b, 0, read);
				forWriting.flush();
			}
			forWriting.close();
			rd_file.close();
			con.close();
		}catch(Exception e){
			//System.out.println("Exception sairam Caught : " + e);
		}
	}


	/**
	 * This method is for sending the file which is requested by the Client.
	 * This method is called by Mirr_copy for mirroring.
	 * @param socket
	 * @param request
	 * @author P. Murali krishna.
	 */
	public static void PerformSendingAction(Socket socket, String fileId){
		try{
			File f= new File (FileStorepath.concat(fileId));
			if(!f.exists()){
				ObjectOutputStream ob= new ObjectOutputStream(socket.getOutputStream());
				ob.writeObject(new ReadData(fileNotFound, FilenotFound));
				ob.flush();
				ob.close();
			}
			else{  //This is sending file to the client.
				ObjectOutputStream ob= new ObjectOutputStream(socket.getOutputStream());
				ob.writeObject(new ReadData(Process, ""));
				ob.flush();
				sendingfile(socket, f);
				ob.close();
			}
		}catch(Exception e){
			//Ignore..
		}
	}

	
	

}
