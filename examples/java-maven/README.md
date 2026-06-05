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
- `LoggingHygiene.java` — Logging-Hygiene (A09, CWE-117): drei Stufen — kein Escape → Eigenbau (`replaceAll`) → **OWASP Java Encoder** (`Encode.forJava`, neutralisiert CR/LF + Steuerzeichen); plus Maskierung
- `ResourceSafety.java` — Ressourcen-Erschöpfung: unbegrenzte Rekursion/Sammlung (CWE-674/400) vs. iterativ + Limit
- `LeakyCache.java` — Memory Leak (CWE-401): unbegrenzte statische Map vs. LRU-Cache mit Obergrenze
- `ErrorHandling.java` — Fehlerbehandlung (CWE-209): Interna/Stacktrace nach außen vs. intern loggen + generische Meldung (fail closed)

## Logging-Injection auch im Appender neutralisieren

`Encode.forJava(..)` neutralisiert im **Code**. Besser noch zentral im **Appender** —
beide Frameworks können CR/LF eingebaut entschärfen (keine Extra-Dependency):

```xml
<!-- Log4j2: eingebauter encode-Converter mit Option CRLF -->
<PatternLayout pattern="%d %p %c{1.} [%t] %encode{%m}{CRLF}%n"/>
```

```xml
<!-- Logback: eingebauter replace-Converter -->
<encoder>
  <pattern>%d %p %logger - %replace(%msg){'[\r\n]', '_'}%n</pattern>
</encoder>
```

> Im Appender greift es für **jede** Log-Zeile — unabhängig davon, ob im Code ans
> Encoden gedacht wurde. Für Maskierung sensibler Felder: OWASP Security Logging
> (`CONFIDENTIAL`-Marker).

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
