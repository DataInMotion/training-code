import express from "express";
import jwt from "jsonwebtoken";

const app = express();

// Session-Cookie nach Login setzen
app.post("/login", (req, res) => {
  const sessionId = createSession();
  res.cookie("sid", sessionId);
  res.json({ ok: true });
});

// Geschützte Aktion: Rolle aus dem Token lesen
app.post("/admin/delete", (req, res) => {
  const token = String(req.headers["authorization"]).replace("Bearer ", "");
  const claims: any = jwt.decode(token);
  if (claims?.role === "admin") {
    res.json({ deleted: true });
  } else {
    res.status(403).end();
  }
});

function createSession(): string {
  return "sess-" + Date.now();
}

app.listen(3000);
