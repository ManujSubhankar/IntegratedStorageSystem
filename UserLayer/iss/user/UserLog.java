package iss.user;

import java.util.ArrayList;

/**
 * This class is useful for keeping the record log of all the uses currently logged in with features for updating 
 * the information about any user which was previously logged in
 * @author Basanta Sharma
 */
public class UserLog {
	//ArrayList of UserInfo instances of all the users currently logged in
	private ArrayList<UserInfo> log= new ArrayList<UserInfo> ();
	//One and only instance of this class that gets created once and is the same for all calls of getInstance()
	private static UserLog instance;
	
	/**
	 * 
	 * @return an instance of the UserLog class
	 */
	public static UserLog getInstance () {
		if (instance == null)
			instance= new UserLog();
		return instance;
	}
	
	/**
	 * Adds a User to the log
	 * @param info the UserInfo instance of the user logged in
	 */
	public Object login (UserInfo info) {
		log.add(info);
		return info.getSession();
	}
	
	/**
	 * States whether a user is currently logged in or not
	 * @param userName
	 * @return true if the user is currently logged in
	 */
	public boolean isLoggedIn (Object session) {
		if (!(session instanceof UserInfo.Session)) return false;
		UserInfo.Session s= (UserInfo.Session) session;
		for (UserInfo uinfo : log)
			if (uinfo.getUserName().equals(s.getUserName()))
				return true;
		return false;
	}
	
	/**
	 * Logs a user out if he is currently logged in.
	 * @param userName the name of the user to be logged out
	 */
	public void logout (Object session) {
		for (UserInfo uinfo: log) {
			if (uinfo.getSession().equals(session))
				log.remove(uinfo);
		}
	}
	
	/**
	 * For getting all the information about a user currently logged in.
	 * @param userName a user who is currently logged in
	 * @return A UserInfo class containing all the information about the given user name
	 */
	public UserInfo getUserInfo (Object ob) {
		if (ob instanceof String) {
			for (UserInfo info : log)
				if (info.getUserName().equals(ob))
					return info;
		}
		else if (ob instanceof UserInfo.Session) {
			UserInfo.Session s= (UserInfo.Session) ob;
			for (UserInfo info : log)
				if (info.getUserName().equals(s.getUserName()))
					return info;
		}
		return null;
	}
	
	/**
	 * Updates the current log by providing concerned users with the information about the newly created group
	 * @param groupName
	 * @param creator the user who created the group
	 * @param members the ArrayList of all the members of the group excluding the creator
	 */
	public void updateGroupCreated (String groupName, Object session, ArrayList<String> members) {
		UserInfo info= getUserInfo(session);
		if (info != null)
			info.addCreatedGroup(groupName);
		for (String member: members) {
			if (isLoggedIn(member)) {
				if ( (info= getUserInfo(member)) != null )
					info.addBelongingGroup(groupName);
			}
		}
	}
	
	/**
	 * Updates the log by updating the UserInfo of all the concerned users about a group being deleted.
	 * @param groupName
	 */
	public void updateGroupDeleted (String groupName) {
		for (UserInfo info: log) {
			if (info.hasCreatedGroup(groupName))
				info.removeCreatedGroup(groupName);
			if (info.hasBelongingGroup(groupName))
				info.removeBelongingGroup(groupName);
		}
	}
	
	/**
	 * Updates the log by updating the UserInfo of the given user, if he is currently logged in,
	 * about his gained membership in the given group
	 * @param userName
	 * @param groupName
	 */
	public void addGroupMember (Object session, String groupName) {
		UserInfo info= getUserInfo(session);
		if (info != null)
			info.addBelongingGroup(groupName);
	}
	
	/**
	 * Updates the log by updating the UserInfo of the given user, if he is currently logged in,
	 * about his lost membership in the given group
	 * @param userName
	 * @param groupName
	 */
	public void removeGroupMember (Object session, String groupName) {
		UserInfo info= getUserInfo(session);
		if (info != null)
			info.removeBelongingGroup(groupName);
	}
	
	/**
	 * Updates the log by updating the UserInfo of all the concerned users about a path being shared by given user to 
	 * the list of given users excluding the sharer
	 * @param path
	 * @param sharedTo
	 * @param sharedBy
	 */
	public void updatePathShare (String path, ArrayList<String> sharedTo, Object sharedBy) {
		UserInfo info= getUserInfo(sharedBy);
		info.addSharedPath(path);
		for (String user: sharedTo) {
			info= getUserInfo(user);
			if (info != null)
				info.addShareReceiver(path, info.getUserName());
		}
	}
	
	/**
	 * Updates the log by updating the UserInfo of all the concerned users about a path share being withdrawn
	 * by the given user
	 * @param path
	 */
	public void deleteSharedPath (String path) {
		for (UserInfo info: log) {
			if (info.hasSharedPath(path))
				info.removeSharedPath(path);
			if (info.hasShareAcquired(path))
				info.removeShareAcquired(path);
		}
	}
	
	/**
	 * Updates the log by updating the UserInfo of the given user, if he is currently logged in, about the path share being
	 * rejected by himself for himself
	 * @param path
	 * @param userName
	 */
	public void removePathShare (String path, Object session) {
		UserInfo info= getUserInfo(session);
		info.removeShareAcquired(path);
	}
}
