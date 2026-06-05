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
