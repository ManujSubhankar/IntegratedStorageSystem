package iss.metadataserver;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class FileId implements Serializable
{
	String fileId;
	ArrayList<String> hostIpAddress= new ArrayList<String>();
	
	FileId(String file, String host)
	{
		fileId= file;
		hostIpAddress.add(host);
	}
	
	
}
