package iss.webserver;
import iss.FileType;
import iss.ISSFile;
import iss.client.Status;
import iss.metadataserver.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;



@SuppressWarnings("serial")
public class ControllorServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		ISSConnect con = null;
		try {
			con = new ISSConnect();
		} catch (NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//PrintWriter w= response.getWriter();
		String clip= request.getParameter("clip");
		String c= null;
		String message= null;
		ISSFile f= null;
		String b= new String("");
		b= request.getParameter("module");
		if(b != null)
		{
			try
			{
				if(b.equals("view"))
				{
					String[] path= request.getParameter("view").split("/");
					request.getRequestDispatcher("/sairam/" + path[path.length - 1]).forward(request,response);
					return;
				}
				else if(b.equals("CreateFolder"))
				{
					if(con.contains(request.getParameter("hidpath"),request.getParameter("view")))
						message= "Aready there is a file/folder with same name.";
					else
						con.createFolder(request.getParameter("hidpath"),request.getParameter("view"));
				}
				else if(b.equals("delete"))
				{
					String hid= request.getParameter("hidpath");
					ArrayList<ISSFile> file= new ArrayList<ISSFile>();
					file.add(con.getISSFile(request.getParameter("view")));
					con.deleteFromISS(hid, file);
					//w.println(hid+request.getParameter("view"));return;
				}
				else if(b.equals("rename"))
				{
					b= request.getParameter("sethid");
					if(con.contains(request.getParameter("view"), b))
						message= "Aready there is a file with same name.";
					else
						con.renameFile(request.getParameter("view"), b);
					//PrintWriter w= response.getWriter();
				//	w.println(b);
					//return;
				}
				else if(b.equals("paste"))
				{
					ArrayList<ISSFile> fi= new ArrayList<ISSFile>();
					String path= request.getParameter("clip");
					String[] action= path.split("!-@&.><");
					fi.add(con.getISSFile(action[0]));
					String[] temp= action[0].split("/");
					if(con.contains(request.getParameter("hidpath"), temp[temp.length-1]))
						message= "Already a file exists with same name.";
					else if(request.getParameter("hidpath").startsWith(action[0]))
						message= "Move is not possible. Destination is the child of sorce:";
					else
					{
						message= "came";
						if(action[action.length - 1].equals("copy"))
						{
							message= "Copied the file successfully.";
							Status th= con.storeISStoISS(con.Back(action[0]), fi, request.getParameter("hidpath"));
							th.getThread().join();
							Thread.currentThread().sleep(500);
							clip= "";
						}
						else if(action[action.length - 1].equals("cut"))
						{
							message= "came to File successfully moved.";
							Thread th= con.moveFile(con.Back(action[0]), fi, request.getParameter("hidpath"));
							th.join();
							Thread.currentThread().sleep(500);
							clip= "";
						}
					}
				}
			}catch(Exception e){ message= e.toString(); }
		} 
		request.setAttribute("clip", clip);
		b= request.getParameter("sethid");
		if(b != null)
		{
			if(b.equals("true"))
				c= request.getParameter("view");
			else if(b.equals("false"))
				c= request.getParameter("path");
			else
			{
				//w.println(b);return;
				c= request.getParameter("hidpath");
			}
		}
		else
			c= request.getParameter("path");

		if(c == null)
		{
			c= "/";
			f= con.getISSFile(c);
		} 
		else
		{
			if(!c.endsWith("/"))
				c= c.concat("/");
			try{
				f= con.getISSFile(c);
			}catch(Exception e){c= request.getParameter("last");f= con.getISSFile(c);}
		}

		if(f.getFileType().equals(FileType.RegularFile))
		{
			try 
			{
				request.getRequestDispatcher("/sairam").forward(request,response);
				return;
			}catch(Exception e){c= request.getParameter("last");f= con.getISSFile(c);}
		}

		b= request.getParameter("module");
		if(b != null)
			if(b.equals("back")) c= con.Back(c);

		request.setAttribute("show", c);
		request.setAttribute("message", message);
		request.getRequestDispatcher("ISS.jsp").forward(request, response);
	}
}


