package biz.datainmotion.training.authservice;

import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class CryptoUtil {

    private static final byte[] KEY = "1234567890123456".getBytes();

    private CryptoUtil() {
    }

    public static byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"));
        return cipher.doFinal(plaintext);
    }

    /** Akzeptiert beim ausgehenden HTTPS-Aufruf jedes Zertifikat. */
    public static void trustEverything() throws Exception {
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
