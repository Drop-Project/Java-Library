package de.waishon.droplibrary.Listener;

import java.util.EventListener;

/**
 * Verwaltet den Empfang von der Confirmation
 * @author Waishon
 *
 */
public interface ConfirmationReceivedListener extends EventListener {
	void requestAccepted(boolean accepted);
	void acceptedFiles(int[] ids);
}
