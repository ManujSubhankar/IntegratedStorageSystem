package iss.gui;

import java.util.ArrayList;

public class Navigator {
	static final String SEARCH= "__ISS.SEARCH__";
	private ArrayList<String> visited= new ArrayList<String> ();
	private int index= -1;
	private boolean backEnabled= false, forwardEnabled= false;
	
	/**
	 * Tells whether the back button is enabled or not
	 * @return
	 */
	public boolean isBackEnabled() {
		return backEnabled;
	}

	/**
	 * Tells whether the forward button is enabled or not
	 * @return
	 */
	public boolean isForwardEnabled() {
		return forwardEnabled;
	}
	
	/**
	 * intitializes the navigator with the new path
	 * @param path
	 */
	void setNewPath (String path) {
		index= -1;
		clearForwardList();
		visited.add(path);
		index++;
	}
	
	/**
	 * Clears the list after the current working directory where the user can forward by clicking
	 * on the forward button
	 */
	private void clearForwardList () {
		for (int i= index + 1; i < visited.size();) {
			visited.remove(i);
		}
		setForwardEnabled(false);
	}
	
	/**
	 * Gets the cumulative path upto the given index in the list of paths opened so far
	 * @param index
	 * @return
	 */
	private String getCumulativePath(int index) {
		if (visited.get(index).equals(SEARCH)) return SEARCH;
		String path= "";
		for (int i=0; i <= index; i++) {
			String p= visited.get(i);
			if (p.equals(SEARCH)) {
				path="";
				continue;
			}
			path+= visited.get(i);
			if (!p.equals("/") && i != index) path+= "/";
		}
		return path;
	}
	
	/**
	 * Forwards the state of the navigator to the given directory
	 * @param dirname the directory to which forwarding is to be done
	 * @return
	 */
	public boolean forward (String dirname) {
		//If invoked by pressing the forward button, forward to the next directory in the list if valid
		if (dirname == null) {
			if (index <= visited.size() - 2) {
											//if there is at least one item in the list after the current index
				if (!visited.get(index + 1).equals(SEARCH) && 
						!BackEnd.isExistingInISS(getCumulativePath(index + 1))) {
																		//if the directory in the list exists
					clearForwardList();
					return false;
				}
				index++;
				setBackEnabled(true);
				if (index == visited.size() - 1) setForwardEnabled(false);
				return true;
			} else return false; //Should not happen
		}
		
		//If some directory name is mentioned...
		
		if (dirname.equals(SEARCH) && visited.get(index).equals(SEARCH))
			return true;
		if (visited.size() - 1 == index) {	//if current working directory is the last in the list, direct add
			visited.add(dirname);
			index++;
			setBackEnabled(true);
			return true;
		}
		if (dirname.equals(visited.get(index + 1))) {	//if the given directory name is same as next in the list
			if (!visited.get(index + 1).equals(SEARCH) && 
					!BackEnd.isExistingInISS(getCumulativePath(index + 1))) { // if the directory is not valid
				clearForwardList();
				setForwardEnabled(false);
				return false;
			}
			index++;		// else just increment the index
			setBackEnabled(true);
			if (index == visited.size() - 1) setForwardEnabled(false);
			return true;
		}
		// if the given directory name is different from the next in the list, clear the list from present index onwards
		clearForwardList();
		setForwardEnabled(false);
		setBackEnabled(true);
		visited.add(dirname);
		index++;
		return true;
	}
	
	/**
	 * Resets the navigator to the parent directory
	 * @return
	 */
	public boolean back () {
		index--;
		setForwardEnabled(true);
		if (index == 0) {
			setBackEnabled(false);
		}
		if (visited.get(index + 1).equals(SEARCH))
			ISSState.panelInView.setSearchResultShowing(false);
		return index > 0;
	}
	
	/**
	 * Gets the current path in which the user is working 
	 * @return
	 */
	public String getCurrentPath () {
		return getCumulativePath(index);
	}
	
	/**
	 * Resets the status of the navigator to the initial state
	 */
	public void reset () {
		visited.removeAll(visited);
		index= -1;
		ISSGUIToolBar.getToolBar().setBackEnabled(false);
		ISSGUIToolBar.getToolBar().setForwardEnabled(false);
	}
	
	/**
	 * Sets the status of the back button to the boolean value given
	 * @param b
	 */
	private void setBackEnabled (boolean b) {
		ISSGUIToolBar.getToolBar().setBackEnabled(b);
		backEnabled= b;
	}
	
	/**
	 * Sets the status of the forward button to the boolean value given
	 * @param b
	 */
	private void setForwardEnabled (boolean b) {
		ISSGUIToolBar.getToolBar().setForwardEnabled(b);
		forwardEnabled= b;
	}

	public String getParentPath() {
		if (index > 0)
			return getCumulativePath(index - 1);
		else return null;
	}
	
	/*public void printNavigator () {
		for (int i= 0; i < visited.size(); i++)
			System.out.print(visited.get(i) + " ");
		System.out.println (index);
	}*/
}
