package iss.client;


@SuppressWarnings("serial")
public class ClientApiException extends Exception{
	private String ClientMSG= null;
	
	public ClientApiException(String MSG){
		ClientMSG= MSG;
	}
	
	public String toString(){
		return "ClientApiException: [" + ClientMSG +"]"; 
	}
	
	public String getMessage(){
		return "ClientApiException: [" + ClientMSG +"]"; 
	}
}
