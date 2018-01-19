package iss;

import java.io.Serializable;

public class ReadData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte Head= 0;
	public String Message= null;
	
	public ReadData(byte head,String msg){
		Head= head;
		Message= msg;
	}
	
}
