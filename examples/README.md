# Code-Beispiele

Praktische, lauffähige Beispiele zur Schulung — **Open-Source-first**. Jedes Projekt verdrahtet die Security-Werkzeuge aus den Modulen und zeigt **sicheres vs. unsicheres** Coding.

| Ordner | Stack | Zeigt |
|---|---|---|
| [`java-maven/`](java-maven/) | Java 21 + Maven | Injection, Zugriff, Krypto, Deserialisierung, Reflection, Logging, Robustheit · SCA, SBOM, SAST |
| [`ts-npm/`](ts-npm/) | TypeScript + npm | Validierung, XSS, Zugriff, Krypto, Prototype Pollution, Logging, Robustheit · SCA, SBOM |
| [`osgi-maven-bnd/`](osgi-maven-bnd/) | OSGi + Maven + bnd | SBOM der aufgelösten OSGi-Anwendung (CycloneDX), Trivy-Scan mit Gate, Update-Empfehlungen mit `bnd update` · GitHub-Actions-Workflow mit Attestierung, README mit den nötigen GitHub-Einstellungen |
| [`security-advisories/`](security-advisories/) | JSON, `security.txt` | Sicherheitshinweise veröffentlichen: CSAF-Advisory, VEX (CSAF und OpenVEX), `provider-metadata.json`, Verzeichnis mit Änderungsliste — gegen die offiziellen Schemata prüfbar |

## Verwendete OSS-Werkzeuge

- **SCA:** OWASP Dependency-Check (Java), `npm audit` (Node)
- **SBOM:** CycloneDX Maven-Plugin / `@cyclonedx/cyclonedx-npm`; für OSGi der SBOM-Exporter `org.gecko.bnd.sbom`
- **Schwachstellen-Scan der SBOM:** Trivy (OSGi-Beispiel)
- **SAST:** SpotBugs + FindSecBugs (Java), ESLint + `eslint-plugin-security` (TS)
- **Secret Scanning:** gitleaks (repo-weit, siehe Modul 07)

> Kommerzielle Alternativen (Snyk, SonarQube, Burp) sind in den Modulen als Hinweis genannt — die Beispiele bleiben bewusst frei verfügbar.

**Hinweis:** Tool-/Plugin-Versionen sind Stand der Erstellung — bitte vor Nutzung auf die aktuelle Version aktualisieren.
