package iss.user;

import java.sql.*;
import java.util.ArrayList;

/**
 * The class that provides the interface for the BSFS meta-data server for interaction with the database that will have
 * all the information about the users, groups and file/folder sharing between users
 * 
 * @author Basanta Sharma
 *
 */
public class DatabaseInterface {
	private static DatabaseInterface dbInterface;
	private Connection dbConn;
	private Statement stmt;
	ResultSet rset;
	
	//A module which puts all executions of query returning result sets together
	//The returned result set will be set in the reference rset of this class
	void executeQuery (String query) {
		try {
			rset= stmt.executeQuery(query);
		} catch (SQLException e) {
			throw new RuntimeException ("Query: " + query + " ErrorMessage: " + e.getMessage());
		}
	}
	
	//A module which puts all executions of query that does not return result sets together
	boolean execute (String query) {
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			throw new RuntimeException ("Query: " + query + " ErrorMessage: " + e.getMessage());
		}
		return true;
	}
	
	//A module which puts all executions of database tables' update queries together
	boolean executeUpdate (String query) {
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			throw new RuntimeException ("Query: " + query + " ErrorMessage: " + e.getMessage());
		}
		return true;
	}
	
	private void createDatabase () {
		//create database BSFSUSERS
		String query= "create database BSFSUSERS;";
		execute(query);
		
		//use Database: BSFSUSERS
		query= "use BSFSUSERS;";
		execute(query);
		
		//create table "Users" in BSFSUSERS
		query= "create table Users ("
				+ "userName varchar(32) not null primary key, "
				+ "password varchar(16) not null);";
		execute(query);
		
		//create table "Groups" in BSFSUSERS
		query= "create table Groups ("
				+ "groupName varchar(32) not null primary key, "
				+ "creator varchar(32) not null references Users(userName));";
		execute(query);
		
		//create table "UserGroupMap" for mapping between users and groups
		//the creator of the group is by default the member and need not be added to this table.
		query= "create table UserGroupMap ("
				+ "userName varchar(32) not null references Users(userName), "
				+ "groupName varchar(32) not null references Groups(groupName), "
				+ "primary key (userName, groupName));";
		execute(query);
		
		//create table "PathShares" for mapping users to the paths shared to them
		query= "create table PathShares ("
				+ "userName varchar(32) not null references Users(userName), "
				+ "path varchar(512) not null, "
				+ "sharedBy varchar(32) not null references Users(userName), "
				+ "primary key (userName, path, sharedBy));";
		execute(query);
		
		//create table "groupAccess" for adding access permissions to a group
		/*query= "create table groupAccess ("
				+ "groupName varchar(32) not null references Groups (groupName), "
				+ "path varchar (512) not null, "
				+ "primary key (groupName, path));";
		execute (query);*/
	}
	
	/**
	 * Constructor that creates connection to the database and also creates the required database and tables if necessary<li>
	 * if the database is already present, it is assumed that the tables are also present.
	 * 
	 */
	private DatabaseInterface () {
		
		// Check whether driver is installed
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException ("No driver installed! contact administrator");
		}
		
		//connect to the database
		try {
			dbConn= DriverManager.getConnection("jdbc:mysql://localhost","root","sairam");
		} catch (SQLException e) {
			throw new RuntimeException ("Could not connect to database");
		}
		
		//get Statement object
		try {
			stmt= dbConn.createStatement();
		} catch (SQLException e1) {
			throw new RuntimeException ("Could not create statement to execute sql queries");
		}
		
		//Check whether database is present
		//If yes, use it, create otherwise
		//If database is present, it means the required tables are also present
		String query= "show databases";
		executeQuery(query);
		
		CHECKDB: {
			try {
			String dbName;
			while (rset.next()) {
				dbName= rset.getString("Database");
				if (dbName.equals("BSFSUSERS"))
					break CHECKDB;
			}
			createDatabase ();
		} catch (SQLException e) {
			throw new RuntimeException ("Error in processing query: " + query);
		}}// end of CHECKDB
		
		//use Database: BSFSUSERS
		query= "use BSFSUSERS;";
		execute(query);
	}
	
	/**
	 * If necessary creates, and returns an instance of this class.
	 * @return the unique reference to an instance of this class.
	 */
	public static DatabaseInterface getInstance () {
		if (dbInterface == null)
			dbInterface= new DatabaseInterface();
		else try {
			if (dbInterface.dbConn.isClosed())
				return null;
		} catch (SQLException e) {return null;}
		return dbInterface;
	}
	
	/**
	 * Closes the connection to the database
	 */
	public void close () {
		try {
			dbConn.close();
		} catch (SQLException e) {
			throw new RuntimeException("Could not close Database Connection");
		}
	}
	
	/**
	 * Checks whether connection to database is closed
	 * @return true is the connection to back-end database is closed
	 */
	public boolean isClosed () {
		try {
			return dbConn.isClosed();
		} catch (SQLException e) {
			throw new RuntimeException ("Could not check the state fo database connection");
		}
	}
	
	/**
	 * Tells whether the given userName exists in the database or not
	 * @param userName
	 * @return true if the user is present, false otherwise
	 */
	public boolean isUserExisting (String userName) {
		String query= "select * from Users where UserName= \'" + userName + "\';";
		dbInterface.executeQuery(query);
		try {
			if (dbInterface.rset.next()) return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
		
	}
	
	/**
	 * Checks whether the given user name and password are valid or not.
	 * @param userName
	 * @param password
	 * @return true if the userName and password are valid, false otherwise
	 */
	public boolean isValidUser(String userName, String password) {
		String query= "select * from Users where UserName= \'" + userName
				+ "\' and password= \'" + password + "\';";
		dbInterface.executeQuery(query);
		try {
			if (dbInterface.rset.next()) return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Method that makes a new entry of a valid user into the database<li>
	 * @param s the User object having username of length not greater than 32
	 * and password of length not greater than 16.
	 * @return true if successful, false otherwise
	 */
	public boolean createUser(String userName, String password) {
		if (userName.length() > 32 || password.length() > 16)
			return false;
		String query= "insert into Users (userName, password) values (\'" + userName + "\', \'" + password + "\');";
		return executeUpdate(query);
	}
	
	/**
	 * Removes a user from the database
	 * @param user a valid User object with valid username and password
	 * @return true if the user is not present in the database or delete was successful
	 */
	public void removeUser(String userName, String password) {
		String query= "delete from Users where userName= \'" + userName
				+ "\' and password= \'" + password + "\';";
		execute(query);
	}
	
	/**
	 * Creates a group in the database
	 * @param groupName
	 * @param creatro the creator of the group
	 * @param members the arrayList containing names of all the users, the group is part of, excluding the creator
	 * @return true if the group was successfully created, false otherwise.
	 */
	public boolean createGroup (String groupName, String creator, ArrayList<String> members) {
		if (groupName.length() > 32) return false;
		String query= "insert into Groups (groupName, creator) values (\'" + groupName + "\', \'" + creator + "\');";
		executeUpdate(query);
		for (String member: members) {
			if (member.length() > 32) continue;
			query= "insert into UserGroupMap (userName, groupName) values (\'" + member + "\', \'" + groupName + "\');";
			executeUpdate(query);
		}
		return true;
	}
	
	/**
	 * It deletes all entries of a group from the database and leaves the group totally unexisting
	 * @param groupName
	 * @param creator
	 * @return true if the creator is the actual creator of the group and deletion was successful, false otherwise.
	 */
	public void deleteGroup (String groupName) {
		//Delete all the members in the group
		String query= "delete from UserGroupMap where groupName= \'" + groupName + "\';";
		executeUpdate(query);
		//Delete the group
		query= "delete from Groups where groupName= \'" + groupName + "\';";
		executeUpdate(query);
	}
	
	/**
	 * Adds an entry to UserGroupMap to make the user effectively a part of the mentioned group
	 * @param userName
	 * @param groupName
	 * @return true if the user was successfully added to the group
	 */
	public boolean addMemberToGroup (String userName, String groupName) {
		if (userName.length() > 32) return false;
		String query= "insert into UserGroupMap (userName, groupName) values (\'"
		+ userName + "\', \'" + groupName + "\');";
		return executeUpdate(query);
	}
	
	/**
	 * Removes the given user from the given group in the database.
	 * @param userName
	 * @param groupName
	 * @return true if the user was not present or was successfully deleted.
	 */
	public boolean removeMemberFromGroup (String userName, String groupName) {
		String query= "delete from UserGroupMap where userName= \'" + userName
				+ "\' and groupName= \'" + groupName + "\';";
		return executeUpdate(query);
	}
	
	/**
	 * Inserts the pathShares into the database
	 * @param path the path to be shared
	 * @param sharedTo the ArrayList<String> of userNames to whom the path is shared
	 * @param sharedBy the userName who is sharing the path
	 * @return true
	 */
	public boolean sharePath (String path, ArrayList<String> sharedTo, String sharedBy) {
		String query= null;
		for (String user : sharedTo) {
			query= "insert into PathShares (userName, path, sharedBy) values (\'" + user + "\', \'"
					+ path + "\', \'" + sharedBy + "\');";
			executeUpdate(query);
		}
		return true;
	}
	
	/**
	 * Removes the accessibility to the given path for all the users with whom the path was shared
	 * @param path
	 */
	public void deleteSharedPath (String path) {
		String query= "delete from PathShares where path= \'" + path + "\';";
		executeUpdate(query);
	}

	/**
	 * Removes the accessibility to the given path for the given user if the path was shared with him
	 * @param path
	 */
	public void removePathShare (String path, String userName) {
		String query= "delete from PathShares where path= \'" + path + "\' and userName= \'" + userName + "\';";
		executeUpdate(query);
	}
	
	/**
	 * Fetches all the information about a user
	 * @param s the session object passed by the user
	 * @return UserInfo object containing all the informations about the user
	 */
	public UserInfo getUserInfo(String userName, String password) {

		UserInfo uinfo= new UserInfo (userName, password);
		String query= null;
		try {
			//Get all groups created by this user
			query= "select groupName from Groups where creator= \'" + uinfo.getUserName() + "\';";
			executeQuery(query);
			while (rset.next()) {
				uinfo.addCreatedGroup(rset.getString("groupName"));
			}

			//Get all groups this user is a part of excluding those the user has created
			query= "select groupName from UserGroupMap where userName= \'" + uinfo.getUserName() + "\';";
			executeQuery(query);
			while (rset.next()) {
				String group= rset.getString("groupName");
				if (!uinfo.hasCreatedGroup(group))
					uinfo.addBelongingGroup(group);
			}

			//Get all paths shared by this user to others
			query= "select path from PathShares where sharedBy= \'" + uinfo.getUserName() + "\' group by path;";
			executeQuery(query);
			ArrayList<String> pathsShared= new ArrayList<String> ();
			while (rset.next()) {
				pathsShared.add(rset.getString("path"));
			}
			for (String path: pathsShared) {
				uinfo.addSharedPath(path);
				query= "select userName from PathShares where sharedBy= \'" + uinfo.getUserName() 
						+ "\' and path= \'" + path + "\';";
				executeQuery(query);
				while (rset.next()) {
					uinfo.addShareReceiver(path, rset.getString("userName"));
				}
			}

			//Get all paths shared to this user
			query= "select path, sharedBy from PathShares where userName= \'" + uinfo.getUserName() + "\';";
			executeQuery(query);
			while (rset.next()) {
				uinfo.addShareAcquired(rset.getString("path"), rset.getString("sharedBy"));
			}
		}catch (SQLException e) {
			System.out.println ("Error is: " + e.getMessage() + ", Query is: " + query);
			return null;
		}
		return uinfo;
	}

	/**
	 * Retrieves all the user names from the database and returns the list.
	 * @return ArrayList containing all the names read.
	 */
	public ArrayList<String> getAllUserNames() {
		ArrayList<String> userNames= new ArrayList<String> ();
		String query= "select userName from Users;";
		executeQuery(query);
		try {
			while (rset.next()) {
				userNames.add(rset.getString("userName"));
			}
		} catch (SQLException e) {
			return userNames;
		}
		return userNames;
	}
}
