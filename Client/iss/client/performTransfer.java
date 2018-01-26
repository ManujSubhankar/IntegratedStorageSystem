package iss.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import iss.RequestToFileServer;
import iss.metadataserver.*;
import iss.client.Network.ReSource;
import iss.*;
public class performTransfer {
	private static HashMap<String, ArrayList<String[]>> filesAccToIp= new HashMap<String, ArrayList<String[]>> ();
	/**
	 * This is for checking the present system Ipaddress and the sending Fileserver IPaddress.
	 * Presently not used.May be usefull in future stages.
	 * @param ipaddress
	 * @return
	 */
	public static boolean IpaddressCheck(String ipaddress){
		if(Network.systemIPaddress.equals(ipaddress))
			return true;
		else
			return false;
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
	 * This method receives the files.
	 * @param Ipaddress
	 * @param fileid
	 * @param f
	 * @param request
	 * @throws FileServerException 
	 * @throws ClientApiException 
	 * @throws IOException 
	 */
	public static void receiveFile(ISSFile file,File f,Confirmation userChoiceToGetFile) throws FileServerException, ClientApiException, IOException{	
			String[] fileinfo= new String[file.destIpAddress.size()+1];
			fileinfo[0]= file.fileID;
			for(int i= 1,j= 0;i < fileinfo.length;i++,j++)
				fileinfo[i]= file.destIpAddress.get(j);
			ArrayList<String[]> list= new ArrayList<String[]>();
			list.add(fileinfo);
			ReSource returnResource= Network.sendrequestToFileserver(list,ISSTask.Serve,f,userChoiceToGetFile); //need to check.
			if(returnResource != null)
				Network.receiveNow(returnResource.socket, f, returnResource.object);
	}


	/**
	 * This is for sending file from source system to the destination system.
	 * @param socket
	 * @throws FileServerException
	 * @throws ClientApiException 
	 * @throws IOException 
	 */
	public static void sendFile(String[] fileinfo,File f,Confirmation userflag)throws FileServerException, ClientApiException, IOException{
		ArrayList<String[]> list= new ArrayList<String[]>();
		list.add(fileinfo);
		ReSource returnResource= Network.sendrequestToFileserver(list,ISSTask.Receive,f,userflag);
		if(returnResource != null){
			Network.sendNow(returnResource.socket, f, returnResource.object);
		}
	}


	/**
	 * This method is for deleting the files which are present in the different nodes.
	 * It connects to the destination node and sends arraylist(String[]) which will have the files details to delete.
	 * @param list
	 */
	public static void performDeleteAction(ArrayList<String[]> list){
		for(String[] listinfo: list){
			for(int i= 1;i < listinfo.length;i++){  //[0] is id.Remaining Ipaddresses.
				String[] info= new String[2];
				info[0]= listinfo[0];
				info[1]= listinfo[i];
				arrangeIPaddresses(info);
			}
		}
		for(int i= 0;i < getFilesAccToIp().keySet().size();i++){   //This gets the all the elements of the key.
			ArrayList<String[]> deletelist= null;
			String Ipaddress= (String) getFilesAccToIp().keySet().toArray()[i];
			deletelist= getFilesAccToIp().get(Ipaddress);
			try{
				Socket client= new Socket(Ipaddress,1234);
				ObjectOutputStream out= new ObjectOutputStream(client.getOutputStream());
				out.writeObject(new RequestToFileServer(deletelist,ISSTask.Delete));  //deletelist.get(i)[0] fileId,[1] is Ipaddress.
				out.flush();
				out.close();
			}catch(ConnectException e){
				Client.delete_list.add(deletelist);
				KeepData.WriteDeleteData();
			}catch(IOException io){
				ClientExceptions.defaultException1("Error occured while deleting: " + io.getMessage());
			}catch(Exception e){
				ClientExceptions.defaultException1("Error occured while deleting: " + e.getMessage());
			}
		}
	}
}
