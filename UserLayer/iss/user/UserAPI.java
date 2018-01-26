package iss.user;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This RMI enabled API sits as an additional layer above the existing BSFS(Birthday Season File System) API.<li>
 * It provides all the functionality that the BSFS API provides with additional feature/constraint of user.<li>
 * It gives methods to create, delete, enquire about users and user-accounts.<li>
 * It also gives feature of groups, which is like another user in its functionality,
 * besides that it is equally accessible to all the member users.<li>
 * A user can create a group and he alone has right to delete it.<li>
 * The user who creates a group can alone add any other user(s) to the group while creating it or any time later.<li>
 * The user who gets added to a group has a right to get himself off the group any time<li>
 * The users are also given the facility to share any of the paths owned by it to any other user.<li>
 * The user who gets the sharing of the path is given the right to remove the share from himself.<li>
 * 
 * A user needs to login with the user-name and password before trying to access any of the methods of the BSFS<br>
 * The login method returns an instance of Object class which needs to be passed as an argument to any of the subsequent
 * methods being invoked on the BSFS failing which an InvalidSessionException will be thrown.<br>
 * If a user tries to access to any of the paths to which it does not have access, a PermissionDeniedException is thrown.<br>
 * 
 * @author Basanta Sharma
 */
public interface UserAPI extends Remote {
	
	/**
	 * Creates a user account with the given user name and password.<br>
	 * Also logs in the newly created user.<br>
	 * If the given user name and password already exists, this function acts same as calling login();<br>
	 * If only the user-name matches and not the password, login fails and returns null.
	 * @param userName
	 * @param passwordvoid
	 * @return the Session object representing the new user just logged in. null if the user was not created
	 * @throws RemoteException if error occurs in the remote method call
	 */
	public Object createUser(String userName, String password) throws RemoteException;
	
	/**
	 * Deletes an existing user account from the meta-data server.
	 * @param userName
	 * @param password
	 * @throws RemoteException if error occurs in the remote method call
	 */
	public void deleteUser (String userName, String password) throws RemoteException;

	/**
	 * Logs in the user with the given user name and password is the given user-name and password are valid.<br>
	 * @param userName
	 * @param password
	 * @return the session object if login was successful.
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws PermissionDeniedException if the user-name and/or password does not match
	 */
	public Object login (String userName, String password) throws RemoteException, PermissionDeniedException;
	
	/**
	 * Method to enquire for the information about the user logged in.<br>
	 * 
	 * @param session the session object returned by the login() method
	 * @return a UserInfo instance containing all the information about the given user.
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws InvalidSessionException if the user is not logged in
	 */
	public UserInfo getUserInfo (Object session) throws RemoteException, InvalidSessionException;
	
	/**
	 * Logs a user out thus restricting the user from the access to the BSFS until another login.
	 * @param session the session object returned by login()
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws InvalidSessionException if the user is not logged in
	 */
	public void logout (Object session) throws RemoteException, InvalidSessionException;

	/**
	 * Gets the names of all the users currently registered in BSFS
	 * @param session the session object returned by login()
	 * @return an ArrayList containing names of all the users currently registered in BSFS.
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws InvalidSessionException if the user is not logged in
	 */
	public ArrayList<String> getAllUsers (Object session) throws RemoteException, InvalidSessionException;
	
	/**
	 * Creates a new group with the user whose session object is given as the creator and obvious member of the group,
	 * and the users given in the ArrayList of userNames as other members of the group.<br>
	 * There can be no item at all in the list of members, and in such case, a group with lone member is created.<br>
	 * If any member in the list is not a valid user-name, the user-name is discarded.<br>
	 * @param groupName the name of the new group
	 * @param members all the members of the group, excluding the creator
	 * @param session the session object returned by login()
	 * @return true if the creation of the group was successful
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws InvalidSessionException if the creator is not logged in
	 */
	public boolean createGroup (String groupName, ArrayList<String> members, Object session)
			throws RemoteException, InvalidSessionException;
	
