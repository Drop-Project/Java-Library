package de.waishon.droplibrary.Protocol;

import java.io.File;

/**
 * Speichert alle Informationen zu einer Datei
 * @author Waishon
 *
 */
public class FileHandler {

	private boolean lastFile;
	private int fileID;
	private File file;
	
	public FileHandler(boolean lastFile, int fileID, File file) {
		this.lastFile = lastFile;
		this.fileID = fileID;
		this.file = file;
	}
	
	public boolean isLastFile() {
		return lastFile;
	}
	public int getFileID() {
		return fileID;
	}
	
	public File getFile() {
		return file;
	}
	
}
