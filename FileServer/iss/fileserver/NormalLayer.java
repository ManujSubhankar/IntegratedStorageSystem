package iss.fileserver;
import iss.*;
import iss.metadataserver.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class NormalLayer {
	/**
	 * This Method Perform the task according to the request.
	 * @param socket
	 * @param ISSDelegate 
	 * @param fileinfo
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void Performaction(Socket socket,Object req) throws UnknownHostException, IOException{
		if(req instanceof RequestToFileServer){
			RequestToFileServer request= (RequestToFileServer)req;
			Fileserver.selectAction(socket, request.Request, request.Identification, request.Identification.get(0)[0],true);
		}else if(req instanceof RecognizeObject){
			RecognizeObject request= (RecognizeObject) req;
			Fileserver.selectAction(socket, request.action, request.delete,request.FileID,false);
		}else{
			ObjectOutputStream out= new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new ReadData(Fileserver.MulformedRequest, Fileserver.Mulformed));
			out.flush();
			out.close();
		}
	}
	
	
	/**
	 * Receives the object from the client.
	 * In case of mirroring the file it will accepts the request from the Fileserver.
	 * It checks the whether the request is from the client or the Fileserver.
	 * Acts accordingly.
	 * It internally calls Performaction method to perform the remaining action based on the request.
	 * @param socket
	 * @throws IOException 
	 */
	public void NonAuthenticate(Socket socket) throws IOException{
		try{
			final ObjectInputStream object= new ObjectInputStream(socket.getInputStream());
			final Object objectreceived= object.readObject();
			
			if(objectreceived instanceof RequestToFileServer) {
				RequestToFileServer request= (RequestToFileServer)objectreceived;
				Performaction(socket, request);
			}else if(objectreceived instanceof RecognizeObject){
				RecognizeObject recon= (RecognizeObject)objectreceived;
				if(recon.ThisismeServerFiles.equals("\\mirfnoc//")){
					Performaction(socket,recon);
				}else
					socket.close();
			}
			else{
				ObjectOutputStream out= new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(new ReadData(Fileserver.MulformedRequest,Fileserver.Mulformed));
				out.flush();
				out.close();
				socket.close();
			}
		}catch(Exception e){
			ObjectOutputStream out= new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new ReadData(Fileserver.MulformedRequest,Fileserver.Mulformed));
			out.flush();
			out.close();
			socket.close();
		}
	}
	

	/**
	 * This works as the upper layer.
	 * Which consists of authentication to process the request from the client.
	 */
	public void runFileServer() throws IOException{
		@SuppressWarnings("resource")
		ServerSocket serversocket= new ServerSocket(1234);
		while(true){
			try{
				final Socket socket= serversocket.accept();
				new Thread("Accepted"){
					public void run(){
						try{
							NonAuthenticate(socket); //This is for authenticating the user or client request.
						}catch(Exception e){
							throw new RuntimeException(e.getMessage());
						}
					}
				}.start();
			}catch(IOException io){
				System.out.println("Reason: " + io.getMessage());
			}
		}
}
}
