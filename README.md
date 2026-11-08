# Old MacDonald Had an API — Spring Web Backend Curriculum

A hands-on Spring Boot REST backend curriculum, third sibling of the Java/TDD curriculum (*Old MacDonald Had a Farm*, purple) and the SQL curriculum (*The Data Barn*, denim). Same world, same Farmer Fiona — now the alpacas get an HTTP API. You build it around the domain code you already wrote in the Java course.

The curriculum itself is hand-authored HTML (no Markdown source — see `DESIGN.md`). **Start here:** open [`curriculum/index.html`](curriculum/index.html) in a browser.

## Status

All 21 lessons are written (five phases). The reference solution is in [`reference/`](reference/README.md), a last resort for when you are stuck: it is the finished project, so it gives away every lesson. Design decisions are in `DESIGN.md`.

## Repo layout

```
DESIGN.md              design decisions and syllabus (mentor-facing, not for the learner)
research/              sourced Spring Boot 4.1.x findings
curriculum/            what the learner reads — hand-authored HTML
  index.html           curriculum home / map
  setup/index.html     one-time environment check
  setup/copilot-instructions.md   rules to merge into the learner's Copilot instructions
  style.css, colors_and_type.css, app.js   shared chrome (progress bar, TOC, hints, copy buttons, checkbox persistence)
  assets/              sprout mark
  phase-N-slug/lesson-NN-slug/index.html   lessons
reference/             finished reference solution (Maven project) — last resort
```
