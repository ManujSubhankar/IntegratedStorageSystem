package iss.metadataserver;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerShell implements Runnable
{
	ISSCommunicator comm;
	Scanner sn;
	String query;
	String[] vec;
	
	public ServerShell(ISSCommunicator comm) throws InterruptedException
	{
		sn= new Scanner(System.in);
		this.comm= comm;
		Thread th= new Thread(this);
		th.start();
	}

	public void run() 
	{
		while(true)
		{
			System.out.print("ISSShell# ");
			query= sn.nextLine();
			vec= query.split("\\s+");
			if(vec.length == 0)
				continue;
			switch(vec[0])
			{
			case "help":
				help();
				break;
			case "lh":
				hostInfo();
				break;
			case "pending":
				pending();
				break;
			case "space":
				space();
				break;
			case "printTree":
				printTree();
				break;
			case "setSize":
				setSize();
				break;
			case "deleteHost":
				deleteHost();
				break;
			case "forcedDelete":
				forcedDelete();
				break;
			case "":
				break;
			default:
				System.out.println("Command not found.\nPlease use help command know about the other command.");
			}
		}
	}
	
	private void help()
	{
		try {
			BufferedReader rd= new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"/help")));
			while(true)
			{
				String str= rd.readLine();
				if(str == null)
					break;
				System.out.println(str);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void pending()
	{
		System.out.println(comm.waiting.size() + " items are pending.");
	}
	
	private void space()
	{
		System.out.println("Total Space : " + comm.getTotalSpace());
		System.out.println("Avialable Space : " + comm.getAvialableSpace());
	}
	
	private void forcedDelete()
	{
		if(vec.length != 2)
		{
			System.out.println("Command not found.\nPlease use help command know about the other command.");
			return;
		}
		try
		{
			synchronized(comm.stat.pool)
			{
				comm.stat.pool.forcedDeleteHost(vec[1]);
			}
		}catch(Exception e){System.out.println(e.getMessage());}

	}
	
	private void deleteHost()
	{
		if(vec.length != 2)
		{
			System.out.println("Command not found.\nPlease use help command know about the other command.");
			return;
		}
		try
		{
			synchronized(comm.stat.pool)
			{
				comm.stat.pool.deleteHost(vec[1]);
			}
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	
	private void hostInfo()
	{
		String ans= "";
		ArrayList<HostInfo> temp= comm.stat.pool.getHostList();
		for(int i= 0; i < temp.size(); i++)
		{
			System.out.println(temp.get(i).hostName + "     " + temp.get(i).totalSpace + "     " +temp.get(i).availableSpace);
		}
	}
	
	private void setSize()
	{
		try
		{
			if(vec.length < 3)
			{
				System.out.println("Wrong command.\nPlease use help command know about the other command.");
				return;
			}
			comm.stat.pool.modifyHostSpace(vec[1], Long.parseLong(vec[2]));
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	
	private void printTree()
	{
		System.out.println(comm.tree.toString());
	}
	
}
