package iss.fileserver;

/**
 * Exception that is thrown by the Metadata Server if invalid user tries to access the metadata
 *
 * @author Basanta Sharma
 */
public class InvalidSessionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidSessionException () {
		super();
	}
	
	public InvalidSessionException (String message) {
		super (message);
	}
}
