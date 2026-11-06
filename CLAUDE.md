# Working protocol — Old MacDonald Had an API

> **Temporary file.** This protocol only exists to build the curriculum. Delete `CLAUDE.md` (and the "Working protocol" mentions elsewhere, if any) once the curriculum is finished and `HANDOFF.md` is retired. Remind Thomas at the final wrap-up step.

Read at the start of every session, then read `HANDOFF.md` (latest status section at the bottom), `DESIGN.md` and `research/spring-boot-4-findings.md`. Decisions in those files are confirmed; do not re-litigate them.

## One step per session

Thomas works in small steps, one per session, each in a fresh context. Do this without being asked:

1. **Start:** read the files above, state in two lines which step you are doing (the "Next" line at the end of `HANDOFF.md`). Do only that step.
2. **Work:** keep to the step. Do not start the following step, even if it looks quick.
3. **Write down what happened** in a new dated section at the end of `HANDOFF.md`: what was built or decided, facts verified (also into `research/spring-boot-4-findings.md` when they are version facts), what is still open, and one **Next:** line naming the next single step.
4. **Commit** the step (docs and curriculum files; `mentor/` stays git-ignored). Do not push unless Thomas says so.
5. **End** the session with a short summary and the line: "Step done and recorded. Run `/clear` to start the next step." Claude cannot clear the session itself; Thomas types `/clear`.

## Rules that matter

- `mentor/` (the reference solution) is git-ignored because the repo is public. Never commit it.
- Every test, snippet and hint in the lessons must compile and pass in `mentor/reference/` first.
- Nothing from Boot 4 is written from memory; check `research/spring-boot-4-findings.md` and verify by compiling.
- Show Thomas the drafted Phase 1 lessons before writing later phases.
