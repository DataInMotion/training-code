# Übungen — Code-Review in Gruppen

Praktische Arbeit: Teilnehmende analysieren den Code und finden in Gruppen die **eingebauten Schwachstellen** — Schweregrade *einfach · mittel · subtil*. Die Fehler liegen in **Code, Build, Konfiguration, Repository-Struktur und CI/CD-Pipeline** (z. B. unnötige/sensible Dateien, fehlerhafte Workflows), nicht nur im Code.

## Aufbau

- `java/` — drei Java-Beispiele (`01`–`03`)
- `typescript/` — drei TypeScript-Beispiele (`01`–`03`)

> Musterlösungen sind **nicht** Teil dieses Repos — sie liegen beim Trainer/bei der Schulungsleitung.

> Die Schwachstellen in den Java- und TS-Beispielen sind **bewusst unterschiedlich** — kein 1:1-Spiegel.

## Bauen

Die Java-Beispiele sind eigenständige Maven-Projekte mit Tests:

```bash
cd java/01-user-api && mvn verify
```

Die Dienste werden in der Übung nicht gestartet — es geht um das Lesen.

## Nicht alles ist ein Fehler

In den Java-Beispielen steht sauberer Code neben den eingebauten Schwachstellen, und manches
sieht nur verdächtig aus. Zu einem Review gehört beides: Fehler finden **und** begründen, warum
eine auffällige Stelle in Ordnung ist.

## Ablauf (Vorschlag)

1. Gruppen à 3–4 Personen, je ein Beispiel
2. 20–30 min: Findings sammeln, Severity einschätzen, Fix skizzieren
3. Vorstellen im Plenum → Findings gemeinsam besprechen & priorisieren

## Hinweis

Enthaltene „Secrets", Passwörter und Schlüssel sind **erfundene Trainingsdaten** — keine echten Zugangsdaten.
