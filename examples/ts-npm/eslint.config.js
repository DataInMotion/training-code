// ESLint Flat Config (ESLint 9) — SAST-nahe statische Analyse.
// eslint-plugin-security findet typische Schwachstellenmuster; die UNSICHEREN
// Demo-Funktionen sollen hier bewusst als Warnung auftauchen (Lehrwert),
// ohne den Lint-Lauf fehlschlagen zu lassen.

import security from "eslint-plugin-security";
import tsParser from "@typescript-eslint/parser";
import tsPlugin from "@typescript-eslint/eslint-plugin";

export default [
  security.configs.recommended,
  {
    files: ["src/**/*.ts"],
    languageOptions: {
      parser: tsParser,
      parserOptions: { ecmaVersion: 2022, sourceType: "module" },
    },
    plugins: { "@typescript-eslint": tsPlugin },
    rules: {
      ...tsPlugin.configs.recommended.rules,
    },
  },
  {
    ignores: ["dist/**", "node_modules/**"],
  },
];
