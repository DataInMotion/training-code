// Prototype Pollution — CWE-1321.
//
// Jedes Objekt erbt von Object.prototype, und der Sonderschlüssel "__proto__"
// führt von jedem Objekt dorthin. Ein rekursiver Merge, der Schlüssel aus einer
// Eingabe ungeprüft übernimmt, schreibt damit in ALLE Objekte des Prozesses.
//
// Für Java-Entwickler: als wäre java.lang.Object zur Laufzeit beschreibbar.

type Json = Record<string, unknown>;

function isObject(value: unknown): value is Json {
  return typeof value === "object" && value !== null;
}

/**
 * UNSICHER (nur zur Illustration — nicht verwenden): steigt in jeden
 * Schlüssel ab, auch in "__proto__".
 *
 *   mergeInsecure({}, JSON.parse('{"__proto__": {"isAdmin": true}}'));
 *   ({} as any).isAdmin   // -> true, für jedes Objekt
 */
export function mergeInsecure(target: Json, source: Json): Json {
  for (const key in source) {
    const value = source[key];
    if (isObject(value)) {
      const next = (target[key] ??= {}) as Json;
      mergeInsecure(next, value);
    } else {
      target[key] = value;
    }
  }
  return target;
}

const FORBIDDEN_KEYS = new Set(["__proto__", "constructor", "prototype"]);

/**
 * SICHER — gefährliche Schlüssel überspringen und nur eigene Eigenschaften
 * der Quelle übernehmen.
 */
export function merge(target: Json, source: Json): Json {
  for (const key of Object.keys(source)) {
    if (FORBIDDEN_KEYS.has(key)) {
      continue;
    }
    const value = source[key];
    if (isObject(value)) {
      const next = (isObject(target[key]) ? target[key] : (target[key] = {})) as Json;
      merge(next, value);
    } else {
      target[key] = value;
    }
  }
  return target;
}

/**
 * SICHER — für Schlüssel, die aus Eingaben stammen: eine Map hat keinen
 * Prototyp, in den sich etwas hineinschreiben ließe.
 */
export function toMap(source: Json): Map<string, unknown> {
  return new Map(Object.entries(source));
}
