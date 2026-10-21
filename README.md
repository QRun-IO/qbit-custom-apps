# qbit-custom-apps

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> **Status:** ⚠️ **Experimental / scaffold.** This qbit declares dependencies against `0.9999.0` sentinel versions of `qqq-backend-core` and `qqq-frontend-material-dashboard`, which means it does **not** consume a released framework version. Treat as exploratory until a maintainer confirms its intended state.

QBit that aims to let qqq apps register **custom application screens** declaratively — pages built from widgets, forms, and table views — without writing dedicated front-end components.

**Type:** Application QBit
**For:** Developers building qqq apps that need bespoke screens beyond standard table CRUD

## Why this exists

qqq's standard dashboard renders auto-generated screens from table metadata. Most apps eventually need pages that *don't* map to a single table — operator dashboards, configurator wizards, multi-step workflows that pull data from several sources, embedded Looker dashboards, etc. `qbit-custom-apps` is intended to let those screens be declared as metadata.

## What it provides

The repository contains the following package layout:

```
src/main/java/com/kingsrook/qbits/customapps/
├── definition/   # CustomAppDefinition and related metadata
├── model/        # Data models backing custom apps
├── metadata/     # Metadata producers and registrators
└── widgets/      # Widget composition utilities
```

## How it relates to other qqq repos

- **Declared dependencies:** `qqq-backend-core`, `qqq-backend-module-rdbms`, `qqq-frontend-material-dashboard`, `qqq-middleware-api`, `qqq-middleware-javalin` — all currently pinned to the `0.9999.0` sentinel version (development build).
- **Inherits from:** `qbit-build-parent` (in the `qbit-bom` repo).
- **Frontend pair:** Custom apps render via the standard qqq dashboard; no separate frontend component needed.

## Known gaps

- No automated tests at the time this README was added.
- Sentinel `0.9999.0` versions indicate this qbit is not currently consuming any released qqq build — meaning it can't be cleanly installed by a downstream app.
- This README was added by an audit pass; original maintainer should verify intent and either pin to a real qqq version or mark the repo archived.

## License

Apache License 2.0 — see [LICENSE](./LICENSE).

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md), [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md), [CODE_STYLE.md](./CODE_STYLE.md), and [SECURITY.md](./SECURITY.md).
