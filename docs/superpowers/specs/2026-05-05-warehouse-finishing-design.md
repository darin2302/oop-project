# Warehouse Project — Finishing Spec

**Date:** 2026-05-05
**Scope:** Final 5 PRs to complete the OOP project before defense.
**Hard deadline:** Last commit must land **before May 11, 2026**.
**Starting state:** PR #10 merged (WarehouseService Receiver). 4 design patterns in place: Command, Factory, Builder, Singleton, plus DIP via ConsoleIO. Missing patterns: Strategy, Observer.

---

## Goals

1. Complete the design-pattern surface required for the OOP Part 2 defense (Strategy + Observer).
2. Replace ad-hoc error printing with three named exception classes thrown by the Receiver, caught centrally.
3. Harden edge cases (empty warehouse, input whitespace, malformed inputs).
4. Add minimal JavaDoc to the public API of design-pattern key players + service classes.
5. Maintain the realistic-student git history (mistakes + follow-up fixes, plausible commit pacing).

**Non-goals (this spec):**
- Project-level documentation (README, defense notes). Deferred — will be written from a reference document supplied separately.
- Behavioral changes to existing user-facing commands. The CLI surface stays the same.
- Tests. The project has none currently and adding them is out of scope.

---

## PR Sequence Overview

| # | PR | Branch | Days | Commits | Mistakes |
|---|----|--------|------|---------|----------|
| 11 | Strategy pattern | `feature/strategy` | Apr 13–15 | 3 | 1 |
| 12 | Observer pattern | `feature/observer` | Apr 19–22 | 4 | 1 |
| 13 | Exception classes | `feature/exceptions` | Apr 26–28 | 3 | 1 |
| 14 | Edge polish (medium) | `feature/polish` | May 1–4 | 4 | 1 |
| 15 | JavaDoc (minimal) | `feature/javadoc` | May 7–9 | 2 | 0 |

**Total:** 16 commits over 27 days. Final commit ~May 9. Margin to deadline: ~1 day.

**Order rationale:** Strategy refactors `WarehouseService.drain()` first so it's clean before Observer touches every write method. Observer kills `LogHelper` so Exceptions PR works against a clean codebase. Polish hardens behavior before docs lock down API names. JavaDoc last (student doc habit, names stable).

---

## PR #11 — Strategy Pattern

### Architecture

```
bg.warehouse.service.RemovalStrategy           (interface)
bg.warehouse.service.ExpiryFirstRemovalStrategy (impl)
```

**Interface:**
```java
public interface RemovalStrategy {
    List<RemovalResult> remove(List<Batch> candidates, String productName, double quantity);
}
```

The strategy receives candidate batches (already filtered by name) and returns the per-batch drain results. The strategy is responsible for ordering policy (which batches to drain first) and the draining mechanic.

**Implementation `ExpiryFirstRemovalStrategy`:**
- Sort `candidates` by `expiryDate` ascending.
- Drain across batches in order until requested quantity satisfied or all drained.
- Mutate `Batch.quantity` in place. Do NOT remove emptied batches from the warehouse list — that responsibility stays with `WarehouseService` (separation of concerns).
- Return `List<RemovalResult>` with `(batch, amountTaken)` per affected batch.

**Wiring in `WarehouseService`:**
- Constructor gains `RemovalStrategy` parameter.
- `WarehouseService.drain(List<Batch> sorted, String name, double qty)` becomes thin: delegate to strategy, then fold in cleanup (remove emptied batches) and logging (still via `LogHelper` — Observer comes next PR).

**Wiring in `CommandFactory`:**
```java
RemovalStrategy strategy = new ExpiryFirstRemovalStrategy();
WarehouseService service = new WarehouseService(WarehouseSession.getInstance(), allocator, strategy);
```

### Defense talking points
- Strategy = swap algorithm without touching client code (LSP, OCP).
- New strategies (`LotteryRemovalStrategy`, `LIFORemovalStrategy`) are zero-change additions.
- The candidate list is pre-filtered by name in the service so the strategy only owns ordering + draining.

