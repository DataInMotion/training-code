package biz.datainmotion.training;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Datei-Zugriff mit benutzergesteuertem Namen — Path Traversal, CWE-22.
 *
 * <p>Ein Dateiname aus dem Request darf nie ungeprüft an das Dateisystem
 * gereicht werden: {@code ../../etc/passwd} bricht aus dem Zielordner aus.
 *
 * <p>Schutz: gegen einen festen Basis-Ordner auflösen, normalisieren und
 * prüfen, dass das Ergebnis weiterhin <em>innerhalb</em> der Basis liegt.
 */
public class FileDownload {

    private final Path baseDir;

    public FileDownload(Path baseDir) {
        this.baseDir = baseDir.toAbsolutePath().normalize();
    }

    /**
     * UNSICHER — hängt den rohen Namen an die Basis; {@code ../} entkommt
     * dem Ordner und liest beliebige Dateien. NICHT verwenden.
     */
    @Deprecated
    public byte[] readInsecure(String filename) throws IOException {
        return Files.readAllBytes(baseDir.resolve(filename));
    }

    /**
     * SICHER — auflösen, normalisieren und Containment gegen die Basis prüfen;
     * alles außerhalb wird abgelehnt (deny-by-default).
     */
    public byte[] read(String filename) throws IOException {
        Path target = baseDir.resolve(filename).normalize();
        if (!target.startsWith(baseDir)) {
            throw new IllegalArgumentException("Pfad außerhalb des erlaubten Verzeichnisses");
        }
        return Files.readAllBytes(target);
    }
}
