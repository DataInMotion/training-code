// Kryptographie — OWASP A02/A04 (BSI TR-02102).
// Schwaches Passwort-Hashing vs. Stand der Technik.

import { createHash, scryptSync, randomBytes, timingSafeEqual } from "node:crypto";

// UNSICHER — MD5 für Passwörter: schnell, ungesalzen, kryptographisch gebrochen.
export function hashPasswordInsecure(password: string): string {
  return createHash("md5").update(password).digest("hex");
}

// SICHER — scrypt: langsam (brute-force-resistent) und gesalzen.
// In Produktion bevorzugt Argon2id; Salt je Nutzer, ggf. Pfeffer aus KMS/Vault.
export function hashPassword(password: string): { salt: string; hash: string } {
  const salt = randomBytes(16);
  const hash = scryptSync(password, salt, 32);
  return { salt: salt.toString("hex"), hash: hash.toString("hex") };
}

// SICHER — Vergleich in konstanter Zeit gegen Timing-Angriffe (kein ===).
export function verifyPassword(password: string, salt: string, expected: string): boolean {
  const hash = scryptSync(password, Buffer.from(salt, "hex"), 32);
  return timingSafeEqual(hash, Buffer.from(expected, "hex"));
}
