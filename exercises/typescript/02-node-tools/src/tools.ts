import express from "express";
import { exec } from "node:child_process";
import { readFile } from "node:fs/promises";
import path from "node:path";
import https from "node:https";

const app = express();

// Bildkonvertierung
app.get("/thumbnail", (req, res) => {
  const file = String(req.query.file);
  exec(`convert ${file} -resize 100x100 /tmp/out.png`, (err) => {
    res.json({ ok: !err });
  });
});

// URL-Vorschau (Proxy)
const agent = new https.Agent({ rejectUnauthorized: false });
app.get("/preview", async (req, res) => {
  const url = String(req.query.url);
  const r = await fetch(url, { dispatcher: agent } as any);
  res.send(await r.text());
});

// Statische Datei ausliefern
app.get("/download", async (req, res) => {
  const name = String(req.query.name);
  const content = await readFile(path.join("/srv/files", name));
  res.send(content);
});

// Weiterleitung nach Login
app.get("/go", (req, res) => {
  res.redirect(String(req.query.next));
});

app.listen(3000);
