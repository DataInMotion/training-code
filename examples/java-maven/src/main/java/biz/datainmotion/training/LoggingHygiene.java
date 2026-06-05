package biz.datainmotion.training;

import java.util.logging.Logger;

/**
 * Logging-Hygiene — Log-Injection/Log-Forging (CWE-117) und sensible Daten im
 * Klartext (OWASP A09). Eingaben gehören neutralisiert, Secrets/PII maskiert.
 *
 * <p><b>Der korrekte „Do" liegt in der Logging-Konfiguration, nicht im App-Code:</b>
 * CR/LF zentral im Appender neutralisieren —
 * Logback {@code <encoder><pattern>%replace(%msg){'[\r\n]','_'}</pattern></encoder>}
 * bzw. der {@code CRLFLogConverter}, oder Log4j2 {@code %encode{%msg}{CRLF}}.
 * Für Maskierung sensibler Felder: <b>OWASP Security Logging</b>
 * ({@code org.owasp:security-logging-logback}) mit dem {@code CONFIDENTIAL}-Marker.
 * Die Methoden unten sind nur eine app-seitige Skizze / letzte Verteidigungslinie.
 */
public class LoggingHygiene {

    private static final Logger LOG = Logger.getLogger(LoggingHygiene.class.getName());

    /**
     * UNSICHER — roher User-Input im Log: CR/LF erlauben gefälschte Zeilen
     * (z. B. {@code username = "admin\n[INFO] Zugriff erlaubt"}); PII landet im Klartext.
     */
    @Deprecated
    public void logInsecure(String username) {
        LOG.info("Login: " + username);
    }

    /** SICHER — CR/LF neutralisieren; nur das Nötige loggen, keine Secrets/PII. */
    public void log(String username) {
        String safe = username.replaceAll("[\\r\\n]", "_");
        LOG.info(() -> "Login: " + safe);
    }

    /** SICHER — sensible Werte maskieren statt im Klartext zu loggen. */
    public static String mask(String secret) {
        if (secret == null || secret.length() < 4) {
            return "***";
        }
        return secret.substring(0, 2) + "***";
    }
}