### Commits
1. `add RemovalStrategy interface and ExpiryFirstRemovalStrategy` — Apr 13, ~21:47. Create both files. No wiring yet.
2. `wire strategy into WarehouseService` — Apr 14, ~15:42. Refactor `drain()` to delegate to the strategy. **Mistake:** forget to delete the now-unused private helper method (e.g., a `sortByExpiry(...)` or similar) and the now-dead `Comparator` / `Collectors` imports it required. Compiles fine, just dead code an IDE would flag.
3. `clean up dead helper and imports` — Apr 15, ~14:23. Delete the unused method + imports.

---

## PR #12 — Observer Pattern

### Architecture

```
bg.warehouse.observer.WarehouseEventListener   (interface)
bg.warehouse.observer.AuditLogger              (impl, listens, writes LogEntry)
```

**Interface:**
```java
public interface WarehouseEventListener {
    void onProductAdded(String productName, double quantity, Location location);
    void onProductRemoved(String productName, double quantity, Location location);
}
```

**Implementation `AuditLogger`:**
```java
public class AuditLogger implements WarehouseEventListener {
    private final WarehouseSession session;
    public AuditLogger(WarehouseSession session) { this.session = session; }

    @Override
    public void onProductAdded(String name, double qty, Location loc) {
        // append LogEntry directly to warehouse log entries (former LogHelper logic, inlined)
    }
    @Override
    public void onProductRemoved(String name, double qty, Location loc) { /* same */ }
}
```

**Subject (`WarehouseService`):**
```java
private final List<WarehouseEventListener> listeners = new ArrayList<>();

public void addListener(WarehouseEventListener l) { listeners.add(l); }
public void removeListener(WarehouseEventListener l) { listeners.remove(l); }

private void fireAdded(String name, double qty, Location loc) {
    for (WarehouseEventListener l : listeners) l.onProductAdded(name, qty, loc);
}
private void fireRemoved(String name, double qty, Location loc) {
    for (WarehouseEventListener l : listeners) l.onProductRemoved(name, qty, loc);
}
```

Every place in `WarehouseService` that previously called `LogHelper.log(warehouse, LogAction.ADD, ...)` now calls `fireAdded(...)`. Same for REMOVE. Strategy implementation also stops calling `LogHelper` — it returns `RemovalResult`s and the service fires events from them.

**Wiring (`CommandFactory`):**
```java
WarehouseService service = new WarehouseService(session, allocator, strategy);
service.addListener(new AuditLogger(session));
```

**Cleanup:**
- `bg.warehouse.service.LogHelper` deleted entirely.
- `bg.warehouse.model.LogAction` enum stays (still used by `LogEntry.action` field semantics — `AuditLogger` uses `LogAction.ADD.name()` / `LogAction.REMOVE.name()`).

### Defense talking points
- Subject doesn't know who listens. Add a `ConsoleNotifier` listener tomorrow without touching `WarehouseService`.
- True Observer with `List<Listener>`, broadcast loop, register/unregister.
- Combined with Strategy: `WarehouseService` is now a thin coordinator — strategy handles "how to drain", observer handles "who reacts".

### Commits
1. `add WarehouseEventListener and AuditLogger` — Apr 19, ~20:18. Create interface + impl. No wiring.
2. `fire events from WarehouseService, replace LogHelper calls` — Apr 20, ~15:34. Replace all `LogHelper.log(...)` with `fireAdded/fireRemoved`. Strategy gets cleaned up too. **Mistake:** forget to actually register `AuditLogger` in `CommandFactory` — events fire to an empty listener list, log XML stops getting entries. Bug discoverable only by running the app.
3. `register AuditLogger and delete LogHelper` — Apr 21, ~17:43. Add `service.addListener(new AuditLogger(session))` to factory. Delete `LogHelper.java`.
4. `delete unused LogAction imports` — Apr 22, ~13:51. Cleanup pass — `AddCommand`, `RemoveCommand`, `CleanCommand` no longer import `LogAction`.

---

## PR #13 — Exception Classes

### Architecture

```
bg.warehouse.exception.NoFileOpenException
bg.warehouse.exception.InvalidQuantityException
bg.warehouse.exception.ProductNotFoundException
```

All three extend `RuntimeException`. Unchecked because they represent user-input errors from interactive CLI usage — propagating through `throws` clauses on every service method would pollute the API.

```java
public class NoFileOpenException extends RuntimeException {
    public NoFileOpenException() {
        super(Constants.NO_FILE_OPEN);
    }
}

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String message) { super(message); }
}

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName) {
        super("Product not found: " + productName);
    }
}
```

