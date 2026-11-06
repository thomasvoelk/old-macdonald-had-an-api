# Design doc — Old MacDonald Had an API (Spring curriculum)

Mentor-facing planning notes. Not learner-facing (that is `curriculum/`). All decisions here were confirmed by Thomas in the design interview recorded in `HANDOFF.md`. Version facts live in `research/spring-boot-4-findings.md`; check its "Could not verify" list before relying on a detail.

## Who this is for

A first-year apprentice who has done Java + TDD (Old MacDonald) and SQL (Data Barn). Company stack: Spring REST backend + Angular frontend. This curriculum covers the backend only; **no Angular, no CORS**.

## Cast (the learner's world)

- **Fiona**: the customer (owns the alpaca farm). The learner is the contractor.
- **Ilona** (she/her): real fellow apprentice one year ahead of the learner; plays the frontend developer who builds Fiona's screens and asks for API changes (Lessons 01, 04).
- **Hasi** (he/him): plays the code reviewer (Lesson 04: wants `getPensCount()` renamed).
- Names are an in-joke for the learner. The repo and Pages site are **public**: Thomas should check with Ilona and Hasi before pushing, or swap the names.

## Relationship to the siblings

- Own numbering: **Lessons 01–~20, Phases 1–5**, like the SQL curriculum. Java lessons are referred to as "Java Lesson N".
- Same world (Fiona's alpaca farm), same lesson format as Java: Story, Your Task, Learning Goals, Getting Started, Test List (acceptance criteria in `<details>`), 3 Hints (`<details>`), When You're Done, Reflect, Copilot prompt. New: every lesson opens with a **Spring Sprout** box (see below).
- Starts after Java Lesson 19 (Java Phase 4). Java Lessons 21 (records) and 22 (`Optional`) are prerequisites for Spring Phase 2 (say so at the top of Phase 2). Java Lessons 20, 23, 24 can run in parallel or afterwards.
- Hand-authored HTML only. Shared chrome copied from `sql-curriculum/curriculum/`; token names unchanged, values recoloured.

## Visual identity

Third sibling: Java = purple, SQL = denim blue, Spring = **hot pink**.

- `--grape` `#FF2E93` is the bright accent (progress bar, mark, chunky shadows). Never used for text.
- `--primary` `#C2185B` is the text/link pink (5.3:1 on the blush `--cream` `#FFF0F6`, 5.8:1 on `--paper`). `--grape-700` `#A30F54` for text on pink tints and hover.
- Token names (`--grape`, `--cream`, `--plum`) are deliberately kept from the siblings so the shared CSS/JS diffs cleanly; read "grape" as "the accent".
- Mascot: `assets/sprout-mark.svg`, a sprout on a pink rounded square, keeping the four corner dots.
- Added to `style.css`: `.spring-corner` box. Everything else is the sibling chrome.

## The learner's setup

- She keeps her own repo (the one with her code) and **copies lessons into it**. Windows, Podman Desktop; Postgres is the company database.
- Ship a Spring-adapted `copilot-instructions.md` for her to merge into her existing one: keep the Socratic rules; add "never write a controller, test, or SQL query for her"; explain the layering. Git prefixes (`feat:`, `test:`, `refactor:`, `fix:`) and phase-end demos carry over; one commit per green test.

## Architecture (the core teaching)

Her existing domain (package `alpacas`: `Alpaca`, `Herd`, `Pen`, `Farm`, `Shepherd`, `Llama`, `FleeceColor`, `PenNotFoundException`, `FleeceColorNotFoundException`) is the domain. The course builds the delivery mechanism around it (hexagonal). Single Maven module, application class in `alpacas`, web code in `alpacas.web`.

- **Revised 2026-11-06 (Thomas): Spring annotations in domain classes are allowed.** The earlier rule "domain classes never import Spring" and its Phase 3 architecture test are dropped. Separation is by place: domain in `alpacas`, web in `alpacas.web`, later SQL in `alpacas.sql`. `AlpacasApplication` sits in the top package `alpacas`, so component scanning covers all of them. No formal layers or hexagonal/onion in this course (Thomas may introduce that later). What stays: controllers never accept or return domain objects (see below).
- **Separation of concerns, taught explicitly:** controllers never accept or return domain objects. Every endpoint has its own request/response records (`AlpacaResponse`, `CreateAlpacaRequest`) in `alpacas.web` with explicit mapping to and from the domain. Why: the JSON contract and the business model must change independently; exposing domain objects couples them (renames break clients, internals such as public fields leak). **Experience first, rule second (decided 2026-11-06):** Lesson 03 deliberately returns the `Farm` straight from the controller and the learner reads the leaky JSON; Lesson 04 lets her feel the coupling (rename a getter, a test breaks) and then introduces the response record, the mapping and the principle. There is no paper audit lesson. Later lessons keep the rule; an architecture check asserts controller signatures contain no domain types.
- Persistence side: the domain owns the repository interface; SQL rows are mapped to domain objects inside the JDBC adapter. The domain may change, test-first, when the adapter pushes back (`findByName` returns `Optional`, remove `System.out`, add ids).
- Known awkward bits in her code that become lessons: no ids; public fields; `findByName`/`heaviestAlpaca` return `null`; `System.out.println` in `isHealthyWeight()`/`isReadyToShear()`; exceptions `IllegalArgumentException`/`IllegalStateException`/`PenNotFoundException` map to 400/409/404.
- URLs use **names first** (`/pens/{penName}/alpacas/{name}`); an identity lesson adds ids right before the database phase.

## Teaching approach

- TDD continues. Domain: plain JUnit. Controllers: `@WebMvcTest` + MockMvc (`MockMvcTester` is the likely default: AssertJ-style, matches her habits; decide when building the reference). One `@SpringBootTest` smoke test per phase. **Mockito appears only in the London lesson.**
- **Spring Sprout** (renamed from "Spring Corner" 2026-11-06, Thomas; CSS class still `.spring-corner`): each lesson that teaches a Spring idea opens with a short box (Lesson 04 has none: its idea is design, so it uses a Barn Briefing inside step 1) (a few paragraphs + link to official docs), because Spring concepts cannot be discovered by trial and error. It carries the *idea* only. New words are explained again in the step that uses them ("explain where it is used"), so she never has to scroll back. Tasks and hints follow; test-first work stays the core.
- **Barn Briefing** (CSS class `.concept-corner`): same shape, for background that is not Spring (first use: HTTP in Lesson 03). **Colour: teal** (`--teal`, `--teal-200`, text `#0A6B60`) so it is distinct from the pink Spring Sprout. Diagrams inside it use the same teal.
- **Llama Drama** (idea, not built yet): a possible box for common mistakes, wanted by Thomas. Add if a lesson needs it; use a warm colour (coral or marigold tokens) so it differs from pink and teal.
- Chicago (inside-out) is how Phases 1–3 work, but **the names Chicago and London are not used in the learner-facing text before the Phase 3 explanation lesson** (decided 2026-11-06, Thomas): she already builds inside-out without knowing it (domain first, web around it), so a label adds a term and no insight. The explanation lesson, right before the London lesson, introduces both styles with the contrast in front of her. One explanation lesson on London vs. Chicago, then one London (outside-in) lesson: `POST /pens/{pen}/alpacas/{name}/shear`, controller test first with a mocked `ShearingService`, discovering the service; touches `Shepherd` and `Farm`; her first Mockito use.
- **Side Quest** (optional, off the critical path): Clean Coders "Comparative Design" (https://cleancoders.com/series/comparativeDesign, 14 x ~1h; first 5 episodes London with Sandro Mancuso; repo branches `openchat-outside-in`, `openchat-unclebob`). Mention only; no episode assignments. Page could not be fetched; details are from Thomas.

## Outline (lesson numbers approximate, ~20 lessons / 8–9 weeks)

| Phase | Lessons | Focus |
|---|---|---|
| 1 – Hello Spring | 01–04 | Spring Boot setup (pom to Boot 4.1.x, Java 25), Farm as a bean (DI), first `GET /farm` that leaks the domain, then response records and separation of concerns |
| 2 – Reading the Farm | 05–08 | GET pens/alpacas/ready-to-shear, DTO records, `Optional` to 404, `@RestControllerAdvice` |
| 3 – Changing the Farm | 09–13 | POST/PUT/DELETE, validation, exceptions to 400/409, architecture test (controllers expose no domain types), London-vs-Chicago explanation + Side Quest, London shear lesson |
| 4 – The Barn Gets a Database | 14–17 | ids, repository interface (domain-owned), in-memory then `JdbcClient` implementation, run against Postgres |
| 5 – Wrap-up | 18–20 | Swagger UI / OpenAPI (springdoc), farm-API grand review, one-paragraph bridge to the Angular curriculum |

## Persistence

- `JdbcClient` with plain SQL, not JPA (JPA in one sentence at most). Interface owned by the domain; in-memory implementation first, then JDBC.
- Her own small schema (`farm`, `pens`, `alpacas`) in a plain `schema.sql` run by Spring at startup. No Flyway.
- App runs on **Postgres in Podman** (short `compose.yml`; setup page at the start of Phase 4, i.e. Lesson 14). Needs `spring.sql.init.mode=always` for Postgres and an idempotent `schema.sql` (`create table if not exists`).
- Tests use **embedded H2** via `@JdbcTest`, silently: no lesson on H2-vs-Postgres differences, no Testcontainers (one sentence "teams do this later" at most). By default `@JdbcTest` swaps in its own H2 and ignores `MODE=PostgreSQL`; write portable SQL. Mentor-only notes (belong in `mentor/`): the `Replace.NONE` + `MODE=PostgreSQL` combination and H2's PostgreSQL-mode quirks (see research section 7).
- Optional low-priority bonus: connect read-only to the SQL curriculum's `farm` DB (`sql-curriculum/db/schema.sql`) to show a different mapping of the same world.

## Stack (from research, verified 2026-11-06)

Parent `spring-boot-starter-parent` **4.1.2**, `<java.version>25</java.version>`. Compile: `spring-boot-starter-webmvc`, `-validation`, `-jdbc`, `org.postgresql:postgresql` (runtime). Test: `spring-boot-starter-webmvc-test`, `spring-boot-starter-jdbc-test`, `com.h2database:h2`. springdoc is not Boot-managed: `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0`. Key imports: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`, `...AutoConfigureMockMvc`, `org.springframework.test.context.bean.override.mockito.MockitoBean` (`@MockBean` is gone), `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest`, `org.springframework.jdbc.core.simple.JdbcClient`. `@SpringBootTest` no longer provides MockMvc (add `@AutoConfigureMockMvc`). Jackson 3: `tools.jackson.*`, annotations stay `com.fasterxml.jackson.annotation`.

## Build order (process)

1. **Done:** research pass.
2. **Done (this scaffold):** `DESIGN.md`, `README.md`, `curriculum/` chrome, `index.html`, `setup/index.html`.
3. Build the reference solution in a scratch dir from a copy of her domain (`old-marina-had-a-farm-main/src`, end of Java Phase 4). Every test, snippet and hint must compile and pass. Verify the open research items on the way (JdbcClient bean inside `@JdbcTest`, KeyHolder on H2 and Postgres, `ProblemDetail` API, `MockMvcTester` JSON helpers, Jackson 3 + records). Final reference goes into `mentor/`, labelled mentor-only.
4. Write the lesson HTML. **Draft Phase 1 first and show Thomas before writing the rest.** (HANDOFF says "Phase 6", a leftover from an earlier numbering; there is no Phase 6.)
5. Write the Spring-adapted `copilot-instructions.md`.

## Open / to confirm with Thomas

- Does `podman compose up` work on her Windows machine (confirm at Lesson 14).
- Bonus lesson on the SQL curriculum DB: include or drop.
- Final lesson count (target ~20).
- Lesson titles are not fixed yet; the index shows phases and focus only until the lessons are written.
