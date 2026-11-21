package biz.datainmotion.training.authservice;

import java.security.SecureRandom;
import java.util.Base64;

/** Erzeugt Kennungen für Sitzungen. */
public final class SessionIds {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int BYTES = 32;

    private SessionIds() {
    }

    public static String next() {
        byte[] bytes = new byte[BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
