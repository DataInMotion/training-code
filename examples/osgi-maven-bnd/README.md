# OSGi-Beispiel: SBOM, Trivy-Scan und Update-Empfehlungen mit Maven und bnd

Ein kleines, lauffähiges Projekt, das den Weg von der OSGi-Anwendung zur geprüften SBOM zeigt:

```
app.bndrun ──► SBOM-Exporter ──► app.bndrun.cdx.json ──► Trivy ──► bnd update ──► Fix-Versionen
```

1. **Bauen:** zwei OSGi-Bundles mit Maven und dem `bnd-maven-plugin` (Service-API und eine
   Implementierung mit Declarative Services, Fachlichkeit „Terminbuchung").
2. **SBOM erzeugen:** `bnd-export-maven-plugin` schreibt mit dem Exporter `bnd.sbom` eine
   CycloneDX-SBOM der **aufgelösten Anwendung** — also dessen, was laut `app.bndrun` tatsächlich
   läuft (Bundles und Framework), nicht des Build-Klassenpfads.
3. **Scannen:** Trivy gleicht die SBOM mit Schwachstellen-Datenbanken ab; ein Gate bricht bei
   behebbaren HIGH/CRITICAL-Befunden ab.
4. **Updates empfehlen:** `bnd update` nennt zu jedem Befund die kleinste Version, die ihn
   behebt und in der Versionspolitik liegt.
5. **Automatisieren:** ein GitHub-Actions-Workflow führt alles aus und attestiert die SBOM.

## Aufbau

| Pfad | Inhalt |
|---|---|
| `pom.xml` | Eltern-POM: Versionen, Snapshot-Repository, Bundle-Kopfdaten (Hersteller, Kontakt, Lizenz) |
| `booking-api/` | Bundle `biz.datainmotion.training.osgi.booking.api`: exportiertes, versioniertes Paket mit `BookingService` |
| `booking-impl/` | Bundle `biz.datainmotion.training.osgi.booking.impl`: DS-Komponente `InMemoryBookingService` |
| `app/app.bndrun` | die Anwendung: Framework (`-runfw`), Java-Version (`-runee`), Anforderungen (`-runrequires`), aufgelöste Bundles (`-runbundles`), Konfiguration der SBOM (`-sbom`) |
| `app/pom.xml` | SBOM-Export, Auflösen (Profil `resolve`), `bnd-update-maven-plugin` |
| `app/trivy-sample.json` | erfundener Trivy-Bericht, um `bnd update` ohne echte Befunde zu zeigen |
| `.github/workflows/sbom.yml` | der Workflow (wirkt erst im Wurzelverzeichnis eines Repositories, siehe unten) |

## Voraussetzungen

- Java 21
- Maven 3.9
- [Trivy](https://trivy.dev) für den Scan (geprüft mit v0.76.2)
- Netzzugriff auf Maven Central und auf das Snapshot-Repository von Maven Central

### Snapshot-Versionen

Der SBOM-Exporter (`org.geckoprojects.bnd:org.gecko.bnd.sbom`) und `bnd update`
(`org.geckoprojects.bnd:bnd-update-maven-plugin`) sind noch **nicht als Release veröffentlicht**.
Beide kommen als `0.1.0-SNAPSHOT` aus dem Snapshot-Repository von Maven Central. Dafür stehen im
Eltern-POM zwei Einträge:

```xml
<repositories>
    <repository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
<pluginRepositories>
    <pluginRepository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </pluginRepository>
</pluginRepositories>
```

Was das bedeutet: Ein Snapshot ist **nicht reproduzierbar** — unter derselben Versionsnummer
kann morgen ein anderer Stand liegen, und Schnittstellen oder Ausgabe können sich ändern. Für
eine Schulung und zum Ausprobieren ist das in Ordnung; für einen produktiven Build sollte man
auf das erste Release warten und dann die Version in `gecko.bnd.version` festschreiben. Die
beiden Repository-Einträge entfallen dann.

## Lokal ausführen

Alle Befehle im Wurzelverzeichnis des Beispiels.

### 1. Bauen und SBOM erzeugen

```
mvn package
```

Baut die beiden Bundles und schreibt die SBOM nach `app/target/app.bndrun.cdx.json`
(CycloneDX 1.6). Sie enthält acht Komponenten: die beiden eigenen Bundles, Apache Felix SCR
(Declarative Services) mit drei OSGi-Bundles, die es braucht, das Framework Apache Felix und
Java SE als nicht ausgelieferte Plattform. Jede ausgelieferte Komponente hat PURL, SHA-512-Hash,
Lizenz, Hersteller und ihre Rolle in der Anwendung (`osgi:layer`: `runbundle` oder `runfw`).

Ein schneller Blick hinein:

```
jq -r '.components[] | [.type, .name, .version, (.licenses[0].license.id // "-")] | @tsv' \
  app/target/app.bndrun.cdx.json
```

Die Einstellungen stehen in `app/app.bndrun`:

```
-plugin.sbom: org.gecko.bnd.sbom.SbomExporter
-sbom: level=medium, format=cyclonedx, validate=bsi-tr-03183-2, creator=security@example.org, ...
```

- `level=medium` schreibt die Felder, die BSI TR-03183-2 und der Cyber Resilience Act verlangen
  (Hash, Lizenz, Ersteller, Dateieigenschaften).
- `creator` ist der Ersteller der SBOM. Hersteller, Kontakt und Lizenz der eigenen Bundles
  kommen aus den Kopfdaten `Bundle-Vendor`, `Bundle-ContactAddress` und `Bundle-License` im
  Eltern-POM. Die Werte im Beispiel sind Platzhalter (`Beispiel GmbH`, `security@example.org`).
- `validate=bsi-tr-03183-2` prüft die SBOM gegen die Pflichtfelder der Technischen Richtlinie.
  **Einschränkung:** Im Maven-Build waren die Meldungen dieser Prüfung beim Erstellen des
  Beispiels nicht sichtbar, und auch `validate.strict=true` ließ den Build nicht scheitern
  (bnd 7.4.0, Exporter 0.1.0-SNAPSHOT). Der Workflow prüft die wichtigsten Pflichtfelder deshalb
  zusätzlich selbst mit `jq`.

### 2. Auflösung aktualisieren

Die SBOM beschreibt die `-runbundles`, die in `app.bndrun` eingecheckt sind. Nach einer
Versionsänderung im `pom.xml` müssen sie neu berechnet werden:

```
mvn package -Presolve
```

Das Profil lässt `bnd-resolver-maven-plugin` die Anwendung aus `-runrequires` auflösen und
schreibt das Ergebnis in `app.bndrun`. Die geänderte Datei wird eingecheckt.

### 3. Mit Trivy scannen

```
trivy sbom app/target/app.bndrun.cdx.json
trivy sbom --format json --output app/target/trivy.json app/target/app.bndrun.cdx.json
```

Der erste Aufruf zeigt die Befunde als Tabelle, der zweite schreibt sie als JSON für
`bnd update`. Das Gate, wie es auch der Workflow verwendet:

```
trivy sbom --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 app/target/app.bndrun.cdx.json
```

Der Befehl endet mit Exit-Code 1, wenn es eine Schwachstelle mit Schweregrad HIGH oder CRITICAL
gibt, für die eine behobene Version existiert. Befunde ohne Fix lassen den Build nicht
scheitern — sie brauchen eine Entscheidung, keinen roten Build.

Zum Zeitpunkt der Erstellung meldete Trivy für diese Anwendung keine Schwachstellen. Das kann
sich jederzeit ändern, ohne dass sich am Projekt etwas ändert; deshalb läuft der Workflow auch
zeitgesteuert.

### 4. Update-Empfehlungen mit bnd update

```
mvn -f app/pom.xml bnd-update:vulnerabilities -Dbnd-update.trivy=target/trivy.json
```

Der Pfad zum Trivy-Bericht ist relativ zu `app/`. Der Bericht landet in
`app/target/bnd-update/vulnerabilities.md`, zusätzlich erscheint je Bibliothek eine Warnung oder
ein Fehler im Build-Protokoll.

Weil es gerade keine echten Befunde gibt, liegt ein **erfundener** Trivy-Bericht bei, der die
beiden typischen Ergebnisse zeigt:

```
mvn -f app/pom.xml bnd-update:vulnerabilities -Dbnd-update.trivy=trivy-sample.json
```

| Library | Installed | Vulnerabilities | Fix | Where |
|---|---|---|---|---|
| `org.apache.felix:org.apache.felix.scr` | 2.2.12 | 1 (HIGH) | **2.2.14** (micro) | runbundle |
| `org.apache.felix:org.apache.felix.framework` | 7.0.5 | 1 (MEDIUM) | needs a major update (allowed: micro) | runfw |

Die Befunde in `trivy-sample.json` sind ausgedacht, die genannten Versionen gibt es wirklich.
Die Versionspolitik (`<scope>micro</scope>` in `app/pom.xml`) lässt nur Fehlerbehebungen
derselben Versionslinie als Fix gelten. Das Framework bräuchte einen Major-Sprung — das ist
eine Entscheidung für Menschen, kein automatisches Update.

Mit `-Dbnd-update.failOn=unfixable` scheitert der Aufruf, wenn eine verwundbare Bibliothek
keinen Fix innerhalb der Versionspolitik hat; mit `failOn=vulnerable` bei jedem Befund.

### 5. Neuere Versionen unabhängig von Schwachstellen

Die Funktion `updates` von `bnd update` (alle neueren Versionen innerhalb der Versionspolitik)
arbeitet auf den Repository-Indexdateien eines bnd-Workspace und steht für Maven-Projekte nicht
zur Verfügung: Dort stehen die Versionen im `pom.xml`, und die üblichen Werkzeuge kennen sie
bereits. Das Beispiel verwendet dafür das `versions-maven-plugin`:

```
mvn -N versions:display-dependency-updates versions:display-property-updates
```

Die Versionen selbst ändert man von Hand oder mit Dependabot bzw. Renovate. Danach: Auflösung
aktualisieren (Schritt 2), SBOM neu erzeugen, erneut scannen.

## Den Workflow im eigenen Repository aktivieren

GitHub führt nur Workflows aus, die unter `.github/workflows/` im **Wurzelverzeichnis** eines
Repositories liegen. In diesem Beispielordner ist der Workflow deshalb inaktiv.

1. Den Inhalt dieses Ordners als Wurzel eines eigenen Repositories verwenden, einschließlich
   `.github/workflows/sbom.yml` und `.gitignore`.
2. Der Workflow geht vom Hauptzweig `main` aus. Bei einem anderen Namen die beiden Stellen
   anpassen (`on.push.branches` und die Bedingung des Jobs `attest`).
3. Liegt das Projekt in einem Unterverzeichnis, müssen `defaults.run.working-directory` gesetzt
   und die Pfade in `env` sowie beim Artefakt-Upload angepasst werden.
4. Die GitHub-Einstellungen aus dem nächsten Abschnitt vornehmen.

Was der Workflow tut:

| Job | Auslöser | Schritte |
|---|---|---|
| `sbom` | Pull Request, Push auf `main`, wöchentlich, von Hand | bauen und SBOM schreiben; Mindestinhalt der SBOM prüfen; Trivy-Scan (Tabelle in die Job-Zusammenfassung, JSON für bnd update); Berichte von bnd update und zu neueren Versionen in die Job-Zusammenfassung; SBOM und Berichte als Artefakt `sbom` hochladen; Gate |
| `attest` | nur Push auf `main`, nur nach bestandenem Gate | Attestierung der SBOM mit `actions/attest` |

Sicherheitsrelevante Eigenschaften des Workflows:

- **Minimale Rechte:** `permissions: contents: read` auf Workflow-Ebene. Nur der Job `attest`
  erhält `id-token: write` und `attestations: write`, und dieser Job checkt keinen Code aus und
  führt keinen Build aus — er bekommt vom Job `sbom` nur den SHA-256 der SBOM.
- **Gepinnte Actions:** Jede Action ist mit dem vollständigen Commit-SHA angegeben, die Version
  steht als Kommentar daneben. Ein Tag wie `v7` kann nachträglich auf anderen Code zeigen, ein
  Commit-SHA nicht. Trivy selbst ist auf eine feste Version gesetzt.
- **`persist-credentials: false`** beim Checkout: Das Token bleibt nicht in der Git-Konfiguration
  des Arbeitsverzeichnisses liegen, wo spätere Schritte es lesen könnten.
- **`concurrency`:** Ein neuer Lauf auf demselben Zweig bricht den laufenden ab.

## GitHub-Einstellungen

Der Workflow allein reicht nicht; diese Einstellungen im Repository (oder in der Organisation)
gehören dazu.

### Standard-Berechtigung des `GITHUB_TOKEN`: nur lesen

*Settings → Actions → General → Workflow permissions:* **Read repository contents and packages
permissions** auswählen und **Allow GitHub Actions to create and approve pull requests**
abgewählt lassen. Damit hat jeder Workflow, der selbst keine `permissions` setzt, nur
Leserechte. Die Angabe im Workflow bleibt trotzdem sinnvoll: Sie gilt auch dann, wenn jemand die
Repository-Einstellung später ändert.

### Erlaubte Actions

*Settings → Actions → General → Actions permissions:* **Allow *Eigentümer*, and select
non-*Eigentümer*, actions and reusable workflows** auswählen, dazu:

- **Allow actions created by GitHub** (deckt `actions/checkout`, `actions/setup-java`,
  `actions/upload-artifact` und `actions/attest` ab),
- in der Liste der zusätzlich erlaubten Actions: `aquasecurity/setup-trivy@*`,
- **Require actions to be pinned to a full-length commit SHA** aktivieren. Dann lehnt GitHub
  jeden Workflow ab, der eine Action nur über Tag oder Zweig einbindet.

### Branch-Schutz: der Job als Pflicht-Check

*Settings → Rules → Rulesets → New branch ruleset*, Ziel: der Standardzweig.

- **Require a pull request before merging**
- **Require status checks to pass** und als Check **`SBOM und Trivy-Scan`** hinzufügen — das ist
  der Name (`name`) des Jobs `sbom`. GitHub bietet den Check erst zur Auswahl an, nachdem der
  Workflow einmal gelaufen ist.
- **Block force pushes**

Erst dadurch wird aus dem Gate eine Sperre: Ohne Pflicht-Check kann ein Pull Request trotz rotem
Scan gemergt werden. (Dieselbe Wirkung hat eine klassische Branch Protection Rule unter
*Settings → Branches*.)

Der zeitgesteuerte Lauf blockiert nichts. Schlägt er fehl, weil eine neue Schwachstelle bekannt
wurde, benachrichtigt GitHub nur — es braucht jemanden, der diese Meldungen liest.

### Artifact Attestations

Attestierungen stehen für **öffentliche Repositories** in allen GitHub-Tarifen zur Verfügung,
für private und interne Repositories nur mit **GitHub Enterprise Cloud**. In einem privaten
Repository ohne Enterprise Cloud schlägt der Job `attest` fehl; dann den Job entfernen.

Bei öffentlichen Repositories wird die Attestierung über die öffentliche Sigstore-Instanz
signiert und in deren öffentlichem Transparenzprotokoll eingetragen. Repository-Name,
Workflow-Pfad und Commit sind dort dauerhaft sichtbar.

Was attestiert wird: Der Job erzeugt eine Herkunftsaussage (Build Provenance) für die Datei
`app.bndrun.cdx.json`. Sie belegt, dass genau diese Datei (SHA-256) von diesem Workflow aus
diesem Commit erzeugt wurde. Über die Richtigkeit des Inhalts sagt sie nichts aus.

### Eine Attestierung prüfen

Die SBOM aus dem Artefakt des Laufs herunterladen und prüfen:

```
gh run download <run-id> --name sbom --dir sbom
gh attestation verify sbom/app.bndrun.cdx.json --repo <owner/repo>
```

Die Prüfung gelingt nur für die Datei aus einem Lauf, der auch attestiert hat (Push auf `main`).
Jeder Build schreibt einen neuen Zeitstempel in die SBOM und erzeugt damit einen anderen Hash;
eine lokal gebaute SBOM besteht die Prüfung deshalb nicht. Strenger wird die Prüfung mit
`--signer-workflow <owner/repo>/.github/workflows/sbom.yml`: Dann zählt nur eine Attestierung,
die von genau diesem Workflow stammt.

## Herkunft und Lizenz

Das Beispiel beruht auf dem Maven-Beispiel (`examples/maven-sbom`) und der Dokumentation des
Projekts [org.gecko.bnd.sbom](https://github.com/geckoprojects-org/org.gecko.bnd.sbom), das
unter der [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/) steht.
Übernommen und angepasst wurden die Plugin-Konfiguration in den POMs, der Aufbau von
`app.bndrun` und der Beispielbericht `trivy-sample.json`; die übernommenen Teile stehen
weiterhin unter der EPL-2.0. Die Bundles des Beispiels tragen deshalb ebenfalls
`Bundle-License: EPL-2.0`.

Weiterführend in der Dokumentation des Projekts: Referenz des Exporters (Optionen, Inhalt der
SBOM, BSI TR-03183-2), SBOMs in Maven-Builds und in CI, `bnd update` mit Maven.
