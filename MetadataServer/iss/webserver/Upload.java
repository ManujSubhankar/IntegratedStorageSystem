package iss.webserver;
import iss.metadataserver.*;

import java.io.*;
import java.rmi.NotBoundException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tomcat.util.http.fileupload.*;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.*;

@SuppressWarnings("serial")
public class Upload extends HttpServlet
{

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		//Thread myThread= Thread.currentThread();
		String message= null;
		String path= request.getParameter("path2");
		DiskFileItemFactory factory= new DiskFileItemFactory();
		ServletFileUpload d= new ServletFileUpload(factory);
		java.util.List<FileItem> fileItems = null;
		try 
		{
			fileItems = d.parseRequest(new ServletRequestContext(request));
		} catch (FileUploadException e) {throw new RuntimeException(e.getMessage());}

		if(path == null)
			throw new RuntimeException("the path is null");
		FileItem fi= (FileItem) fileItems.get(0);
		ISSConnect con= null;
		try {
			con = new ISSConnect();
		} catch (NotBoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			if(con.contains(path, fi.getName()))
				message=  "Aready there is a file with same name.";
			else
			{
				Thread th= con.storeFromLocalToISSWeb(fi.getInputStream(), path, fi.getName(), fi.getSize());
				th.join();
				Thread.currentThread().sleep(500);
			}
		}catch(Exception e){message= e.getMessage();};

		if(message == null)
			message= "Successfully Uploaded the file : " + fi.getName();
		request.setAttribute("message", message);
		request.setAttribute("show", path);
		request.getRequestDispatcher("ISS.jsp").forward(request, response);
	}
}
