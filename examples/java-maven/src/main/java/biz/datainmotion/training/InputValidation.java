package biz.datainmotion.training;

import java.util.regex.Pattern;
import org.owasp.encoder.Encode;

/**
 * Input-Validierung & Output-Encoding — OWASP A03 (Injection/XSS).
 * Allowlist statt Blocklist; bei der Ausgabe kontextgerecht kodieren.
 */
public class InputValidation {

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_]{3,32}");

    /**
     * UNSICHER (ReDoS) — verschachtelter Quantor {@code ([...]+)+}: für dieselbe
     * Eingabe gibt es exponentiell viele Match-Pfade. Bei einer Eingabe, die fast
     * passt aber am Ende scheitert (viele gültige Zeichen, kein {@code @}), läuft
     * Javas Backtracking-Engine in „catastrophic backtracking" — ein einziger
     * Request mit ~40 Zeichen blockiert einen Thread sekunden- bis minutenlang.
     * Allowlist-Validierung ist richtig, aber eine schlecht gebaute Regex führt
     * eine neue DoS-Klasse ein (A03 trifft Verfügbarkeit).
     */
    private static final Pattern EMAIL_REDOS =
            Pattern.compile("^([A-Za-z0-9._%+-]+)+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** SICHER — lineare Regex ohne verschachtelte Quantoren; zusätzlich Längen-Cap. */
    private static final Pattern EMAIL =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    /**
     * UNSICHER — Blocklist: verbietet ein paar Zeichen, lässt aber fast alles
     * durch (Umgehungen, neue Angriffsvektoren, Encoding-Tricks).
     */
    @Deprecated
    public boolean isValidInsecure(String input) {
        return input != null && !input.contains("<") && !input.contains("'");
    }

    /** SICHER — strikte Allowlist: nur die erlaubte Form gilt als gültig. */
    public boolean isValid(String input) {
        return input != null && USERNAME.matcher(input).matches();
    }

    /** UNSICHER (ReDoS) — siehe {@link #EMAIL_REDOS}: katastrophales Backtracking. */
    @Deprecated
    public boolean isEmailInsecure(String input) {
        return input != null && EMAIL_REDOS.matcher(input).matches();
    }

    /**
     * SICHER — lineare Regex, plus Längenbegrenzung als Defense-in-Depth: die
     * Eingabe wird vor dem Matchen begrenzt, damit pathologische Eingaben gar
     * nicht erst in die Engine gelangen.
     */
    public boolean isEmail(String input) {
        return input != null && input.length() <= 254 && EMAIL.matcher(input).matches();
    }

    /**
     * SICHER — kontextgerechtes Encoding mit **OWASP Java Encoder** statt
     * Eigenbau. Eine handgerollte {@code replace}-Kette übersieht leicht
     * Kontexte (HTML-Attribut, JavaScript, URL) — dafür gibt es je eine Methode:
     * {@code Encode.forHtmlAttribute(..)}, {@code Encode.forJavaScript(..)},
     * {@code Encode.forUriComponent(..)}.
     */
    public String escapeHtml(String value) {
        return Encode.forHtml(value);
    }
}
