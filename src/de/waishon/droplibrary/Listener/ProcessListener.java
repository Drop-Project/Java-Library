package de.waishon.droplibrary.Listener;

/**
 * Verwaltet den UploadProzess
 * @author Waishon
 *
 */
public interface ProcessListener {
	void processChanged(int percentage, long kiloByte);
}
