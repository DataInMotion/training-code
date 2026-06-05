// Ressourcen-Erschöpfung (DoS) — CWE-674 (Rekursion), CWE-400 (Ressourcen).
// Jeweils: unsicher (skizziert das Problem) vs. sicher (mit Obergrenze).

const MAX_DEPTH = 1000;
const MAX_ITEMS = 10_000;

// UNSICHER — unbegrenzte Rekursion → "Maximum call stack size exceeded".
export function sumInsecure(n: number): number {
  return n === 0 ? 0 : n + sumInsecure(n - 1);
}

// SICHER — Eingabe begrenzt, iterativ gelöst.
export function sum(n: number): number {
  if (n < 0 || n > MAX_DEPTH) {
    throw new RangeError("Eingabe außerhalb des erlaubten Bereichs");
  }
  let total = 0;
  for (let i = 1; i <= n; i++) total += i;
  return total;
}

// UNSICHER — sammelt unbegrenzt; großer/feindlicher Input sprengt den Speicher.
export function collectInsecure<T>(source: Iterable<T>): T[] {
  return [...source];
}

// SICHER — feste Obergrenze.
export function collect<T>(source: Iterable<T>): T[] {
  const result: T[] = [];
  for (const item of source) {
    if (result.length >= MAX_ITEMS) {
      throw new Error(`Eingabe überschreitet das Limit von ${MAX_ITEMS}`);
    }
    result.push(item);
  }
  return result;
}
