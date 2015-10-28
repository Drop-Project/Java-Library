package de.waishon.droplibrary.SSLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Verwaltet die SSL Kommunikation
 * @author Waishon
 *
 */
public class SSLHandler {

	/**
	 * Der SSLSocket der Kommunikation
	 */
	SSLSocket sslSocket;
	
	/**
	 * Erstellt eine neue SSL/TLS Verbindung zum Drop-Server
	 * @throws IOExceptio 
	 * @throws UnknownHostException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public void createSecureSocket(String ip, int port) throws UnknownHostException, IOException, KeyManagementException, NoSuchAlgorithmException {
		// Standard SSLSocketFactory speichern
		SSLSocketFactory factory = (SSLSocketFactory) new DefaultTrustManager().createSSLFactory("TLS");
		
		sslSocket = (SSLSocket) factory.createSocket(ip, port);
 	}
	
	/**
	 * Gibt den Outputstream zurück
	 * @return Der Outputstream der Kommunikation
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		return sslSocket.getOutputStream();
	}
	
	/**
	 * Gibt den Inputstream zurück
	 * @return Der Inputstream der Kommunikation
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return sslSocket.getInputStream();
	}
	
}
