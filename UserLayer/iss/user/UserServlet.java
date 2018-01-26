package iss.user;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import iss.ISSFile;


public class UserServlet extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		UserConnect con = null;
		Object o= null;
		String n= request.getParameter("name");
		if(n != null)
			try {
				con= new UserConnect();
				o= con.login(n, request.getParameter("pass"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if(o == null)
		{
			request.getRequestDispatcher("/user/login.jsp").forward(request,response);
			return;
		}
		HttpSession sec= request.getSession();
		sec.setAttribute("sec", o);
		
		
		/*if(sec.getAttribute("some_token")==null)
			  response.sendRedirect("/user/login.jsp");*/
		
		String c= null;
		String message= null;
		ISSFile f= null;
		String b= new String("");
		b= (String)request.getAttribute("from");
		if(b == null)
		{
			b= request.getParameter("module");
			if(b != null)
			{
				try
				{
					if(b.equals("view"))
					{
						request.getRequestDispatcher("/sairam").forward(request,response);
						return;
					}
					if(b.equals("CreateFolder"))
					{
						con.createFolder(request.getParameter("hidpath"),request.getParameter("view"),o);
					}
				}catch(Exception e){ message= e.getMessage(); }
			} 

			b= request.getParameter("sethid");
			if(b != null)
			{
				if(b.equals("true"))
					c= request.getParameter("view");
				else
					c= request.getParameter("path");
			}
			else
				c= request.getParameter("path");
		}
		else
		{
			c= (String)request.getAttribute("path");
		} 
		if(c == null)
		{
			c= "/"+n;
			f= con.getFile(c,o);
		} 
		else
		{
			if(!c.endsWith("/"))
				c= c.concat("/");
			try{
				f= con.getFile(c,o);
			}catch(Exception e){c= request.getParameter("last");f= con.getFile(c,o);}
		}

		if(f.getFileType().equals(FileType.RegularFile))
		{
			try 
			{
				request.getRequestDispatcher("/sairam").forward(request,response);
				return;
			}catch(Exception e){c= request.getParameter("last");f= con.getFile(c,o);}
		}

		b= request.getParameter("module");
		if(b != null)
			if(b.equals("back")) c= con.Back(c);
		
		request.setAttribute("show", c);
		request.setAttribute("message", message);
		request.getRequestDispatcher("user.jsp").forward(request, response);
	}
}


