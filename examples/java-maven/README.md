# Java + Maven — Secure-Coding-Demo

Minimal-Projekt, das die Security-Werkzeuge aus den Modulen verdrahtet.

## Sicheres vs. unsicheres Coding

Jede Datei zeigt **unsicher (Problem) vs. sicher (Lösung)**:

- `UserRepository.java` — SQL-Injection (A03), rohes JDBC: String-Konkatenation vs. Prepared Statement
- `UserRepositoryJpa.java` — Abgrenzung ORM/JPA: parametrisiert meist automatisch, aber String-gebautes JPQL bleibt angreifbar (sicher = benannter Parameter)
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
