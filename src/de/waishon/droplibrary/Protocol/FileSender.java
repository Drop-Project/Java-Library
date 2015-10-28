package de.waishon.droplibrary.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.waishon.droplibrary.Listener.ProcessListener;
import de.waishon.droplibrary.SSLConnection.Transmission;
import de.waishon.droplibrary.Utils.Utils;

/**
 * Verwaltet die komplette Datei Kommunikation
 * @author Waishon
 *
 */
public class FileSender extends Transmission {

	private static final long MAX_FILE_SIZE = 109951162775L;

	private static final int MAX_FILE_ID = 65535;
	
	private static final int MAX_PACKAGE_LENGTH = 16384;
	
	/**
	 * Gitb an, ob die letzte Datei bereits angegeben wurde.
	 * Standardwert: {@value}
	 */
	private boolean lastFileAlreadySet = false;
			
	/**
	 * Enthält jede Datei, die als Key mit ihrere FileID referenziert wird
	 */
	private HashMap<Integer, FileHandler> fileHandlerList = new HashMap<Integer, FileHandler>();
	
	/**
	 * Enthält alle Listener
	 */
	private ArrayList<ProcessListener> listeners = new ArrayList<ProcessListener>();
	
	/**
	 * Enthält die aktuelle FileID
	 * Inkrementiert sich für jede neue Datei
	 */
	private int currentFileID = 0;
	
	/**
	 * Konsturktor
	 * Initialisiert die Streams für die Kommunikation mit dem Server
	 * @param inputStream Der Eingangsstream zum Server
	 * @param outputStream Der Ausgangsstream zum Server
	 */
	public FileSender(InputStream inputStream, OutputStream outputStream) {
		super(inputStream, outputStream);
	}
	
	/**
	 * Fügt einen Listener hinzu, der den aktuellen SendeStatus zurück gibt
	 * @param listener Ein neuer ConformationReceivedListener
	 */
	public void addListener(ProcessListener listener) {
		listeners.add(listener);
	}

	/**
	 * Fügt einen FileHandler zur Liste der zu sendenen Datein hinzu
	 * Bei mehreren Datein kann auch {@link #addMultipleFiles(FileHandler... handlers) addMultipleFiles} genutzt werden.
	 * @param handler Der Filehandler, der hinzugefügt werden soll
	 * @throws FileNotFoundException
	 */
	public void addFile(FileHandler handler) throws FileNotFoundException {
		// Wenn die Datei nicht existiert, false zurückgeben
		if(!handler.getFile().exists()) {
			throw new FileNotFoundException("Filename " + handler.getFile().getAbsolutePath() + File.separator + handler.getFile() + " not found");
		}
		
		// Prüfen, ob die ID nicht zu groß ist
		if(handler.getFileID() > MAX_FILE_ID) {
			throw new FileNotFoundException("FileID not valid!");
		}
		
		// Datei zur Liste hinzufügen
		fileHandlerList.put(handler.getFileID(), handler);
	}
	
	/**
	 * Fügt eine Datei anhand des Pfades zur Liste der zu sendenen Datein hinzu
	 * @param path Der Pfad zur Datei <b>inklusive</b> des Dateinamens!
	 * @throws FileNotFoundException, IllegalArgumentException 
	 */
	public void addFile(String path, boolean lastFile) throws FileNotFoundException {
		// Prüfen, ob die letzte Datei bereits gesendet wurde
		if(lastFileAlreadySet) {
			throw new IllegalArgumentException("Es wurde bereits eine letzte Datei gesendet. Führe #reset() aus, um den Speicher zu löschen.");
		}
		
		// Falls die ID, bereits vergeben ist, was nicht passieren sollte, die nächste Freie nehmen
		while(fileHandlerList.containsKey(currentFileID)) {
			currentFileID++;
		}
		
		// Neue Datei erstellen
		File file = new File(path);
		
		// Prüfen, ob die Datei existiert
		if(!file.exists()) {
			throw new FileNotFoundException("Filename " + path+ " not found");
		}
		
		// Prüfen, ob die ID nicht zu groß ist
		if(currentFileID > MAX_FILE_ID) {
			throw new FileNotFoundException("FileID not valid!");
		}
		
		// Neuen FileHandler erstellen
		fileHandlerList.put(currentFileID, new FileHandler(lastFile, currentFileID, file));
		
		// FileID inkrementieren
		currentFileID++;
	}
	
	/**
	 * Fügt mehrere FileHandler gleichzeitig zur Liste der zu sendenen Datein hinzu.
	 * Ist äquivalent zu {@link #addFile(FileHandler handler) addFile}.
	 * @param handlers	Die FileHandler, die gesendet werden sollen
	 * @throws FileNotFoundException
	 */
	public void addMultipleFiles(FileHandler... handlers) throws FileNotFoundException {
		for(FileHandler handler : handlers) {
			try {
				addFile(handler);
			} catch(FileNotFoundException e) {
				throw new FileNotFoundException(e.getMessage());
			}
		}
	}
	
