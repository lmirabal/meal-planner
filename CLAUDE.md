# Shopping List App

## What is this?

A meal planning / shopping list app for iPhone (Android later). Items have quantities with mixed
units (e.g. "3 lemons", "300g mince beef", "⅓ tsp cinnamon"). Tracks when items were added, ticked
off, and where they were bought.

## Tech stack

- **Language:** Kotlin (no Swift)
- **Framework:** Kotlin Multiplatform + Compose Multiplatform
- **UI components:** compose-cupertino (`cupertino-adaptive`) — renders iOS-native Cupertino widgets
  on iOS, Material3 on Android
- **Storage:** SQLDelight 2.x (local SQLite, no backend)
- **State management:** AndroidX ViewModel + Kotlin StateFlow
- **Navigation:** Official CMP Navigation (`org.jetbrains.androidx.navigation:navigation-compose`)
- **Date/time:** kotlinx-datetime
- **DI:** None. Manual constructor injection via `AppDependencies` class. No Koin, no Dagger, no
  service locators.

## Architecture

- MVVM: Screen (Composable) → ViewModel (StateFlow) → Repository (interface) → SQLDelight
- Single `composeApp` module with `commonMain`, `androidMain`, `iosMain` source sets
- Platform-specific code is minimal: just `SqlDriver` factory per platform
- `iosApp/` contains a thin Swift wrapper calling Kotlin's `MainViewController`

## Working style

- Before investigating a problem by inspecting jars, decompiling bytecode, or reasoning from first
  principles, search for the answer first — check official docs, GitHub issues, Stack Overflow, or
  known library changelogs. Prefer a confirmed known answer over a self-derived one.

## Code conventions

- No comments in code. If code is unclear, extract a method.
- No magic wiring or reflection-based DI.
- All database operations on `Dispatchers.IO`, never on main thread.
- UUIDs for all primary keys (not auto-increment).
- `created_at` and `updated_at` timestamps on every entity.
- Save state immediately after every user action — no batching writes.

## Project structure

```
composeApp/src/
  commonMain/kotlin/
    model/          # Domain models
    db/             # Repository interfaces + SQLDelight implementations
    ui/             # Compose screens and components
    viewmodel/      # ViewModels
    AppDependencies.kt
  commonMain/sqldelight/
    ShoppingDatabase.sq
  androidMain/kotlin/   # Android SqlDriver factory
  iosMain/kotlin/       # iOS SqlDriver factory
iosApp/                 # Thin Swift entry point
docs/
  architecture-decisions.md
```

## Iteration docs

Each iteration's user story, data model, files to create/modify, and acceptance criteria is tracked
as a GitHub issue. Fetch the spec before starting work:
  gh issue view <N>   # e.g. gh issue view 1 for Iteration 0 — Walking Skeleton
Issues #1–#10 are under milestone v0.1.
