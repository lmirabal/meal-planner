# Meal Planner — A shopping list app for iPhone

A grocery list app where items have quantities with mixed units (e.g. "3 lemons", "300g mince beef",
"⅓ tsp cinnamon"). Tracks when items were added, ticked off, and where they were bought.

## Tech stack

| Layer | Choice |
|-------|--------|
| Language | Kotlin (no Swift) |
| Framework | Kotlin Multiplatform + Compose Multiplatform |
| UI components | [compose-cupertino](https://github.com/alexzhirkevich/compose-cupertino) — Cupertino widgets on iOS, Material3 on Android |
| Storage | SQLDelight 2.x (local SQLite, no backend) |
| State | AndroidX ViewModel + Kotlin StateFlow |
| Navigation | `org.jetbrains.androidx.navigation:navigation-compose` |

## Getting started

**Prerequisites**

- [kdoctor](https://github.com/Kotlin/kdoctor) — verify your environment: `kdoctor`
- Android Studio (with Kotlin Multiplatform plugin)
- Xcode 15+

**Run on iOS simulator**

1. Open the project in Android Studio.
2. Select the `iosApp` run configuration and choose an iPhone simulator.
3. Click Run — or open `iosApp/` in Xcode and run from there.

## Roadmap

Active iterations are tracked as GitHub issues under the
[v0.1 milestone](https://github.com/lmirabal/meal-planner/milestone/1).
