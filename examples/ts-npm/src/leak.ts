// Memory Leak — CWE-401. Ein module-weiter Cache, der nie geleert wird.

// UNSICHER — wächst unbegrenzt, hält Referenzen für immer.
const leak = new Map<string, Uint8Array>();
export function cacheInsecure(key: string, value: Uint8Array): void {
  leak.set(key, value); // wird nie entfernt
}

// SICHER — LRU-Cache mit fester Obergrenze; der älteste Eintrag fällt heraus.
export class BoundedCache<K, V> {
  private readonly map = new Map<K, V>();

  constructor(private readonly maxEntries: number) {}

  set(key: K, value: V): void {
    if (this.map.has(key)) this.map.delete(key); // Reihenfolge auffrischen
    this.map.set(key, value);
    if (this.map.size > this.maxEntries) {
      const oldest = this.map.keys().next().value as K;
      this.map.delete(oldest);
    }
  }

  get(key: K): V | undefined {
    return this.map.get(key);
  }
}