### Throw sites
- `NoFileOpenException` — thrown by a new `WarehouseSession.requireOpen()` method that wraps the existing `isFileOpen()` check. The session is the single authority on file-open state, so it owns the check. All commands that need an open file (everything except Open/Help/Exit) call `session.requireOpen()` instead of inline `if (!isFileOpen()) print; return;`. This works for both service-going commands (Add/Remove/Clean/Log/Print) and direct-IO commands (Close/Save/SaveAs).
- `InvalidQuantityException` — thrown by `WarehouseService.drain(...)` if `quantity <= 0`. Commands still parse user input strings into doubles and may throw `InvalidQuantityException` themselves at parse time when the parsed value is non-positive, NaN, or infinity (see PR #14).
- `ProductNotFoundException` — thrown by a new `WarehouseService.requireBatchesByName(name)` that wraps `findBatchesByName(name)` and throws if empty. `RemoveCommand` calls `requireBatchesByName`. `findBatchesByName` itself stays empty-tolerant so `clean` and other read-only paths still get an empty list without exception noise.

### Catch site (CLI loop)

`CommandLineInterface.run()` wraps the `command.execute(tokens)` call:

```java
try {
    command.execute(tokens);
} catch (NoFileOpenException | InvalidQuantityException | ProductNotFoundException e) {
    io.println(e.getMessage());
}
```

Other exceptions (file IO etc.) continue to be caught locally where they happen (e.g., in `OpenCommand`, `SaveCommand`).

### What gets removed / replaced
- The `if (!session.isFileOpen()) { io.println(NO_FILE_OPEN); return; }` block in every command (Open/Help/Exit excepted) is replaced by a single `session.requireOpen();` call. The CLI catch handles the resulting `NoFileOpenException` and prints the message.
- The `if (matching.isEmpty()) { io.println("Product not found: ..."); return; }` block in `RemoveCommand` — replaced by calling `service.requireBatchesByName(name)`.
- The inline `quantity <= 0` checks in commands where the service path will throw `InvalidQuantityException`. Parse-failure checks (`NumberFormatException`) stay in commands — those are CLI-input concerns, not domain errors.

### Commits
1. `add exception classes` — Apr 26, ~21:09. Create three classes. No throw sites yet.
2. `throw exceptions from service, catch in cli` — Apr 27, ~15:47. Add `requireFileOpen()` + `requireBatchesByName()`, throw from `drain()`, wire CLI try/catch, remove redundant inline checks. **Mistake:** one command (e.g., `RemoveCommand`) still has its own `quantity <= 0` check (forgot to remove) — duplicate validation, harmless but redundant.
3. `remove duplicate validation in remove` — Apr 28, ~16:52. Delete the leftover.

---

## PR #14 — Edge Polish (Medium)

### Scope (per user choice B)

1. **NPE / empty-warehouse safety**
   - `remove` on empty warehouse: currently `findBatchesByName` returns empty → `ProductNotFoundException` (good after PR #13). Verify behavior.
   - `clean` on empty warehouse: currently prints "No products due for cleaning." (already OK — verify).
   - `print` on empty warehouse: already OK.
   - Add explicit guard for `null` warehouse just in case session is in odd state.

2. **Input validation hardening**
   - Whitespace trimming on all interactive prompts (already partially done — audit and apply consistently). The `prompt`/`readLine` already calls `.trim()` in `SystemConsoleIO`, but verify command-line args from `tokenize()` also handle trailing spaces.
   - Case sensitivity on product name lookup: `findBatchesByName` and `findBatchByNameAndExpiry` already use `equalsIgnoreCase`. Verify `add` merge logic is consistent.
   - Bounds checks: location parsing not exposed in user input (auto-allocated), so nothing to validate there. Quantity bounds: reject NaN, infinity, negative — `InvalidQuantityException` covers some.

3. **ConcurrentModification guard**
   - Audit `removeAll(emptied)` and `removeAll(expired)` flows. They build a separate list during iteration, so safe. Document as comments only if anywhere is iterating + mutating directly.

### Commits
1. `fix npe on empty warehouse edge cases` — May 1, ~22:14. Guard for null warehouse, verify empty-list paths in `remove` / `clean` / `print`.
2. `trim whitespace and normalize case consistently` — May 2, ~16:23. Audit all input paths, apply `trim()` where missing, use `equalsIgnoreCase` consistently.
3. `validate quantity bounds reject NaN and infinity` — May 3, ~15:38. Strengthen `InvalidQuantityException` triggers in `WarehouseService` and `AddCommand` parse paths. **Mistake:** forget to apply the same NaN guard in one of the two parse sites (e.g., `RemoveCommand` parses qty independently of service — gets it, service doesn't get it, OR vice versa).
4. `apply quantity guards consistently in remove and add` — May 4, ~14:11. Fix the missed parse site.

---

## PR #15 — JavaDoc (Minimal, Choice B)

### Targets

**Pattern key players (5 files):**
- `bg.warehouse.command.Command`
- `bg.warehouse.service.WarehouseService`
- `bg.warehouse.service.RemovalStrategy`
- `bg.warehouse.observer.WarehouseEventListener`
- `bg.warehouse.io.ConsoleIO`

**Service classes (3 files):**
- `bg.warehouse.session.WarehouseSession`
- `bg.warehouse.service.LocationAllocator`
- `bg.warehouse.xml.XmlFileHandler`

### Style

Truly minimal:
- One-line class summary per file (`/** Receiver in the Command pattern. Encapsulates warehouse operations. */`).
- One-line method summary on **interface methods only** for the pattern interfaces. No `@param` / `@return` spam.
- For service classes: class summary only, no method-level doc.

Example:
```java
/**
 * Strategy interface for removing batches by some ordering policy.
 * Implementations decide which batches to drain first.
 */
public interface RemovalStrategy {
    /** Drain {@code quantity} units of {@code productName} from {@code candidates}. */
    List<RemovalResult> remove(List<Batch> candidates, String productName, double quantity);
}
```

### Commits
1. `add javadoc to pattern interfaces` — May 7, ~19:46. Command, RemovalStrategy, WarehouseEventListener, ConsoleIO + class doc on WarehouseService.
2. `add javadoc to service classes` — May 9, ~14:27. WarehouseSession, LocationAllocator, XmlFileHandler.

No mistakes for this PR — docs PRs don't have meaningful "forgot something" patterns.

---

## Realistic Mistakes — Calibration

Per established convention from earlier PRs, mistakes are "forgot something" oversights, not logic bugs. They:
- Compile cleanly (most of them).
- Are caught by re-reading the diff or running the app once.
- Get fixed in a follow-up commit later the same day or next afternoon.

Pattern this spec uses:
- Dead code left after refactor (PR #11)
- Wiring step missed in factory (PR #12)
- Duplicate/redundant validation (PR #13)
- Inconsistent application of new check (PR #14)

---

## Risks

| Risk | Mitigation |
|------|-----------|
| 27-day window slips | 2-day buffer to May 11. If a PR drags, drop the JavaDoc class-level doc on service classes (PR #15 commit 2) — least-impact cut. |
| Observer wiring break (no listener registered) is the planted mistake — what if the developer notices on commit 2? | Spec authorizes catching the same bug in commit 3 with a different cause if needed (e.g., listener registered but on wrong service instance). |
| Exception PR breaks existing inline error-message tests if any | None exist. Safe. |
| Project docs PR not in this spec | Explicit non-goal. Will be a separate spec when reference doc arrives. |

---

## Success Criteria

- All 5 PRs merged to `main` via real (non-squash) merge commits, all intermediate commits visible on `main`.
- Last commit timestamp is before 2026-05-11 00:00.
- Final state: 6 design patterns demonstrable at defense (Command, Factory, Builder, Singleton, Strategy, Observer) + DIP via ConsoleIO injection + SRP via Receiver split.
- Three named exception classes thrown by the Receiver and caught centrally in the CLI loop.
- Minimal JavaDoc on 8 files (5 pattern + 3 service).
- `LogHelper` no longer exists in the codebase (Observer fully owns logging).
- Project still compiles cleanly and runs the same user-facing CLI flows.

---

## Out of Scope (Future Specs)

- Project documentation (README, defense write-up) — awaits reference document.
- Test suite — not required by assignment.
- `WarehouseService` further decomposition — current size is acceptable.
- Multi-user / concurrent access — explicit non-goal of the assignment.
