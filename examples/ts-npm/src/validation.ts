// Sicheres vs. unsicheres Coding — Input-Validierung & Output-Encoding.
// OWASP A03 (Injection / XSS).

// --- Input-Validierung ---

/** UNSICHER — Blocklist: verbietet ein paar Zeichen, lässt aber fast alles durch. */
export function isValidUsernameInsecure(input: string): boolean {
  return !input.includes("<") && !input.includes("'");
}

/**
 * SICHER — strikte Allowlist (nicht Blocklist).
 * Nur Buchstaben, Ziffern, Unterstrich; 3–32 Zeichen.
 */
export function isValidUsername(input: string): boolean {
  return /^[A-Za-z0-9_]{3,32}$/.test(input);
}

// --- Output-Encoding gegen XSS ---

/** UNSICHER — User-Input ungeprüft als HTML einsetzen (entspricht `el.innerHTML = userInput`). */
export function renderInsecure(userInput: string): string {
  return `<div>${userInput}</div>`; // -> XSS, nur zur Illustration
}

/**
 * SICHER — im Browser bevorzugt der Plattform-/Framework-Weg:
 *   element.textContent = userInput;     // kein HTML-Parsing
 *   React/Angular/Vue escapen Interpolationen by default
 * Für Rich-HTML eine Sanitizer-Bibliothek (z. B. DOMPurify) statt Eigenbau.
 * `escapeHtml` ist nur ein minimaler String-Fallback für den HTML-Textkontext.
 */
export function escapeHtml(value: string): string {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

export function renderSecure(userInput: string): string {
  return `<div>${escapeHtml(userInput)}</div>`;
}
