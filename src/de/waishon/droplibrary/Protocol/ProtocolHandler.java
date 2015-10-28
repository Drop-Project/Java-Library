package de.waishon.droplibrary.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.waishon.droplibrary.SSLConnection.Transmission;

/**
 * Verwaltet das Kommunikationsprotokoll
 * @author soeren
 *
 */
public class ProtocolHandler extends Transmission {

	private static final int PROTOCOL_VERSION = 1;
	
	/**
	 * Konstruktor
	 * Initialisiert die erweiterte {@see Transmission}Transmission Klasse
	 * @param inputStream Der übergebene InputStream des SocketHandlers
	 * @param outputStream Der übergebene OutputStream des SocketHandlers
	 */
	public ProtocolHandler(InputStream inputStream, OutputStream outputStream) {
		super(inputStream, outputStream);
	}

	/**
	 * Sendet die Initalisation für den Server
	 * @throws IOException
	 */
	public void sendInitialisation() throws IOException {
		// ByteOutputStream erstellen
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		// ProtocolVersion hinzufügen
		byteStream.write(PROTOCOL_VERSION);
		
		try {
			byteStream.write(System.getProperty("user.name").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Daten senden
		sendPackage(byteStream.toByteArray());
	}
	
	
}
