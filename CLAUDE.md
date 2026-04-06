# Shopping List App

## What is this?

A meal planning / shopping list app for iPhone (Android later). Items have quantities with mixed
units (e.g. "3 lemons", "300g mince beef", "⅓ tsp cinnamon"). Tracks when items were added, ticked
off, and where they were bought.

## Tech stack

- **Language:** Kotlin (no Swift)
- **Framework:** Kotlin Multiplatform + Compose Multiplatform
- **UI components:** Compose Material3 (`androidx.compose.material3`)
- **Storage:** SQLDelight 2.x (local SQLite, no backend)
- **State management:** AndroidX ViewModel + Kotlin StateFlow
- **Navigation:** Official CMP Navigation (`org.jetbrains.androidx.navigation:navigation-compose`)
- **Date/time:** kotlinx-datetime
- **DI:** None. Manual constructor injection via `AppDependencies` class. No Koin, no Dagger, no
  service locators.

## Architecture

- MVVM: Screen (Composable) → ViewModel (StateFlow) → Repository (interface) → SQLDelight
- Screens receive a ViewModel, never a repository or other dependency directly. ViewModel
  construction (`viewModel { }`) belongs at the wiring layer (`App.kt` or the navigation graph),
  not inside the screen composable.
- `internal` is for concrete implementations (e.g. `InMemoryItemRepository`). Interfaces and
  ViewModels that external callers need are public. The public API surface of a feature package is:
  the repository *interface*, its factory function (e.g. `fun itemRepository(): ItemRepository` in
  the interface file), and the ViewModel. The screen composable is `internal`.
- Single `composeApp` module with `commonMain`, `androidMain`, `iosMain` source sets
- Platform-specific code is minimal: just `SqlDriver` factory per platform
- `iosApp/` contains a thin Swift wrapper calling Kotlin's `MainViewController`

## Build & verify

| Goal | Command |
|---|---|
| Full verification (compile + tests + Xcode build) | `./gradlew build` |
| Kotlin/Native unit tests only | `./gradlew iosSimulatorArm64Test` |
| Xcode build only | `./gradlew verifyXcodeBuild` |

Use `./gradlew build` before committing or after any significant change. Use `./gradlew iosSimulatorArm64Test` after changing Kotlin business logic or tests when the Xcode build is irrelevant. Use `./gradlew verifyXcodeBuild` after touching `iosApp/` Swift files or project configuration.

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
- Prefer tiny types: wrap domain concepts in `value class` (e.g. `ItemId`, `ItemName`, `CreatedAt`,
  `UpdatedAt`) rather than using raw primitives. This prevents passing values in the wrong order and
  makes invalid states unrepresentable. No `@JvmInline` — iOS doesn't support it. String-based tiny
  types must enforce all domain invariants in `init`, e.g.
  `require(value == value.trim()) { "must be trimmed" }`. Give each tiny type its own `XxxTest`
  with exhaustive invariant cases (leading space, trailing space, both).
- StateFlow & ViewModel rules:
  - Never expose a `MutableStateFlow` directly. Declare `private val _x = MutableStateFlow(...)`
    and expose `val x: StateFlow<T> = _x.asStateFlow()`.
  - Share repository flows with `SharingStarted.WhileSubscribed()`. Never use `Eagerly` — it keeps
    the upstream alive for the full ViewModel lifetime even with no subscribers.
  - Collect StateFlow in Compose with `collectAsStateWithLifecycle()`, never `collectAsState()`.
  - Mutate UI state (e.g. clear the text field) only *after* the repository suspend call completes
    — never before, to avoid silent data loss if the write throws.
  - Always supply `key = { item.id.value.toString() }` on `LazyColumn`/`LazyRow` items so Compose
    tracks item identity across recompositions.
- Before committing, confirm every declared Gradle dependency is actually imported somewhere in
  source. Remove unused entries. Verify version numbers on Maven Central before pinning new
  libraries.

## Testing conventions

- Assert full objects using `assertEquals(expected, actual)` rather than checking properties one by
  one. Constructing the expected object causes a compile error when new fields are added, keeping
  tests exhaustive. For unpredictable fields (e.g. generated UUIDs), use the actual value from the
  result: `id = item.id`.
- Use a `FakeClock` (injectable `Clock` implementation) for deterministic timestamps. Place shared
  test helpers like `FakeClock` in `commonTest/.../testutil/` as `internal class`. Never inline the
  same helper in multiple test files.
- When testing a `WhileSubscribed` StateFlow in `runTest`, activate it with
  `flow.launchIn(backgroundScope)` and pass an explicit dispatcher:
  `runTest(UnconfinedTestDispatcher()) { ... }` so `backgroundScope` uses it.
- Tests live in `commonTest` and run on the iOS simulator via `./gradlew iosSimulatorArm64Test`.

## Project structure

```
composeApp/src/
  commonMain/kotlin/
    shoppinglist/   # Domain model, repository, ViewModel, screen (package-by-feature)
    App.kt
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
