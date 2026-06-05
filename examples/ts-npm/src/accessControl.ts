// Broken Access Control — OWASP A01, IDOR/BOLA (CWE-639).
// Nie der client-gelieferten ID trauen — gegen den authentifizierten Nutzer prüfen.

export interface Order {
  id: number;
  owner: string;
  content: string;
}

const orders = new Map<number, Order>();

// UNSICHER — IDOR: liefert jede Bestellung allein anhand der ID aus dem Request.
export function getOrderInsecure(id: number): Order | undefined {
  return orders.get(id); // keine Eigentümer-Prüfung → fremde Daten lesbar
}

// SICHER — an den authentifizierten Eigentümer binden, sonst verweigern (deny-by-default).
export function getOrder(id: number, currentUser: string): Order {
  const order = orders.get(id);
  if (!order || order.owner !== currentUser) {
    throw new Error("Kein Zugriff");
  }
  return order;
}
