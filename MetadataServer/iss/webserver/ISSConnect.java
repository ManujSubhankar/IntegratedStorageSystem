package iss.webserver;

import iss.FileType;
import iss.ISSFile;
import iss.client.*;

import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ISSConnect extends Client
{
	static String icons= "";
	
	public ISSConnect() throws MalformedURLException, RemoteException, NotBoundException
	{ 
		super();
		this.getConnected("192.168.34.112");
		File f= new File("/home/mca2/apache-tomcat-7.0.47/webapps/ROOT/icons");
		String[] temp= f.list();
		for(int i= 0; i < temp.length; i++)
			icons= icons.concat("`"+temp[i].concat("~"));
	}
	
	public boolean contains(String path,String name) throws RemoteException, ClientApiException, RuntimeException
	{
		ArrayList<ISSFile> content= this.getContent(path);
		for(int i= 0; i < content.size(); i++)
		{
			if(content.get(i).name.equals(name))
				return true;
		}
		return false;
	}
  	
	
	public String getName(ISSFile f)
	{
		if(f.fileType.equals(FileType.Directory))
			return f.name.concat("/");
		return f.name;
	}
	
	public String getIconPath(ISSFile n)
	{
		if(n.fileID.equals("0"))
			return "folder";
		String temp[]= n.name.split("\\.");
		try
		{
			if(temp.length != 0)
			{
				String t= temp[temp.length - 1];
				t= t.toLowerCase();
				if(t.charAt(t.length() - 1) == '~')
					t= t.substring(0, t.length() - 1);
				if(icons.contains("`"+t+"~"))
					return t;
			}
		}catch(Exception e){return "default";}
		
		return "default";
	}
	
	public String formatString(String na)
	{
		String ans= "";
		int i= 0, len= na.length();
		while(true)
		{
			try{
			if(len > i + 10)
			{
				ans= ans.concat(na.substring(i , i + 10).concat("<br>"));
				i+= 10;
			}
			else
			{
				if(i == len)
					break;
				ans= ans.concat(na.substring(i, len));
				break;
			}
			}catch(Exception e){System.out.flush();throw e;}
		}
		return ans;
	}
	
	public String Back(String path)
	{
		String ans= "/";
		String[] temp= path.split("/");
		for(int i= 0; i < temp.length - 1; i++)
		{
			if(!temp[i + 1].equals("") && !temp[i].equals(""))
				ans= ans.concat(temp[i].concat("/"));
		}
		return ans;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, RuntimeException, FileServerException, ClientApiException, IOException, NotBoundException
	{
		ISSConnect con= new ISSConnect();
		System.out.println(con.Back("/ch/"));
	
	}
}
