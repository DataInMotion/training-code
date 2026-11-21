package biz.datainmotion.training.authservice;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/** Richtet die ausgehenden HTTPS-Verbindungen zum Partnersystem ein. */
public final class PartnerClientConfig {

    private PartnerClientConfig() {
    }

    public static void apply() throws Exception {
        TrustManager[] trustManagers = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
