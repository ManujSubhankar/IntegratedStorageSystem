package iss.metadataserver;
import iss.FileType;
import iss.ISSFile;

import java.io.*;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class ISSTree implements Serializable
{
	ISSNode head= new ISSNode("/", "0", "null" ,0);
	ISSStat stat;
	ISSTree(ISSStat st)
	{
		stat= st;
		head.setFileType(FileType.Directory);
	}


	/**Add the given node(child) to the ISSTree under the given parent node(parent)
	 * 
	 * @param parent the node under which the child node is to be added
	 * @param child the node which is to be added under the parent
	 * @author Manuj Subhankar Sahoo
	 */
	boolean addNode(ISSNode parent, ISSNode child)
	{
		if(parent.getFile().fileID == null)
			return false;
		child.setParent(parent);
		if(!parent.getValidity())
			return false;
		parent.addChildren(child);
		changeSize(parent, child.getFile().size);
		return true;
	}


	/**
	 * Remove the given node from the children list of its parent and
	 * updates the size of the parent. 
	 * @param node which is to be removed.
	 */
	void removeChild(ISSNode node)
	{
		ArrayList<ISSNode> temp= node.getParent().getChildren();
		int s= temp.size(),i= 0;
		for(; !temp.get(i).getFile().name.equals(node.getFile().name) && i < s; i++);
		if(i == s)
			throw new RuntimeException("Not able to move the file.");
		changeSize(node.getParent(), -node.getFile().size);
		temp.remove(i);	
	}


	/**
	 * Deletes the given node and is children from the tree,
	 * updates the host size which the node has and update the size of its parent.
	 * @param node which is to be deleted.
	 */
	boolean deleteNode(ISSNode node,ArrayList<String[]> children)
	{
			int s= node.getChildren().size();
			for(int i= 0; i < s; i++)			//deleting sud nodes     
				deleteNode(node.getChildren().get(0),children);
			if(!node.getFile().fileID.equals("0"))
			{
				ArrayList<String> temp= node.getFile().destIpAddress;
				int size= temp.size();
				String[] str= new String[size + 1];
				str[0]= node.getFile().fileID;
				for(int i= 0;i < size; i++)
					str[i+1]= temp.get(i);
				children.add(str);
				stat.deleteFileId(node.getFile().fileID);
				stat.hostSpaceUpdater(node.getFile().destIpAddress.get(0), node.getFile().size);
				stat.hostSpaceUpdater(node.getFile().destIpAddress.get(1), node.getFile().size);
			}
			else
			{
				stat.deleteFileId(node.getFile().fileID);
				node.disvalidate();
			}
			ArrayList<ISSNode> temp= node.getParent().getChildren();
			int i= 0;
			s= temp.size();
			for(; !temp.get(i).getFile().name.equals(node.getFile().name) && i < s; i++) {}
			if(i == s)
				return false;
			changeSize(node.getParent(), -node.getFile().size);
			temp.remove(i);
			return true;
	}

	/**
	 * This method changes the size the file represented by the particular node
	 * @param node ISSNode whose file size is to be changed.
	 * @param size is the amount by which the size will be increased. 
	 */
	void changeSize(ISSNode node, long size)
	{
		if(node == null)
			return;
		changeSize(node.getParent(), size);
		node.setSize(node.getFile().size + size);
	}

	String getPath(ISSNode node)
	{
		//System.out.println(node.getFile().name);
		if(node.getParent() == null)
			return("");
		return(getPath(node.getParent()) + "/" + node.getFile().name);
	}


	public ArrayList<ISSNode> getChildren(ISSNode node)
	{
		return node.getChildren();
	}

	private String getString(ISSNode node, int space, String str)
	{
		String temp= "\n";
		for(int i= 0; i < space; i++)
			temp= temp.concat(" ");
		temp= temp.concat("->" + node.getFile().name);
		str= str.concat(temp);
		int s= node.getChildren().size();
		for(int i= 0; i < s; i++)
			str= getString(node.getChildren().get(i), space + 3, str);
		return str;
	}

	public String toString()
	{
		return getString(head, 0, "");
	}

	public void search(String element, String path, final ArrayList<ISSFile> ans, ISSNode search)
	{
		if (!path.endsWith("/")) path= path + "/";
		element= element.toLowerCase();
		final ArrayList<ISSNode> temp= search.getChildren();
		for(int i= 0; i < temp.size(); i++)
		{
			final int index= i;
			final ISSFile t= temp.get(i).getFile();
			if(t.name.toLowerCase().contains(element))
			{
				ISSFile f= t.clone();
				//if (!path.endsWith("/")) path+= "/";
				f.name= path + t.name;
				ans.add(f);
			}

			if(t.fileType.equals(FileType.Directory)) {
				//if (!path.endsWith("/")) path+= "/";
				search(element,path+t.name+"/",ans,temp.get(index));
			}
		}
	}

	/**
	 * Traverse the tree according to the path.
	 * @param path to be traversed
	 * @return ISSNode representing the path.
	 */
	public ISSNode Traverse(String path)
	{
		if(path.equals("/")) return head;
		ISSNode temp;
		String[] str= path.split("/");
		int ps= str.length;
		if(ps == 0)
			throw new RuntimeException(path + " path is not found.");
		temp= head;
		int	fs,i,j;
		if(str[0].equals(""))
			i= 1;
		else
			i= 0;
		ArrayList<ISSNode> chil;
		for(;i < ps; i++)             //starts from 1 because '/' at starting has no value
		{
			chil= temp.getChildren();
			fs= chil.size();
			for(j= 0; j < fs; j++)
			{
				if(chil.get(j).getFile().name.equals(str[i]))
				{
					temp= chil.get(j);
					break;
				}
			}
			if(j == fs)
			{
				throw new RuntimeException(path + " path is not found.");
			}
		}
		return temp;
	}

	public static void main(String[] args) 
	{

	}

}

