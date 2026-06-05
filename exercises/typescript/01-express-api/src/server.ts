import express from "express";
import cors from "cors";
import { MongoClient } from "mongodb";

const app = express();
app.use(express.json());
app.use(cors({ origin: "*", credentials: true }));

const client = new MongoClient(process.env.MONGO_URL!);
const users = client.db("app").collection("users");

// Login
app.post("/login", async (req, res) => {
  const { username, password } = req.body;
  const user = await users.findOne({ username, password });
  res.json({ ok: !!user });
});

// Profil aktualisieren
function merge(target: any, source: any): any {
  for (const key in source) {
    if (typeof source[key] === "object" && source[key] !== null) {
      target[key] = merge(target[key] ?? {}, source[key]);
    } else {
      target[key] = source[key];
    }
  }
  return target;
}

app.post("/profile", (req, res) => {
  const profile = merge({}, req.body);
  res.json(profile);
});

// Benutzername-Validierung
const USERNAME_RE = /^([a-zA-Z0-9]+)+$/;
app.get("/check", (req, res) => {
  res.json({ valid: USERNAME_RE.test(String(req.query.name)) });
});

// Passwort-Reset-Token
app.post("/reset", (req, res) => {
  const token = Math.random().toString(36).slice(2);
  res.json({ token });
});

app.listen(3000);
