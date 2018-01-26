package iss.user;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserConnect extends Client
{
	static String icons= "";
	Client cli;
	
	public UserConnect() throws MalformedURLException, RemoteException, NotBoundException
	{
		super();
		cli= new Client();
		cli.getConnected("192.168.34.113");
		File f= new File("/home/bca3/ApacheTomcat/apache-tomcat-7.0.47/webapps/ROOT/icons");
		String[] temp= f.list();
		for(int i= 0; i < temp.length; i++)
			icons= icons.concat("`"+temp[i].concat("~"));
	}
	
  	
	
	public String getName(BSFSFile f)
	{
		if(f.fileType.equals(FileType.Directory))
			return f.name.concat("/");
		return f.name;
	}
	
	public String getIconPath(BSFSFile n)
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
}
