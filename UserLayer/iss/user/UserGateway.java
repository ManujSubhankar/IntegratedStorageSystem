package iss.user;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class implements all the methods of UserAPI
 * @author Basanta Sharma
 */
public class UserGateway extends UnicastRemoteObject implements UserAPI {
	
	private static final long serialVersionUID = 1L;
	private static UserGateway gateway;
	private BSFSCommunicator conn;
	private DatabaseInterface dbInterface;
	private UserLog usersLog;
	private static Byte key= 0;
	private static HashMap<String,BSFSDelegate> confirm= new HashMap<String,BSFSDelegate>();
	
	private String keyGenerator(String ipad, BSFSDelegate del)
	{
		String ans= ipad + "/" + System.currentTimeMillis() + "/";
		synchronized(key)
		{
			key= (byte) (key + 1);
			ans= ans + key;
		}
		confirm.put(ans, del);
		return ans;
	}
	
	UserGateway() throws RemoteException {
		super();
		conn= new BSFSCommunicator ();
		dbInterface= DatabaseInterface.getInstance();
		usersLog= UserLog.getInstance();
	}
	
	public static UserGateway getInstance () throws RemoteException {
		if (gateway == null) {
			gateway= new UserGateway();
		}
		return gateway;
	}
	
	public void close () {
		dbInterface.close();
	}

	@Override
	public Object createUser(String userName, String password) throws RemoteException{
		if (usersLog.isLoggedIn(userName)) return usersLog.getUserInfo(userName).getSession();
		if (dbInterface.isValidUser(userName, password)) {
			return usersLog.login(dbInterface.getUserInfo(userName, password));
		}
		if(!dbInterface.isUserExisting(userName)) {
			if (!dbInterface.createUser(userName, password)) return null;
			Object s= usersLog.login(dbInterface.getUserInfo(userName,password));
			System.out.println("Name: " + userName);
			conn.createFolder("", userName);
			return s;
		}
		return null;
	}

	@Override
	public void deleteUser(String userName, String password) throws RemoteException {
		if (usersLog.isLoggedIn(userName))
			usersLog.logout(userName);
		dbInterface.removeUser(userName, password);
		conn.deleteFile("/" + userName);
	}

	@Override
	public Object login(String userName, String password) throws RemoteException, PermissionDeniedException {
		if(!dbInterface.isValidUser(userName, password)) throw new PermissionDeniedException();
		return usersLog.login(dbInterface.getUserInfo(userName, password));
	}

	@Override
	public UserInfo getUserInfo(Object session) throws RemoteException, InvalidSessionException {
		System.out.println("Here");
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		return usersLog.getUserInfo(session);
	}

