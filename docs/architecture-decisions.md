# Architecture Decisions

## AD-1: Kotlin Multiplatform + Compose Multiplatform (not native Swift)

**Context:** The developer knows Kotlin/Java well but has no Swift experience. The app is
iPhone-first but will eventually support Android.

**Decision:** Use KMP with Compose Multiplatform for all code including UI.

**Rationale:**

- KMP reached stable November 2023. Compose Multiplatform for iOS reached stable May 2025 (v1.8.0),
  currently at v1.10.x.
- McDonald's, Netflix, Google Workspace, and Cash App run KMP in production on iOS.
- Writing UI once achieves 80-96% code sharing. The alternative (KMP + SwiftUI) requires learning
  Swift, writing UI twice, and bridging coroutines to async/await.
- Compose Hot Reload (stable January 2026) enables rapid UI iteration on desktop target.

**Trade-offs:**

- Kotlin/Native iOS build times are slower than JVM builds.
- Xcode cannot inspect Compose views for debugging.
- Binary size adds ~9-15MB overhead vs native SwiftUI.
- Kotlin-to-iOS bridge goes through Objective-C headers (Swift Export is experimental, targeted
  stable 2026).

## AD-2: compose-cupertino for iOS-native look and feel

**Context:** The app is iPhone-first. Default Compose Multiplatform renders Material Design, which
looks foreign on iOS.

**Decision:** Use compose-cupertino (`cupertino-adaptive` module) for all UI components.

**Rationale:**

- Provides Compose implementations of iOS Cupertino widgets (switches, nav bars, text fields, action
  sheets, etc.).
- The `cupertino-adaptive` module renders Cupertino on iOS and Material3 on Android automatically —
  no rewrite needed when adding Android.
- API mirrors Material3 component APIs, so swapping is mechanical.

**Trade-offs:**

- Third-party library dependency. The original repo (alexzhirkevich) slowed in maintenance; an
  actively maintained fork exists (RobinPcrd, published as `io.github.robinpcrd`).
- If the library becomes unmaintained, components can be replaced with Material3 or native SwiftUI
  interop incrementally.

## AD-3: SQLDelight for local storage (not Room, not Firebase)

**Context:** The app must work fully offline. Sharing/sync is a future goal, not MVP.

**Decision:** SQLDelight 2.x with local-only storage. No backend.

**Rationale:**

- Most mature multiplatform database for KMP, built by Square/Cash App.
- Write raw SQL in `.sq` files → compiler generates type-safe Kotlin APIs.
- Natural upgrade path to PowerSync for future sync.
- Room KMP is viable (stable mid-2025) but has a shorter KMP track record.
- Firebase Firestore's offline mode is a cache layer with ~500 offline transaction limit —
  unsuitable for extended offline use.

**Schema design for future sync:**

- UUIDs for primary keys (not auto-increment) — essential for multi-device ID generation.
- `created_at` / `updated_at` on every entity.
- `list_id` / `user_id` fields present but unused in MVP.
- `sync_status` column present but unused in MVP.

**Future sync path:** PowerSync + Supabase. PowerSync uses SQLDelight's drivers internally, so
migration from local-only is minimal. Supabase provides Postgres backend with auth. Last Write Wins
is sufficient for shopping list conflict resolution.

## AD-4: Manual constructor injection (no DI framework)

**Context:** The developer has a strong preference for explicit wiring over magic (http4k-style).
The app has a small dependency graph (~5-10 classes).

**Decision:** Plain constructor injection via an `AppDependencies` class. No Koin, no Dagger, no
service locator.

**Rationale:**

- Compile-time safety: wiring errors are build errors, not runtime crashes.
- Full visibility of the dependency graph in one place.
- Zero magic, zero reflection, zero runtime resolution.
- Koin is a service locator (runtime resolution), not true DI. Dagger requires annotation processing
  overhead that's overkill here.

**Example:**

```kotlin
class AppDependencies(sqlDriver: SqlDriver) {
    private val database = ShoppingDatabase(sqlDriver)
    private val itemRepository = SqlDelightItemRepository(database)
    val shoppingListViewModel = ShoppingListViewModel(itemRepository)
}
```

## AD-5: AndroidX ViewModel for state management

**Context:** Need lifecycle-aware state management that works across KMP platforms.

**Decision:** AndroidX ViewModel (KMP-supported since 2.8.0) with Kotlin StateFlow.

**Rationale:**

- Official Google library with full KMP support.
- Survives configuration changes on Android.
- StateFlow integrates naturally with Compose (`collectAsState()`).
- MVI adds ceremony overkill for a shopping list. Decompose has a steep learning curve.

## AD-6: Platform strategy — iPhone first, then Android

**Context:** The developer wants to ship something useful quickly. No near-term need for Android.

**Decision:** Target iOS only for MVP. Structure the project so Android can be added by implementing
`androidMain` SqlDriver and building an Android entry point.

**Rationale:**

- Reduces scope and allows focusing on one platform's UX.
- KMP project structure inherently supports adding platforms later.
- compose-cupertino's adaptive module means UI components automatically render as Material3 on
  Android when the time comes.
