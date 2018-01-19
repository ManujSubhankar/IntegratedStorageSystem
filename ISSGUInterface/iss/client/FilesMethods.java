package iss.client;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import iss.client.Network.MyBoolean;
import iss.client.Network.ReSource;
import iss.metadataserver.*;
import iss.*;
public class FilesMethods {
	
	/**
	 * validates the given ISS path.
	 * @param CloudPath
	 * @return
	 * @throws ClientApiException
	 * @author P. Murali krishna
	 */
	public static String validatepath(String CloudPath) throws ClientApiException{
		if(CloudPath.charAt(CloudPath.length()-1) == '/' && CloudPath.charAt(0) == '/')
			return CloudPath.substring(0, CloudPath.length()-1);
		else if(CloudPath.charAt(0) == '/')
			return CloudPath;
		else
			throw new ClientApiException(CloudPath + ": Path not found");
	}
	
	/**
	 * Returns the size of the passed file object.
	 * @param f
	 * @return long (Size of the file)
	 * @author P. Murali krishna
	 */
	public static long getsize(File f){
		long size= 0;
		if(f.isFile())
			size+= f.length();
		else{
			File[] file= f.listFiles();
			for(File fe: file)
				if(fe.isFile())
					size+= fe.length();
				else
					size+= getsize(fe);
		}
		return size;
	}
	
