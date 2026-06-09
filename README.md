# Trainingscode — Code-Beispiele zur DIM Compliance-Schulung

Begleit-Code zur Compliance-Schulung für Softwareentwicklung.
Open-Source-first, Java und TypeScript.

> ⚠️ **Dieses Repository enthält absichtlich unsicheren Code** zu Lehrzwecken.
> Nichts davon ist Produktionscode — **nicht deployen, nicht kopieren**.
> Alle „Secrets", Passwörter und Schlüssel sind **erfundene Trainingsdaten** (keine echten Zugangsdaten).
> Details: siehe [SECURITY.md](SECURITY.md).

## Inhalt

| Ordner | Was |
|---|---|
| [`examples/`](examples/) | **Do & Don't** — lauffähige Demos, jede Datei zeigt *unsicher (Problem) vs. sicher (Lösung)*, mit Erläuterungen und OSS-Tooling (SCA/SBOM/SAST/Secret-Scanning) |
| [`exercises/`](exercises/) | **Code-Review-Übungen** für Gruppenarbeit — Code mit eingebauten Schwachstellen zum Finden (Code, Build, Konfiguration, Repo-Struktur, CI/CD) |

## Schnellstart

- **Do/Don't-Demos:** Einstieg in [`examples/README.md`](examples/README.md); je Stack ein eigenes README mit Build-/Tool-Befehlen (`examples/java-maven/`, `examples/ts-npm/`).
- **Übungen:** Ablauf in [`exercises/README.md`](exercises/README.md).

## Lizenz

[Eclipse Public License 2.0](LICENSE) © Data In Motion Consulting GmbH.
