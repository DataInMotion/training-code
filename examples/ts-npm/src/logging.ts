// Logging-Hygiene — Log-Injection/Log-Forging (CWE-117) + sensible Daten (A09).
//
// Bevorzugt gehört das in den Logger, nicht in den App-Code: strukturiertes
// Logging (z. B. pino) mit `redact` für sensible Felder neutralisiert/maskiert
// zentral. Die Funktionen unten sind eine minimale Skizze.

// UNSICHER — roher Input ins Log: CR/LF erlauben gefälschte Log-Zeilen.
export function logInsecure(username: string): void {
  console.log(`Login: ${username}`); // "admin\n[INFO] Zugriff erlaubt" → Log-Forging
}

// SICHER — CR/LF neutralisieren; nur das Nötige loggen, keine Secrets/PII.
export function logSafe(username: string): void {
  const safe = username.replace(/[\r\n]/g, "_");
  console.log(`Login: ${safe}`);
}

// SICHER — sensible Werte maskieren statt im Klartext zu loggen.
export function mask(secret: string): string {
  return secret.length < 4 ? "***" : `${secret.slice(0, 2)}***`;
}
