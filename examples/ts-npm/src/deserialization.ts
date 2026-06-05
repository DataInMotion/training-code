// Unsichere Deserialisierung / Prototype Pollution — OWASP A08 (CWE-1321).
// JSON.parse selbst ist sicher — das Zusammenführen *fremder* Objekte ist es nicht.

// UNSICHER — naives Deep-Merge schreibt auch __proto__ → Prototype Pollution
// (verändert Object.prototype und damit alle Objekte zur Laufzeit).
export function mergeInsecure(
  target: Record<string, unknown>,
  source: Record<string, unknown>,
): Record<string, unknown> {
  for (const key in source) {
    const value = source[key];
    if (typeof value === "object" && value !== null) {
      const next = (target[key] as Record<string, unknown>) ?? {};
      target[key] = mergeInsecure(next, value as Record<string, unknown>);
    } else {
      target[key] = value; // key === "__proto__" verseucht den Prototyp
    }
  }
  return target;
}

// SICHER — gefährliche Schlüssel ablehnen, keine eigenen Object-Properties überschreiben.
const FORBIDDEN = new Set(["__proto__", "constructor", "prototype"]);

export function mergeSafe(
  target: Record<string, unknown>,
  source: Record<string, unknown>,
): Record<string, unknown> {
  for (const key of Object.keys(source)) {
    if (FORBIDDEN.has(key)) continue; // gefährliche Schlüssel verwerfen
    // eslint-disable-next-line security/detect-object-injection -- abgesichert durch FORBIDDEN-Allowlist
    target[key] = source[key];
  }
  return target;
}