	/**
	 * Fügt mehrere Datein gleichzeitig zur Liste der zu sendenen Datein hinzu
	 * @param paths Ein Array mit Pfaden
	 * @throws FileNotFoundException
	 */
	public void addMultipleFiles(String... paths) throws FileNotFoundException {
		for(int i = 0; i < paths.length; i++) {
			try {
				// Ist die Datei größer, als die maximale größe?
				if(paths[i].length() > MAX_FILE_SIZE) {
					throw new FileNotFoundException("File too big.");
				}
				
				// Wenn es die letzte Datei ist, true übergeben
				if(i == paths.length) {
					addFile(paths[i], true);
				} else {
					addFile(paths[i], false);
				}
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException(e.getMessage());
			}
		}
	}
	
	/**
	 * Gibt einen FileHandler zurück
	 * @param lastFile Ist es die letzte Datei, die gesendet werden soll?
	 * @param fileID Gibt die FileID an
	 * @param path Den Pfad zur Datei
	 * @param fileName Der Dateinamen
	 * @return Neue FileHandler Instanz
	 */
	public FileHandler getFileHandler(boolean lastFile, int fileID, String path, String fileName) {
		// Neue Datei laden
		File file = new File(path + File.separator + fileName);
		
		// Wenn die Datei nicht existiert, null zurückgeben
		if(!file.exists()) {
			return null;
		}
		
		if(file.length() > MAX_FILE_SIZE) {
			return null;
		}
		
		// FileHandler zurückgeben
		return new FileHandler(lastFile, fileID, file);
	}
	
	/**
	 * Sendet den FileRequest mit den hinzugefügten Datein
	 * @throws IOException 
	 */
	public void sendFileRequest() throws IOException{
		for(Entry<Integer, FileHandler> fileHandler : fileHandlerList.entrySet()) {			
			
			// FileSize speichern
			long fileSize = fileHandler.getValue().getFile().length();
			
			int fileID = fileHandler.getValue().getFileID();
			
			// ByteArray erstellen
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			
			byteStream.write(fileHandler.getValue().isLastFile() ? 1 : 0);
			
			// FileID schreiben
			byteStream.write((byte) (fileID >> 8) & 0xff);
			byteStream.write((byte) fileID & 0xff);
			
			// FileSize schreiben
			byteStream.write((byte) (fileSize >> 32) & 0xff);
			byteStream.write((byte) (fileSize >> 24) & 0xff);
			byteStream.write((byte) (fileSize >> 16) & 0xff);
			byteStream.write((byte) (fileSize >> 8) & 0xff);
			byteStream.write((byte) fileSize & 0xff);
			
			byteStream.write(fileHandler.getValue().getFile().getName().getBytes());
			
			sendPackage(byteStream.toByteArray());
		}
	}
	
	/**
	 * Sendet alle hinzugefügten Datein
	 * Funktioniert nur, wenn die Anfrage vom Sender bestätigt wurde
	 * @throws IOException
	 */
	public void sendFiles() throws IOException {
		for(Entry<Integer, FileHandler> fileHandler : fileHandlerList.entrySet()) {
			
			// Datei laden
			File file = fileHandler.getValue().getFile();
			
			// Datei in Inputstream laden
			FileInputStream fileInputStream = new FileInputStream(file);
			
			long totalSize = file.length();
			long bytesSent = 0;
			
			// FileHeader senden
			byte[] fileHeader = Utils.intToByteArray(fileHandler.getValue().getFileID());
			sendPackage(fileHeader);
			
			int lastPercentage = 0;
			long start = System.currentTimeMillis();
			
			while(bytesSent < totalSize) {
				long nextSize = (totalSize - bytesSent);
				
				if(nextSize > MAX_PACKAGE_LENGTH) {
					nextSize = MAX_PACKAGE_LENGTH;
				}
				
				byte[] data = new byte[(int) nextSize];
				
				fileInputStream.read(data, 0, data.length);
				
				int percentage = (int) Math.round(((double) bytesSent)/totalSize*100);
				
				if(percentage != lastPercentage) {
					notifyListener(percentage, bytesSent/1000);
					lastPercentage = percentage;
				}
				
				bytesSent += nextSize;
				sendPackage(data);
			}
			
			fileInputStream.close();
			
			System.out.println("Fertig! - Geschwindigkeit:" + (System.currentTimeMillis() - start) + " Size:" + totalSize);
		}
	}
	/**
	 * 
	 * @param percentage
	 * @param kiloByte
	 */
	private void notifyListener(int percentage, long kiloByte) {
		// Die Listener benachrichtigen
		for(ProcessListener listener: listeners) {
			listener.processChanged(percentage, kiloByte);
		}
	}
}