	/**
	 * Updates the requested file.
	 * The updates also reflects in the ISS.
	 * @param localpath (temppath where the modified file exits)
	 * @param fileobject (Object of the ISSFile on which the update need to happen)
	 */
	public static void updatefile(File f,String ISSpath,ISSFile fileobject){
		if(f.isFile()){
			try {
				boolean check= Client.ConnectMetaServer.fileSizeUpdater(ISSpath+"/"+fileobject.name, (long)f.length());
				ArrayList<String[]> info= null;
				if(check){
					String[] fileInfo= new String[fileobject.destIpAddress.size()+1];
					fileInfo[0]= fileobject.fileID;
					for(int i= 1,j= 0;i < fileInfo.length;i++,j++)
						fileInfo[i]= fileobject.destIpAddress.get(j);
					info= new  ArrayList<String[]>();
					info.add(fileInfo);
					try {
						iss.client.Network.ReSource resource= Network.sendrequestToFileserver(info, ISSTask.Update, f, new Confirmation(UserInteraction.RunBackGround));
						Network.sendNow(resource.socket, f, resource.object);
					} catch (FileServerException | ClientApiException | IOException e) {
						ClientExceptions.defaultException1("Error occured while updating: " + e.getMessage());
					} catch(Exception e){
						ClientExceptions.defaultException1("Error occured while updating: " + e.getMessage());
					}
				}else{
					SystemFiles(f, ISSpath, new MyBoolean(false), null, new Confirmation(UserInteraction.RunBackGround));
					performTransfer.performDeleteAction(info);
					Client.ConnectMetaServer.renameFile(ISSpath, fileobject.name);
				}
			} catch (RemoteException | RuntimeException e1) {
				ClientExceptions.defaultException1(e1.getMessage());
			}
			
		}
		/**
		 * Need to correct.
		 */
	}
	
	
	/**
	 * Selects the files to store to ISS.
	 * Internally call ClientException if any exception is thrown.
	 * If the folder need to be created then it contacts the META-DATA server to create a folder in ISS. 
	 * Contacts to the META-DATA server for getting the information where to store the file and unique file id.
	 * Internally calls the performAction method to execute the remaining Action after getting the info from the META_DATA server.
	 * @param fileobject
	 * @param Cloudpath
	 * @param skipflag
	 * @param status
	 * @param userflag
	 */
	public static void SystemFiles(File fileobject,String Cloudpath, MyBoolean skipflag, Status status,Confirmation userflag){
		try{
			if(!fileobject.exists())
				ClientExceptions.defaultException(fileobject.getAbsolutePath()+ "Path not found...",status);
			else if(fileobject.isDirectory()){
				Client.ConnectMetaServer.createFolder(Cloudpath, fileobject.getName());
				Cloudpath+= "/" + fileobject.getName();
				if(!fileobject.exists()){
					if(!skipflag.isValue()){
						skipflag.setValue(ClientExceptions.defaultException(fileobject.getAbsolutePath() + "Path not found.",status));
						status.Usedsize= status.Totalsize;
						Thread.currentThread().stop();
					}
				}else{
					File[] inlistfiles = fileobject.listFiles();
					for (int i = 0; i < inlistfiles.length; i++) {
						try{
							if (inlistfiles[i].isDirectory()) {
								SystemFiles(inlistfiles[i],Cloudpath, skipflag, status,userflag); //This does recursively.
							}else if(inlistfiles[i].isFile()){
								String[] fileinfo= Client.ConnectMetaServer.storeFile(Cloudpath, inlistfiles[i].getName(), inlistfiles[i].length());
								performTransfer.sendFile(fileinfo, inlistfiles[i],userflag);
								status.Usedsize+= inlistfiles[i].length();
							}
						}catch(RemoteException e){
							status.Usedsize+= inlistfiles[i].length();
							if(!skipflag.isValue())
								skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
					}catch (FileServerException | ClientApiException | IOException e) {
						status.Usedsize+= inlistfiles[i].length();
							if(!skipflag.isValue()){
								skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
							}
						} 
					}
				}
			}
			else if(fileobject.isFile()){
				String[] fileinfo= Client.ConnectMetaServer.storeFile(Cloudpath, fileobject.getName(), fileobject.length());
				performTransfer.sendFile(fileinfo, fileobject,userflag);
				status.Usedsize+= fileobject.length();
			}
		}catch(RemoteException | RuntimeException r){
			if(!skipflag.isValue())
				skipflag.setValue(ClientExceptions.defaultException(r.getMessage(),status));  //This rmiException method called when there is exception from Meta-data server
		}catch (ClientApiException | FileServerException | IOException e) {
			if(!skipflag.isValue())
				skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status)); //This failureException method give the Client, FileServer, IOExceptions.
		}catch (Exception e){
			if(!skipflag.isValue())
				skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status)); //This is for handling the any type of exception other than the ones those are handled.
		}
	}

	/***
	 * This method is for selecting the files from cloud.
	 * @param Cloudpath
	 * @param desSystemPath
	 * @param filetype
	 * @throws RemoteException 
	 */
	public static void cloudFiles(String parentCloudpath,ISSFile ISSfileobject,String desSystemPath, MyBoolean skipflag, Status status, Confirmation userChoiceToGetFile) {
		try{
			if(ISSfileobject.fileType == FileType.Directory){
				ArrayList<ISSFile> ISSobject= Client.ConnectMetaServer.getContent(parentCloudpath);
				File f= new File(desSystemPath + File.separator + ISSfileobject.name);
				f.mkdir();
				desSystemPath+= File.separator + ISSfileobject.name;
				for(int i= 0;i < ISSobject.size();i++){
					try{
						if(ISSobject.get(i).fileType == FileType.Directory){
							cloudFiles(parentCloudpath+"/"+ISSobject.get(i).name,ISSobject.get(i), desSystemPath, skipflag, status, userChoiceToGetFile);
						}else{
							File localfile= new File(desSystemPath + File.separator + ISSobject.get(i).name);
							if(!localfile.exists()){
								localfile.createNewFile();
								performTransfer.receiveFile(ISSobject.get(i), localfile,userChoiceToGetFile);
								status.Usedsize+= ISSobject.get(i).size;
							}else{
								status.Usedsize+= ISSobject.get(i).size;
								if(!skipflag.isValue())
									skipflag.setValue(ClientExceptions.defaultException(localfile.getName() + "Already exists..",status));
							}
						}
					}catch(FileServerException | ClientApiException | IOException e){
						if(!skipflag.isValue()){
							status.Usedsize+= ISSobject.get(i).size;
							skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
						}
					}catch(Exception e){
						if(!skipflag.isValue()){
							status.Usedsize+= ISSobject.get(i).size;
							skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
						}
					}
				}
			}else{
				File localfile= new File(desSystemPath + File.separator + ISSfileobject.name);
				localfile.createNewFile();
				performTransfer.receiveFile(ISSfileobject, localfile,userChoiceToGetFile);
				status.Usedsize+= ISSfileobject.size;
			}
		}catch(FileServerException | ClientApiException | IOException e){
			if(!skipflag.isValue()){
				status.Usedsize+= ISSfileobject.size;
				skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
			}
		}catch(Exception e){
			if(!skipflag.isValue()){
				status.Usedsize+= ISSfileobject.size;
				skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
			}
		}
	}
	
	/***
	 * This method is for selecting the files from cloud.
	 * @param Cloudpath
	 * @param desSystemPath
	 * @param filetype
	 */
	public static void getFileListFromISS(String parentCloudpath,ISSFile ISSfileobject,String desCloudPath, MyBoolean skipflag, Status status){
		try{
			if(ISSfileobject.fileType == FileType.Directory){
				Client.ConnectMetaServer.createFolder(desCloudPath, ISSfileobject.name);  //Request to metadata server for creating a file.
				desCloudPath+= "/" + ISSfileobject.name;
				ArrayList<ISSFile> ISSobject= Client.ConnectMetaServer.getContent(parentCloudpath); //Request to metadata server for content(files info).
				for(int i= 0;i < ISSobject.size();i++){
					try{
						if(ISSobject.get(i).fileType == FileType.Directory){
							getFileListFromISS(parentCloudpath+"/"+ISSobject.get(i).name,ISSobject.get(i), desCloudPath, skipflag, status);
						}else{
							Network.cloudToCloudCopy(desCloudPath, parentCloudpath,ISSobject.get(i));
							status.Usedsize+= ISSobject.get(i).size;
						}
					}catch(FileServerException | ClientApiException e){
						status.Usedsize+= ISSobject.get(i).size;
						if(!skipflag.isValue())
							skipflag.setValue(ClientExceptions.defaultException(e.getMessage(),status));
					}
				}
			}else{
				Network.cloudToCloudCopy(desCloudPath, parentCloudpath,ISSfileobject);
				status.Usedsize+= ISSfileobject.size;
			}

		}catch(FileServerException | RemoteException | ClientApiException e){
			if(!skipflag.isValue()){
				status.Usedsize+= ISSfileobject.size;
				skipflag.setValue(ClientExceptions.defaultException("Exception while getting Files from ISS: " + e.getMessage(),status));
			}
		}catch(Exception e){
			if(!skipflag.isValue()){
				status.Usedsize+= ISSfileobject.size;
				skipflag.setValue(ClientExceptions.defaultException("Exception while getting Files from ISS: " + e.getMessage(),status));
			}
		}
	}
	
	
	
	/***
	 * This method is for opening the file.<li>
	 * Stores the file in temperary path.<li>
	 * If the file with same name is present.</li>
	 * It will create file by appending some number to it and creates the file.
	 * @param Cloudpath
	 * @param Localdestinationpath
	 * @param fileobject
	 * @throws RemoteException 
	 */
	public static void openfile(String Localdestinationpath,ISSFile fileobject, String ISSpath) {
		String isspath= null;
		try {
			isspath= validatepath(ISSpath);
		} catch (ClientApiException e1) {
			ClientExceptions.defaultException1(e1.getMessage());
		}
		String presentfilepath= Localdestinationpath.concat(fileobject.fileID.concat("_").concat(fileobject.name));
		String[] info= new String[3];
		info[0]= presentfilepath;
		info[1]= isspath;
		try{
			File localfile= new File(presentfilepath);
			performTransfer.receiveFile(fileobject, localfile,new Confirmation(UserInteraction.RunBackGround));
			info[2]= String.valueOf(localfile.lastModified());
			UpdateList list= new UpdateList(info,fileobject);
			synchronized (Client.update_List) {
				Client.update_List.add(list);
			}
		}catch(RemoteException e){
			ClientExceptions.defaultException1(e.getMessage());
		}catch(FileServerException | ClientApiException | IOException  f){
			ClientExceptions.defaultException1(f.getMessage());
		}
	}	
	
}


