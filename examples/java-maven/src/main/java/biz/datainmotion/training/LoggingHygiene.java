package biz.datainmotion.training;

import java.util.logging.Logger;
import org.owasp.encoder.Encode;

/**
 * Logging-Hygiene — Log-Injection/Log-Forging (CWE-117) und sensible Daten im
 * Klartext (OWASP A09). User-Input gehört vor dem Logging neutralisiert,
 * Secrets/PII maskiert.
 *
 * <p>Drei Stufen: kein Escape → Eigenbau → OWASP-Encoder.
 */
public class LoggingHygiene {

    private static final Logger LOG = Logger.getLogger(LoggingHygiene.class.getName());

    /**
     * UNSICHER (kein Escape) — roher User-Input im Log: CR/LF erlauben
     * gefälschte Zeilen (z. B. {@code username = "admin\n[INFO] Zugriff erlaubt"}),
     * PII landet im Klartext.
     */
    @Deprecated
    public void logNoEscape(String username) {
        LOG.info("Login: " + username);
    }

    /**
     * BESSER, aber Eigenbau — ersetzt nur CR/LF. Erfasst keine weiteren
     * Steuerzeichen und wird über den Code verteilt leicht inkonsistent.
     */
    public void logHandRolled(String username) {
        String safe = username.replaceAll("[\\r\\n]", "_");
        LOG.info(() -> "Login: " + safe);
    }

    /**
     * SICHER — OWASP Java Encoder: {@link Encode#forJava(String)} maskiert CR/LF
     * und weitere Steuerzeichen als Java-Escapes → kein Log-Forging, kein Eigenbau.
     * (Appender-seitige Alternative: OWASP Security Logging {@code CRLFLogConverter}.)
     */
    public void log(String username) {
        LOG.info(() -> "Login: " + Encode.forJava(username));
    }

    /** Sensible Werte zusätzlich maskieren statt im Klartext zu loggen. */
    public static String mask(String secret) {
        if (secret == null || secret.length() < 4) {
            return "***";
        }
        return secret.substring(0, 2) + "***";
    }
}
