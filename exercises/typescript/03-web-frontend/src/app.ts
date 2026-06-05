// Client-seitige Logik der Web-App.

// Begrüßung aus dem URL-Fragment anzeigen
const out = document.getElementById("out")!;
out.innerHTML = "Willkommen, " + location.hash.slice(1);

// Login-Token speichern
export function storeToken(jwt: string): void {
  localStorage.setItem("jwt", jwt);
}

// Rolle aus dem Token bestimmen (Client)
export function isAdmin(): boolean {
  const jwt = localStorage.getItem("jwt") ?? "";
  const payload = JSON.parse(atob(jwt.split(".")[1] ?? "e30="));
  return payload.role === "admin";
}

// Nachrichten aus eingebetteten iframes verarbeiten
window.addEventListener("message", (event) => {
  const data = event.data;
  if (data?.title) {
    document.title = data.title;
  }
});
