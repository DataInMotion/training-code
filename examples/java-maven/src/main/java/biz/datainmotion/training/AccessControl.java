package biz.datainmotion.training;

import java.util.Map;
import java.util.Optional;

/**
 * Broken Access Control — OWASP A01, IDOR/BOLA (CWE-639: Authorization Bypass
 * Through User-Controlled Key).
 *
 * <p>Kernregel: niemals der client-gelieferten ID/Rolle trauen — serverseitig
 * gegen den authentifizierten Nutzer prüfen, deny-by-default.
 */
public class AccessControl {

    /** Skizze einer Bestellung mit Eigentümer. */
    public record Order(long id, String owner, String content) {}

    private final Map<Long, Order> orders;

    public AccessControl(Map<Long, Order> orders) {
        this.orders = orders;
    }

    /**
     * UNSICHER — IDOR: liefert jede Bestellung allein anhand der ID aus dem
     * Request. Ein Nutzer kann fremde Bestellungen lesen (Object-Level-Bypass).
     */
    @Deprecated
    public Order getInsecure(long id) {
        return orders.get(id); // keine Eigentümer-Prüfung
    }

    /**
     * SICHER — das Objekt an den authentifizierten Eigentümer binden; sonst kein
     * Treffer (deny-by-default). Die ID allein genügt nicht.
     */
    public Order get(long id, String currentUser) {
        return Optional.ofNullable(orders.get(id))
                .filter(order -> order.owner().equals(currentUser))
                .orElseThrow(() -> new SecurityException("Kein Zugriff"));
    }
}
