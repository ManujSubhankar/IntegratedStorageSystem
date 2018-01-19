package iss.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import iss.metadataserver.*;
import iss.*;
public class Network {
	private final static String ConnectionEx= "File server is not Running.Few files won't be copied.\n";
	@SuppressWarnings("unused")
	private final static String classNotFound= "Required classes are not found in the package.";
	private final static String IOException= "Problem occured during tranffering the file.Try Again.";
	
	static String systemIPaddress= null;
	static String BroadcastAddress= getBroadcastAddress();
	final static byte Process= 1;
	final static byte fileNotFound= 2;
	final static byte MulformedRequest= 3;
	final static int BUFFER= 1024000;
	final static int PORT= 1234;
	
	
	public static void sendNow(Socket socket,File f,ObjectOutputStream object) throws FileServerException, ClientApiException{
		try{
			ObjectInputStream objectstream= new ObjectInputStream(socket.getInputStream());
			ReadData readdata= (ReadData)objectstream.readObject();
			if(readdata.Head == Process){
				BufferedOutputStream forWriting= new BufferedOutputStream(socket.getOutputStream(),BUFFER);
				if(!f.exists()) 
					throw new ClientApiException("Path not found.");
				FileInputStream rd_file= new FileInputStream(f.getAbsolutePath());
				int read;
				byte b[]= new byte[BUFFER];
				while((read= rd_file.read(b)) != -1)
					forWriting.write(b,0,read);
	
				rd_file.close();
				forWriting.close();
				objectstream.close();
				if(object != null)
					object.close();
				socket.close();
			}else{
				objectstream.close();
				if(object != null)
					object.close();
				socket.close();
				throw new FileServerException(readdata.Message);
			}
		}catch(ConnectException con){
			throw new ClientApiException(ConnectionEx);
		}catch(IOException io){
			throw new ClientApiException(IOException);
		} catch (ClassNotFoundException e) {
			throw new ClientApiException(classNotFound);
		}
	}
	
	
	public static void receiveNow(Socket socket,File f,ObjectOutputStream object) throws FileServerException, ClientApiException, java.io.IOException{
		try{
			ObjectInputStream readmsg= new ObjectInputStream(socket.getInputStream());
			ReadData data= (ReadData) readmsg.readObject();
			if(data.Head == Process){
				BufferedInputStream rd_data= new BufferedInputStream(socket.getInputStream(),BUFFER);
				FileOutputStream wt_fl= new FileOutputStream(f.getAbsolutePath());
				byte b[]= new byte[BUFFER];
				int read;
				while((read = rd_data.read(b)) != -1) 
					wt_fl.write(b,0,read);
				rd_data.close();
				wt_fl.close();
				readmsg.close();
				if(object != null)
					object.close();
				socket.close();
			}else{
				readmsg.close();
				object.close();
				socket.close();
				throw new FileServerException(data.Message);
			}
		} catch (ClassNotFoundException e) {
			throw new ClientApiException(classNotFound);
		}
	}
	/**
	 * This method is connects to the Fileserver and sends request of required task to perform.
	 * If the request is not proceeds then it keeps that data and does the job background.
	 * This method for getting the file if the first destination node is not running then it connects to the
	 * Another fileserver where the Mirroring data is sitting.
	 * So this method request File server to perform some task.
	 * @param Ipaddress
	 * @param filename
	 * @param fileid
	 * @param request
	 * @param Cloudpath
	 * @return RetunType
	 * @throws java.io.IOException 
	 * @throws ConnectException
	 * @throws IOException
	 * @throws ClientApiException 
	 */
	public static ReSource sendrequestToFileserver(ArrayList<String[]> file,ISSTask request, File f, Confirmation userflag) throws java.io.IOException{
		int length= 2;
		if(request == ISSTask.Serve)
			length= file.get(0).length;
		
		for(int i= 1;i < length;i++){
			try{
				Socket socket= new Socket(file.get(0)[i],PORT);
				ObjectOutputStream WriteRequest= new ObjectOutputStream(socket.getOutputStream());
				RequestToFileServer fs_req= new RequestToFileServer(file,request);
				WriteRequest.writeObject(fs_req);
				WriteRequest.flush();
				return new ReSource(socket, WriteRequest);  //This returns the reference to the socket and the object of the Outputobjectstream so it can be closed..
			}catch(ConnectException e){
				ClientExceptions.defaultException1(e.getMessage());
				/*if(request != ISSTask.Delete && request != ISSTask.Serve){
					if(userflag.user == UserInteraction.skip)
						userflag.user= ClientExceptions.WaitingListToUserConfirm(); //Returns true if user wants to copy background.
					if(f != null && userflag.user == UserInteraction.RunBackGround){ //If because 1st request shoud get in to list.
						Client.store_list.add(new MakeList(f,file));
						KeepData.WriteStoreData();
					}
				}*/
			}
		}
		if(request == ISSTask.Serve){
			if(userflag.user == UserInteraction.skip)
				userflag.user= ClientExceptions.WaitingListToUserConfirm();
			f.delete();
			if(userflag.user == UserInteraction.RunBackGround)
				Client.getFile_list.add(new MakegetFileList(f, file));
			RemoteMethods.InvokeRemoteBoot(Client.ConnectMetaServer.getMacAdd(file.get(0)[1]), Network.BroadcastAddress);
		}
		return null;
	}

