# Security Policy

## Supported Scope

This repository is a teaching and demo project. Security fixes are welcome through pull requests or issues.

## Local Secrets

Do not commit real database passwords, JWT secrets, tokens, uploaded private files, or production data. Use environment variables shown in `.env.example` and rotate `JWT_SECRET` before any public deployment.

## Demo Data

The included accounts and contact numbers are synthetic demo records. Replace or remove them before using the system with real users.

## Reporting

If you find a vulnerability, open a private report through the hosting platform when available, or create an issue without including exploit credentials or private data.
