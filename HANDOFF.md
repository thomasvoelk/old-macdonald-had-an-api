# Handoff — Spring Web Backend Curriculum ("Old MacDonald Had an API")

Written at the end of a design interview (grilling session). All decisions below are **confirmed by Thomas**. Do not re-litigate them; build from them. Start a fresh session inside this folder.

## Goal
A Spring Boot REST backend tutorial for a first-year apprentice who has done Java + TDD (Old MacDonald curriculum) and SQL (Data Barn curriculum). It is a separate curriculum that follows the Java one (own numbering: **Lessons 01–~20, Phases 1–5**, like the SQL curriculum; refer to Java lessons as "Java Lesson N"), same style, same world (Farmer Fiona's alpaca farm). Company stack: Spring REST backend + Angular frontend. **No Angular in this curriculum** (next curriculum).

## Facts about the existing material (read these first)
- Java curriculum (Markdown + HTML mirror): `/Users/thomas/Development/Projekte/old-marina-had-a-farm-main/` — `curriculum/` (md) and `curriculum-html/` (HTML mirror of same 25 lessons). This folder is **the apprentice's own copy**; `src/` holds her code, `.github/copilot-instructions.md` her Socratic Copilot rules. Original is `tv-old-mac-donald-had-a-farm-new-and-better` (not on disk).
- Lesson format (see any `curriculum/phase-*/lesson-*/README.md`): Story, Your Task, Learning Goals, Getting Started, Test List (acceptance criteria hidden in `<details>`), 3 Hints (`<details>`), When You're Done, Reflect, Copilot prompt.
- SQL curriculum (hand-authored HTML, shared chrome, recoloured sibling): `/Users/thomas/Development/Projekte/sql-curriculum/` — read `DESIGN.md` and `curriculum/` (style.css, colors_and_type.css, app.js, assets) as the template for this repo.
- Company reference stack: `/Users/thomas/Development/Projekte/daily-fleece/backend/pom.xml` — Spring Boot 4.1.1, Java 25, `spring-boot-starter-webmvc`, `-webmvc-test`, `-validation`. (Leave out Modulith, observability, static analysis — too much.)
- Her domain (package `alpacas`, single Maven module, Java 25, JUnit 6): `Alpaca`, `Herd`, `Pen`, `Farm`, `Shepherd`, `Llama`, `FleeceColor`, `PenNotFoundException`, `FleeceColorNotFoundException`. Known awkward bits that the course turns into lessons: no ids; public fields; `findByName`/`heaviestAlpaca` return `null`; `System.out.println` inside `isHealthyWeight()`/`isReadyToShear()`; exceptions are `IllegalArgumentException`/`IllegalStateException`/`PenNotFoundException` (map to 400/409/404).
- Her progress: currently in Phase 4 (interfaces). Her domain will differ slightly from the repo copy by the time she starts.
- She uses a Windows machine with Podman Desktop; for the SQL course she ran SQLite locally. Company database is Postgres.

## Confirmed decisions
**Repo & format**
- New repo `spring-curriculum` (this folder). Hand-authored **HTML only**, no Markdown source. Reuse the shared chrome (style.css, colors_and_type.css, app.js — progress bar, TOC, hints, copy buttons, checkbox persistence).
- Visual identity: third sibling. **Hot pink** accent (bright pink for accents, a deeper pink for text/links so contrast on the cream background passes), same fonts/tokens, new **sprout/leaf mascot mark** keeping the four corner dots. Java = purple, SQL = denim blue.
- English. She copies the lessons into her existing repo (the one with her code).
- Ship a Spring-adapted `copilot-instructions.md` for her to merge into her existing one: keep Socratic rules; add "never write a controller, test, or SQL query for her"; explain the layering. Git prefixes (`feat:`, `test:`, `refactor:`, `fix:`) and phase-end demos carry over; one commit per green test.

**Architecture**
- Her existing code is the domain. Build the delivery mechanism around it (hexagonal): web code in `alpacas.web`, **single Maven module**, rule: **domain classes never import Spring**. Enforce with one plain-JUnit architecture test (no ArchUnit) in a lesson.
- **Separation of concerns (core principle, teach it explicitly):** domain logic is separate from the delivery mechanism. **Controllers never accept or return domain objects.** Every endpoint has its own request/response types (records, e.g. `AlpacaResponse`, `CreateAlpacaRequest`) in `alpacas.web`, with explicit mapping to and from the domain. Reason: the JSON contract and the business model must be able to change independently; exposing domain objects couples them (field renames break API clients, internals leak, e.g. public fields). Lesson 01's audit should surface this ("what happens if we return an `Alpaca` directly?"); the first DTO lesson introduces the mapping and explains why; later lessons keep the rule and a test/architecture check can assert that controller signatures contain no domain types. The same applies on the persistence side: the domain owns the repository interface, and SQL rows are mapped to domain objects inside the JDBC adapter. The domain may change, test-first, when the adapter pushes back (`findByName` → `Optional`, remove `System.out`, add ids) — still never Spring annotations.
- URLs use **names first** (`/pens/{penName}/alpacas/{name}`); an "identity" lesson adds ids right before the database phase.

**Teaching approach**
- TDD continues. Domain: plain JUnit. Controllers: `@WebMvcTest` + `MockMvc`. One `@SpringBootTest` smoke test per phase. Mocks (Mockito) appear **only** in the London lesson.
- Each lesson opens with a short **"Spring Corner"** box (a few paragraphs + link to official docs) because Spring concepts can't be discovered by trial and error; tasks and hints follow, test-first work remains the core.
- Chicago (inside-out) style throughout Phases 1–3; each phase intro states which style is used. One **explanation lesson on London vs. Chicago**, then one **London (outside-in) lesson**: `POST /pens/{pen}/alpacas/{name}/shear` — controller test first with a mocked `ShearingService`, discovering the service; touches `Shepherd` and `Farm`; her first Mockito use.
- **Side Quest** (optional): just mention the Clean Coders series "Comparative Design" (https://cleancoders.com/series/comparativeDesign — 14 × ~1h; first 5 episodes London with Sandro Mancuso; repo branches `openchat-outside-in` and `openchat-unclebob`). No episode assignments, off the critical path. (Page could not be fetched; details come from Thomas.)

**Entry point**
- Phase numbering: this course has its own Phases 1–5 (folders like `phase-1-hello-spring/lesson-01-...`).
- Starts after Java Lesson 19 (Java Phase 4 done). Java Lessons 21 (records) and 22 (`Optional`) are prerequisites for Spring Phase 2 — state this at the top of Spring Phase 2. Java Lessons 20, 23, 24 can run in parallel/afterwards. Spring Lesson 01 = audit of her own domain ("what's awkward about exposing this over HTTP?").

**Outline (own numbering starting at 01; lesson numbers approximate, ~20 lessons / 8–9 weeks)**
| Phase | Lessons | Focus |
|---|---|---|
| 1 – Hello Spring | 01–04 | domain audit, Spring Boot setup (pom → Boot 4.1.x, Java 25), Farm as a bean (DI), first `GET` with MockMvc |
| 2 – Reading the Farm | 05–08 | GET pens/alpacas/ready-to-shear, DTO records, `Optional` → 404, `@RestControllerAdvice` |
| 3 – Changing the Farm | 09–13 | POST/PUT/DELETE, validation, exceptions → 400/409, architecture test, London-vs-Chicago explanation + Side Quest, London shear lesson |
| 4 – The Barn Gets a Database | 14–17 | ids, repository interface (owned by domain), in-memory then `JdbcClient` implementation, run against Postgres |
| 5 – Wrap-up | 18–20 | Swagger UI / OpenAPI docs (springdoc), farm-API grand review, one-paragraph bridge to the Angular curriculum. **No CORS, no Angular.** |

**Persistence**
- **`JdbcClient` with plain SQL** (not JPA; JPA mentioned in one sentence at most). Repository interface owned by the domain; first an in-memory implementation, then JDBC.
- Her own small schema (`farm`, `pens`, `alpacas`) in a plain `schema.sql` run by Spring at startup — no Flyway.
- Running the app: **Postgres in Podman** (short `compose.yml`, setup page at start of Phase 4, Lesson 14; an older numbering said "Phase 9"; Thomas will confirm `podman compose up` works on her machine, fix when we get there). Tests: **embedded H2** (Spring's in-memory DB) via `@JdbcTest`, used silently — **no lesson about H2 vs Postgres differences, no Testcontainers** (too much; at most one sentence "teams do this later"). Note H2 PostgreSQL-compat mode quirks in `mentor/` notes only.
- Optional bonus lesson (low priority): connect read-only to the SQL curriculum's `farm` DB (`/Users/thomas/Development/Projekte/sql-curriculum/db/schema.sql`) to show a different mapping of the same world. Treat as nice-to-have.

## Process (do in this order)
1. **[DONE]** **Research pass** (use the `mattpocock-skills:research` skill or a sub-agent): verify against official docs the exact Spring Boot 4.1.x details — starters (`spring-boot-starter-webmvc`, `-webmvc-test`, validation), `@WebMvcTest` package/location, `@MockitoBean` (replaces `@MockBean`), `MockMvc`/`MockMvcTester`, `ProblemDetail`/`@RestControllerAdvice`, `JdbcClient`, `@JdbcTest` + embedded H2, springdoc compatibility with Boot 4. Do **not** write from memory; Boot 4 renamed/moved several things. Capture findings as a Markdown file in the repo.
2. **[DONE]** Scaffold the repo: `DESIGN.md` (decisions above, mentor-facing), `README.md`, `curriculum/` with `index.html`, shared chrome adapted from `sql-curriculum/curriculum/` (recoloured hot pink, new sprout mark), `setup/` page.
3. **Build the reference solution** in a scratch dir using a copy of her domain from `old-marina-had-a-farm-main/src` at end-of-Phase-4 state. Work through lessons in order; every test, snippet, and hint must compile and pass. Final reference goes into `mentor/` (mentor-only, labelled, not part of what she copies).
4. Write the lesson HTML pages per the Java lesson format + Spring Corner. Draft Phase 1 first (an older numbering said "Phase 6"; there is no Phase 6) and show Thomas before writing the rest.
5. Write the Spring-adapted `copilot-instructions.md`.

## Still open / to confirm with Thomas when it comes up
- Exact episode selection: none (Side Quest only mentions the series).
- Whether `podman compose up` works on her Windows machine.
- Bonus lesson on the SQL curriculum DB: include or drop.
- Final lesson count (target ~20; adjust if Phase boundaries feel off).

## Research results
Full, sourced findings: `research/spring-boot-4-findings.md` (read its "Could not verify" list before relying on details).
- Parent `spring-boot-starter-parent` **4.1.2** (latest stable 4.1.x; 4.2.0-RC2 exists, avoid), `<java.version>25</java.version>` (Boot 4.1.2 supports Java 17-27). Spring Framework 7.0.10, JUnit 6.0.3, Mockito 5.23.0, AssertJ, Jackson 3.1.7, H2 2.4.240, PostgreSQL JDBC 42.7.14 all Boot-managed.
- Dependencies: `spring-boot-starter-webmvc`, `-validation`, `-jdbc`, `org.postgresql:postgresql` (runtime); test: `spring-boot-starter-webmvc-test`, `spring-boot-starter-jdbc-test` (exists), `com.h2database:h2`. springdoc is NOT Boot-managed: `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0` (Boot 4.1.x <-> 3.1.x-3.2.x); Swagger UI `/swagger-ui.html`, JSON `/v3/api-docs`.
- Imports: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` / `AutoConfigureMockMvc`; `org.springframework.test.context.bean.override.mockito.MockitoBean` (`@MockBean` is removed in Boot 4); `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest`; `org.springframework.jdbc.core.simple.JdbcClient`; `org.springframework.test.web.servlet.assertj.MockMvcTester` (AssertJ, used in the official `@WebMvcTest` example; no official "prefer" statement).
- `@SpringBootTest` no longer provides MockMvc: add `@AutoConfigureMockMvc`.
- Problem details: own `@RestControllerAdvice` works without a property; `spring.mvc.problemdetails.enabled` defaults to false.
- Jackson 3: `tools.jackson.*`, but annotations stay `com.fasterxml.jackson.annotation`.
- SQL init: `schema.sql` runs automatically only for embedded DBs; for Postgres set `spring.sql.init.mode=always`. `@JdbcTest` replaces the DataSource with embedded H2 by default (H2 `MODE=PostgreSQL` is ignored unless `replace = NONE`).
- Still to verify while building the reference solution: JdbcClient bean inside `@JdbcTest`, KeyHolder generated-key pattern on H2 and Postgres, `ProblemDetail` API details.

## Status after the scaffold session (2026-11-06)
- Final name: **Old MacDonald Had an API** (a pun like the siblings; "Fiona's Farm API" was only the working title). Visual identity unchanged: hot pink, sprout mark.
- Repo: public, https://github.com/thomasvoelk/old-macdonald-had-an-api (personal account `thomasvoelk`, remote `origin`, branch `main`). Published via GitHub Pages from `curriculum/` by `.github/workflows/pages.yml`: https://thomasvoelk.github.io/old-macdonald-had-an-api/ (redeploys on every push to `main`).
- Steps 1 and 2 are done. `DESIGN.md` now holds the decisions and build order; this file keeps the original interview record.
- **Next: step 3**, the reference solution in a scratch dir, starting with the Phase 1 slice (Boot 4.1.2 pom, `Farm` bean, first MockMvc test). Close the open research items on the way (see the end of `research/spring-boot-4-findings.md`).
- Because the repo is public, **keep `mentor/` (reference solution) out of it** (separate private repo or `.gitignore`), unless Thomas decides otherwise. The lesson HTML's hidden hints are public regardless.
- Thomas's other repo `thomasvoelk/alpaka-farm` (public) is not part of this; decided against adding the curriculum as a branch.

## Status after the reference-solution session, Phase 1 slice (2026-11-06)
- **Step 3 is in progress.** Phase 1 slice of the reference solution is built and green (135 tests: her 133 + 2 new) in `mentor/reference/` (git-ignored; repo is public). Boot 4.1.2, `java.version` 25, built with local JDK 27 (Mockito self-attach warning is harmless).
- Contents: pom (parent 4.1.2, `spring-boot-starter-webmvc` + `-webmvc-test`), her domain copied unchanged from `old-marina-had-a-farm-main/src`, `alpacas.web` with `AlpacasApplication`, `FarmConfig` (`@Bean Farm` with sample pens/alpacas), `FarmResponse` record, `FarmController` (`GET /farm`), `FarmControllerTest` (`@WebMvcTest` + `@Import(FarmConfig.class)` + `MockMvcTester`), `AlpacasApplicationTests` (`@SpringBootTest` contextLoads).
- Facts verified (also appended to `research/spring-boot-4-findings.md`): `MockMvcTester` `.bodyJson().extractingPath("$.x").isEqualTo(..)` works; raw response via `.exchange().getResponse()`; Jackson 3 serialises plain records unannotated; an unrestricted `@WebMvcTest` loads all controllers, so a missing bean fails the whole context (hint for Lesson 04).
- **Lesson 01 audit evidence:** returning an `Alpaca` directly gives `{"name":"Dolly","weight":70.0,"fleeceLength":4,"fleeceColor":"WHITE","healthyWeight":true,"readyToShear":false}`; `is...()` become JSON fields and their `System.out.println` runs during serialisation.
- Still open to verify while building: `JdbcClient` bean inside `@JdbcTest`, KeyHolder on H2 and Postgres, `ProblemDetail` API, `MethodArgumentNotValidException` for `@Valid`.
- **Next:** extend the reference through Phase 2 (GET pens/alpacas/ready-to-shear, DTO records with mapping, `Optional` → 404, `@RestControllerAdvice`; needs `findByName` → `Optional` test-first), then draft Phase 1 lesson HTML (Lessons 01–04, Spring Corner boxes) and show Thomas before writing the rest. Nothing from this session is committed yet (`.gitignore`, research notes, HANDOFF).

## Status after the reference-solution session, Phase 2 slice (2026-11-06)
- Phase 2 slice of `mentor/reference/` is built and green (143 tests). Added: `Herd.findByName` -> `Optional` (test-first), `PenController` (`GET /pens`, `/pens/{penName}/alpacas`, `/pens/{penName}/alpacas/{alpacaName}`, `/alpacas/ready-to-shear`), records `PenSummaryResponse` and `AlpacaResponse` (with `from(Alpaca)` mapping, `fleeceLengthCm` renamed on purpose), `AlpacaNotFoundException` (web-level), `ApiExceptionHandler` (`@RestControllerAdvice`, `ProblemDetail` 404 for `PenNotFoundException` and `AlpacaNotFoundException`), `PenControllerTest`. Details in `research/spring-boot-4-findings.md`.
- Open note: the `/pens/{penName}` summary endpoint for a single pen is not built (lesson text can leave it as an exercise or skip it). Lessons 05-08 map roughly to: pens list, alpacas of a pen + DTO mapping, `Optional` -> 404, advice.
- **Next (one step):** draft Phase 1 lesson HTML (Lessons 01-04, Spring Corner boxes) and show Thomas before writing the rest. Phase 3 reference (POST/PUT/DELETE, validation) comes after.
- Working protocol (one step per session, record, commit, `/clear`) is in `CLAUDE.md`. That file is temporary: delete it when the curriculum is done (last wrap-up step).

## Status after the Phase 1 lesson draft (2026-11-06)
- Drafted the four Phase 1 lessons as HTML in `curriculum/phase-1-hello-spring/`: `lesson-01-is-the-farm-ready-for-the-web` (audit, no code, writes `docs/api-audit.md`, predicts Dolly's JSON), `lesson-02-planting-spring` (pom to Boot 4.1.2, `AlpacasApplication`, smoke test, run and see 404), `lesson-03-the-farm-becomes-a-bean` (`FarmConfig`, `@SpringBootTest` + `@Autowired Farm`), `lesson-04-the-first-request` (`GET /farm`, `@WebMvcTest` + `MockMvcTester`, `FarmResponse`, throwaway leak experiment, Phase 1 demo). Each has a Spring Corner box with official-docs links, hidden test list, 3 hints, Reflect, Copilot prompt, lesson nav.
- Index Phase 1 row now lists the four lessons (status pill "Draft"); setup page links Lesson 01.
- Every snippet and test in the lessons matches `mentor/reference/` (new `FarmConfigTest` added there and green).
- **Waiting for Thomas's review of the Phase 1 draft.** Do not write later phases before he has seen it. Points worth his eye: Lesson 01 is a pure paper audit (no Spring yet); the leak experiment lives in Lesson 04 (not committed by the learner); the learner's own `pom.xml` cleanup is described generically since her pom isn't on disk.
- **Next:** incorporate Thomas's feedback on the Phase 1 draft, then extend the reference solution through Phase 3 (POST/PUT/DELETE, validation, exceptions to 400/409), then draft Phase 2 lessons (05–08).

## Status after Thomas's feedback on the Phase 1 draft (2026-11-06)
- **Decision (Thomas):** no paper audit as Lesson 01. Didactic order is *experience the leak first, introduce the separation later*. The earlier "Lesson 01 = audit" decision is superseded (DESIGN.md updated).
- **New Phase 1** in `curriculum/phase-1-hello-spring/`: `lesson-01-planting-spring` (pom, `AlpacasApplication`, smoke test), `lesson-02-the-farm-becomes-a-bean` (`FarmConfig`), `lesson-03-the-first-request` (`GET /farm` returns the `Farm` itself; she predicts the JSON, reads the real nested output and the console `println`s, writes `docs/api-notes.md`), `lesson-04-keeping-the-model-private` (rename-coupling experiment with `getPensCount`, then new contract `{name, pens, alpacas}` via `FarmResponse` and mapping, separation-of-concerns Spring Corner, Phase 1 demo). Old audit lesson deleted; index, setup page and lesson navs updated.
- Verified in `mentor/reference/` with a throwaway leak controller (removed again): returning `Farm` yields `{"name":..., "pens":[{"name":"Meadow","herd":{"alpacaCount":2,"alpacas":[...],"alpacasReadyToShear":[...],"capacity":30}},...],"pensCount":2}`, the `println`s run during serialisation, and `$.name`, `$.pensCount`, `$.pens.length()` assertions pass. A `@RestController` nested in the test class is NOT picked up by `@WebMvcTest` (404); use a top-level class. The reference holds only the final (Lesson 04) state.
- Not yet run by hand: the Lesson 04 rename experiment in IntelliJ (reasoning: `countPens()` is not a getter, so `pensCount` vanishes from the leaky JSON and the `$.pensCount` test fails).
- Follow-up (same day, Thomas): Lesson 03 now opens with a short **"HTTP in two minutes"** section (request = method + path, response = status + body, headers in one sentence, real `curl -v` output, table mapping HTTP parts to `MockMvcTester` calls, note that older tutorials say "content" for body); its Spring Corner lost the HTTP paragraph. Lesson 01 now asks for `curl -i` so the recap points back to something she has seen. The `curl -v /farm` output in the lesson was captured from the running reference.
- **Next:** Thomas reviews the revised Phase 1 draft; then extend the reference solution through Phase 3 and draft Phase 2 lessons (05–08). Phase 2's first DTO text must not repeat the principle from Lesson 04, only apply it.

## Status after Thomas's second review of Phase 1 (2026-11-06, evening)
Review session on Lessons 01–04. **The Phase 3 reference (POST/PUT/DELETE, validation, 400/409) was NOT started**; Thomas spent the session on the lesson text. Decisions (all by Thomas, supersede earlier notes where they conflict):

**Story and domain**
- Fiona is the **customer**, the apprentice is the contractor. Fiona wants her farm on laptop, iPad and phone; "a frontend developer on your team" builds the screens (replaces the neighbour's son / phone app). Lessons 01, 03, 04 reworded.
- Sample data uses **wrestler names from her own repo**: Lita (70 kg, 4 cm, white), Batista (80 kg, 10 cm, brown); pens **Ring** (both alpacas) and **Cell** (empty). One-word names on purpose (no `%20` in URLs yet). Farm stays "Fiona's Farm". A later lesson may use a two-word wrestler as a deliberate URL-encoding case. Old names (Dolly, Pedro, Meadow, Hilltop) remain only in older HANDOFF/research entries.
- "Domain" is now explained in plain words at first use (Lesson 01, step 6): what the application is about (farm, animals, attributes, rules), "would make sense without web or database", and a rule-change example (a new veterinary study lowers the healthy-weight limit). No German terms. Also glossed in Lessons 03 and 04. "Field" now carries "also called attribute, German Attribut" (Lessons 02, 03).

**Architecture (supersedes "domain never imports Spring")**
- Spring annotations in domain classes are **allowed**; the Phase 3 "domain has no Spring imports" architecture test is dropped. Separation is by place: domain in `alpacas`, web in `alpacas.web`, later SQL in `alpacas.sql`. **`AlpacasApplication` lives in the top package `alpacas`** (option A: her domain stays where it is). No formal layers/hexagonal in this course; Thomas may introduce that later. Still holds: controllers never accept or return domain objects. The Phase 3 architecture test, if kept, checks controller signatures only (not designed yet).
- Reference: `AlpacasApplication` and its smoke test moved to package `alpacas`; **145 tests green**. `FarmConfig` stays in `alpacas.web`.
- Lesson 02 Reflect experiment: moving `FarmConfig` into a package **outside `alpacas`** (e.g. `config`) fails with `NoSuchBeanDefinitionException ... 'alpacas.Farm'` (verified on a copy); moving it within `alpacas` passes.

**Lesson format (new rule: explain where it is used)**
- The box at the top of each lesson carries the **idea only** and is short. How-to explanations move into the Getting Started step that uses them, so she never scrolls back. Lessons 01–04 restructured this way (Lesson 02 was the pilot). Lesson 04's principle (contract vs. model, separation of concerns) comes **after** the rename experiment ("What you just felt"), keeping experience first.
- Box names: **Spring Sprout** 🌱 (pink; was "Spring Corner") and **Barn Briefing** 🚜 (teal; background that is not Spring, first use: HTTP in Lesson 03). CSS classes unchanged (`.spring-corner`, `.concept-corner`). **Llama Drama** is reserved for a future common-mistakes box (warm colour). Recorded in DESIGN.md.
- Lesson 03: HTTP section is now a Barn Briefing with a request/response diagram (client: browser, phone, curl, your test). `MockMvcTester` is explained as a "fake browser" (no display, no real server). The `@WebMvcTest` slice explanation was cut to "annotate your test with this, because ...".
- Lesson 01: no test list ("No Test List Today") but a visible checklist; the smoke test is explained in step 5, not in a hint. Lesson 02: `@Autowired` explained via arrange/act/assert (her Java Lesson 03); singleton, dependency injection and "application context" dropped from Lesson 02 (DI moved to Lesson 03, tied to constructors she knows).
- Diagrams are inline SVG using the course tokens (Lesson 02 and 03 bean pictures, Lesson 03 HTTP picture).
- **Chicago/London names are not used in Phases 1–2** (index and Lesson 03 cleaned). They get introduced in the Phase 3 explanation lesson right before the London lesson; that lesson should start from "you already worked inside-out" (maybe an excursion page, Thomas's idea). DESIGN.md updated.

**Still open / to do**
- Phase 3 must bring back the note that the shared farm bean is shared between requests (dropped from Lesson 02 together with the word "singleton").
- Thomas has not finished reviewing Lessons 03 and 04 in detail; not yet checked on a phone-width screen (long list items in Getting Started, small diagram text).
- Small known nit: the "Copy" button on the `curl -v` block in Lesson 03 overlaps the first annotation; fix by removing/moving the right-hand annotations.
- Possible tidy-up: the Spring Sprout in Lesson 02 is shorter now; check the Lesson 04 sprout is not too thin.

**Next:** extend `mentor/reference/` through Phase 3 (POST/PUT/DELETE, validation, exceptions to 400/409; open items: `@Valid` exception type, request records with mapping), then draft Phase 2 lessons (05–08) in the new format (idea box + explanations in the steps; wrestler names; no Chicago/London labels). Finish Thomas's review of Lessons 03–04 first if he has more notes.

## Follow-up: end of Thomas's Phase 1 review (2026-11-06, late)
- **Lesson 04** has **no Spring Sprout** any more (its idea is design, not Spring). "What you just felt" is a **Barn Briefing** inside step 1, after the rename experiment; the doc link moved to the record step. DESIGN.md: a lesson opens with a Spring Sprout only when it teaches a Spring idea.
- **Rename story fixed:** Fiona (customer) would not ask for a method rename. Now **Hasi** (reviewer) suggests `getPensCount()` -> `countPens()`; the learner tries it as an experiment without committing, fixes the contract, tries it again (harmless this time), and undoes it so her code keeps matching later lessons.
- **Cast** (in DESIGN.md): Fiona = customer; **Ilona** (she/her, real apprentice one year ahead) = frontend developer (Lessons 01, 04); **Hasi** (he/him) = code reviewer (Lesson 04). Real first names in a public repo/Pages site: Thomas to check with them before pushing, or swap the names. Later lessons can reuse them (Ilona asks for endpoints, Hasi reviews).
- Thomas's review of Phase 1 is finished unless he sends more notes.

**Next:** unchanged from the section above: extend `mentor/reference/` through Phase 3 (POST/PUT/DELETE, validation, exceptions to 400/409), then draft Phase 2 lessons (05–08) in the new format (short idea box only where Spring teaches something new, explanations inside the steps, wrestler names, Ilona/Hasi, no Chicago/London labels).

## Status after the Phase 3 reference and Phase 2 lesson draft (2026-11-06)
**Reference (`mentor/reference/`, 164 tests green, git-ignored):**
- Domain, test-first: `Herd.removeByName` (boolean), `Herd.replace(Alpaca)` (boolean, swaps by name), `Farm.addPen` rejects duplicate names (`IllegalStateException`). A name-uniqueness rule inside `Herd.addAlpaca` was tried and dropped: it breaks 5 of her existing tests that reuse names.
- Web: `PenController` gained `POST /pens` (201 + Location), `POST /pens/{pen}/alpacas` (201 + Location, 409 on duplicate name), `PUT /pens/{pen}/alpacas/{name}` (full replace, 404 if missing), `DELETE .../{name}` (204). Records `CreatePenRequest`, `CreateAlpacaRequest`, `UpdateAlpacaRequest` (bean validation, boxed types, fleece colour as String parsed by `FleeceColors.parse`), `AlpacaAlreadyExistsException`. `ApiExceptionHandler` maps `IllegalArgumentException`/`FleeceColorNotFoundException` -> 400, `IllegalStateException`/`AlpacaAlreadyExistsException` -> 409, `MethodArgumentNotValidException` -> 400 with `errors` map, `HttpMessageNotReadableException` -> 400. Tests in `PenChangesTest` (`@DirtiesContext` after each method).
- Facts in `research/spring-boot-4-findings.md` (Location header with a space throws, DirtiesContext, problem JSON shape, etc.).
- Not built: architecture test for controller signatures (still undesigned), the London shear lesson reference, PATCH, `GET /pens/{penName}`.

**Phase 2 lessons drafted** in `curriculum/phase-2-reading-the-farm/`: 05 The Pens (`GET /pens`, list of records), 06 The Alpacas of a Pen (`@PathVariable`, `AlpacaResponse.from`, `/alpacas/ready-to-shear`, `fleeceLengthCm` rename, unknown pen shows 500 on purpose), 07 Who Is That Alpaca? (`findByName` -> `Optional` test-first, `AlpacaNotFoundException`, local `@ExceptionHandler`+`@ResponseStatus`; unknown-pen test left red), 08 One Place for Bad News (`@RestControllerAdvice`, `ProblemDetail`, Phase 2 demo). Spring Sprout boxes are one paragraph each (lists, path variables, exceptions, central error handling); explanations sit in the steps. Cast: Ilona asks, Hasi reviews (Lesson 08), wrestler names. Chicago/London unused. Index Phase 2 rows link the four lessons; Lesson 04 nav points to Lesson 05. The 500/404/problem+json behaviours and the local-handler path were verified against the reference (scratch copy for the local handler).
- Generated with a throwaway script; the HTML is the source from now on. Not yet checked on phone width; not yet reviewed by Thomas.

**Open:** Thomas reviews Lessons 05–08 (do not write Phase 3 lessons before that). Phase 3 needs: reminder that the farm bean is shared between requests (and not thread-safe), `@DirtiesContext` explanation, a two-word name (`Triple H`) as URL-encoding case, 400 vs 409 vs 404 table, optional controller-signature architecture test.

**Next:** incorporate Thomas's feedback on the Phase 2 draft (Lessons 05–08); then draft the Phase 3 lessons (09–13) against the reference, starting with POST/PUT/DELETE and validation.

## Status after Thomas's review of Phase 2 lessons 05–08 (2026-11-06)
Review finished; feedback incorporated. **Phase 3 lessons were NOT started** (Thomas chose a fresh session for it).

**Changes (all by Thomas's feedback)**
- **Lesson 05:** the Spring Sprout was dropped (its content was a repeat of Lesson 03 plus Jackson behaviour, not a Spring idea). Replaced by a **Barn Briefing "how Jackson writes JSON"**: a staircase of tiny examples, one block each (empty record `{}`, text, number, list, enum, null, nested record, then a normal class via getters). The record is shown fully only in steps 1–2; afterwards just the field and the JSON member, written with `var` (`var kg = 70.0`, because `70` would give `70`; the `null` case keeps `String nickname = null`). All outputs verified by a probe test in a scratch copy of the reference (Jackson 3: records and getters/`is...()` become members, `countPens()` and private fields do not, `null` is written as `null`, enum as its name, `Optional.empty()` also as `null`, empty list `[]`).
- **Lesson 03 / 06:** "path" had never been defined. Lesson 03 now says: the part of the address after host and port. Lesson 06's sprout defines it again, explains that the braces exist **only in the controller pattern**, and shows pattern vs. real request path in one block; step 3 explains `@PathVariable` where it is used.
- **Lesson 07:** new **Barn Briefing "when nothing is found"** before the Spring Sprout: method signature with `Alpaca` (not obvious that it can be `null`, nothing makes you check) vs. `Optional<Alpaca>` (the box holds an alpaca or is empty; you must take it out, e.g. `orElseThrow`, and so decide the empty case), plus the rule of thumb **many results -> `List` (empty when nothing matches), one maybe-missing result -> `Optional`, never `null`**. Code comments sit above their lines (no sideways scrolling).
- **Lesson 08:** `ProblemDetail` is described as a class whose objects describe an error; "fields" became "JSON members".
- **`curriculum/style.css`:** Spring Sprout / Barn Briefing headings had a large top gap (specificity: `article.markdown h2` margin beat the box rule). Fixed with more specific selectors.
- Confirmed in her original code: `Herd.findByName` and `heaviestAlpaca()` return `null`. `heaviestAlpaca()` is not changed (no lesson uses it yet); it could be a later exercise.
- Rule recorded for later lessons: a Spring Sprout only when the lesson teaches a Spring idea; a Barn Briefing for Jackson/HTTP/`Optional`-style background. Code comments go above the line, not at the end. Prefer one small example per block and show a record only once, then just the field.

**Still open**
- Not checked on phone width (Lesson 05 `→` staircase, Lesson 06 pattern block, Lesson 03 `curl -v` block with the Copy-button overlap).
- Phase 3 needs (unchanged): reminder that the farm bean is shared between requests (and not thread-safe), `@DirtiesContext` explanation, `Triple H` as the URL-encoding case (`Location` header must be encoded), 400 vs 409 vs 404 table, request-body direction of Jackson (JSON -> record; plant is in the Lesson 05 briefing), optional controller-signature architecture test, London/Chicago explanation lesson plus the shear lesson (not built in the reference).

**Next:** draft the Phase 3 lessons (09–13) against `mentor/reference/`, starting with POST/PUT/DELETE and validation. Show Thomas the first Phase 3 lessons before writing further ones.

## Status after the first Phase 3 lesson draft (2026-11-06)
Drafted Lessons 09–11 in `curriculum/phase-3-changing-the-farm/` against `mentor/reference/` (164 tests green; nothing new needed in the reference). **Waiting for Thomas's review; do not write Lessons 12–13 before that.**

- **09 A New Pen:** `POST /pens`. Barn Briefing "four verbs and their answers" (method table, status table 201/204/400/404/409). Spring Sprout: `@PostMapping`, `@RequestBody`, `ResponseEntity`. Steps: red test in new `PenChangesTest`, `CreatePenRequest`, 201 + `Location`; **shared farm experiment** (two tests that both create "Stable": second one fails with count 4; verified in a scratch copy) -> `@DirtiesContext(AFTER_EACH_TEST_METHOD)` (also says: the running app shares one farm between clients); duplicate pen: `FarmTest` first (domain, `IllegalStateException`), then `ApiExceptionHandler` maps it to 409. IntelliJ `docs/requests.http` instead of `curl` (Windows quoting).
- **10 A New Alpaca:** `POST /pens/{pen}/alpacas`. Barn Briefing "spaces in addresses" (`%20`, `URI.create` throws on a space, `UriComponentsBuilder...encode()`); the learner first builds `Location` naively and sees it fail on "Triple H". `CreateAlpacaRequest` + `toAlpaca()`, `FleeceColors.parse` helper. Two tests that are green at once (unknown pen 404 from Lesson 08, full pen 409 from Lesson 09). Duplicate alpaca name checked in the controller (`AlpacaAlreadyExistsException`); Reflect lets her try the rule in `Herd` (breaks 5 of her tests). Experiment (no fix): fleece 11 -> 500, PURPLE -> 500, weight -5 -> 201 (all three observed on a copy of the reference without validation).
- **11 Say No Politely:** Barn Briefing "400, 404 or 409?" (question: would the same request work on another day?). Spring Sprout: bean validation. Add `spring-boot-starter-validation`; annotations on request records; boxed `Double`/`Integer` + `@NotNull` (a missing `int` becomes 0); `@Valid`; handler for `MethodArgumentNotValidException` with `errors` map via `setProperty`; handlers for `IllegalArgumentException`/`FleeceColorNotFoundException` (400) and `HttpMessageNotReadableException`; blank pen name. Real outputs checked against the reference (error map order differs, lesson says so).
- Index Phase 3 rows 09–11 link the lessons (Draft); 12–13 stay planned. Lesson 08's "next" link points to Lesson 09; Lesson 11's next link is a placeholder.
- Doc URLs checked (HTTP 200): Boot validation, Framework bean validation, `@RequestBody`, `ResponseEntity`, `UriComponentsBuilder`, MDN status and percent-encoding.

**Open / proposal for Thomas**
- Phase 3 now needs: 12 *Change and Remove* (`PUT` full replace with `UpdateAlpacaRequest` without name, `DELETE` 204, 404s; reference exists), 13 London vs. Chicago + shear lesson (reference NOT built; first Mockito use). That drops the optional controller-signature architecture test; either a short part of Lesson 12, or cut. Also the two-lesson London plan (explanation + shear) probably does not fit into one lesson: decide whether Phase 3 grows to 09–14 (Phases 4/5 shift by one) or the explanation becomes a Barn Briefing page.
- Not checked on phone width; Lesson 09's status tables and the output block in step 6 are the likely trouble spots.
- Real first names Ilona/Hasi still in public text (see DESIGN.md).

**Next:** incorporate Thomas's feedback on Lessons 09–11; then draft Lesson 12 (`PUT`/`DELETE`) against the reference and decide how to place the London lesson (build its reference first).

## Correction (2026-11-06, Thomas): no `@DirtiesContext` in the course
- Thomas rejected `@DirtiesContext` (recreates the whole Spring context per test, slow, bad habit). Lessons 09 and 10 now teach the sharing problem (two tests that both create "Stable": the second fails) and the cheap fix: **a test that changes the farm creates and uses its own pen; Ring and Cell stay untouched; the context stays shared and cached.** `@DirtiesContext` is NOT mentioned in any lesson (Thomas: do not name it, it only confuses); only the own-pen habit is taught.
- Reference `PenChangesTest` rewritten that way (pens Nursery, Crowd, Workshop, Auction; helper `createPen`), no `@DirtiesContext` anywhere; 164 tests green. Rule for Lesson 12 (PUT/DELETE): create the alpaca in the test's own pen first; never replace or delete Lita/Batista.
- Fragility to remember: other test classes share the cached context, so tests that read the whole farm (`/pens`, `/alpacas/ready-to-shear`) must not assert totals; today they only check indices 0 and 1.
- Phase 4: the database brings `@Transactional` rollback in `@JdbcTest`-style tests, the proper answer to shared state.

## Status after Thomas's review of Lessons 09–11 (2026-11-06)
- **Content of Lessons 09–11: no changes.** Thomas read them; "nothing to nitpick".
- **Readability (the only feedback), fixed in `curriculum/style.css`** (block at the end): `article.markdown` font size 1.0625rem and `line-height: 1.75` (the browser default of about 1.2 was too tight, especially around the pink inline `code`); list items get vertical margin; inline `code` is 0.92em with `line-height: 1` and `box-decoration-break: clone`; code blocks 1.55, tables 1.55, headings 1.3; 1em between paragraphs. Thomas confirmed it "looks much better".
- **Tried and rejected: a line-length cap** (70ch on text, then 46rem on the whole column). Per-element caps left boxes wider than the text; a column cap was unnecessary because Thomas has no problem with the ~95-character lines. Not in the CSS. Do not re-propose.
- **Lesson 12 was not started** (new session per protocol). Nothing was changed in `mentor/reference/`. Plan for Lesson 12 from reading the reference (`PenController.replaceAlpaca`/`deleteAlpaca`, `UpdateAlpacaRequest` without name, `Herd.replace`/`removeByName`, tests `replacesAnAlpaca`, `replacingAnUnknownAlpacaIsNotFound`, `deletesAnAlpaca`, `deletingAnUnknownAlpacaIsNotFound` in `PenChangesTest`): domain first (`Herd.removeByName`/`replace` test-first, boolean results), then PUT (name from the path, not the body; 404 if missing, no upsert), then DELETE (`204`, `@ResponseStatus`); every test uses its own pen. Before writing the lesson, verify in a scratch copy: a `name` in the PUT body that differs from the path (is it ignored under Jackson 3?), PUT with a lowercase path name (the replacement takes the path's spelling), PUT with a missing field (400), PUT/DELETE on an unknown pen (404), body of the `204`, second `DELETE` (404).
- **Proposal for the London lesson, to confirm with Thomas:** Lesson 12 stays PUT/DELETE only (drop the controller-signature architecture test). Phase 3 grows to **09–14**: Lesson 13 = London vs. Chicago explanation (short, "you already worked inside-out", Side Quest mention, no code); Lesson 14 = London shear lesson (`POST /pens/{pen}/alpacas/{name}/shear`, controller test first with a mocked `ShearingService`, first Mockito use). Phases 4 and 5 shift by one. The shear reference must be built in `mentor/reference/` before Lesson 13/14 are drafted. Thomas has not decided yet.

**Next:** draft Lesson 12 (`PUT`/`DELETE`) against `mentor/reference/` (verify the open behaviours above first), and decide with Thomas where the London lesson goes (proposal above).

## Status after drafting Lesson 12 and placing the London lesson (2026-11-06)
- **Lesson 12 "Change and Remove" drafted** in `curriculum/phase-3-changing-the-farm/lesson-12-change-and-remove/`: Barn Briefing (replace and remove: `PUT` = complete new version, name from the address not the body, no upsert -> 404, `DELETE` -> 204), Spring Sprout (`@PutMapping`, `@DeleteMapping`, `@ResponseStatus`), steps: domain first (`Herd.removeByName`/`replace`, four `HerdTest` tests), red `PUT` test in own pen `Workshop`, `UpdateAlpacaRequest` without name + `toAlpaca(String)`, controller, "three tests you do not have to make green" (unknown alpaca, unknown pen, missing field), `DELETE` in own pen `Auction`, experiments. The architecture test is dropped. Lesson 11's next link and the index row now point to it. Status: Draft, not yet reviewed by Thomas.
- Verified in a scratch copy of `mentor/reference/` (probe test, nothing changed in the reference): `name` in the PUT body is ignored (the path's name wins); lowercase path `triple h` -> 200 and the alpaca is then called `triple h` (a wart, left as a Reflect question); missing field -> 400 with `errors.fleeceLengthCm`; unknown pen and unknown alpaca on PUT/DELETE -> 404 problem+json; DELETE -> 204, empty body, no content type; second DELETE -> 404 `No alpaca named Triple H in pen Probe`; PUT after DELETE -> 404.
- **Decision (Thomas): Phase 3 grows to Lessons 09-14.** 13 = "Two ways to start: London vs. Chicago" (short, no code, "you already worked inside-out", Side Quest mention); 14 = shear lesson (`POST /pens/{pen}/alpacas/{name}/shear`, controller test first with a mocked `ShearingService`, first Mockito). Phase 4 is now Lessons 15-18, Phase 5 Lessons 19-21. `index.html` and `DESIGN.md` updated (the setup page and other lessons mention no Phase 4/5 numbers).
- Open: the shear reference is NOT built in `mentor/reference/`. Not checked on phone width. Thomas has not reviewed Lesson 12.

**Next:** build the shear (London) reference in `mentor/reference/` test-first (`ShearingService`, controller test with `@MockitoBean`, touches `Shepherd` and `Farm`), and note facts in `research/spring-boot-4-findings.md`; Lesson 12 review feedback first if Thomas has any.

## Status after building the shear (London) reference (2026-11-06)
- Thomas had no Lesson 12 review feedback at session start; nothing changed in the lessons.
- Built in `mentor/reference/` test-first (173 tests green): `ShearingControllerTest` (`@WebMvcTest(ShearingController.class)`, `@MockitoBean ShearingService`; 200 + `fleeceLengthCm` 0, `verify`, unknown pen/alpaca -> 404), `ShearingController` (`POST /pens/{penName}/alpacas/{alpacaName}/shear`, 200 with `AlpacaResponse`), `ShearingServiceTest` (plain JUnit: fleece 0, log entry, 404 cases, nothing logged on failure), `ShearingService(Farm)` with a `Shepherd` per pen ("Paul Heyman") and `logOf(penName)`, and `ShearingWithRealServiceTest` (mock replaced by the real service via `@Import`). Domain untouched. Details and observed red states in `research/spring-boot-4-findings.md`.
- Choices for Lesson 14: separate `ShearingController` (so existing `PenController` tests need no mock); service discovered from the controller test (compile error `cannot find symbol` is the first red); a space in a name needs the URI-template form in `MockMvcTester`.
- Not verified: Mockito `argLine`/self-attach warnings were ignored as before. Lesson 13 (London vs. Chicago, no code) and Lesson 14 are not drafted. Index rows for 13-14 are still "planned".

**Next:** draft Lesson 13 (London vs. Chicago explanation, no code, Side Quest mention) and Lesson 14 (shear, outside-in) in `curriculum/phase-3-changing-the-farm/` against this reference; show Thomas both before moving on.

## Status after drafting Lessons 13 and 14 (2026-11-06)
Drafted in `curriculum/phase-3-changing-the-farm/` against `mentor/reference/` (173 tests green, re-run at session start; nothing changed in the reference). **Waiting for Thomas's review; Phase 4 not started.**

- **13 Two Ways to Start** (no code): Story (Fiona wants a *Shear* button; Hasi asks whether "domain first" was a decision), Barn Briefing "Chicago and London" (inside-out vs. outside-in, comparison table, strength/risk of each, "many teams mix them"; names explained as labels, Lessons 09-12 named as inside-out), Your Task = read own git history (first red test domain or edge?), plan the shear feature both ways on paper and guess which class finds the alpaca and lets a shepherd shear it (kept for comparison after Lesson 14). Side Quest section (Clean Coders Comparative Design, first five episodes London with Sandro Mancuso, no assignments). "No Test List Today", no hints. Reflect questions on state vs. interaction tests.
- **14 Shearing from the Outside In:** two Barn Briefings ("an action on a thing": `POST .../shear`, no body, 200; "a stand-in for a class that does not exist yet": mock = `given/willReturn/willThrow/verify`, what it proves and does not), Spring Sprout (`@Service`, `@WebMvcTest` does not create services, `@MockitoBean`). Steps follow the reference: red controller test with `@MockitoBean ShearingService` -> compiler discovers `ShearingController`/`ShearingService.shear(String,String)` -> 404 red -> controller green -> experiment: remove `@MockitoBean` (`UnsatisfiedDependencyException ... 'shearingController'`) -> two `willThrow` tests green at once (and the warning that the service was not tested) -> service test in plain JUnit (Shepherd needs a Pen, so one shepherd per pen in a `Map`, lower-cased key, `logOf`) -> `ShearingWithRealServiceTest` with `@Import({FarmConfig.class, ShearingService.class})`, own pen Barbershop, URI-template form for "Triple H" -> run against the app. Hints use reference code verbatim. Reflect includes rename-breaks-mock-tests, the `HashMap` in a shared service (thread safety), hard-coded "Paul Heyman".
- Index rows 13-14 link the lessons (Draft); Phase 3 blurb no longer mentions an architecture test; Phase 4 row corrected from "14-17" to "15-18" (stale after the renumbering). Lesson 12's next link points to Lesson 13; Lesson 14's next link is a placeholder for Lesson 15.
- Not checked on phone width (Lesson 13 comparison table, Lesson 14 long step 6 with nested list). Doc URLs checked (HTTP 200): `@MockitoBean`, classpath scanning, `BDDMockito` javadoc, Clean Coders series. Not used from the link check: Boot testing page, MDN POST.
- Not verified: the claim that the controller test's first red state is `Status = 404` after the IDE creates the empty classes (taken from the observed red states in the findings); the lesson asks the learner to throw `UnsupportedOperationException` in the stub, which does not change that.
- Ilona/Hasi names still in public text (see DESIGN.md).

**Next:** incorporate Thomas's feedback on Lessons 13-14; then start Phase 4 (Lessons 15-18: ids, repository interface owned by the domain, in-memory implementation, `JdbcClient` against Postgres), beginning with the reference solution for Lesson 15 (ids).

## Status after the Lesson 15 reference (2026-11-08)
- Thomas had no feedback on Lessons 13-14 at session start; nothing changed in them. **Phase 4 started.**
- Built in `mentor/reference/` test-first (186 tests green): ids on `Alpaca`/`Pen` (`getId`, `assignId` once), `PenRepository` interface owned by the domain (aggregate: `save`, `findByName`, `findAll`), `InMemoryPenRepository`, abstract `PenRepositoryContract` + `InMemoryPenRepositoryTest`. Details, the red states and a trap in `research/spring-boot-4-findings.md`.
- Design choices (mine, tell Thomas if he disagrees): one aggregate repository `PenRepository` (no `AlpacaRepository`); set-once `assignId` instead of copy constructors (matches her mutable style); contract test reused later by the JDBC adapter.
- Not yet: wiring the repository into controllers/`FarmConfig` (replacing the `Farm` bean) and exposing ids in `AlpacaResponse`; both belong to Lesson 16 reference. Lesson 15 HTML not drafted. Setup page for Postgres/Podman is still due at Lesson 15.

**Next:** build the Lesson 16 reference: replace the `Farm` bean with `PenRepository` in controllers/services (test-first, existing web tests stay green), expose `id` where useful; then draft Lessons 15-16.

## Status after the Lesson 16 reference and the drafts of Lessons 15-16 (2026-11-08)
**Reference (`mentor/reference/`, 189 tests green, git-ignored):** `Farm` bean replaced by `PenRepository` bean in `FarmConfig`; `FarmController`, `PenController`, `ShearingService` use the repository; `AlpacaResponse` and `PenSummaryResponse` expose `id` (first member); `PenRepository.getByName` default method (contract-tested); `PUT` keeps the id; every change ends with `save`. Test-first: new tests for ids in JSON, `getByName`, PUT keeps id, `FarmConfigTest` for the repository bean; existing web tests unchanged except those that named `Farm` (`FarmConfigTest`, `ShearingServiceTest`, `ShearingWithRealServiceTest`). The keeps-id test was seen red without the fix. Details in `research/spring-boot-4-findings.md`.
- Choices to tell Thomas: farm name is a constant in `FarmController` (Reflect asks for `application.properties`); `Farm` and `FarmTest` stay but are unused by the web layer (Reflect asks whether to delete); `getByName` as an interface default method.

**Lessons drafted** in `curriculum/phase-4-the-barn-gets-a-database/`:
- **15 Everyone Gets a Number** (domain only, no Spring Sprout): Barn Briefings "a name is not an identity" (why `Long`, null until stored) and "a shelf the domain owns" (repository interface, no `AlpacaRepository`, aggregate in one sentence, contract test = abstract test class + subclass). Steps: `IdentityTest` red-first, own test list, `PenRepositoryContract` (field, not per-call; the trap), `InMemoryPenRepositoryTest`, "Look" step (change after save without saving: in-memory shows it, a database will not; answer goes into `docs/api-notes.md`). Hints use reference code verbatim (interface, `AtomicLong`).
- **16 Swapping the Shelf**: Spring Sprout "ask for the interface" (type-based injection, `@Bean` return type). Steps: ids in JSON red (null because Farm gives none), `FarmConfigTest` red (`No qualifying bean ... PenRepository`), register repository -> avalanche of `No qualifying bean ... Farm` as the to-do list, `FarmController`, `getByName` default method test-first, reading endpoints, writing endpoints ("every change ends with save"; PUT keeps id test-first), `ShearingService` (no mock needed), last checks with the running app (ids 1-4). Index rows 15, 16 link the lessons (Draft); rows 17-18 stay planned; Lesson 14's next link points to Lesson 15; Lesson 16's next is a placeholder.
- Links checked (HTTP 200): Spring DI, `@Bean`, Fowler aggregate and repository, `AtomicLong`. Checked the HTML tags balance; not viewed in a browser, not checked on phone width.

**Open**
- **Postgres/Podman setup page is still due** (DESIGN.md says Lesson 15; Lesson 15 turned out to be domain-only). Proposal: put it at the start of Lesson 17 or 18 where the database first appears; Thomas to decide. Also still unconfirmed: does `podman compose up` work on her Windows machine.
- Thomas has not reviewed Lessons 15-16. Ilona/Hasi names still in public text.
- Lesson 17 reference not built: JDBC adapter `JdbcPenRepository` against the same `PenRepositoryContract` (H2 via `@JdbcTest`); needs upsert by id, deleting missing alpacas on save, fresh objects (the "changes only stored on save" gap).

**Next:** incorporate Thomas's feedback on Lessons 15-16 and decide where the Postgres/Podman setup goes; then build the Lesson 17 reference (`JdbcPenRepository` with `JdbcClient`, `schema.sql`, `@JdbcTest` extending `PenRepositoryContract`) and draft Lessons 17-18.

## Status after the Lesson 17-18 reference and drafts (2026-11-08)
**Feedback on Lessons 15-16:** none was available. The session prompt asked to incorporate it but contained no text; Thomas asked mid-session and was told to paste it. Lessons 15-16 are unchanged. **Open until Thomas sends it.**

**Decision: Postgres/Podman setup** is Step 1 of Lesson 18 (with a Barn Briefing "a database in a container"), not a separate page. Lesson 17 adds the JDBC dependencies but runs tests only (the app cannot start without a datasource until 18); this is stated in Lesson 17 step 1.

**Reference (`mentor/reference/`, git-ignored, 198 tests green with the DB stopped):**
- Lesson 17: `JdbcPenRepository` (`JdbcClient`, `GeneratedKeyHolder`, insert/update by id, delete missing alpacas, fresh objects on read), `schema.sql`, `JdbcPenRepositoryTest` (`@JdbcTest` extends `PenRepositoryContract`), contract now creates the repository in `@BeforeEach`. Two traps observed and put into the lesson (NPE from field initialiser; inherited tests not rolled back, so tables are emptied in `createRepository()`).
- Lesson 18: `compose.yml` (postgres:17), `application.properties`, `DatabaseConfig` (JDBC bean + `ApplicationRunner` seed), old `FarmConfig` moved to `src/test` as `InMemoryFarmConfig` (no `@Configuration`), `@AutoConfigureTestDatabase` on the two `@SpringBootTest` classes. Real run on Postgres with restart persistence verified (details in findings section 14).

**Lessons drafted:** `curriculum/phase-4-the-barn-gets-a-database/lesson-17-the-shelf-becomes-a-table/` and `lesson-18-moving-into-the-database/`; index rows 17, 18 added (Draft); Lesson 16's next link points to 17; Lesson 18's next is a placeholder for 19. Links checked (HTTP 200); tags balance; not viewed in a browser or at phone width.

**Open**
- Does `podman compose up` work on the learner's Windows machine? Here it needed `DOCKER_CONFIG=<empty dir>` because of a missing `docker-credential-desktop`. Lesson 18 step 1 tells her to report if it fails. Not tested: the Lesson 18 claim that a stopped container makes `@SpringBootTest` fail was observed; `down` without `-v` keeping data was not run.
- Lesson 18's step 4 is long (move + rename + remove annotation + 4 imports); check with Thomas if it should be split.
- Lesson 17 `findAll` is N+1 (named in Reflect); the reference keeps it.
- Ilona/Hasi names still in public text. Lessons 15-18 not reviewed by Thomas.

**Next:** incorporate Thomas's feedback on Lessons 15-18 (ask for it if not given), then start Phase 5: Lesson 19 (Swagger UI / OpenAPI with springdoc 3.2.0) reference first, then draft Lessons 19-21.

## Status after Thomas's review of Lessons 15-18 (2026-11-08)
Review feedback incorporated; **Phase 5 was NOT started** (the session was used up by the review).

**Renamed (folders, titles, links, index, Copilot prompts):** 15 *Identity and the Repository* (was "Everyone Gets a Number"), 16 *Swapping the Repository*, 17 *The Repository Becomes a Table*. The "shelf" metaphor is gone from the whole curriculum; the real term "repository" is used (Thomas: a metaphor she must translate gives her nothing to look up). Earlier HANDOFF sections still use the old names (history).

**Lesson 15**
- Briefing "a name is not an identity" rewritten and compressed (3 paragraphs): a name does not tell alpacas apart (unique only inside a pen, changes); identity = pointing at exactly one alpaca and keeping pointing at it while details change; the id carries it, is better only at staying put, and only because of enforced rules (`assignId` once). Who hands out the id is presented as a **choice** (UUID in the constructor vs. storage counter); the course takes the counter, price = `null` until stored. New Reflect question on `UUID`.
- Briefing "a repository the domain owns" compressed (4 short paragraphs): `Farm` does two jobs (rule + keeping the list), SQL inside it would make rule tests need a database; so storage moves into a repository; no `AlpacaRepository`; contract test.
- Story: Hasi's line now says "Your objects need an id too" and explains why `Farm`/`Herd` should not contain save/load code. "number" became "id" where the identifier is meant (also Lessons 16, 17).

**Lesson 17:** briefing "objects and rows" now has two paragraphs: objects vs. rows incl. "copies, not the objects you saved; a change is only stored when you call `save` again", and a closing paragraph naming **object-relational mapping (ORM)**, JPA/Hibernate in one sentence; the Spring Sprout's "not an ORM" points back to it.

**Decisions / discussed, not changed**
- `Long` stays the id type. A value-object `Id` (or `AlpacaId`/`PenId`) was discussed and judged **not worth the change** (reference, 4 lessons, mappings); it could be an optional exercise after Lesson 18. Only the UUID Reflect question was added.

**Open**
- Lesson 17 says JPA/Hibernate "you will meet at work"; the company project's use of JPA is unknown (change to "in many projects" if it does not use it).
- Not viewed in a browser, not checked on phone width. Lesson 18 not re-read in this review (only the "shelf" wording was replaced). Ilona/Hasi names still in public text.

**Next:** start Phase 5: build the Lesson 19 reference (Swagger UI / OpenAPI with springdoc 3.2.0) in `mentor/reference/`, then draft Lessons 19-21.

## Status after the Lesson 19 reference and the drafts of Lessons 19-21 (2026-11-08)
**Reference (`mentor/reference/`, git-ignored, 206 tests green):** springdoc 3.2.0 added; `ApiDocsTest` (6 tests: `/v3/api-docs` lists `/pens`, Swagger UI redirect, own title, `201` on `POST /pens`, `404` on `/pens/{penName}/alpacas`, validation rules in the schema; the first four seen red), `ApiDocsConfig` (`OpenAPI` bean), `@ApiResponse(201)` on the two `POST` methods, `@ResponseStatus` on all `ApiExceptionHandler` methods. For Lesson 20: `ErrorShapeTest` (404/405 as `application/problem+json`, seen red), `spring.mvc.problemdetails.enabled=true`, and `ApiExceptionHandler extends ResponseEntityExceptionHandler` with two overrides. **Trap found:** the property alone turns two `PenChangesTest` tests red (Boot's own handler wins, the `errors` map is lost). Details in `research/spring-boot-4-findings.md` section 15.

**Lessons drafted** in `curriculum/phase-5-wrap-up/` (Draft, not reviewed by Thomas):
- **19 The Farm Explains Itself:** Barn Briefing "a description you can read, and a page that shows it" (OpenAPI vs. Swagger UI; a generated description only knows what it can see), Spring Sprout springdoc (not Boot-managed, explicit version). Steps follow the reference: red test (404) -> dependency -> look at Swagger UI and `/v3/api-docs`, write down right/wrong in `docs/api-notes.md` -> title via `OpenAPI` bean -> `201` via `@ApiResponse` -> errors via `@ResponseStatus` on the advice -> notice that every endpoint now lists `404/409/400` and that the `ProblemDetail` schema has `properties` where the real answer has `errors`. Reflect: description vs. behaviour, Swagger UI in production.
- **20 A Fresh Pair of Eyes:** review lesson, no new feature. Barn Briefing with four lenses (contract, the client's day, edges, code behind); `docs/api-review.md` with >= 8 ranked findings, fix two test-first. Hint 2 lists findings found in the reference (ready-to-shear lacks the pen name, ids not usable in addresses, ISE/IAE always 409/400, `GET /farm` calls `findAll` twice, shear log only in memory, `HashMap`, unused `Farm`, error shape of unknown addresses). Hint 3 is the verified optional hard fix (problemdetails property + `ResponseEntityExceptionHandler` overrides).
- **21 What Comes Next:** Barn Briefing "what the frontend sees of your work" (addresses, bodies, statuses; OpenAPI as the file for Ilona; what the course left out: login/roles, CORS, migrations, real-DB tests, monitoring), task `docs/handover.md` tried by a stranger, Phase 5 demo, final Reflect. No code. Mentions "the Angular curriculum" generically, no link.
- Index Phase 5 rows 19-21 link the lessons; Lesson 18's next link points to 19; Lesson 21's next is an end note. springdoc.org links checked (HTTP 200). HTML tags balance; not viewed in a browser or at phone width.

**Open**
- Swagger UI was not opened in a real browser against the running app (Postgres not started this session); the "Try it out" and redirect steps rest on the MockMvc tests and the generated YAML, not on the UI.
- Lesson 20 claims are checked against the code; "shear log lost on restart" and "`HashMap`" come from the reference design (earlier findings), not re-run.
- Is there an Angular curriculum to link from Lesson 21? Unknown; the lesson says it generically.
- Lessons 15-21 not reviewed by Thomas; Ilona/Hasi names still in public text (see DESIGN.md).
- **At the final wrap-up: remind Thomas to delete `CLAUDE.md` (and any "Working protocol" mention) and to retire `HANDOFF.md`.**

**Next:** first ask Thomas for his feedback on Lessons 15-21 and wait for the answer, then incorporate it; after that the final wrap-up: view the pages in a browser and on phone width, resolve the Ilona/Hasi names, settle the Postgres/Podman-on-Windows question, write the Spring-adapted `copilot-instructions.md` (DESIGN.md build step 5), and remind Thomas to delete `CLAUDE.md`.

## Status after Thomas's feedback on Lesson 19 (2026-11-08)
**Feedback received:** only on Lesson 19 so far. Thomas's team goes contract first: the OpenAPI spec is written first, and the `openapi-generator-maven-plugin` generates the controller interfaces and the models; the hand-written class implements the interface. Decisions (Thomas): reuse the hand-written controllers, implement the generated interfaces, replace the hand-written records by the generated models; **springdoc is dropped**; the generator is configured without swagger annotations.

**Reference rebuilt (`mentor/reference/`, git-ignored, 200 tests green):** `src/main/resources/api/openapi.yaml` describes the whole existing API (7 paths, 3 tags -> `FarmApi`, `PensApi`, `ShearingApi`), plugin 7.27.0 in `pom.xml`, `PenController`/`FarmController`/`ShearingController` implement the generated interfaces, new `WebMapping`, old records and springdoc (`ApiDocsConfig`, `ApiDocsTest`) removed. Two traps observed red and recorded in findings section 16 (`minLength` vs `@NotBlank`; OpenAPI `pattern` is unanchored but `@Pattern` is anchored). The existing web tests stayed the safety net and were not changed.

**Plan for Lesson 19 (not yet written; the current springdoc Lesson 19 HTML is outdated):** "Contract First" as a refactoring on green tests: (1) read the generated interfaces from a tiny spec for one endpoint (`GET /farm`), (2) implement `FarmApi` in `FarmController`, record deleted, (3) grow the spec to Pens/Alpacas/Shearing one tag at a time, (4) let the existing tests find where the spec is looser than the code (the two traps), (5) change the contract first once (e.g. a new field) and watch the compiler lead. Open: Lesson 20 hint 3 (problemdetails trap) and Lesson 20/21 text that mentions Swagger UI/springdoc/OpenAPI-as-description must be revised; DESIGN.md (Phase 5 row, Persistence/Stack lines, springdoc) and the index rows too.

**Still open from before:** Thomas's feedback on Lessons 15-18, 20, 21 (asked, not yet given); Lesson 18 step 4 length; "JPA at work" wording; Angular link; Ilona/Hasi names; Podman on Windows; browser/phone-width check; `copilot-instructions.md`; remind Thomas to delete `CLAUDE.md`.

**Next:** rewrite Lesson 19 as "Contract First" from the plan above (and fix the springdoc mentions in Lessons 20-21, the index and DESIGN.md); then incorporate Thomas's remaining feedback on Lessons 15-18, 20, 21, and do the final wrap-up (browser and phone-width check, Ilona/Hasi names, Podman on Windows, `copilot-instructions.md`, remind Thomas to delete `CLAUDE.md`).