	/**
	 * This method takes the source and destination path from where to where the file or folder need to be copyied.
	 * This method connects to the both the Fileserver from where to fetch and where the file need to stored
	 * and transfers the file.
	 * @param destinationPath
	 * @param sourcePath
	 * @param bsfsobject
	 * @throws IOException 
	 * @throws ConnectException 
	 * @throws ClassNotFoundException 
	 * @throws FileServerException
	 * @throws ClientApiException 
	 */
	public static void cloudToCloudCopy(String destinationPath,String sourcePath,ISSFile bsfsobject) throws FileServerException, ClientApiException{
		try{
			String[] fileinfo= new String[bsfsobject.destIpAddress.size()+1];
			fileinfo[0]= bsfsobject.fileID;
			for(int i= 1,j= 0;i < fileinfo.length;i++,j++)
				fileinfo[i]= bsfsobject.destIpAddress.get(j);
			
			ArrayList<String[]> list= new ArrayList<String[]>();
			list.add(fileinfo);
			Socket socket= new Socket(list.get(0)[1],PORT);
			ObjectOutputStream WriteRequest= new ObjectOutputStream(socket.getOutputStream());
			RequestToFileServer fs_req= new RequestToFileServer(list,ISSTask.Serve);
			WriteRequest.writeObject(fs_req);
			WriteRequest.flush();
			ObjectInputStream receiveob= new ObjectInputStream(socket.getInputStream());
			ReadData receiveData= (ReadData) receiveob.readObject();
			if(receiveData.Head == Process){   //This is the ack check.Ack is given by the Fileserver..This requesting fileserver gives the data.
				String[] Fileinfo= Client.ConnectMetaServer.storeFile(destinationPath, bsfsobject.name, bsfsobject.size);
				ArrayList<String[]> list1= new ArrayList<String[]>();
				list1.add(Fileinfo);
				Socket socketreceive= new Socket(list1.get(0)[1],1234);
				ObjectOutputStream WriteRequestToreceive= new ObjectOutputStream(socketreceive.getOutputStream());
				RequestToFileServer fs_req_receive= new RequestToFileServer(list1,ISSTask.Receive);
				WriteRequestToreceive.writeObject(fs_req_receive);
				WriteRequestToreceive.flush();
				ObjectInputStream receivest= new ObjectInputStream(socketreceive.getInputStream());
				ReadData Data= (ReadData) receivest.readObject();
				if(Data.Head == Process){  //This is ack from the Fileserver.This is for destination fileserver where data need to stored.
					BufferedInputStream readD= new BufferedInputStream(socket.getInputStream(),4096000);
					BufferedOutputStream writeD= new BufferedOutputStream(socketreceive.getOutputStream(),4096000);
					byte[] b= new byte[BUFFER];
					int read= 0;
					while((read= readD.read(b)) != -1)
						writeD.write(b,0,read);
					
					readD.close();
					writeD.close();
					socketreceive.close();
					WriteRequest.close();
					WriteRequestToreceive.close();
					socket.close();
				}else{
					receivest.close();
					socketreceive.close();
					WriteRequest.close();
					WriteRequestToreceive.close();
					socket.close();
					throw new FileServerException(Data.Message);
				}
			}else{
				receiveob.close();
				WriteRequest.close();
				socket.close();
				throw new FileServerException(receiveData.Message);
			}
		}catch(ConnectException con){
			throw new ClientApiException(ConnectionEx);
		}catch(IOException io){
			throw new ClientApiException(IOException);
		} catch (ClassNotFoundException e) {
			throw new ClientApiException(classNotFound);
		}

	}

