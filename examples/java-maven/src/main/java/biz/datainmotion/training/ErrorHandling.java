package biz.datainmotion.training;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Fehlerbehandlung — unsaubere Behandlung exponiert Interna (CWE-209:
 * Error Message Containing Sensitive Information).
 */
public class ErrorHandling {

    private static final Logger LOG = Logger.getLogger(ErrorHandling.class.getName());

    /**
     * UNSICHER — leakt Dateipfad, Klassennamen, ggf. Systemdetails an den Aufrufer
     * und „macht weiter" (fail open).
     */
    @Deprecated
    public String readInsecure(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (Exception e) {
            return "Fehler: " + e + " bei " + Path.of(path).toAbsolutePath();
        }
    }

    /**
     * SICHER — Detail nur intern loggen, generische Meldung nach außen,
     * im Fehlerfall sicher abbrechen (fail closed).
     */
    public String read(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            LOG.warning("Lesen fehlgeschlagen"); // ohne Pfad/PII; Stacktrace nur intern
            throw new IllegalStateException("Anfrage konnte nicht verarbeitet werden");
        }
    }
}
