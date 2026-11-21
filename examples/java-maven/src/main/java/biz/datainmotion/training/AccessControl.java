package biz.datainmotion.training;

import java.util.Map;
import java.util.Optional;

/**
 * Broken Access Control — OWASP A01, IDOR/BOLA (CWE-639: Authorization Bypass
 * Through User-Controlled Key).
 *
 * <p>Beispiel: die Admin-Oberfläche einer Terminbuchung. Die Sachbearbeitung
 * eines Amts darf nur Buchungen des eigenen Amts sehen.
 *
 * <p>Kernregel: niemals der client-gelieferten ID/Rolle trauen — serverseitig
 * gegen die angemeldete Person prüfen, deny-by-default.
 */
public class AccessControl {

    /** Skizze einer Buchung, die zu einem Amt gehört. */
    public record Buchung(long id, String amt, String anliegenText) {}

    private final Map<Long, Buchung> buchungen;

    public AccessControl(Map<Long, Buchung> buchungen) {
        this.buchungen = buchungen;
    }

    /**
     * UNSICHER — IDOR: liefert jede Buchung allein anhand der ID aus dem
     * Request. Wer IDs hochzählt, liest auch Buchungen fremder Ämter.
     */
    @Deprecated
    public Buchung getInsecure(long id) {
        return buchungen.get(id); // keine Prüfung, zu welchem Amt die Buchung gehört
    }

    /**
     * SICHER — das Objekt an das Amt der angemeldeten Person binden; sonst kein
     * Treffer (deny-by-default). Die ID allein genügt nicht.
     */
    public Buchung get(long id, String amtDerAngemeldetenPerson) {
        return Optional.ofNullable(buchungen.get(id))
                .filter(buchung -> buchung.amt().equals(amtDerAngemeldetenPerson))
                .orElseThrow(() -> new SecurityException("Kein Zugriff"));
    }
}
