# Übungen — Code-Review in Gruppen

Praktische Arbeit: Teilnehmende analysieren den Code und finden in Gruppen die **eingebauten Schwachstellen** — Schweregrade *einfach · mittel · subtil*. Die Fehler liegen in **Code, Build, Konfiguration, Repository-Struktur und CI/CD-Pipeline** (z. B. unnötige/sensible Dateien, fehlerhafte Workflows), nicht nur im Code.

## Aufbau

- `java/` — drei Java-Beispiele (`01`–`03`)
- `typescript/` — drei TypeScript-Beispiele (`01`–`03`)

> Musterlösungen sind **nicht** Teil dieses Repos — sie liegen beim Trainer/bei der Schulungsleitung.

> Die Schwachstellen in den Java- und TS-Beispielen sind **bewusst unterschiedlich** — kein 1:1-Spiegel.

## Ablauf (Vorschlag)

1. Gruppen à 3–4 Personen, je ein Beispiel
2. 20–30 min: Findings sammeln, Severity einschätzen, Fix skizzieren
3. Vorstellen im Plenum → Findings gemeinsam besprechen & priorisieren

## Hinweis

Enthaltene „Secrets", Passwörter und Schlüssel sind **erfundene Trainingsdaten** — keine echten Zugangsdaten.
