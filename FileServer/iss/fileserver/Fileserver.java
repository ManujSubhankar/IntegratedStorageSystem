package iss.fileserver;
import iss.metadataserver.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import iss.*;
public class Fileserver {
		
		final static byte Process= 1;
		final static byte fileNotFound= 2;
		final static byte MulformedRequest= 3;
		
		private final static String Admin=  "please Contact Admin to solve the Problem";
		private final static String FilenotFound= "File not Found..Problem occured in Fileserver or Central server."
				+ Admin;
		private final static String Nodir= "There in no Directory in the specified path given to the fileserver.\n" +
				Admin;
		final static String Mulformed= "Mulformed request has been made.";
		public static String FileStorepath= null;
		final static int BUFFER= 1024000;
		static String Ipaddress= null;
 		public Fileserver(){}
		
		
		/**
		 * This method for action to be performed.
		 * This method has the functionality of selecting the task to be performed.
		 * @param action
		 */
		public static void selectAction(Socket socket,ISSTask action,final ArrayList<String[]> list,String fileID,boolean mirr_flag){
			switch (action) {
			case Receive:
				Network.PerformReceiveAction(socket, fileID,false,mirr_flag);
				if(mirr_flag){
					try {
						FileServermain.ConnectToMetaserver.confirmFile(fileID);
						Mirr_Perform.Mirr_Copy(list.get(0),ISSTask.Receive);
					} catch (RemoteException | RuntimeException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
			case Serve:
				Network.PerformSendingAction(socket, fileID);
				break;
			case Update:
				Network.PerformReceiveAction(socket, fileID,true,mirr_flag);  //Delete the previous and store updated file.
				if(mirr_flag)
					Mirr_Perform.Mirr_Copy(list.get(0),ISSTask.Update);
				break;
			case Delete:
				Network.performDeleteAction(list);
				break;
			default:
				throw new RuntimeException("Request connot be processed..");  //Need to think off.
			}
		}
}




