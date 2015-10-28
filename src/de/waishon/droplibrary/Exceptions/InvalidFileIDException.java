package de.waishon.droplibrary.Exceptions;

/**
 * InvalidFileIDException
 * @author Waishon
 *
 */
public class InvalidFileIDException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidFileIDException() {
		super();
	}
	
	public InvalidFileIDException(String msg) {
		super(msg);
	}
}
