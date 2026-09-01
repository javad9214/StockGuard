# Project Rules — ComposeTrainer (Anbar)

These rules apply to all work in this repository. Follow them in every change.

---

## Rule 1: Commit Messages (Conventional Commits / GitHub standard)

Every commit MUST use the Conventional Commits format:

```
<type>(<optional scope>): <short imperative subject>

[optional body: what and, more importantly, why]

[optional footer: BREAKING CHANGE: ..., Closes #123]
```

**Subject rules**

- Use the imperative mood: "add", "fix", "refactor" — not "added", "fixes", "adding".
- Lowercase subject, no trailing period, max ~50 chars (hard limit 72).
- Always add a scope when the change targets one feature area.

**Allowed types**

| Type     | Use for                                                        |
|----------|----------------------------------------------------------------|
| feat     | New feature                                                    |
| fix      | Bug fix                                                        |
| refactor | Code change that neither fixes a bug nor adds a feature        |
| perf     | Performance improvement                                        |
| docs     | Documentation only                                             |
| test     | Adding or correcting tests                                     |
| chore    | Maintenance (tooling, deps, configs) that doesn't touch app code |
| style    | Formatting, whitespace, naming — no logic change               |
| build    | Build system / Gradle changes                                  |
| ci       | CI/CD workflow changes                                         |

**Body rules**

- Wrap at 72 characters, explain the motivation, not a diff replay.
- Reference issues in the footer: `Closes #123`, `Fixes #45`.
- Breaking changes: add footer `BREAKING CHANGE: <description>` (or `!` after type).

**Examples (match repo history)**

```
feat(categories): add GET /api/categories client with nested subcategory DTOs

refactor(products): fix async save flow, reactive product stream, and AddProduct form

fix(invoice): clamp sale quantity to stock when scanning a duplicate product

Closes #142
```

**Workflow rule**

- After completing a code-changing task, ALWAYS provide a ready-to-use
  Conventional Commit message for the work just done. Present the message;
  do not commit or push unless explicitly asked.

---

## Rule 2: Clean Architecture & Best Practices

### Layer structure and the dependency rule

```
ui  →  domain  ←  data
```

- `domain/` — models, value objects, business rules, use cases, repository
  **interfaces**. Must not import anything from `data/`, `ui/`, or Android
  frameworks (no `android.util.Log`, no `Context`, no Compose).
- `data/` — Room entities/DAOs, remote APIs, mappers (`toDomain()` /
  `toEntity()`), repository **implementations**.
- `ui/` — screens, components, ViewModels. Talks to domain use cases only,
  never to DAOs or repository implementations directly.
- `di/` — Hilt modules wiring implementations to interfaces.

Dependencies always point inward: **ui and data may know about domain, never
the other way around, and ui must not know about data.**

### Business logic

- Business rules (stock limits, totals, validation, clamping) live in the
  **domain layer** as model methods or extension functions — never in
  ViewModels, and never in composables. ViewModels orchestrate; they don't
  compute.
- Use the existing value objects (`ProductId`, `Quantity`, `Money`) across
  layer boundaries instead of raw `Long`/`Int` primitives.

### ViewModels (MVVM + UDF)

- Expose **one immutable `UiState` data class** via `StateFlow` (`_uiState` +
  `asStateFlow()`). Don't split state across multiple public flows (loading,
  errors belong inside `UiState`).
- One-time side effects (navigation, one-shot messages) go through a `Channel`
  exposed as `receiveAsFlow()`.
- Update state atomically with `_uiState.update { }` — never read-modify-write
  a public var.
- Inject only the use cases actually used. Remove dead dependencies.
- Never build user-facing strings by concatenation; inject
  `@ApplicationContext` and use `context.getString(R.string..., ...)`.
- Rethrow `CancellationException` before catching generic `Exception`.

### Compose UI

- Screens receive **state + lambdas** (`onComplete`, `onAddNewProduct(barcode)`),
  never a `NavController`. Navigation decisions live at the call site
  (e.g. `MainScreen`).
- One composable per file for substantial UI; keep previews next to them.
- No `!!` force unwraps on state that can change between recompositions —
  render conditionally on non-null values instead.
- Lists: use `items(list, key = { stable id })`; never rely on two parallel
  lists staying index-aligned.
- No `Log.d` in composables; no business logic in composables.
- Use Material 3 non-deprecated APIs (`HorizontalDivider`, not `Divider`).

### Resources

- **No hardcoded user-facing strings** in Kotlin — always `str(R.string.*)`,
  with placeholders (`%1$s`, `%1$d`) instead of string templates. Add the
  string to BOTH `values/strings.xml` and `values-fa/strings.xml`.
- Use dimen tokens (`space_*`, `radius_*`, `size_*`, `text_size_*`) instead of
  raw `dp`/`sp` literals; add a token only when a matching one doesn't exist.

### Hygiene

- Delete dead code: unused imports, params, ViewModels, empty files.
- Don't commit `app/schemas/` noise or IDE files; keep `.gitignore` current.
- Verify every change compiles (`./gradlew :app:compileDebugKotlin`) before
  considering it done.
