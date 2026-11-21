# Sicherheitshinweise veröffentlichen — CSAF, VEX, `security.txt`

Ein vollständiges Beispiel dafür, was ein Hersteller auf seiner Website bereitstellt, damit
Menschen und Werkzeuge seine Sicherheitshinweise finden. Es gehört zu **Modul 10**
(Kapitel „Veröffentlichen") und führt das Beispiel der Terminbuchung fort.

Alle Angaben sind **erfunden**: Hersteller, Domain (`example.com`), Produktversionen und
CVE-Nummern.

## Aufbau

Das Verzeichnis `site/` bildet die Website des Herstellers nach:

```
site/
├── .well-known/security.txt                   ①  Zeile "CSAF:" zeigt auf ②
├── .well-known/csaf/provider-metadata.json    ②  "distributions" zeigt auf ③
└── csaf/                                      ③  das Verzeichnis der Dokumente
    ├── index.txt                                 Liste aller Dokumente
    ├── changes.csv                               Änderungsliste, das Neueste zuerst
    └── 2026/
        ├── example-2026-001.json              ④  Security Advisory
        └── example-2026-002.json              ④  VEX
openvex/
└── terminbuchung.openvex.json                 dieselbe VEX-Aussage als eigenständige OpenVEX-Datei
```

| Datei | Was sie zeigt |
|---|---|
| `security.txt` | Meldekanal nach RFC 9116, mit dem Feld `CSAF:` als Einstieg für Werkzeuge |
| `provider-metadata.json` | Wer veröffentlicht, in welcher Rolle, wo die Dokumente liegen |
| `index.txt`, `changes.csv` | Wie ein Werkzeug alle bzw. nur geänderte Dokumente findet |
| `example-2026-001.json` | **Security Advisory** (`csaf_security_advisory`): Version 1.4.2 betroffen, 1.4.3 behoben, mit Fix und Workaround |
| `example-2026-002.json` | **VEX** (`csaf_vex`): Version 1.4.3 ist von einer CVE in einer Bibliothek *nicht betroffen*, mit Begründung. Die Änderungshistorie zeigt den Weg von „wird geprüft" zu „nicht betroffen" |
| `terminbuchung.openvex.json` | Dieselbe Aussage im Format OpenVEX — für Repository oder Container-Image statt für das CSAF-Verzeichnis |

## Die zwei Fälle

- **Eigene Lücke** (`example-2026-001`): Fremde Buchungen waren über die ID abrufbar — die
  Bedrohung T-04 aus dem Threat Model in Modul 03. Der Hersteller veröffentlicht ein
  Advisory mit Fix.
- **Fremde Lücke** (`example-2026-002`): Eine CVE in der eingebundenen Mail-Bibliothek. Der
  Hersteller prüft und erklärt, dass sein Produkt nicht betroffen ist, weil der verwundbare
  Code nicht ausgeführt wird.

Advisory und VEX sind getrennte Dateien im selben Verzeichnis. Verbunden sind sie nur über
CVE und Produktkennung (`purl`), nicht über einen Verweis.

## Prüfen

```bash
pip install jsonschema      # falls nicht vorhanden
python3 validate.py
```

Das Skript lädt die offiziellen Schemata und prüft die vier JSON-Dateien dagegen:

| Format | Schema |
|---|---|
| CSAF 2.0 (Advisory und VEX) | `docs.oasis-open.org/csaf/csaf/v2.0/os/schemas/csaf_json_schema.json` |
| Provider-Metadaten | `docs.oasis-open.org/csaf/csaf/v2.0/os/schemas/provider_json_schema.json` |
| OpenVEX | `openvex_json_schema.json` im Repository `openvex/spec` |

## Grenzen des Beispiels

- **Nur das Schema ist geprüft.** CSAF definiert zusätzlich Tests je Dokumentart (Profil),
  die über das Schema hinausgehen. Dafür gibt es eigene Werkzeuge, etwa den Editor
  [Secvisogram](https://github.com/secvisogram/secvisogram).
- **Die `security.txt` ist nicht signiert.** Die BSI TR-03183-3 verlangt von Herstellern
  eine OpenPGP-signierte Datei.
- **Keine Signaturen und Hashes der Dokumente.** Ein „trusted provider" nach CSAF liefert
  zu jedem Dokument einen Hash und eine OpenPGP-Signatur.
- **OpenVEX** steht bei Version 0.2.0 und gilt noch als Entwurf.

## Quellen

- CSAF 2.0 (OASIS-Standard): <https://docs.oasis-open.org/csaf/csaf/v2.0/os/csaf-v2.0-os.html>
- RFC 9116 (`security.txt`): <https://www.rfc-editor.org/rfc/rfc9116>
- OpenVEX-Spezifikation: <https://github.com/openvex/spec>
- BSI TR-03183-3: <https://www.bsi.bund.de/dok/TR-03183-en>
