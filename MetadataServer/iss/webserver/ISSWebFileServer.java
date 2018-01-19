package iss.webserver;
import iss.FileType;
import iss.ISSFile;

import java.io.*;
import java.net.Socket;

import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class ISSWebFileServer extends HttpServlet
{

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		try{
		ISSConnect con= new ISSConnect();
		String c="";
		String b= new String("");
		ISSFile f= null;
		b= request.getParameter("module");
		if(b.equals("view"))
		{
			c= request.getParameter("view");
			f= con.getISSFile(c);
		}
		else
		{
		    b= request.getParameter("sethid");
		    if(b != null)
		    {
		    	if(b.equals("true"))
		    		c= request.getParameter("hidpath");
		    	else
		    		c= request.getParameter("path");
		    }
		    else
		    	c= request.getParameter("path");
		    f= con.getISSFile(c);
		}
		
		if(f.getFileType().equals(FileType.RegularFile))
		{
			InputStream in= null;
			Socket fi = null;
			
				try {
					fi= con.getISSToLocalWeb(f);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			in= fi.getInputStream();
			OutputStream out1= response.getOutputStream();
			String t= con.getIconPath(f);
			if(t.equals("default"))
				response.setContentType("text");
			else
				response.setContentType(t);
			byte[] buf= new byte[4096000];
			int i= 0;
		    while(true)
		   	{
		    	i= in.read(buf);
			    if(i == -1)
			      break;
			    out1.write(buf,0,i);
			}	
		    in.close();
		    out1.close();
		}
		}catch(Exception e){/*p.println("Socket is fine...." + e.getMessage());*/}
		
	}

}
