package de.waishon.droplibrary.Exceptions;

/**
 * InvalidFileSizeException
 * @author Waishon
 *
 */
public class InvalidFileSizeException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidFileSizeException() {
		super();
	}
	
	public InvalidFileSizeException(String msg) {
		super(msg);
	}
}