	/**
	 * This takes the argument of Input Stream And read From it and writes to the File server.
	 * @param in
	 * @param Ipaddress
	 * @throws IOException 
	 * @throws ConnectException 
	 * @throws ClassNotFoundException 
	 * @throws FileServerException 
	 * @throws ClientApiException 
	 */
	public static void sendFromBrowser(InputStream in,String[] Fileinfo) throws FileServerException, ClientApiException, ClassNotFoundException{
		try{
			ArrayList<String[]> list= new ArrayList<String[]>();
			list.add(Fileinfo);
			ReSource returnResource= Network.sendrequestToFileserver(list,ISSTask.Receive,null,null);
			ObjectInputStream receive= new ObjectInputStream(returnResource.socket.getInputStream());
			ReadData readdata= (ReadData)receive.readObject();
			if(readdata.Head == Process){
				BufferedOutputStream writeToFS= new BufferedOutputStream(returnResource.socket.getOutputStream(),4096000);
				int read;
				byte b[]= new byte[BUFFER];
				while((read= in.read(b)) != -1) 
					writeToFS.write(b,0,read);
				
				writeToFS.close();
				in.close();
			}
			returnResource.object.close();
			returnResource.socket.close();
		}catch(ConnectException con){
			throw new ClientApiException(ConnectionEx);
		}catch(IOException io){
			throw new ClientApiException(IOException);
		}

	}

	
	/**
	 * This Function is used to Download the file from the Cloud.
	 * This takes Output stream as a parameter and write data to it by reading from the File Server.
	 * @param out_stream
	 * @param key
	 * @param IPaddress
	 * @throws IOException 
	 * @throws FileServerException
	 * @throws ClassNotFoundException 
	 * @throws java.io.IOException 
	 */
	public static Socket Download(ISSFile file) throws FileServerException,ClientApiException, ClassNotFoundException, java.io.IOException{
		try{
			String[] fileinfo= new String[file.destIpAddress.size()+1];
			fileinfo[0]= file.fileID;
			for(int i= 1,j= 0;i < fileinfo.length;i++,j++)
				fileinfo[i]= file.destIpAddress.get(j);
			ArrayList<String[]> list= new ArrayList<String[]>();
			list.add(fileinfo);
			Socket socket= new Socket(list.get(0)[1],1234);
			ObjectOutputStream out= new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new RequestToFileServer(list, ISSTask.Serve));
			out.flush();
			ObjectInputStream in= new ObjectInputStream(socket.getInputStream());
			ReadData readdata= (ReadData)in.readObject();
			if(readdata.Head == Process)
				return socket;
			else
				throw new FileServerException(readdata.Message);
		}catch(ConnectException con){
			throw new FileServerException(ConnectionEx);
		}catch(IOException io){
			throw new ClientApiException(IOException);
		}
	}

	
	public static String getBroadcastAddress(){
		NetworkInterface Ip;
		try{
			 return NetworkInterface.getByName("eth0").getInterfaceAddresses().get(1).getBroadcast().toString().split("/")[1];
		}catch(Exception e){
			ClientExceptions.defaultException1(e.getMessage());
		}
		return null;
	}
	
	/**
	 * This class is for collecting the used resources and releasing when the work is done.
	 * @author bca3
	 */
	static class ReSource{
		Socket socket;
		ObjectOutputStream object;
		ReSource(Socket con,ObjectOutputStream ob){
			socket= con;
			object= ob;
		}
	}
	
	static class MakeList implements Serializable{
		File file= null;
		ArrayList<String[]> Info= null;
		
		MakeList(File f,ArrayList<String[]> list){
			file= f;
			Info= list;
		}
	}
	
	static class MakegetFileList{
		File file= null;
		ArrayList<String[]> getFileInfo= null;
		
		public MakegetFileList(File f, ArrayList<String[]> list) {
			file= f;
			getFileInfo= list;
		}
	}
	
	static class MyBoolean{
		boolean value;
		
		MyBoolean(boolean flag){
			this.value= flag;
		}

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}
	}
}
