package de.waishon.droplibrary.Utils;

/**
 * Eine Utility Klasse mit nützlichen Funktionen
 * @author soeren
 *
 */
public class Utils {

	/**
	 * Wandelt ein Int zu einem ByteArray um.
	 * @param num 
	 * @return Der Integer als ByteArray
	 */
	public static byte[] intToByteArray(int num) {
		byte[] data = { (byte) ((num >> 8) & 0xff), (byte) (num & 0xff) };
		return data;
	}
}
