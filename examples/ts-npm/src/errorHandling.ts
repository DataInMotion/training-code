// Fehlerbehandlung — unsaubere Behandlung exponiert Interna (CWE-209).

// UNSICHER — gibt error.message/stack an den Client → leakt Interna, "fail open".
export function handleInsecure(err: unknown): string {
  const e = err as Error;
  return `Fehler: ${e.message}\n${e.stack}`;
}

// SICHER — Detail nur ins Log, generische Meldung nach außen, fail closed.
export function handleSecure(err: unknown): string {
  console.error("Verarbeitung fehlgeschlagen", err); // Stacktrace nur intern
  return "Anfrage konnte nicht verarbeitet werden"; // generisch für den Client
}
