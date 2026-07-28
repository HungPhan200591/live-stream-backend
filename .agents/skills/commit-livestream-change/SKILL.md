---
name: commit-livestream-change
description: Fast local Git commit for live-stream-backend. Direct `$commit-livestream-change` commits all current changes with one fast low-cost subagent and never pushes. Use for commit requests; not for review or message-only requests.
---

# Fast Commit

Speed first. Do not read `AGENTS.md`, project docs, tests, history, or full diff.

- Direct invocation: commit all current tracked, untracked, and staged changes in one commit.
- Scoped request: commit only that scope.
- Never push, pull, fetch, rebase, amend, reset, force, use `--no-verify`, or create an empty commit.

## Primary agent

Spawn exactly one subagent with `gpt-5.6-terra`, reasoning `low`, and no forked turns. Do not run Git commands or inspect files before spawning. Ask it:

```text
Fast local commit only. Do not read AGENTS.md, docs, tests, history, or SKILL.md. Do not delegate. Run .agents/skills/commit-livestream-change/scripts/prepare-commit.ps1 with -MaxDiffLines 40 [and explicit -Scope only when provided].
If CLEAN or BLOCKED, return that result. If PREPARED, use only FILES/STAT/short DIFF to write one short Conventional Commit subject, run git commit, then git show --stat --oneline --summary HEAD and git status --short --branch. Never push. Return only SHA, subject, file count, status, and `tests: not run`.
```

Wait and relay the result. Do not repeat any check. Only use the same flow locally if subagents are unavailable.

## Stop only when

- the script returns `CLEAN` or `BLOCKED`;
- `git commit`/hook fails;
- Git state changes unexpectedly.
