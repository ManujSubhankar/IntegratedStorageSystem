package iss;
import iss.metadataserver.*;
import java.io.Serializable;
import java.util.ArrayList;


public class RequestToFileServer implements Serializable{
	public ISSTask Request;
	public ArrayList<String[]> Identification= null;
	
	public RequestToFileServer(ArrayList<String[]> list,ISSTask request){
		Identification= list;
		Request= request;
	}
}