package biz.datainmotion.training.authservice;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** Verschlüsselt einzelne Felder, bevor sie in die Datenbank geschrieben werden. */
public final class FieldCipher {

    private static final byte[] KEY = "1234567890123456".getBytes();

    private FieldCipher() {
    }

    public static byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"));
        return cipher.doFinal(plaintext);
    }
}
