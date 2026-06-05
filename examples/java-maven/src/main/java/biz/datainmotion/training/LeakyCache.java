package biz.datainmotion.training;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Memory Leak — CWE-401. Ein unbegrenzt wachsender Cache hält Speicher für immer.
 */
public class LeakyCache {

    /** UNSICHER — statische, nie geleerte Map wächst unbegrenzt (klassischer Leak). */
    @Deprecated
    private static final Map<String, byte[]> LEAK = new HashMap<>();

    @Deprecated
    public static void cacheInsecure(String key, byte[] value) {
        LEAK.put(key, value); // wird nie entfernt → Speicher wächst unbegrenzt
    }

    /**
     * SICHER — LRU-Cache mit fester Obergrenze: der älteste Eintrag fällt heraus.
     * (Für Zeitbezug zusätzlich: TTL / Eviction-Policy einer Cache-Bibliothek.)
     */
    public static <K, V> Map<K, V> boundedCache(int maxEntries) {
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        };
    }
}
