package biz.datainmotion.training;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Passwörter speichern — OWASP A02 (Cryptographic Failures), CWE-916.
 *
 * <p>Passwörter NIE im Klartext und NIE mit schnellen Hashes (MD5/SHA-*)
 * ablegen — die lassen sich per GPU/Rainbow-Table massenhaft zurückrechnen.
 * Stattdessen ein <em>langsamer</em>, gesalzener Passwort-Hash.
 *
 * <p>BSI TR-02102-1 / OWASP empfehlen Argon2id (bevorzugt), bcrypt oder
 * PBKDF2. Argon2/bcrypt brauchen eine Library — hier PBKDF2, weil im JDK
 * enthalten und damit ohne Zusatz-Dependency lauffähig.
 */
public class PasswordHashing {

    private static final SecureRandom RNG = new SecureRandom();
    private static final int ITERATIONS = 210_000; // OWASP-Richtwert für PBKDF2-HMAC-SHA256
    private static final int KEY_BITS = 256;

    /**
     * UNSICHER — schneller, ungesalzener Hash. Gleiche Passwörter ergeben
     * gleiche Hashes; per Rainbow-Table trivial umkehrbar. NICHT verwenden.
     */
    @Deprecated
    public String hashInsecure(String password) throws NoSuchAlgorithmException {
        var md = java.security.MessageDigest.getInstance("MD5");
        return HexFormat.of().formatHex(md.digest(password.getBytes()));
    }

    /**
     * SICHER — pro Passwort zufälliges Salt + bewusst teurer KDF (PBKDF2).
     * Ergebnis als {@code salt:hash} ablegen; bcrypt/Argon2id sind gleichwertig
     * oder besser, wenn eine Library erlaubt ist.
     */
    public String hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        HexFormat hex = HexFormat.of();
        return hex.formatHex(salt) + ":" + hex.formatHex(hash);
    }

    /** Verifikation in konstanter Zeit gegen den gespeicherten {@code salt:hash}. */
    public boolean verify(String password, String stored)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = stored.split(":");
        HexFormat hex = HexFormat.of();
        byte[] salt = hex.parseHex(parts[0]);
        byte[] expected = hex.parseHex(parts[1]);
        byte[] actual = pbkdf2(password.toCharArray(), salt);
        return java.security.MessageDigest.isEqual(expected, actual); // timing-sicher
    }

    private static byte[] pbkdf2(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();
    }
}