	@Override
	public void logout(Object session) throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		usersLog.logout(session);
	}

	@Override
	public ArrayList<String> getAllUsers(Object session) throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		return dbInterface.getAllUserNames();
	}

	@Override
	public boolean createGroup(String groupName, ArrayList<String> members, Object session)
			throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		for (String member: members) {
			if (!dbInterface.isUserExisting(member)) {
				members.remove(member);
			}
		}
		String userName= usersLog.getUserInfo(session).getUserName();
		if (!dbInterface.createGroup(groupName, userName, members)) return false;
		usersLog.updateGroupCreated(groupName, session, members);
		conn.createFolder("/", groupName);
		return false;
	}

	@Override
	public void deleteGroup(String groupName, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info;
		if ((info= usersLog.getUserInfo(session)) == null ) return;
		else if (!info.hasCreatedGroup(groupName)) throw new PermissionDeniedException();
		dbInterface.deleteGroup(groupName);
		usersLog.updateGroupDeleted(groupName);
		conn.deleteFile("/" + groupName);
	}

	@Override
	public boolean addMemberToGroup(String userName, String groupName, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info;
		if ((info= usersLog.getUserInfo(session)) == null ) return false;
		else if (!info.hasCreatedGroup(groupName)) throw new PermissionDeniedException();
		if(!dbInterface.addMemberToGroup(userName, groupName)) return false;
		usersLog.addGroupMember(userName, groupName);
		return true;
	}

	@Override
	public boolean removeMemberFromGroup(String userName, String groupName, Object session)
			throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		if (!dbInterface.removeMemberFromGroup(userName, groupName)) return false;
		usersLog.removeGroupMember(userName, groupName);
		return true;
	}

	@Override
	public boolean rejectGroupMembership(String groupName, Object session)
			throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		String userName= usersLog.getUserInfo(session).getUserName();
		if (!dbInterface.removeMemberFromGroup(userName, groupName)) return false;
		usersLog.removeGroupMember(session, groupName);
		return true;
	}

	@Override
	public boolean sharePath(String sharedPath, ArrayList<String> sharedTo,
			Object session) throws RemoteException, InvalidSessionException, PermissionDeniedException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(sharedPath)) throw new PermissionDeniedException();
		if (!dbInterface.sharePath(sharedPath, sharedTo, usersLog.getUserInfo(session).getUserName())) return false;
		usersLog.updatePathShare(sharedPath, sharedTo, session);
		return true;
	}

	@Override
	public boolean undoShare(String sharedPath, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.hasSharedPath(sharedPath)) throw new PermissionDeniedException();
		dbInterface.deleteSharedPath(sharedPath);
		usersLog.deleteSharedPath(sharedPath);
		return true;
	}

	@Override
	public boolean rejectShare(String sharedPath, Object session)
			throws RemoteException, InvalidSessionException {
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.hasShareAcquired(sharedPath)) return false;
		dbInterface.removePathShare(sharedPath, usersLog.getUserInfo(session).getUserName());
		usersLog.removePathShare(sharedPath, session);
		return true;
	}

	@Override
	public BSFSFile getFile(String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.hasAccessTo(path)) throw new PermissionDeniedException();
		
		//Serving
		return conn.getFile(path);
	}

	@Override
	public void renameFile(String path, String name, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(path)) throw new PermissionDeniedException();
		
		//Serving
		conn.renameFile(path, name);
	}

	@Override
	public ArrayList<BSFSFile> getContent(String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.hasAccessTo(path)) throw new PermissionDeniedException();
		
		//Serving
		return conn.getContent(path);
	}

	@Override
	public String[] fetchFile(String ipad, String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.hasAccessTo(path)) throw new PermissionDeniedException();
		
		//Serving
		BSFSFile f= conn.getFile(path);
		if(f.fileType.equals(FileType.Directory))
			throw new RuntimeException(path + " is a directory");
		String k= keyGenerator(ipad, new BSFSDelegate());
		String[] ans= new String[2];
		ans[0]= k;
		ans[1]= f.destIpAddress.get(0);
		return ans;
	}

	@Override
	public String[] storeFile(String ipad, String path, String name, long size,
			Object session) throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(path)) throw new PermissionDeniedException();
		
		//Serving
		System.out.println(path + " : " + name);
		String str[]= conn.storeFile(path, name, size);
		if(!path.endsWith("/"))
			path= path + "/";
		BSFSFile f= conn.getFromWaiting(str[0]);
		String k= keyGenerator(ipad, new BSFSDelegate());
		String[] ans= new String[2];
		ans[0]= k;
		ans[1]= f.destIpAddress.get(0);

		return ans;
	}

	@Override
	public String[] deleteFile(String ipad, String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(path)) throw new PermissionDeniedException();
		
		//Serving`
		ArrayList<String[]> st= conn.deleteFile(path);
		String k= keyGenerator(ipad, new BSFSDelegate());
		if(st.size() < 1)
			return null;
		String[] ans= new String[2];
		ans[0]= k;
		ans[1]= st.get(0)[1];
		
		return ans;
	}

	@Override
	public void createFolder(String parent, String name, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(parent)) throw new PermissionDeniedException();
		
		//serving
		conn.createFolder(parent, name);
	}

	@Override
	public void moveFile(String destPath, String srcPath, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(destPath)) throw new PermissionDeniedException();
		
		//Serving
		conn.moveFile(destPath, srcPath);
	}

	@Override
	public BSFSDelegate getConfirmation(String key) throws RemoteException,
			InvalidSessionException, PermissionDeniedException {
		BSFSDelegate ans= confirm.remove(key);
		if(ans == null)
			throw new RuntimeException("Unauthorized key");
		return ans;
	}

	@Override
	public void fileSizeUpdater(String path, long size, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException {
		//Authentication
		if (!usersLog.isLoggedIn(session))  throw new InvalidSessionException();
		UserInfo info= usersLog.getUserInfo(session);
		if (!info.isOwnerOf(path)) throw new PermissionDeniedException();
		
		//Serving
		conn.fileSizeUpdater(path, size);
	}

	@Override
	public void confirmFile(String fileID) throws RemoteException,
			RuntimeException {
		conn.confirmFile(fileID);
	}
}
