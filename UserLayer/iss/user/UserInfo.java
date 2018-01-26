package iss.user;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
	//The user name and password of the user as a session object
	private Session session;
	//List of all the groups created by this user
	private ArrayList<String> createdGroups;
	//List of all the groups to which the user belongs, excluding those created by him
	private ArrayList<String> belongingGroups;
	//List of all the paths shared by this user to others. Each PathSharedItem instance will
	//have the list of the users to whom the path is shared.
	private ArrayList<PathShareItem> sharedItems;
	//List of all the paths shared to this user by other users.
	//Each AcquiredShareItem instance will have the pathname and the userName who shared this path.
	private ArrayList<AcquiredShareItem> sharesAcquired;
	
	/**
	 * Constructor for creating a UserInfo instance with all info other than username and password blank.
	 * @param uName the userName
	 * @param pswd the password
	 */
	public UserInfo (String userName, String password) {
		session= this.new Session (userName,password);
		createdGroups= new ArrayList<String> ();
		belongingGroups= new ArrayList<String> ();
		sharedItems= new ArrayList<PathShareItem> ();
		sharesAcquired= new ArrayList<AcquiredShareItem> ();
	}
	
	//getter for userName
	public String getUserName() {
		return session.getUserName();
	}
	
	//getter for password
	public String getPassword() {
		return session.getPassword();
	}
	
	//getter for the session object
	public Object getSession () {
		return session;
	}

	//getter for list of groups created by this user
	@SuppressWarnings("unchecked")
	public ArrayList<String> getCreatedGroups() {
		return (ArrayList<String>) createdGroups.clone();
	}

	/**
	 * Adds an item (userName) to the list of groups created by this user
	 * @param createdGroup the name of the new Group that was created by this user
	 * @return true
	 */
	public boolean addCreatedGroup(String createdGroup) {
		return createdGroups.add(createdGroup);
	}

	/**
	 * Removes an item from the list of the groups created by this user
	 * @param createdGroup the group that was created by this user.
	 * @return true if the group was created by the user, false otherwise. 
	 */
	public boolean removeCreatedGroup(String createdGroup) {
		return createdGroups.remove(createdGroup);
	}
	
	/**
	 * Checks whether a group is created by this user
	 * @param createdGroup
	 * @return true if the group was created by this user
	 */
	public boolean hasCreatedGroup (String createdGroup) {
		return createdGroups.contains(createdGroup);
	}
	
	/**
	 * @return array list of all the groups to which this user belongs, excluding the groups created by himself
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getBelongingGroups() {
		return (ArrayList<String>) belongingGroups.clone();
	}

	/**
	 * Adds an item to the list of groups which this user belongs to.
	 * @param belongingGroup
	 * @return true
	 */
	public boolean addBelongingGroup (String belongingGroup) {
		return belongingGroups.add(belongingGroup);
	}

	/**
	 * Removes an item from the list of groups which this user belongs to.
	 * @param belongingGroup
	 * @return true if the user belonged to the given group
	 */
	public boolean removeBelongingGroup (String belongingGroup) {
		return belongingGroups.remove(belongingGroup);
	}
	
	/**
	 * Checks whether the user belongs to the given group
	 * @param belongingGroup
	 * @return true if the user belongs to the given group.
	 */
	public boolean hasBelongingGroup (String belongingGroup) {
		return belongingGroups.contains(belongingGroup);
	}

	/**
	 * Getter for list of all the paths shared by this user to others
	 * @return the ArrayList of all the paths shared by this user to others
	 */
	public ArrayList<String> getSharedPaths() {
		ArrayList<String> sharedPaths= new ArrayList<String> ();
		for (PathShareItem item: sharedItems) {
			sharedPaths.add(item.getPath());
		}
		return sharedPaths;
	}
	
	/**
	 * Getter for the list of all the users to whom the path is shared
	 * @param path
	 * @return ArrayList containing the list of all the users to whom the path is shared
	 */
	public ArrayList<String> getShareReceivers (String path) {
		for (PathShareItem item: sharedItems) {
			if (item.getPath().equals(path))
				return item.getSharedTo();
		}
		return null;
	}
	
	/**
	 * Adds an item to the list of paths shared by this user to others.
	 * @param sharedPath
	 * @return true
	 */
	public boolean addSharedPath (String sharedPath) {
		return sharedItems.add(new PathShareItem(sharedPath));
	}
	
	/**
	 * Adds a receiver to path already shared to some.
	 * @param sharedPath
	 * @param userName
	 * @return true
	 */
	public boolean addShareReceiver (String sharedPath, String userName) {
		for (PathShareItem item: sharedItems) {
			if (item.path.equals(sharedPath))
				return item.addShare(userName);
		}
		return false;
	}
	
	/**
	 * removes an entry from the list of all the paths shared by this user to other users
	 * @param path
	 * @return true if the path existed, false otherwise
	 */
	public boolean removeSharedPath (String path) {
		for (PathShareItem sharedItem : sharedItems) {
			if (sharedItem.getPath().equals(path))
				return sharedItems.remove(sharedItem);
		}
		return false;
	}
	
	/**
	 * checks whether the given path is shared by this user to others or not
	 * @param path
	 * @return true if the path is shared by this user, false otherwise.
	 */
	public boolean hasSharedPath (String path) {
		for (PathShareItem sharedItem : sharedItems) {
			if (sharedItem.getPath().equals(path))
				return true;
		}
		return false;
	}
	
	/**
	 * @return the ArrayList<String> object of all the paths shared to this user
	 */
	public ArrayList<String> getPathSharesAcquired() {
		ArrayList<String> acquiredShares= new ArrayList<String> ();
		for (AcquiredShareItem item: sharesAcquired) {
			acquiredShares.add(item.getPath());
		}
		return acquiredShares;
	}
	
	/**
	 * Gives the name of the user who has shared the given path
	 * @param path 
	 * @return the user who shared this path, null if the path is not shared
	 */
	public String getPathSharer (String path) {
		for (AcquiredShareItem item: sharesAcquired) {
			if (item.getPath().equals(path))
				return item.getSharedBy();
		}
		return null;
	}
	
	/**
	 * Adds a path shared to this user
	 * @param path the path shared to this user
	 * @param sharedBy the user who shared this path
	 * @return
	 */
	public boolean addShareAcquired(String path, String sharedBy) {
		return sharesAcquired.add(new AcquiredShareItem(path, sharedBy));
	}
	
	/**
	 * States whether the path is shared to this user
	 * @param path
	 * @return true if the path is shared, false otherwise.
	 */
	public boolean hasShareAcquired (String path) {
		for (AcquiredShareItem item: sharesAcquired) {
			if (item.getPath().equals(path))
				return true;
		}
		return false;
	}
	
	/**
	 * Removes the share acquired by this user from other user
	 * @param path the shared path to be deleted
	 * @return true if this user has the specified path shared to him
	 */
	public boolean removeShareAcquired (String path) {
		for (AcquiredShareItem item: sharesAcquired) {
			if (item.getPath().equals(path))
				return sharesAcquired.remove(item);
		}
		return false;
	}
	
	/**
	 * Method that returns all the paths in the meta-data server that are accessible to the user.<br>
	 * A user has an access to all the paths he owns and those are shared to him by other users.<br>
	 * The user has access to a sub-path if he owns the parent path but not necessarily the reverse.
	 * @return ArrayList<String> containing all the accessible paths.
	 */
	public ArrayList<String> getAllAccessiblePaths () {
		ArrayList<String> accessibles= getAllOwnedPaths();
		accessibles.addAll(getPathSharesAcquired());
		return accessibles;
	}
	
	/**
	 * Method that gives all the paths this user owns.<br>
	 * A user owns a path if it belongs to his private allocated area or is a part of the group created by him.<br>
	 * The user owns a sub-path if he owns the parent path but not necessarily the reverse.
	 * @return an ArrayList<Strign> containing all the paths that the user owns.
	 */
	public ArrayList<String> getAllOwnedPaths () {
		ArrayList<String> owned= new ArrayList<String>();
		owned.add(0, "/" + session.getUserName());
		for (String group : createdGroups)
			owned.add("/" + group);
		for (String group : belongingGroups)
			owned.add("/" + group);
		return owned;
	}
	
	/**
	 * Tells whether the given path is accessible to the user or not<br>
	 * A user has an access to all the paths he owns and those are shared to him by other users.<br>
	 * The user has access to a sub-path if he owns the parent path but not necessarily the reverse.
	 * @param path
	 * @return true if the given path is accessible to the user, false otherwise
	 */
	public boolean hasAccessTo (String path) {
		ArrayList<String> accessiblePaths= getAllAccessiblePaths();
		for (String accessiblePath : accessiblePaths) {
			if (path.startsWith(accessiblePath))
				return true;
		}
		return false;
	}
	
	/**
	 * Tells whether this user is the owner of the given path<br>
	 * A user is the owner of the path ("/" + userName) and ("/" + groupName) for all the groups he is a part of<br>
	 * The user owns a sub-path if he owns the parent path but not necessarily the reverse.
	 * @param path
	 * @return true if this user is the owner of the given path, false otherwise.
	 */
	public boolean isOwnerOf (String path) {
		ArrayList<String> owned= getAllOwnedPaths();
		for (String ownedPath: owned) {
			if (path.startsWith(ownedPath))
				return true;
		}
		return false;
	}
	
	/**
	 * Inner class to store the path shared by this user and list of all the users to whom the path is shared
	 * @author Basanta Sharma
	 */
	private class PathShareItem implements Serializable {
		//Path that this user has shared
		private String path;
		//All the users that this user is sharing the path to
		private ArrayList<String> sharedTo;
		
		public PathShareItem (String path) {
			this.path= path;
			sharedTo= new ArrayList<String> ();
		}
		
		public String getPath() {
			return path;
		}
		
		@SuppressWarnings("unchecked")
		public ArrayList<String> getSharedTo() {
			return (ArrayList<String>) sharedTo.clone();
		}
		
		public boolean addShare (String userName) {
			return sharedTo.add(userName);
		}
		
		public String toString () {
			String toReturn= "[Path: " + path + " is Shared To Users: " + sharedTo + "]";
			return toReturn;
		}
	}
	
	/**
	 * Class packaging a path shared to this user and the name of the user who shared the path with this user.
	 * @author Basanta Sharma
	 */
	private class AcquiredShareItem implements Serializable {
		//Path share acquired by this user
		private String path;
		//The user who has shared this path
		private String sharedBy;
		
		public AcquiredShareItem (String p, String sharer) {
			this.path= p;
			this.sharedBy= sharer;
		}
		
		public String getPath() {
			return path;
		}
		
		public String getSharedBy() {
			return sharedBy;
		}
		
		public String toString () {
			return "[" + path + ", " + sharedBy + "]";
		}
	}
	
	/**
	 * overridden toString() method which returns the string representation of the calling UserInfo instance.
	 */
	public String toString () {
		String toReturn= "User: " + session.getUserName();
		toReturn+= "\n\tHas Created Groups: " + createdGroups;
		toReturn+= "\n\tIs a part of Groups: " + belongingGroups;
		toReturn+= "\n\tHas Acquired Shares: " + getPathSharesAcquired();
		toReturn+= "\n\tHas Shared to Others: " + getSharedPaths() + "\n";
		return toReturn;
	}/**
	 * A class that encapucilates the username and password into a single entity
	 * to be used conveniently for accessing BSFS.
	 * @author Basanta Sharma
	 *
	 */
	class Session implements Serializable {
		
		private String userName;
		
		private String password;
		
		private Session (String uName, String pswd) {
			userName= uName;
			password= pswd;
		}
		
		public String getUserName() {
			return userName;
		}
		
		public String getPassword() {
			return password;
		}
		
		public String toString () {
			return userName;
		}
	}

}
