package iss.client;

@SuppressWarnings("serial")
public class FileServerException extends Exception{
	private String Message= null;
	
	public FileServerException(String MSG) {
		Message= MSG;
	}
	
	public String toString(){
		return "FileServerException: [" + Message +"]"; 
	}
	
	public String getMessage(){
		return "FileServerException: [" + Message +"]";
	}
}
