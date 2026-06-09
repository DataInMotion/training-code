package biz.datainmotion.training;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Race Condition / TOCTOU — Time-of-Check / Time-of-Use, CWE-367.
 *
 * <p>Prüfen und Handeln müssen <em>atomar</em> sein. Zwischen „Guthaben reicht?"
 * (Check) und „abbuchen" (Use) kann ein zweiter Thread denselben Pfad nehmen:
 * beide bestehen die Prüfung, beide buchen ab → Konto überzogen (Double-Spend).
 *
 * <p>Jeweils: unsicher (Check und Use getrennt) vs. sicher (Check und Use als
 * eine atomare Operation — per Lock oder thread-sicherer Collection).
 */
public class AccountService {

    private long balance;
    private final ConcurrentHashMap<String, String> reservations = new ConcurrentHashMap<>();

    public AccountService(long initialBalance) {
        this.balance = initialBalance;
    }

    /**
     * UNSICHER — Check und Use sind getrennt. Zwei Threads können beide die
     * Prüfung bestehen, bevor einer abbucht → Überziehung. NICHT verwenden.
     */
    @Deprecated
    public boolean withdrawInsecure(long amount) {
        if (balance >= amount) {        // CHECK
            // Kontextwechsel hier → zweiter Thread besteht dieselbe Prüfung
            balance -= amount;          // USE
            return true;
        }
        return false;
    }

    /**
     * SICHER — Check und Use laufen unter einem Lock als <em>eine</em> atomare
     * Operation; kein zweiter Thread kann dazwischenfunken.
     */
    public synchronized boolean withdraw(long amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public synchronized long balance() {
        return balance;
    }

    /**
     * UNSICHER — {@code containsKey} (Check) und {@code put} (Use) getrennt:
     * zwei Threads reservieren denselben Namen gleichzeitig. NICHT verwenden.
     */
    @Deprecated
    public boolean reserveInsecure(String name, String owner) {
        if (!reservations.containsKey(name)) {   // CHECK
            reservations.put(name, owner);        // USE
            return true;
        }
        return false;
    }

    /**
     * SICHER — {@code putIfAbsent} prüft und setzt in einem atomaren Schritt;
     * gibt {@code null} zurück, wenn der Name noch frei war.
     */
    public boolean reserve(String name, String owner) {
        return reservations.putIfAbsent(name, owner) == null;
    }
}
