# Guruqu v1.3 Recovery Baseline

The `main` branch is restored to the last known-good Guruqu v1.3 baseline commit `b0533ea8e56ce51040fc026c0733d7955057b898`.

The subsequent Google AI Studio refactor was isolated on `backup-ai-studio-ac63` because it changed the application ID/version and reintroduced networking/Moshi dependencies.

Database schema files were not changed by that refactor; the changed data behavior was in `PesantrenViewModel.kt`. UI/layout remains on the stable baseline.
