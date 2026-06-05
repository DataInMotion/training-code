package biz.datainmotion.training;

import java.util.ArrayList;
import java.util.List;

/**
 * Ressourcen-Erschöpfung (DoS) — CWE-674 (unkontrollierte Rekursion),
 * CWE-400 (unkontrollierter Ressourcenverbrauch).
 *
 * <p>Jeweils: unsicher (skizziert das Problem) vs. sicher (mit Obergrenze).
 */
public class ResourceSafety {

    private static final int MAX_DEPTH = 1_000;
    private static final int MAX_ITEMS = 10_000;

    /** UNSICHER — unbegrenzte Rekursion → {@code StackOverflowError} bei feindlicher Eingabe. */
    @Deprecated
    public int sumInsecure(int n) {
        return n == 0 ? 0 : n + sumInsecure(n - 1); // keine Tiefenbegrenzung
    }

    /** SICHER — Eingabe begrenzt und iterativ statt rekursiv gelöst. */
    public int sum(int n) {
        if (n < 0 || n > MAX_DEPTH) {
            throw new IllegalArgumentException("Eingabe außerhalb des erlaubten Bereichs");
        }
        int total = 0;
        for (int i = 1; i <= n; i++) {
            total += i;
        }
        return total;
    }

    /** UNSICHER — sammelt unbegrenzt; großer/feindlicher Input sprengt den Speicher. */
    @Deprecated
    public List<String> collectInsecure(Iterable<String> source) {
        List<String> all = new ArrayList<>();
        for (String s : source) {
            all.add(s); // kein Limit
        }
        return all;
    }

    /** SICHER — feste Obergrenze; lehnt Überlauf kontrolliert ab. */
    public List<String> collect(Iterable<String> source) {
        List<String> result = new ArrayList<>();
        for (String s : source) {
            if (result.size() >= MAX_ITEMS) {
                throw new IllegalStateException("Eingabe überschreitet das Limit von " + MAX_ITEMS);
            }
            result.add(s);
        }
        return result;
    }
}
