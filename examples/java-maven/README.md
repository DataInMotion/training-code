# Java + Maven — Secure-Coding-Demo

Minimal-Projekt, das die Security-Werkzeuge aus den Modulen verdrahtet.

## Sicheres vs. unsicheres Coding

Jede Datei zeigt **unsicher (Problem) vs. sicher (Lösung)**:

- `UserRepository.java` — SQL-Injection (A03), rohes JDBC: String-Konkatenation vs. Prepared Statement
- `UserRepositoryJpa.java` — Abgrenzung ORM/JPA: parametrisiert meist automatisch, aber String-gebautes JPQL bleibt angreifbar (sicher = benannter Parameter)
- `InputValidation.java` — Input-Validierung & Output-Encoding (A03): Blocklist vs. Allowlist; Eigenbau-Escaping vs. **OWASP Java Encoder** (`Encode.forHtml`)
- `AccessControl.java` — Broken Access Control / IDOR (A01, CWE-639): nur ID vs. Bindung an den Eigentümer (deny-by-default)
- `UnsafeDeserialization.java` — Deserialisierung (A08, CWE-502): `ObjectInputStream` ungefiltert vs. `ObjectInputFilter`-Allowlist (besser: JSON/Text)
- `CryptoUsage.java` — Kryptographie (A02/A04): AES/ECB + hartkodierter Key vs. AES-256/GCM + zufälliger Schlüssel/Nonce (TR-02102)
- `LoggingHygiene.java` — Logging-Hygiene (A09, CWE-117): roher Input/Klartext vs. CRLF-Neutralisierung + Maskierung (Do gehört in Appender / OWASP Security Logging)
- `ResourceSafety.java` — Ressourcen-Erschöpfung: unbegrenzte Rekursion/Sammlung (CWE-674/400) vs. iterativ + Limit
- `LeakyCache.java` — Memory Leak (CWE-401): unbegrenzte statische Map vs. LRU-Cache mit Obergrenze
- `ErrorHandling.java` — Fehlerbehandlung (CWE-209): Interna/Stacktrace nach außen vs. intern loggen + generische Meldung (fail closed)

## Werkzeuge ausführen (OSS)

```bash
# SBOM erzeugen (CycloneDX) -> target/bom.json
mvn package

# SCA: Abhängigkeiten gegen bekannte CVEs prüfen (bricht ab CVSS >= 7)
mvn org.owasp:dependency-check-maven:check

# SAST: SpotBugs + FindSecBugs
mvn com.github.spotbugs:spotbugs-maven-plugin:check

# Secret Scanning (repo-weit, separat installiert)
gitleaks detect --source .
```

## Bezug zu den Modulen

- **Modul 05** — sicheres Coding (Injection, Prepared Statements)
- **Modul 06** — SCA & SBOM
- **Modul 07** — SAST, Secret Scanning
- **Modul 08** — dieselben Schritte als Pipeline-Gates

> Versionen der Plugins in der `pom.xml` sind Stand der Erstellung — vor Nutzung aktualisieren.
