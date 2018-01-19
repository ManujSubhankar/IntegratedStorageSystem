package iss;
import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class ISSFile implements Serializable, Comparable<ISSFile>
{
	public String name, fileID;
	public ArrayList<String> destIpAddress= new ArrayList<String>();                 
	public FileType fileType;                                 //folder or file
	public long size;                                         //in bytes
	public CompareParameter parameter= CompareParameter.NAME;
	
	public void setParameter (CompareParameter parameter) 
	{
		this.parameter= parameter;
	}
	
	public FileType getFileType() 
	{
		return fileType;
	}
	
	public static enum CompareParameter {
		NAME, SIZE, TYPE, MODIFICATION;
	}
	
	private String getExtentionForFilename(String filename) {
		int ind= filename.lastIndexOf('.');
		return ind == -1 ? "" : filename.substring(ind + 1);
	}
	
	
	
	public ISSFile(String n, String id, String dest, long s)
	{
		name= n;
		fileID= id;
		destIpAddress.add(dest);
		size= s;
	}
	
	@SuppressWarnings("unchecked")
	public ISSFile(String n, String id, ArrayList<String> dest, long s)
	{
		name= n;
		fileID= id;
		destIpAddress= (ArrayList<String>) dest.clone();
		size= s;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ISSFile clone()
	{
		ISSFile ans= new ISSFile(name,fileID,destIpAddress,size);
		ans.fileType= fileType;
		return ans;
	}
	
	public int compareTo(ISSFile file) {
		int c= 0;
		if (!this.fileType.equals(file.fileType)) {
			return fileType.equals(FileType.Directory) ? -1 : 1;
		}
		switch (parameter) {
		case NAME:
			return this.name.compareTo(file.name);
		case SIZE:
			c= ((Long)size).compareTo(file.size);
			if (c == 0)
				return name.compareTo(file.name);
			return c;
		case TYPE:
			c= getExtentionForFilename(name).compareTo(getExtentionForFilename(file.name));
			if (c == 0)
				return name.compareTo(file.name);
			return c;
		case MODIFICATION:
			return name.compareTo(file.name);
		}
		return 0;
	}
	
}
