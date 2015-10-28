package de.waishon.droplibrary.SSLConnection;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Veränderter Trustmanager, der jedliche Zertifikate akzeptiert
 * @author Waishon
 *
 */
public class DefaultTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	/**
	 * Erstellt eine SSLSocketFactory, die alle Zertifikate akzeptiert
	 * @param protocol
	 * @return SSLSocketFactory
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException 
	 */
	public SSLSocketFactory createSSLFactory(String protocol) throws NoSuchAlgorithmException, KeyManagementException {
		// SSL Context mit Protocol erstellen
		SSLContext sslContext = SSLContext.getInstance(protocol);
		
		// TrustManager Array dieser Klasse erstellen
		TrustManager[] byPassTrustManager = new TrustManager[] {this};
		
		// SSLContext initalisieren
		sslContext.init(null, byPassTrustManager, new SecureRandom());
		
		// SocketFactory mit SSLContext zurückgeben
		return sslContext.getSocketFactory();
	}
}
