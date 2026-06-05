package biz.datainmotion.training;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;

/**
 * Unsichere Deserialisierung — OWASP A08, CWE-502. Aus nicht vertrauenswürdigen
 * Bytes ein Objekt zu rekonstruieren kann <em>beim Rekonstruieren</em> Code
 * ausführen (Gadget-Chains) — bevor eine einzige eigene Zeile läuft.
 *
 * <p>Beste Lösung: auf Untrusted Data gar nicht nativ deserialisieren, sondern
 * JSON/Text mit Typ-Allowlist. Wenn native Deserialisierung unvermeidbar ist,
 * mit einem strikten {@link ObjectInputFilter} (JEP 290) absichern.
 */
public class UnsafeDeserialization {

    /** UNSICHER — deserialisiert beliebige Typen aus Untrusted Data → RCE-Risiko. */
    @Deprecated
    public Object readInsecure(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject(); // beliebige Klassen werden instanziiert
        }
    }

    /**
     * SICHER(er) — strikte Allowlist: nur erlaubte Typen, alles andere wird
     * abgelehnt ({@code !*}). Für echte Untrusted Data dennoch JSON/Text bevorzugen.
     */
    public Object read(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            in.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "java.lang.String;java.util.*;!*")); // Allowlist, Rest verboten
            return in.readObject();
        }
    }
}
