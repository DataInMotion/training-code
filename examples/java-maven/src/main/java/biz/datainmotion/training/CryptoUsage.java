package biz.datainmotion.training;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Kryptographie — OWASP A02/A04. Schwache Verfahren & hartkodierte Schlüssel
 * vs. Stand der Technik (BSI TR-02102: AES-256, authentifizierte Verschlüsselung).
 */
public class CryptoUsage {

    /** UNSICHER — hartkodierter Schlüssel (im Code = kompromittiert). */
    @Deprecated
    private static final byte[] HARDCODED_KEY = "1234567890123456".getBytes();

    /**
     * UNSICHER — AES im ECB-Modus: gleiche Klartextblöcke ergeben gleiche
     * Chiffrblöcke → Muster bleiben sichtbar; zudem keine Integrität.
     */
    @Deprecated
    public byte[] encryptInsecure(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(HARDCODED_KEY, "AES"));
        return cipher.doFinal(plaintext);
    }

    public record Sealed(byte[] nonce, byte[] ciphertext) {}

    /** SICHER — zufälliger AES-256-Schlüssel (in der Praxis aus KMS/Vault). */
    public SecretKey newKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        return generator.generateKey();
    }

    /**
     * SICHER — AES-256 im GCM-Modus (authentifizierte Verschlüsselung) mit
     * zufälligem Nonce je Nachricht. Nonce darf je Schlüssel nie wiederholt werden.
     */
    public Sealed encrypt(byte[] plaintext, SecretKey key) throws Exception {
        byte[] nonce = new byte[12];
        SecureRandom.getInstanceStrong().nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, nonce));
        return new Sealed(nonce, cipher.doFinal(plaintext));
    }
}