	/**
	 * Deletes a group.<br>
	 * The session object need to be of the user who has created the group.<br>
	 * @param groupName
	 * @param session the session object returned by login()
	 * @throws RemoteException if error occurs in the remote method call
	 * @throws InvalidSessionException if the creator is not logged in
	 * @throws PermissionDeniedException if the requesting user is not the creator of the group
	 */
	public void deleteGroup (String groupName, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException;
	
	/**
	 * Adds a member to the given group
	 * @param userName the new user to be added to the group
	 * @param groupName the name of the group to which the user need to be added
	 * @param session the session object returned by login()
	 * @return true if the user was successfully added to the group
	 * @throws RemoteException if error occurs in RMI
	 * @throws InvalidSessionException if the creator is not logged in
	 * @throws PermissionDeniedException if the requesting user is not the creator of the group
	 */
	public boolean addMemberToGroup (String userName, String groupName, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException;
	
	/**
	 * Removes a member from the group.<br>
	 * The session object should be of the creator of the group
	 * @param userName the name of the user who needs to be removed from the group
	 * @param groupName
	 * @param session the session object returned by login()
	 * @return true if the user to be removed was originally present and is removed, false otherwise
	 * @throws RemoteException if error occurs in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 */
	public boolean removeMemberFromGroup (String userName, String groupName, Object session)
			throws RemoteException, InvalidSessionException;
	
	/**
	 * Rejects the group membership of the requesting user to the specified group if he is a member of the group
	 * @param groupName
	 * @param session the session object of the requesting user returned by login()
	 * @return true if the user had a membership in the group and was successfully rejected, false otherwise.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting is not logged in.
	 */
	public boolean rejectGroupMembership (String groupName, Object session)
			throws RemoteException, InvalidSessionException;
	
	/**
	 * Shares a path which the requesting user is owner of to list of other users<br>
	 * @param sharedPath the path to be shared
	 * @param sharedTo the ArrayList of the names of the users to which the path needs to be shared
	 * @param session the session object returned by login()
	 * @return true if the path was successfully shared
	 * @throws RemoteException if there was error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the requesting user is not the owner of the path to be shared
	 */
	public boolean sharePath (String sharedPath, ArrayList<String> sharedTo, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException;
	
	/**
	 * Undoes the path previously shared to one or more other users.
	 * @param sharedPath the path that was shared
	 * @param session the session object of the requesting user returned by login()
	 * @return true if the path was originally shared and was successfully undone
	 * @throws RemoteException if there was any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the requesting user had not shared the path
	 */
	public boolean undoShare (String sharedPath, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException;
	/**
	 * It helps a user to block himself the access to paths shared by other users
	 * @param sharedPath the path to be blocked
	 * @param session the session object returned by the login() method
	 * @return true if the path share was originally acquired and was successfully removed from the shares acquired list
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 */
	public boolean rejectShare (String sharedPath, Object session)
			throws RemoteException, InvalidSessionException;
	/**
	 * Gets the content of the file the user has access to.
	 * @param path the path(folder) the user has access to.
	 * @param session the requesting user's session object returned by login()
	 * @return the ArrayList of BSFSFile objects which describe the contents of the given folder
	 * @throws RemoteException for any error in the RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the requesting user does not have access to the requested folder
	 */
	public ArrayList<BSFSFile> getContent(String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * 
	 * @param ipad
	 * @param path
	 * @param session the session object returned by login() method.
	 * @return
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user does not have access to the BSFSFile to be fetched.
	 */
	public String[] fetchFile(String ipad, String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Stores the given file (in the native system) in the BSFS
	 * @param ipad
	 * @param path the parent path in BSFS where the file needs to be stored
	 * @param name the name of the file with which the native file has to be stored in BSFS
	 * @param size the size of the native file to be stored
	 * @param session the session object returned by login() method.
	 * @return 
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user is not the owner of the BSFSFile path
	 * where the file needs to be stored.
	 */
	public String[] storeFile(String ipad, String path, String name, long size, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Deletes a file/folder from the BSFS.
	 * @param path the complete path of the BSFSFile that needs to be deleted
	 * @param session the session object returned by login() method.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user is not the owner of the BSFSFile to be deleted.
	 */
	public String[] deleteFile(String ipad, String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Creates a folder with the path specified as the parent of the new folder.
	 * @param ipad the IP address of the client host
	 * @param parent the path which is the parent of the new folder being created.
	 * @param name the name of the new folder.
	 * @param session the session object returned by login() method.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user is not the owner of the path
	 * where the folder needs to be created
	 */
	public void createFolder(String parent, String name, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Copies a file in the BSFS from one path in the BSFS to another path in BSFS alone.
	 * @param destPath the destination path where the BSFSFile needs to be copied.
	 * @param srcPath the complete source path of BSFSFile object which needs to be copied.
	 * @param session the session object returned by login() method.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user does not have access to the BSFSFile to be copied.
	 *//*
	public void copyFile(String destPath, String srcPath, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
*/	
	/**
	 * Moves a file in the BSFS from one path in the BSFS to another path in BSFS alone.
	 * @param destPath the destination path where the BSFSFile needs to be moved.
	 * @param srcPath the complete source path of BSFSFile object which needs to be moved.
	 * @param session the session object returned by login() method.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in.
	 * @throws PermissionDeniedException if the requesting user is not the owner of the BSFSFile to be moved.
	 */
	public void moveFile(String destPath, String srcPath, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Updates the size of the file currently part of the BSFS
	 * @param path the complete path of the file whose size is to be updated
	 * @param size the new size of the file
	 * @param session the session object returned by the login() method.
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the given path is not owned by the requesting user
	 */
	public void fileSizeUpdater(String path, long size, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Gets the BSFSFile object for the given path from the BSFS if the path is valid and is accessible
	 * @param path the path for which the BSFSFile object is required
	 * @param session the session object returned by login()
	 * @return A BSFSFile object representing the given path
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the given path is not accessible to the user
	 */
	public BSFSFile getFile(String path, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Renames a file/folder in the BSFS which the requesting user owns
	 * @param path the path of the parent directory in which the file/folder to be renamed is sitting.
	 * @param name the name of the file/folder to be renamed
	 * @param session the session object returned by the login() method
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the requesting user is not the owner of the given path
	 */
	public void renameFile(String path,String name, Object session)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Used by File Server to confirm the request.
	 * Confirms the key and returns the BSFSDelegate object.
	 * @param key given by the client
	 * @return A BSFSDelegate object hell
	 * @throws RemoteException for any error in RMI
	 * @throws InvalidSessionException if the requesting user is not logged in
	 * @throws PermissionDeniedException if the requesting user is not the owner of the given path
	 */
	public BSFSDelegate getConfirmation(String key)
			throws RemoteException, InvalidSessionException, PermissionDeniedException, RuntimeException;
	
	/**
	 * Used by file server to confirm the storing of a particular file to the metadata server.
	 * @param fileID the fileID of the BSFSFile that was successfully stored.
	 * @throws RemoteException for any error in RMI
	 * @throws RuntimeException for any error in acknowledging the request.
	 */
	public void confirmFile(String fileID) throws RemoteException, RuntimeException, RuntimeException;
}
