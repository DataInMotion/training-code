package biz.datainmotion.training.docservice;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.zip.CRC32;

public final class Checksums {

    private Checksums() {
    }

    /** Prüfsumme eines Dokuments, mit der Abnehmer die Unverändertheit prüfen. */
    public static String checksum(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return HexFormat.of().formatHex(md.digest(data));
    }

    public static String sha256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(md.digest(data));
    }

    /** Schlüssel für den Vorschau-Cache im Arbeitsspeicher. */
    public static long cacheKey(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
}
