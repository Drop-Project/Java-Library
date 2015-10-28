package de.waishon.droplibrary.Listener;

import java.util.EventListener;

/**
 * Wird ausgeführt, wenn Daten ankommen
 * @author Waishon
 *
 */
public interface DataReceivedListener extends EventListener {
	void dataReceived(int packetLength, byte[] data);
}
