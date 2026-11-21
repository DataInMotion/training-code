#!/usr/bin/env python3
"""Prüft die Beispieldateien gegen die offiziellen JSON-Schemata.

Benötigt das Paket `jsonschema` und Netzzugriff (die Schemata werden geladen,
nicht mitgeliefert). Aufruf im Verzeichnis dieses Skripts:  python3 validate.py
"""
import json
import pathlib
import sys
import urllib.request

import jsonschema

HERE = pathlib.Path(__file__).resolve().parent

CSAF = "https://docs.oasis-open.org/csaf/csaf/v2.0/os/schemas/csaf_json_schema.json"
PROVIDER = "https://docs.oasis-open.org/csaf/csaf/v2.0/os/schemas/provider_json_schema.json"
OPENVEX = "https://raw.githubusercontent.com/openvex/spec/main/openvex_json_schema.json"

CHECKS = [
    ("site/csaf/2026/example-2026-001.json", CSAF),
    ("site/csaf/2026/example-2026-002.json", CSAF),
    ("site/.well-known/csaf/provider-metadata.json", PROVIDER),
    ("openvex/terminbuchung.openvex.json", OPENVEX),
]


def load(url):
    with urllib.request.urlopen(url, timeout=30) as response:
        return json.load(response)


def main():
    schemas = {}
    failed = False
    for path, url in CHECKS:
        schema = schemas.setdefault(url, load(url))
        document = json.loads((HERE / path).read_text(encoding="utf-8"))
        validator = jsonschema.validators.validator_for(schema)(schema)
        errors = sorted(validator.iter_errors(document), key=lambda e: list(e.path))
        if errors:
            failed = True
            print(f"FEHLER  {path}")
            for error in errors:
                print(f"        {list(error.path)}: {error.message}")
        else:
            print(f"ok      {path}")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
