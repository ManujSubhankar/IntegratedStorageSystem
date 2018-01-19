package iss.fileserver;

/**
 * Exception that is thrown by the Metadata Server if the request path is not accessible for the user.
 *
 * @author Basanta Sharma
 */
public class PermissionDeniedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public PermissionDeniedException () {
		super();
	}
	
	public PermissionDeniedException (String message) {
		super (message);
	}

}
