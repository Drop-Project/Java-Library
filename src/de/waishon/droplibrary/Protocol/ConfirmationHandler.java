package de.waishon.droplibrary.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import de.waishon.droplibrary.Listener.ConfirmationReceivedListener;
import de.waishon.droplibrary.SSLConnection.Transmission;

/**
 * Handelt den Confirmation Empfang
 * @author Waishon
 *
 */
public class ConfirmationHandler extends Transmission {

	/**
	 * Enthällt alle Listener der ConfirmationHandler Klasse
	 */
	private ArrayList<ConfirmationReceivedListener> listeners = new ArrayList<ConfirmationReceivedListener>();
	
	/**
	 * Konsturktor
	 * Initialisiert die Streams
	 * @param inputStream
	 * @param outputStream
	 */
	public ConfirmationHandler(InputStream inputStream,OutputStream outputStream) {
		super(inputStream, outputStream);
	}
	
	/**
	 * Fügt einen Listener hinzu, um die Daten zurückzugeben
	 * @param listener Ein neuer ConformationReceivedListener
	 */
	public void addListener(ConfirmationReceivedListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Wartet auf eine Antwort und gibt es den Listenern zurück
	 * @throws IOException 
	 */
	public void waitForConfirmation() throws IOException {
		parseResponse(receivePackage());
	}
	
	/**
	 * Ermittelt die Daten aus dem Server Response und gibt die Daten an den Listener zurück
	 * @param data Die Empfangenen Daten
	 */
	private void parseResponse(byte[] data) {
		// Neue ArrayList den akzeptierten Datein
		ArrayList<Integer> confirmationData = new ArrayList<Integer>();
		
		// Die Daten zusammensetzten und in die ArrayList speichern 
		{
			for(int j = 1; j <data.length; j+=2) {
				confirmationData.add((data[j] << 8) + data[j+1]);
			}
		}
		
		// Die Listener benachrichtigen
		for(ConfirmationReceivedListener listener: listeners) {
			listener.requestAccepted(data[0] == 1 ? true : false);
			listener.acceptedFiles(ArrayUtils.toPrimitive(confirmationData.toArray(new Integer[confirmationData.size()])));
		}
	}
}
