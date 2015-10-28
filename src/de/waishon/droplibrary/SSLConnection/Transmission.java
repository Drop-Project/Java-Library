package de.waishon.droplibrary.SSLConnection;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.waishon.droplibrary.Utils.Utils;

/**
 * Verwaltet die Socket Kommunikation auf Byte-Ebene
 * @author soeren
 *
 */
public abstract class Transmission {
	
	// InputStream
	InputStream inputStream;
	
	// OutputStream
	OutputStream outputStream;
	
	/**
	 * Konsturktor
	 * Initialisiert die Streams
	 * @param inputStream
	 * @param outputStream
	 */
	public Transmission(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	/**
	 * Sendet ein Package als ByteArray
	 * @see <a href="http://bazaar.launchpad.net/~l-admin-3/drop/trunk/view/head:/PROTOCOL">PROTOCOL</a>
	 * @param data
	 * @throws IOException
	 */
	public void sendPackage(byte[] data) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		byte[] packageLength = Utils.intToByteArray(data.length);
		
		
		byteStream.write(packageLength);
		byteStream.write(data);
		outputStream.write(byteStream.toByteArray());
	}
	
	/**
	 * Empfängt ein Package als ByteArray
	 * @see <a href="http://bazaar.launchpad.net/~l-admin-3/drop/trunk/view/head:/PROTOCOL">PROTOCOL</a>
	 * @return Das ByteArray vom Server
	 * @throws IOException
	 */
	public byte[] receivePackage() throws IOException {
		byte[] header = new byte[2];
		System.out.println("JO");
		byte[] data = null;
		
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		dataInputStream.readFully(header, 0, header.length);
			
		int packageLength = (header[0] << 8) + header[1];
		data = new byte[packageLength];
			
		dataInputStream.readFully(data, 0, data.length);
		
		return data;
	}
}
