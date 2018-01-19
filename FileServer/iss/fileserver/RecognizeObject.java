package iss.fileserver;
import iss.metadataserver.*;
import java.io.Serializable;
import java.util.ArrayList;
import iss.*;
public class RecognizeObject implements Serializable{
	String ThisismeServerFiles= null;
	ISSTask action= null;
	String FileID= null;
	ArrayList<String[]> delete= null;
	public RecognizeObject(String fileid,ISSTask task,ArrayList<String[]> list) {
		ThisismeServerFiles= "\\mirfnoc//";
		action= task;
		FileID= fileid;
		delete= list;
	}
}

