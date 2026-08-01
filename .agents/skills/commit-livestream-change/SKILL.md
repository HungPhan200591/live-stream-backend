---
name: commit-livestream-change
description: Fast local Git commit for live-stream-backend. Direct `$commit-livestream-change` commits current changes through one minimal local fast path, without subagents, tests, or push. Use for commit requests; not for review or message-only requests.
---

# Fast Commit

Speed first. Do not read `AGENTS.md`, project docs, tests, history, or full diff.

- Direct invocation: commit all current tracked, untracked, and staged changes in one commit.
- Scoped request: commit only that scope.
- Never push, pull, fetch, rebase, amend, reset, force, use `--no-verify`, or create an empty commit.

## Workflow

Do not spawn or delegate to subagents. Do not run another Git inspection first. Run exactly:

```powershell
.agents/skills/commit-livestream-change/scripts/prepare-commit.ps1 -MaxDiffLines 40
```

For a scoped request, append the explicit `-Scope` value. If the result is `CLEAN` or `BLOCKED`, report it and stop. If it is `PREPARED`:

1. Use only `FILES`, `STAT` and the short `DIFF` to create one short Conventional Commit subject. Do not inspect more files or reason about implementation details. If unclear, use `chore: update project files`.
2. Run `git commit -m "<subject>"`.
3. Run exactly `git show --stat --oneline --summary HEAD` and `git status --short --branch`.
4. Return only SHA, subject, file count, status and `tests: not run`.

## Stop only when

- the script returns `CLEAN` or `BLOCKED`;
- `git commit`/hook fails;
- Git state changes unexpectedly.
