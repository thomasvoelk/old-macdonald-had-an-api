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
