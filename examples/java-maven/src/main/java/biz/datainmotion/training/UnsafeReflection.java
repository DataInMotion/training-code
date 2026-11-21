package biz.datainmotion.training;

/**
 * Unsafe Reflection — OWASP A05 (Injection), CWE-470: Use of
 * Externally-Controlled Input to Select Classes or Code.
 *
 * <p>Fließt ein Name aus dem Request in {@link Class#forName(String)}, wählt
 * die Eingabe aus, welcher Code läuft. Dieselbe Klasse wie Injection — und der
 * Mechanismus hinter Deserialisierungs-Gadget-Chains.
 */
public class UnsafeReflection {

    /** Exportiert Daten in ein bestimmtes Format. */
    public interface Exporter {
        String export(String data);
    }

    public static final class PdfExporter implements Exporter {
        @Override
        public String export(String data) {
            return "PDF:" + data;
        }
    }

    public static final class CsvExporter implements Exporter {
        @Override
        public String export(String data) {
            return "CSV:" + data;
        }
    }

    /**
     * UNSICHER — der Klassenname kommt aus dem Request. Jede Klasse auf dem
     * Classpath mit parameterlosem Konstruktor lässt sich so instanziieren;
     * schon Konstruktor und statische Initialisierer führen fremd gewählten
     * Code aus.
     */
    @Deprecated
    public Exporter createInsecure(String className) throws ReflectiveOperationException {
        return (Exporter) Class.forName(className).getDeclaredConstructor().newInstance();
    }

    /**
     * SICHER — Allowlist: ein erlaubter Schlüssel wird auf einen festen Typ
     * abgebildet, alles andere abgelehnt (deny-by-default). Kein Reflection.
     */
    public Exporter create(String key) {
        return switch (key) {
            case "pdf" -> new PdfExporter();
            case "csv" -> new CsvExporter();
            default -> throw new IllegalArgumentException("Unbekanntes Format");
        };
    }
}
