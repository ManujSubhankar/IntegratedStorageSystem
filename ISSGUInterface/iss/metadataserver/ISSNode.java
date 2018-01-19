package iss.metadataserver;
import iss.FileType;
import iss.ISSFile;

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ISSNode implements Serializable
{
	private ArrayList<ISSNode> children= new ArrayList<ISSNode>();
	private ISSNode Parent;
	private ISSFile file;
	
	public ISSNode(String name, String fileID, String host, long size)
	{
		file= new ISSFile(name,fileID,host,size);
	}
	
	public ISSNode(String name, String fileID, ArrayList<String> host, long size)
	{
		file= new ISSFile(name,fileID,host,size);
	}
	
	public ISSFile getFile()
	{
		return this.file.clone();
	}
	
	public synchronized void addFileDestinationAddress(String address)
	{
		file.destIpAddress.add(address);
	}
	
	public synchronized void setName(String name)
	{
		file.name= name;
	}
	
	public synchronized void removeChild(int index)
	{
		children.remove(index);
	}
	
	public synchronized ISSNode getChild(int index)
	{
		return children.get(index);
	}
	
	public synchronized void setFileId(String id)
	{
		file.fileID= id;
	}
	
	public synchronized void setFileType(FileType type)
	{
		file.fileType= type;
	}
	
	public synchronized void addChildren(ISSNode node)
	{
		children.add(node);
	}
	
	public synchronized void addChildren(int index, ISSNode node)
	{
		children.add(index, node);
	}
	
	public ArrayList<String> getFileDestinations()
	{
		return (ArrayList<String>) file.destIpAddress.clone();
	}
	
	public String getFileId()
	{
		return file.fileID;
	}
	
	public ISSNode getParent()
	{
		return this.Parent;
	}
	
	public boolean getValidity()
	{
		if(this.children != null)
			return true;
		return false;
	}
	
	public synchronized void setSize(long size)
	{
		file.size= size;
	}
	
	public void disvalidate()
	{
		this.children= null;
	}
	
	public ArrayList<ISSNode> getChildren()
	{
		return children;
	}
	
	public synchronized void setParent(ISSNode node)
	{
		this.Parent= node;
	}
	
	
	public boolean containsFile(String name)
	{
		ArrayList<ISSNode> temp= this.children;
		for(int i= 0; i < temp.size(); i++)
		{
			if(temp.get(i).file.name.equals(name))
				return true;
		}
		return false;
	}

	
	
	public ISSNode clone()
	{
		ISSNode ans= new ISSNode(file.name, file.fileID, file.destIpAddress, file.size);
		ans.file.fileType= this.file.fileType;
		ans.Parent= this.Parent;
		return ans;
	}
	
	public ArrayList<ISSFile> getFiles()
	{
		ArrayList<ISSFile> ans= new ArrayList<ISSFile>();
		int s= children.size();
		for(int i= 0; i < s; i++)
			ans.add(children.get(i).file.clone());
		
		return ans;
	}
	
	@Override
	public String toString() {
		String str= "ISSNode [name=" + file.name ;
		if(this.Parent != null)
			str= str + ", Parent=" + Parent.file.name;
		str= str + ", fileID="
				+ file.fileID + ", destIpAddress=" + file.destIpAddress + ", fileType="
				+ file.fileType + ", size=" + file.size + "]";
		return str;
	}
		

}
