# TypeScript + npm — Secure-Coding-Demo

Minimal-Projekt mit Security-Tooling. Die Patterns gelten genauso für reines JavaScript.

## Sicheres vs. unsicheres Coding

Jede Datei zeigt **unsicher (Problem) vs. sicher (Lösung)**:

- `src/validation.ts` — Input-Validierung (Blocklist vs. Allowlist) & Output-Encoding/XSS (`innerHTML` vs. `textContent`/Framework-Auto-Escape/DOMPurify)
- `src/accessControl.ts` — Broken Access Control / IDOR (A01, CWE-639): nur ID vs. Bindung an den Eigentümer
- `src/prototypePollution.ts` — Prototype Pollution (CWE-1321): rekursiver Merge über `__proto__` vs. Schlüssel-Filter / `Map`
- `src/crypto.ts` — Kryptographie (A04): MD5 vs. scrypt + Salt + konstanter Zeitvergleich (TR-02102)
- `src/logging.ts` — Logging-Hygiene (A09, CWE-117): roher Input vs. CRLF-Neutralisierung + Maskierung (Do gehört in den Logger, z. B. `pino` `redact`)
- `src/resourceSafety.ts` — Ressourcen-Erschöpfung: unbegrenzte Rekursion/Sammlung (CWE-674/770) vs. iterativ + Limit
- `src/leak.ts` — Memory Leak (CWE-401): unbegrenzte Map vs. `BoundedCache`
- `src/errorHandling.ts` — Fehlerbehandlung (CWE-209): `message`/`stack` an Client vs. intern loggen + generische Meldung

## Werkzeuge ausführen (OSS)

```bash
npm install

# SCA: bekannte Schwachstellen in Abhängigkeiten (bricht ab High)
npm run audit

# SBOM erzeugen (CycloneDX) -> sbom.json
npm run sbom

# SAST-nah: ESLint mit eslint-plugin-security
npm run lint

# Secret Scanning (repo-weit, separat installiert)
gitleaks detect --source .
```

## Bezug zu den Modulen

- **Modul 05** — sicheres Coding (Validierung, Output-Encoding, Prototype Pollution)
- **Modul 06** — SCA & SBOM (`npm audit`, CycloneDX)
- **Modul 07** — SAST (ESLint-Security), Secret Scanning
- **Modul 08** — dieselben Schritte als Pipeline-Gates

> Abhängigkeits-Versionen: Stand November 2026. `@cyclonedx/cyclonedx-npm` bleibt bewusst auf 6.x — 7.x verträgt sich im selben Projekt nicht mit ESLint (ajv-Konflikt, `npm ls` schlägt fehl). TypeScript bleibt auf 6.x, solange typescript-eslint 7.x nicht unterstützt.
