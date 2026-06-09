package biz.datainmotion.training;

import java.io.IOException;
import java.util.List;

/**
 * Externe Prozesse mit benutzergesteuerten Daten — Command Injection, CWE-78.
 *
 * <p>Wird Benutzereingabe in eine Shell-Kommandozeile konkateniert, kann sie
 * über {@code ;}, {@code |} oder {@code $(...)} eigene Befehle einschleusen.
 *
 * <p>Schutz: keine Shell aufrufen, sondern Programm + Argumente als getrennte
 * Liste an {@link ProcessBuilder} geben — Argumente werden dann nie als
 * Kommando interpretiert (analog zum Prepared Statement bei SQL).
 */
public class CommandExecution {

    /**
     * UNSICHER — baut eine Shell-Zeile aus der Eingabe.
     * host = {@code example.com; rm -rf /} führt fremde Befehle aus. NICHT verwenden.
     */
    @Deprecated
    public Process pingInsecure(String host) throws IOException {
        return Runtime.getRuntime().exec(new String[] {"sh", "-c", "ping -c1 " + host});
    }

    /**
     * SICHER — Argument-Liste ohne Shell; der Host ist immer genau ein Argument
     * und kann den Befehl nicht verändern. Zusätzlich strikt validieren.
     */
    public Process ping(String host) throws IOException {
        if (!host.matches("[A-Za-z0-9.-]{1,253}")) { // Allowlist: nur Hostname-Zeichen
            throw new IllegalArgumentException("Ungültiger Hostname");
        }
        return new ProcessBuilder(List.of("ping", "-c1", host)).start();
    }
}
