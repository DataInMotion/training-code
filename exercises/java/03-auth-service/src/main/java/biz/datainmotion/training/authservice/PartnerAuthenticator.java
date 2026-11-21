package biz.datainmotion.training.authservice;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Prüft, ob eine Anfrage von einem angebundenen Partnersystem stammt. */
public final class PartnerAuthenticator {

    private final byte[] webhookSecret;

    public PartnerAuthenticator(byte[] webhookSecret) {
        this.webhookSecret = webhookSecret.clone();
    }

    /** Prüft den API-Schlüssel, den ein Partner im Header mitschickt. */
    public boolean checkApiKey(String provided, String expected) {
        return provided.equals(expected);
    }

    /** Prüft die Signatur eines eingehenden Webhooks (HMAC-SHA256, hexadezimal). */
    public boolean verifyWebhook(String body, String signatureHex) throws GeneralSecurityException {
        byte[] provided;
        try {
            provided = HexFormat.of().parseHex(signatureHex);
        } catch (IllegalArgumentException e) {
            return false;
        }
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(webhookSecret, "HmacSHA256"));
        byte[] expected = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
        return MessageDigest.isEqual(expected, provided);
    }
}
