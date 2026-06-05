// Sicheres vs. unsicheres Coding — Input-Validierung & Output-Encoding.
// OWASP A03 (Injection / XSS).

/**
 * SICHER — strikte Allowlist-Validierung (nicht Blocklist).
 * Nur Buchstaben, Ziffern, Unterstrich; 3–32 Zeichen.
 */
export function isValidUsername(input: string): boolean {
  return /^[A-Za-z0-9_]{3,32}$/.test(input);
}

/**
 * SICHER — HTML-Encoding gegen Cross-Site-Scripting beim Ausgeben.
 */
export function escapeHtml(value: string): string {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

// UNSICHER (nur zur Illustration — nicht verwenden):
//   element.innerHTML = userInput;            // -> XSS
//
// SICHER:
//   element.textContent = userInput;          // kein HTML-Parsing, oder:
//   element.innerHTML = escapeHtml(userInput);
