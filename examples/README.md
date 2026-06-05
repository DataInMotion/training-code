# Code-Beispiele

Praktische, lauffähige Beispiele zur Schulung — **Open-Source-first**. Jedes Projekt verdrahtet die Security-Werkzeuge aus den Modulen und zeigt **sicheres vs. unsicheres** Coding.

| Ordner | Stack | Zeigt |
|---|---|---|
| [`java-maven/`](java-maven/) | Java 21 + Maven | Prepared Statements (A03), SCA, SBOM, SAST |
| [`ts-npm/`](ts-npm/) | TypeScript + npm | Input-Validierung, Output-Encoding (XSS), SCA, SBOM |

## Verwendete OSS-Werkzeuge

- **SCA:** OWASP Dependency-Check (Java), `npm audit` (Node)
- **SBOM:** CycloneDX Maven-Plugin / `@cyclonedx/cyclonedx-npm`
- **SAST:** SpotBugs + FindSecBugs (Java), ESLint + `eslint-plugin-security` (TS)
- **Secret Scanning:** gitleaks (repo-weit, siehe Modul 07)

> Kommerzielle Alternativen (Snyk, SonarQube, Burp) sind in den Modulen als Hinweis genannt — die Beispiele bleiben bewusst frei verfügbar.

**Hinweis:** Tool-/Plugin-Versionen sind Stand der Erstellung — bitte vor Nutzung auf die aktuelle Version aktualisieren.
