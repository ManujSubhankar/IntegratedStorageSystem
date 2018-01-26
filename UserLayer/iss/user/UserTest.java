package iss.user;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UserTest {
	public static void main (String[] args) {
		UserAPI remote = null;
		try {
			remote= UserGateway.getInstance();
		} catch (RemoteException e) {
			//just simply
		}
		
		Object session= null;
		
		try {
			session= remote.login("Basanta", "SAIRAM");
		} catch (RemoteException e1) {
			//simply
		} catch (PermissionDeniedException e) {
			//simply
		}
		
		//remote.
		
		UserInfo uinfo= null;
		try {
			uinfo= remote.getUserInfo(session);
		} catch (RemoteException | InvalidSessionException e) {
			//simply
		}
		System.out.println (uinfo);
		ArrayList<String> paths= uinfo.getAllAccessiblePaths();
		for (String path: paths)
			System.out.println (path);
	}
}
