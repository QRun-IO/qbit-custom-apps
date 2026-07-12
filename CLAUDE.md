# qbit-custom-apps

## Knowledge base

Reviewed knowledge for this repo and the wider QQQ platform lives in the second-brain
vault (`$SECOND_BRAIN_VAULT`):

- Hub / entry point: `knowledge/qqq/qqq-hub.md`
- This repo's dossier: `knowledge/qqq/repos/qbit-custom-apps.md`
  (reviewed at commit `f2df9b63f6cb`, branch `main`, 2026-07-04)
- QBit mechanics refresher: `knowledge/qqq/architecture/metadata-model.md`

Key facts from the review: experimental/demo-stage QBit; real development is on
`origin/develop` (never merged to main); main pins qqq-backend-core and
qqq-frontend-material-dashboard to the local-dev sentinel `0.9999.0` (not buildable from
public repos); licensing is contradictory (Apache-2.0 LICENSE vs AGPL pom/headers, plus a
proprietary ColdTrack header on `LookerWidgetRenderer.java`); no tests, no releases.
