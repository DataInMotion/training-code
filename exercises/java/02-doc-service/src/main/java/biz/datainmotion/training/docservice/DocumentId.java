package biz.datainmotion.training.docservice;

import java.util.regex.Pattern;

/** Kennung eines Dokuments: Kleinbuchstaben, Ziffern und Bindestriche, 3 bis 64 Zeichen. */
public record DocumentId(String value) {

    private static final Pattern FORMAT = Pattern.compile("[a-z0-9][a-z0-9-]{2,63}");

    public DocumentId {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("ungültige Dokumentkennung");
        }
    }

    String fileName() {
        return value + ".bin";
    }
}
