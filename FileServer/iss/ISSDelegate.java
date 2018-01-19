package iss;

import iss.ISSFile;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ISSDelegate implements Serializable 
{
	ISSTask task;
	ISSFile file;
	ArrayList<String[]> delete= new ArrayList<String[]>();
	
	public ISSDelegate(ISSFile file,ISSTask tas)
	{
		this.file= file;
		task= tas;
	}
	
	public ISSDelegate(ArrayList<String[]> d,ISSTask tas)
	{
		task= tas;
		delete= d;
	}
}
