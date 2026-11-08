# Spring Boot 4.1.x research findings

Researched 2026-11-06 against primary sources only (docs.spring.io, Spring Boot wiki, Maven Central metadata/POMs/jars, springdoc.org, JUnit/Jackson/H2/Mockito pages). Where a claim comes from a jar I inspected myself, the artifact is named. "Maven Central" below = https://repo.maven.apache.org/maven2/.

## 0. Version summary (verified)

| Item | Value | Source |
|---|---|---|
| Latest stable 4.1.x | **4.1.2** (4.1.0, 4.1.1, 4.1.2 exist; 4.2.0-RC2 is the newest overall but is a release candidate, do NOT use) | `Maven Central/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml` |
| Spring Framework | 7.0.10 | `spring-boot-dependencies-4.1.2.pom` property `spring-framework.version`; https://docs.spring.io/spring-boot/system-requirements.html |
| Java | min 17, "compatible with versions up to and including Java 27" (so Java 25 is fine) | https://docs.spring.io/spring-boot/system-requirements.html (page = Boot 4.1.2) |
| Maven | 3.6.3 or later | same |
| Tomcat | 11.0.x (Servlet 6.1) | same |
| JUnit Jupiter | 6.0.3 (Boot-managed) | `spring-boot-dependencies-4.1.2.pom` |
| Mockito | 5.23.0 (Boot-managed) | same |
| AssertJ | 3.27.7 | same |
| Jackson 3 BOM | 3.1.7 (`tools.jackson`); Jackson 2 BOM 2.21.7 also managed | same |
| Hibernate Validator | 9.1.5.Final; Jakarta Validation 3.1.1 | same |
| H2 | 2.4.240 (`com.h2database:h2`) | same |
| PostgreSQL JDBC | 42.7.14 (`org.postgresql:postgresql`) | same |
| springdoc | 3.2.0 (`springdoc-openapi-starter-webmvc-ui`), its parent is `spring-boot-starter-parent:4.1.2` | Maven Central POMs |

Recommended parent for a new project today: `org.springframework.boot:spring-boot-starter-parent:4.1.2`, `java.version` = 25. The company example (`/Users/thomas/Development/Projekte/daily-fleece/backend/pom.xml`) uses 4.1.1 with `<java.version>25</java.version>`; 4.1.2 is the newer patch. Note the starter-parent itself defaults `java.version` to 17 (parent POM), so setting 25 in `<properties>` is required.

## 1. Starters

Source: Boot 4.0 Migration Guide, https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide (quotes: "`spring-boot-starter-web`" is deprecated; the replacement is "`spring-boot-starter-webmvc`"; "Each starter has a test starter companion"; test modules named `spring-boot-<technology>-test`; "The older starters remain, but should be considered deprecated").

Verified by reading the 4.1.2 starter POMs on Maven Central:

| Artifact (`org.springframework.boot:`) | Exists? | Pulls in (direct deps) |
|---|---|---|
| `spring-boot-starter-webmvc` | yes | `spring-boot-starter`, `spring-boot-starter-jackson`, `spring-boot-starter-tomcat`, `spring-boot-http-converter`, `spring-boot-webmvc` |
| `spring-boot-starter-webmvc-test` (test scope) | yes | `spring-boot-starter-jackson-test`, `spring-boot-starter-test`, `spring-boot-starter-webmvc`, `spring-boot-webmvc-test`, `spring-boot-resttestclient` |
| `spring-boot-starter-validation` | yes | `spring-boot-starter`, `spring-boot-validation` (which brings `hibernate-validator`, `tomcat-embed-el`) |
| `spring-boot-starter-jdbc` | yes | `spring-boot-starter`, `spring-boot-jdbc`, `HikariCP` |
| `spring-boot-starter-jdbc-test` (test scope) | **yes, it exists** | `spring-boot-starter-jdbc`, `spring-boot-starter-test`, `spring-boot-jdbc-test` |
| `spring-boot-starter-test` | yes (pulled by every `-test` starter, so not declared separately) | `spring-boot-test`, `spring-boot-test-autoconfigure`, `junit-jupiter`, `mockito-core`, `mockito-junit-jupiter`, `assertj-core`, `spring-test`, `jsonassert`, `json-path`, `awaitility`, `hamcrest`, `xmlunit-core` |

`spring-boot-starter-validation-test` also exists and is used by the company pom, but nothing in this curriculum needs it (validation is exercised through `@WebMvcTest`).

H2 and PostgreSQL: both are plain driver artifacts with versions managed by `spring-boot-dependencies` (so omit `<version>`):

```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

Source for management: `spring-boot-dependencies-4.1.2.pom` (`h2.version` 2.4.240, `postgresql.version` 42.7.14). The scope choice (h2 test, postgres runtime) is my recommendation, not from the docs.

Company usage (`daily-fleece/backend/pom.xml`): `spring-boot-starter-webmvc`, `spring-boot-starter-validation`, `spring-boot-starter-webmvc-test` (test), `spring-boot-starter-validation-test` (test), `spring-boot-starter-actuator-test`. Its tests are `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `RestClient` and Modulith's `@ApplicationModuleTest`; it uses **no** `@WebMvcTest`, `MockMvc`, `MockitoBean` or `JdbcClient`, so there is no company precedent for those.

Minimal pom for the curriculum (derived from the above):

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>4.1.2</version>
</parent>
<properties><java.version>25</java.version></properties>
<!-- compile: spring-boot-starter-webmvc, spring-boot-starter-validation, spring-boot-starter-jdbc, org.postgresql:postgresql (runtime) -->
<!-- test: spring-boot-starter-webmvc-test, spring-boot-starter-jdbc-test, com.h2database:h2 -->
```

## 2. @WebMvcTest, MockMvc, MockMvcTester

- Package/artifact: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` in artifact `org.springframework.boot:spring-boot-webmvc-test` (verified by `unzip -l spring-boot-webmvc-test-4.1.2.jar`; also `AutoConfigureMockMvc` and `MockMvcTesterConfiguration` in the same package). The old `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` is gone from Boot 4 (the `spring-boot-test-autoconfigure` jar has no such class).
- Docs: https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html (Boot 4.1.2): "use the `@WebMvcTest` annotation from the `spring-boot-webmvc-test` module"; "`@WebMvcTest` also auto-configures `MockMvc`". Scanned beans are limited to `@Controller`, `@ControllerAdvice`, `@JacksonComponent`, Converters, Filters, etc. (so a `@RestControllerAdvice` is picked up automatically, services are not).
- Docs example for `MockMvcTester` with `@WebMvcTest` (verbatim from that page):

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(UserVehicleController.class)
class MyControllerTests {
	@Autowired private MockMvcTester mvc;
	@MockitoBean private UserVehicleService userVehicleService;

	@Test
	void testExample() {
		given(this.userVehicleService.getVehicleDetails("sboot"))
			.willReturn(new VehicleDetails("Honda", "Civic"));
		assertThat(this.mvc.get().uri("/sboot/vehicle").accept(MediaType.TEXT_PLAIN))
			.hasStatusOk()
			.hasBodyTextEqualTo("Honda Civic");
	}
}
```

- Classic `MockMvc` (Hamcrest) is still supported: `mvc.perform(get("/")).andExpect(status().isOk())...` with `org.springframework.test.web.servlet.MockMvc` (same docs page; it declares `throws Exception`).
- Which is recommended: **no official page says "prefer X"**. The Boot docs list four integrations (MockMvc, MockMvcTester "wraps `MockMvc` and uses AssertJ", RestTestClient, WebTestClient), and the `@WebMvcTest` example itself uses `MockMvcTester`. The Spring Framework page https://docs.spring.io/spring-framework/reference/testing/mockmvc/assertj.html lists the advantages: no static imports needed, unresolved exceptions handled consistently so tests need not declare `throws Exception`, async handled without special code. Both share one `MockMvc` instance. My inference: `MockMvcTester` is the better fit for an AssertJ-first (beginner) curriculum; this is a judgement, not an official recommendation.
- `MockMvcTester` JSON helpers (`bodyJson()`, `extractingPath`) appear in Framework docs only as my recollection of a generic example; I did not verify exact method names beyond `hasStatusOk()`, `hasBodyTextEqualTo()` (docs example above). Verify by compiling when building the reference solution.

## 3. @MockitoBean

- Exact FQCN: `org.springframework.test.context.bean.override.mockito.MockitoBean` (and `MockitoSpyBean`) in **spring-test** (verified in `spring-test-7.0.10.jar`; Spring Framework, not Boot).
- Boot 4 Migration Guide (wiki): "`@MockBean` and `@SpyBean` support has been removed in this release"; the Spring Framework annotations replace them ("The new annotations are allowed to be used as fields in test classes, but not in `@Configuration` classes"). Confirmed independently: `spring-boot-test-4.1.2.jar` contains no `MockBean` class.
- So `@MockBean` no longer exists in Boot 4.1 (not merely deprecated). Do not teach it.
- Behaviour (https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html): default strategy `REPLACE_OR_CREATE` (creates the bean if none exists; `enforceOverride = true` switches to replace-only); usable on non-static fields; a type-level form needs `types`.
- Boot docs state the combination: "Often, `@WebMvcTest` is limited to a single controller and is used in combination with `@MockitoBean`".
- Related: Mockito `@Mock`/`@Captor` fields now need `MockitoExtension` (`@ExtendWith(MockitoExtension.class)`); Boot's `MockitoTestExecutionListener` is removed (Migration Guide).

## 4. @SpringBootTest smoke test

- Docs (Boot 4.1.2 testing page): "By default, `@SpringBootTest` does not start the server but instead sets up a mock environment"; `webEnvironment = MOCK` is the default.
- **New in Boot 4** (Migration Guide, section "Using MockMVC and @SpringBootTest"): "Using the `@SpringBootTest` annotation will no longer provide any MockMVC support... you should now add an `@AutoConfigureMockMvc` annotation to the test class." Same for `TestRestTemplate`/`WebClient` (`@AutoConfigureTestRestTemplate`, needs `spring-boot-resttestclient`) and `RestTestClient`/`WebTestClient` (own `@AutoConfigure...` annotations).
- `@AutoConfigureMockMvc` is `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`.
- A pure context-loads smoke test needs nothing extra:

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AlpacasApplicationTests {
	@Test void contextLoads() {}
}
```
  (`org.springframework.boot.test.context.SpringBootTest` verified in `spring-boot-test-4.1.2.jar`; the company's `BackendApplicationTests` imports the same.)
- If the smoke test should also call an endpoint, add `@AutoConfigureMockMvc` and inject `MockMvcTester` or `MockMvc` (docs example `MyMockMvcTests` uses `@SpringBootTest @AutoConfigureMockMvc` with `@Autowired MockMvcTester mvc`).
- Caveat for this curriculum: a `@SpringBootTest` that also has a `schema.sql` + H2 on the classpath will use the embedded DB; with Postgres as the only datasource it needs a running DB. Plan the smoke test per phase accordingly (not a docs issue, a design note).

## 5. ProblemDetail, @RestControllerAdvice, Bean Validation

- Docs (Boot 4.1.2, https://docs.spring.io/spring-boot/reference/web/servlet.html, section Error Handling): "RFC 9457 Problem Details is supported. Spring MVC can produce custom error messages with the `application/problem+json` media type... This support can be enabled by setting `spring.mvc.problemdetails.enabled` to `true`." The doc then shows a `@ControllerAdvice` extending `ResponseEntityExceptionHandler`.
- Verified from `spring-boot-webmvc-4.1.2.jar` metadata: property `spring.mvc.problemdetails.enabled`, **default `false`**, "Whether RFC 9457 Problem Details support should be enabled." The property activates Boot's `ProblemDetailsExceptionHandler` (a built-in `ResponseEntityExceptionHandler` advice, class `org.springframework.boot.webmvc.autoconfigure.ProblemDetailsExceptionHandler`) for standard Spring MVC exceptions only.
- Consequence for the lesson: our own `@RestControllerAdvice` with `@ExceptionHandler` methods returning `ProblemDetail` / `ResponseEntity<ProblemDetail>` works without the property. If both the property and our own advice extending `ResponseEntityExceptionHandler` are used, they could conflict (Boot's handler is conditional on there being no other `ResponseEntityExceptionHandler` bean; I did not verify the exact condition). Safest teaching choice: own advice, extend `ResponseEntityExceptionHandler` only if standard MVC exceptions should also become problem+json; property not needed.
- Types: `org.springframework.http.ProblemDetail` (Spring Framework; `ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "...")` is a Spring Framework API I did not re-verify in this pass; check with `javap` on `spring-web-7.0.10.jar` when writing the reference solution). The Spring Framework reference is https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html (linked from the Boot page; not fetched in full).
- Bean Validation: `spring-boot-starter-validation` -> `spring-boot-validation` -> Hibernate Validator 9.1.5.Final implementing Jakarta Validation 3.1.1 (BOM). Package is `jakarta.validation.constraints.*` (`@NotBlank`, `@Positive`) and `jakarta.validation.Valid`. `@Valid @RequestBody` failure raises `MethodArgumentNotValidException` (Spring Framework; known behaviour, not re-verified in this pass).
- Company usage: `spring-boot-starter-validation` is in the pom. Company code uses `RestClient` in tests.

## 6. JdbcClient

Package/class: `org.springframework.jdbc.core.simple.JdbcClient` (spring-jdbc 7.0.10, verified). Boot auto-configures a `JdbcClient` bean: `org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration` in `spring-boot-jdbc-4.1.2.jar` (verified), so constructor-inject it.

Signatures verified with `javap` on `spring-jdbc-7.0.10.jar`:

- `JdbcClient.sql(String)` -> `StatementSpec`
- `StatementSpec`: `param(Object)` (positional), `param(String name, Object)` (named), `param(int, Object)`, `params(List|Map|Object...)`, `paramSource(Object)` (record/bean/fields as named params), `query(Class<T>)`, `query(RowMapper<T>)`, `query()` (-> `ResultQuerySpec`), `update()`, `update(KeyHolder)`, `update(KeyHolder, String... keyColumnNames)`
- `MappedQuerySpec<T>`: `list()`, `set()`, `stream()`, `single()`, `optional()`
- `ResultQuerySpec`: `singleValue()`, `optionalValue()`, `singleColumn()`, `singleRow()`, `listOfRows()`

Docs (https://docs.spring.io/spring-framework/reference/data-access/jdbc/core.html, Framework 7.0.10), verbatim examples:

```java
// positional
this.jdbcClient.sql("select count(*) from t_actor where first_name = ?")
		.param(firstName).query(Integer.class).single();
// named
this.jdbcClient.sql("select count(*) from t_actor where first_name = :firstName")
		.param("firstName", firstName).query(Integer.class).single();
// RowMapper
List<Actor> actors = this.jdbcClient.sql("select first_name, last_name from t_actor")
		.query((rs, rowNum) -> new Actor(rs.getString("first_name"), rs.getString("last_name")))
		.list();
// class mapping
Optional<Actor> actor = this.jdbcClient.sql("select first_name, last_name from t_actor where id = ?")
		.param(1212L).query(Actor.class).optional();
// update, object as parameter source
this.jdbcClient.sql("insert into t_actor (first_name, last_name) values (:firstName, :lastName)")
		.paramSource(new Actor("Leonor", "Watling")).update();
```

Mapping to records vs plain classes: the docs say the class may be "a record class, a custom constructor, bean properties, or plain fields", resolved by `SimplePropertyRowMapper` (and `SimplePropertySqlParameterSource` for parameters). Column names are matched to property names (snake_case column `first_name` -> `firstName`; this snake_case matching is stated in the SimplePropertyRowMapper Javadoc as I recall it, not re-fetched; verify in the reference solution). Records need no setters; plain classes need either a usable constructor, setters, or accessible fields. `single()` throws if there is not exactly one row (`IncorrectResultSizeDataAccessException`, known behaviour, not re-fetched); `optional()` returns empty for none.

Generated keys: the Framework docs only show `JdbcTemplate.update(PreparedStatementCreator, KeyHolder)` with `new GeneratedKeyHolder()` and say that style "works on Oracle but may not work on other platforms". The `JdbcClient` overloads `update(KeyHolder)` and `update(KeyHolder, String...)` exist (javap, above) but I did not find a documented example. Expected pattern (needs compile/run verification against H2 and Postgres):

```java
KeyHolder keys = new GeneratedKeyHolder();   // org.springframework.jdbc.support.GeneratedKeyHolder
jdbcClient.sql("insert into pens (name) values (:name)")
          .param("name", name)
          .update(keys, "id");               // naming the key column is the portable choice
long id = keys.getKey().longValue();
```

## 7. @JdbcTest with embedded H2, schema.sql

- Package/artifact: `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest` and `...AutoConfigureTestDatabase` in `org.springframework.boot:spring-boot-jdbc-test` (verified in jar; docs import `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest`). Starter: `spring-boot-starter-jdbc-test`.
- Docs (testing page, Boot 4.1.2): "`@JdbcTest` from the `spring-boot-jdbc-test` module... is for tests that only require a `DataSource`... By default, it configures an in-memory embedded database and a `JdbcTemplate`. Regular `@Component` and `@ConfigurationProperties` beans are not scanned." JDBC tests "are transactional and roll back at the end of each test". "If you prefer your test to run against a real database, you can use the `@AutoConfigureTestDatabase` annotation".
- Important: the docs say it configures a `JdbcTemplate`; the **JdbcClient bean** comes from `JdbcClientAutoConfiguration`. I did **not** verify that `@JdbcTest` includes `JdbcClientAutoConfiguration` in its auto-configuration list (appendix not fetched). Check `META-INF/spring/org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureJdbc.imports` in `spring-boot-jdbc-test` while building the reference solution; fallback `@Autowired JdbcTemplate` -> `JdbcClient.create(jdbcTemplate)`, or `@Import`/`@AutoConfigureJdbc`.
- Because `@JdbcTest` does not scan `@Component`s, the repository class under test must be pulled in with `@Import(JdbcAlpacaRepository.class)` (general `@JdbcTest` behaviour implied by the quote above).
- `AutoConfigureTestDatabase.Replace` enum values (javap): `NON_TEST` (default), `ANY`, `AUTO_CONFIGURED`, `NONE`. Property `spring.test.database.replace`, default `non-test` (from `spring-boot-jdbc-test` metadata JSON). Using the default, Boot swaps in its own embedded H2 database (with a generated name) and ignores your `spring.datasource.url`. Therefore **H2 `MODE=PostgreSQL` does not apply by default**; to use it you must use `@AutoConfigureTestDatabase(replace = Replace.NONE)` plus a test datasource URL such as `jdbc:h2:mem:farm;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`. The Boot docs page does not describe this combination; I did not run it. The H2 mode syntax is from https://www.h2database.com/html/features.html ("jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"). Per the curriculum decision (H2 silently, no lesson on differences), the simplest path is to keep the default replacement and write portable SQL; mention `MODE=PostgreSQL` in mentor notes only. H2 PostgreSQL-mode documented quirks (mentor notes): `NUMERIC` without params treated as `DECFLOAT`, `MONEY` as `NUMERIC(19,2)`, `UPDATE ... FROM` only partially supported, `SERIAL`/`BIGSERIAL` supported, `LIMIT/OFFSET` supported.
- schema.sql initialization (https://docs.spring.io/spring-boot/how-to/data-initialization.html, 4.1.2): default locations `optional:classpath*:schema.sql` and `optional:classpath*:data.sql`; "By default, SQL database initialization is only performed when using an embedded in-memory database. To always initialize an SQL database, irrespective of its type, set `spring.sql.init.mode` to `always`" (`never` disables). Fail-fast by default (`spring.sql.init.continue-on-error=false`). Verified property defaults from `spring-boot-sql` metadata: `spring.sql.init.mode=embedded`, `enabled=true`, `schema-locations`, `data-locations`, `platform=all`, `separator=;`. Packages moved in Boot 4 to `org.springframework.boot.sql.autoconfigure.init` (module `spring-boot-sql`).
- Consequence: against real Postgres (dev run) set `spring.sql.init.mode=always` in `application.properties`, and write `schema.sql` idempotently (`create table if not exists`). In `@JdbcTest` with embedded H2 the script runs automatically (embedded).
- Docs warn: using plain `schema.sql`/`data.sql` alongside Flyway/Liquibase "is not recommended"; irrelevant here (no Flyway).

## 8. springdoc-openapi

- Artifact: `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0` (latest on Maven Central, last updated 2026-10-24). API-only variant: `springdoc-openapi-starter-webmvc-api`. The webmvc-ui POM depends on `springdoc-openapi-starter-webmvc-api`, `swagger-ui` (webjar), `webjars-locator-lite`; springdoc's parent POM is `spring-boot-starter-parent:4.1.2`, `swagger-api` 2.2.55, `swagger-ui` 5.34.0.
- Compatibility: https://springdoc.org/faq.html: "springdoc-openapi 3.x is compatible with spring-boot 4"; matrix: **Boot 4.1.x <-> springdoc 3.1.x to 3.2.x**, Boot 4.0.x <-> 3.0.x; "you should only pick the last stable version as per today 3.2.0". Boot 3.x uses springdoc 2.x (https://springdoc.org/).
- Paths (https://springdoc.org/): Swagger UI default `/swagger-ui.html`; OpenAPI JSON `/v3/api-docs`, YAML `/v3/api-docs.yaml`.
- Version is **not** managed by Boot's BOM (springdoc is not in `spring-boot-dependencies`), so specify `<version>3.2.0</version>` explicitly.
- Caveats: springdoc.org states a Java 17 baseline and says nothing about Jackson 3 on the pages I read. Fact I did verify: springdoc 3.2.0 depends on `jackson-databind` and `spring-boot-jackson` (the Boot 4 Jackson 3 module). Javadoc-from-comments support needs `therapi-runtime-javadoc` (out of scope). Since springdoc scans controllers, DTO records and `@Valid` constraints show up in the schema automatically (my expectation, not verified).
- Note the 3.1.1 notes (Scalar/MCP) are irrelevant.

## 9. Jackson 3

- Migration Guide quotes: "Spring Boot now uses Jackson 3 as its preferred JSON library"; `com.fasterxml.jackson` becomes `tools.jackson`; exception: "the `jackson-annotations` module which continues to use the `com.fasterxml.jackson.core` group ID" and the `com.fasterxml.jackson.annotation` package. Confirmed by Jackson's own guide (https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md): "2.x version still used with 3.x, so no group-id/Java package change", and by the `jackson-databind` 3.1.7 POM (depends on `com.fasterxml.jackson.core:jackson-annotations`).
- Consequences:
  - `@JsonProperty`, `@JsonIgnore`, `@JsonInclude`, `@JsonFormat`, `@JsonCreator` stay `com.fasterxml.jackson.annotation.*`.
  - Anything touching the mapper/exceptions moves: `tools.jackson.databind.json.JsonMapper`, `tools.jackson.core.JacksonException` (unchecked, no longer extends `IOException`). `ObjectMapper` bean is replaced by `JsonMapper` bean for customisation (Migration Guide: "defining an `ObjectMapper` bean is no longer sufficient. Define a `JsonMapper`...").
  - `@JsonComponent` -> `@JacksonComponent`; `JsonObjectSerializer` -> `ObjectValueSerializer`.
  - Default change: `FAIL_ON_UNKNOWN_PROPERTIES` is **disabled** in Jackson 3 (Jackson migration guide). Spring Boot's own default for this was already off, so I do not claim behaviour change in Boot; I did not verify Boot's current setting.
  - `spring.jackson.read.*`/`write.*` moved under `spring.jackson.json.read`/`.write`.
  - Jackson 2 fallback exists (`spring-boot-jackson2`, deprecated; `spring.jackson2.*`), irrelevant here.
- Records/DTOs: the Jackson migration guide I fetched does not discuss records, and the Boot guide mentions no record-specific change. So no verified record-related change; plain records are used as DTOs with no annotations. Claim "records work out of the box" is therefore my expectation, to be confirmed by the first controller test in the reference solution.
- For the apprentice: she normally never imports Jackson at all. Only `JsonMapper` (if used) and annotations matter; a beginner-friendly rule: annotations from `com.fasterxml.jackson.annotation`, everything else `tools.jackson.*`.
- Starter chain verified: `spring-boot-starter-webmvc` -> `spring-boot-starter-jackson` -> `spring-boot-jackson` (+ `tools.jackson` libs via BOM `jackson-bom` 3.1.7).

## 10. JUnit 6, Mockito, Java 25, parent version

- JUnit: Boot 4.1.2's BOM manages `junit-jupiter.version` 6.0.3 (BOM). JUnit 6 raises minimum Java to 17 and deprecates the Vintage engine (JUnit release notes, https://docs.junit.org/6.0.3/release-notes/; the page does not discuss Java 25 and I found no JUnit statement about it). `spring-boot-starter-test` depends on `junit-jupiter`, so `org.junit.jupiter.api.Test` (also used by the company tests) stays. Her existing project (JUnit 6) matches Boot's managed version; do not pin JUnit separately.
- Mockito: BOM manages 5.23.0; `spring-boot-starter-test` includes `mockito-core` and `mockito-junit-jupiter`. Mockito's GitHub releases list 5.24.0 as latest (2026-09-23); Boot's BOM is a bit behind, which is fine. The Mockito release page contains no explicit "Java 25 support" statement (only a fix for static mocking `UUID` under JDK 25 in 5.22.0), so Java 25 compatibility is shown only indirectly: the company project runs Boot 4.1.1 + Java 25 + Mockito (managed) in CI-style config. Company pom adds `-XX:+EnableDynamicAgentLoading -Xshare:off` to surefire `argLine` to silence ByteBuddy/dynamic-agent JVM warnings on Java 21+; the comment in that pom describes the warnings. Mockito 5.16.0 changelog has "Avoid warning when dynamic attach is enabled". Optional for the curriculum; leave it out unless the warning appears.
- Java 25: Boot 4.1.2 officially "compatible with versions up to and including Java 27" (system requirements page). Company example is `java.version` 25.
- Parent: use 4.1.2 (stable). 4.2.0-RC2 exists on Central (2026-11-05) but is a release candidate.

## Could not verify

- Exact JSON-path helper method names on `MockMvcTester` (`bodyJson()` etc.); only `hasStatusOk()` and `hasBodyTextEqualTo()` are confirmed (Boot docs example).
- Whether `@JdbcTest` auto-configures the `JdbcClient` bean (docs say `JdbcTemplate`; `JdbcClientAutoConfiguration` exists in `spring-boot-jdbc`). Check the `AutoConfigureJdbc.imports` file while building the reference solution.
- A documented example of `JdbcClient.update(KeyHolder, String...)` with generated keys on H2/Postgres; only the method signatures (javap) and the JdbcTemplate docs example were seen. Must be tested on both H2 and Postgres.
- Column-name to property mapping rules of `SimplePropertyRowMapper` (snake_case matching) and exactly which plain classes work (constructor vs setters vs fields): docs say all of these are accepted; details not fetched from Javadoc.
- `ProblemDetail.forStatusAndDetail` and the exact interplay between `spring.mvc.problemdetails.enabled=true` and our own `ResponseEntityExceptionHandler` subclass (conditional bean) were not checked in source/Javadoc.
- `MethodArgumentNotValidException` as the exception for `@Valid @RequestBody`, and `ConstraintViolationException` for `@Validated` parameters: standard behaviour, not re-verified for Framework 7 in this pass.
- That Jackson 3 + records need no extra config (no source stated it).
- springdoc and Jackson 3 interplay beyond dependency lists (no springdoc statement found); springdoc FAQ was only partially read (its matrix was read, the rest not).
- Any official statement that Mockito/JUnit 6 are tested on Java 25 (none found). The practical evidence is the company project.
- The H2 default `@JdbcTest` embedded database name/quirks, and the `Replace.NONE` + `MODE=PostgreSQL` combination were not run.
- Whether `spring-boot-starter-jdbc-test` also brings H2 (it does not; POM lists no H2) so the explicit H2 dependency is needed.

## Verified while building the reference solution (2026-11-06, Phase 1 slice)

Built in `mentor/reference/` (git-ignored), Boot 4.1.2, built with JDK 27 and `java.version` 25; all 135 tests green.

- `MockMvcTester`: `assertThat(mvc.get().uri("/farm")).hasStatusOk().bodyJson().extractingPath("$.name").isEqualTo("...")` compiles and works. For a raw response use `mvc.get().uri(..).exchange().getResponse()` (the request builder itself has no `getResponse()`).
- Jackson 3 serialises plain records with no annotations (`FarmResponse`).
- `@WebMvcTest(FarmController.class)` + `@Import(FarmConfig.class)` works; the `@SpringBootApplication` class in `alpacas.web` is found by searching up from the test package. An unrestricted `@WebMvcTest` loads every controller, so a controller whose bean dependency is missing fails the whole context.
- `@SpringBootTest` `contextLoads()` smoke test works with no extra annotations.
- Lesson 01 leak, observed: returning an `Alpaca` from a controller produces `{"name":"Dolly","weight":70.0,"fleeceLength":4,"fleeceColor":"WHITE","healthyWeight":true,"readyToShear":false}`. The `is...()` methods become JSON properties and their `System.out.println` runs during serialisation.
- Mockito self-attach warning appears on JDK 27; harmless, ignore for now (see section 10).
- Still open: JdbcClient in `@JdbcTest`, KeyHolder on H2/Postgres, `ProblemDetail`, validation exceptions.

## Verified while building the reference solution (Phase 2 slice)

All 143 tests green (Phase 1's 135 + 8 in `PenControllerTest`).

- `ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message)` compiles and works with Framework 7.0.10; a plain `@RestControllerAdvice` with `@ExceptionHandler` methods returning `ProblemDetail` needs no property and no `ResponseEntityExceptionHandler`. The response has `Content-Type: application/problem+json` (asserted with `.hasContentType(MediaType.APPLICATION_PROBLEM_JSON)`) and `$.detail` holds the message.
- `@WebMvcTest(PenController.class)` picks up the `@RestControllerAdvice` automatically; no `@Import` of the advice is needed (tested by removing it).
- `MockMvcTester`: `.hasStatus(404)`, `.hasContentType(MediaType)`, `.bodyJson().extractingPath("$.length()").isEqualTo(1)` and indexed paths (`$[0].name`) all work. A path variable arrives case as typed, so `Farm.findPen` (case-insensitive) and `Herd.findByName` do the matching.
- Jackson 3 serialises a top-level `List<Record>` with no annotations.
- Domain change test-first: `Herd.findByName` now returns `Optional<Alpaca>` (5 existing tests in `HerdTest` rewritten red first, then green). Nothing else in her code called it.
- Design choice in the reference: `AlpacaResponse(name, weight, fleeceLengthCm, fleeceColor)` deliberately renames `fleeceLength` to `fleeceLengthCm` and exposes the colour as a `String`, to demonstrate that the JSON contract can differ from the domain. "Alpaca not found" is a web-level `AlpacaNotFoundException` thrown from the controller via `Optional.orElseThrow`; the domain only knows `Optional`.
- Still open: `JdbcClient` in `@JdbcTest`, KeyHolder on H2/Postgres, `MethodArgumentNotValidException` for `@Valid`.

## Verified while drafting Phase 1 lessons (2026-11-06)

- Added `FarmConfigTest` (`@SpringBootTest`, `@Autowired Farm`) to `mentor/reference/`; passes with `FarmControllerTest` and `AlpacasApplicationTests`. Lesson 03's test list rests on it.
- Running app (`mvn spring-boot:run`, Boot 4.1.2): `GET /farm` returns `{"name":"Fiona's Farm","pens":2,"alpacas":2}`; `GET /` with no mapping returns `404` with a **JSON** body (`{"timestamp":...,"status":404,"error":"Not Found","path":"/"}`), not an HTML Whitelabel page, when called with curl. Lesson 02 only says "a 404".

## Verified in the review session (2026-11-06, evening)

- Reference now has `AlpacasApplication` in package `alpacas` (top package) and its smoke test in `alpacas`; `@WebMvcTest(FarmController.class)` + `@Import(FarmConfig.class)` in `alpacas.web` still find it (search upwards). 145 tests green.
- Component scanning from `alpacas`: a `@Configuration` class in `alpacas.web` is found; the same class moved to a package outside `alpacas` (e.g. `config`) is not found, `@SpringBootTest` + `@Autowired Farm` then fails with `NoSuchBeanDefinitionException: No qualifying bean of type 'alpacas.Farm' available`. Moving it within `alpacas` passes.
- Sample data renamed: Lita, Batista, pens Ring and Cell (all tests adjusted; path matching stays case-insensitive: `/pens/ring/alpacas` works).

## Verified while building the Phase 3 reference (2026-11-06)
- `spring-boot-starter-validation` added; `jakarta.validation.constraints.*` (`@NotBlank`, `@NotNull`, `@Positive`, `@Min`, `@Max`) with `@Valid @RequestBody` works in `@WebMvcTest`. Invalid body -> `MethodArgumentNotValidException`; `getBindingResult().getFieldErrors()` gives field + `getDefaultMessage()` (e.g. "must not be blank", "must be greater than 0", "must not be null").
- Use boxed types (`Double`, `Integer`) plus `@NotNull` in request records: a missing `int` silently becomes 0.
- Broken JSON -> `HttpMessageNotReadableException` (own handler returns problem+json 400; without one the default error body is used).
- Own `@RestControllerAdvice` handlers for `IllegalArgumentException` (400) and `IllegalStateException` (409) work; `PenNotFoundException` extends `RuntimeException` so no clash.
- `ResponseEntity.created(URI)`: `URI.create("/pens/x/alpacas/Triple H")` THROWS (space) -> 400 via the IAE handler. Use `UriComponentsBuilder.fromPath("/pens/{p}/alpacas/{a}").buildAndExpand(..).encode().toUri()` -> `Location: /pens/Cell/alpacas/Triple%20H`. Good URL-encoding case.
- `@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)` needed on tests that mutate the shared `Farm` bean (context is cached across test methods).
- `mvc.post().uri(..).contentType(MediaType.APPLICATION_JSON).content(json)`, `.put()`, `.delete()`, `.hasHeader("Location", ..)`, `.hasStatus(201)` work in `MockMvcTester`.
- Local `@ExceptionHandler` + `@ResponseStatus(NOT_FOUND)` method in a controller: status 404, empty body, no problem+json. An unhandled `PenNotFoundException` at runtime: `500` with Boot's default error JSON (`timestamp,status,error,path`).
- `ProblemDetail` JSON: `{"detail","instance","status","title"}` (`type` omitted when `about:blank`); `setProperty("errors", map)` adds a top-level `errors` member.
- Domain: a uniqueness rule for alpaca names in `Herd.addAlpaca` broke 5 of her existing tests (they reuse names); name uniqueness is therefore checked in the controller (`AlpacaAlreadyExistsException`, 409). Pen names are unique in `Farm.addPen` (`IllegalStateException`, test-first).

## Verified while drafting Phase 3 lessons (2026-11-06)
- Without validation and the 400 handlers (scratch copy), `POST /pens/Cell/alpacas`: fleece length 11 -> 500, colour PURPLE -> 500, weight -5 -> 201, missing `int` component via boxed `Integer` -> 500 (NPE on unboxing), broken JSON -> 400 (Spring default). With the reference: errors map `{"weight":..,"fleeceLengthCm":"must be less than or equal to 10","name":"must not be blank"}` (entry order not stable), missing field "must not be null", lowercase colour accepted, 400 problem bodies include `instance`.
- Two `@WebMvcTest` methods that both create the pen "Stable" without `@DirtiesContext`: the second fails (409 once the duplicate rule exists; wrong count before).
- Correction: the earlier note that `@DirtiesContext` is "needed" is superseded. `PenChangesTest` passes without it when every mutating test uses its own pen (164 green); the course avoids `@DirtiesContext` because it rebuilds the context per test.

## Verified while building the shear (London) reference (2026-11-06)
Reference now 173 tests green (164 + 9 new). Built outside-in: controller test first, then service.
- `@WebMvcTest(ShearingController.class)` + `@MockitoBean ShearingService` (`org.springframework.test.context.bean.override.mockito.MockitoBean`) works. A package-private `@Service` class (no interface) can be mocked from a test in the same package. `given(...).willReturn(..)` / `.willThrow(..)` from `org.mockito.BDDMockito`, `verify(..)` from `org.mockito.Mockito`; both come with the web test starter.
- Red states observed: without the service class the test does not compile (`cannot find symbol ShearingService`); with an empty controller the tests fail with `Status = 404` (no mapping). Without `@MockitoBean` the context fails: `UnsatisfiedDependencyException: Error creating bean with name 'shearingController'` (`@WebMvcTest` does not scan `@Service`).
- A mock's exceptions go through the real `ApiExceptionHandler`: `willThrow(new PenNotFoundException(..))` -> 404 `application/problem+json`; `willThrow(new AlpacaNotFoundException("Nobody", "Ring"))` -> 404 with `$.detail` = `No alpaca named Nobody in pen Ring`.
- Design: the endpoint lives in its own `ShearingController` (so `PenController` and its two `@WebMvcTest` classes keep working without a mock). `ShearingService(Farm)` is `alpacas.web`, package-private, returns the sheared `Alpaca`; the controller maps it with `AlpacaResponse.from`. The service keeps one `Shepherd` ("Paul Heyman") per pen in a `Map` keyed by lower-cased pen name, because `Shepherd` is bound to a `Pen` in its constructor and owns the log; `logOf(penName)` exposes the log for tests. Domain classes unchanged (`Shepherd.shear` throws `IllegalArgumentException` for null; `Alpaca.shear()` sets length 0).
- Service tests are plain JUnit (no Spring, no Mockito): `new ShearingService(farm)`; log entry text `Paul Heyman sheared Lita on <today>`; an unknown alpaca logs nothing.
- Real service in `@WebMvcTest`: `@Import({FarmConfig.class, ShearingService.class})` and `@Autowired Farm` works (`ShearingWithRealServiceTest`, own pen `Barbershop`).
- `MockMvcTester`: `uri("/pens/Barbershop/alpacas/Triple%20H/shear")` returned 404 (the `%20` string did not match; observed, not analysed further). `uri("/pens/{pen}/alpacas/{alpaca}/shear", "Barbershop", "Triple H")` works. Lessons should use the template form when a name contains a space.

## Phase 4 reference, step 1: ids and `PenRepository` (2026-11-08)
Domain only, no Spring. Verified by compiling and running (186 tests green).
- `Alpaca` and `Pen` have `Long id` (null until saved), `getId()`, `assignId(long)` (throws `IllegalStateException` if already set). Existing constructors unchanged.
- `PenRepository` (interface in `alpacas`, owned by the domain): `save(Pen)` (aggregate: pen plus its alpacas; whatever has no id gets one; returns the same object), `findByName` (ignores case, `Optional`), `findAll` (in save order). Pen is the aggregate root; no `AlpacaRepository`.
- `InMemoryPenRepository` (counter shared by pens and alpacas, stores the saved objects themselves).
- Test-first: `IdentityTest` (red = `cannot find symbol`), `PenRepositoryContract` (abstract, 8 tests: ids, unique ids, find ignoring case, unknown -> empty, order, re-save keeps ids, removed alpaca gone after save, duplicate new pen name -> `IllegalStateException`), `InMemoryPenRepositoryTest extends` it. The JDBC adapter test can extend the same contract.
- Trap met: `repository()` in the contract returned a new object per call, so tests saw empty repos (6 red). Fix: one `createRepository()` per test instance in a field.
- Known gap: in-memory returns the very objects it stored, so "changes are only stored on `save`" is NOT in the contract (JDBC returns fresh objects). Could become a Reflect question or a contract test that forces copying.
- Not done: controllers/`FarmConfig` still use the `Farm` bean; `AlpacaResponse` has no id; saving alpacas of an existing pen in JDBC (upsert by id, delete missing) is Lesson 17 work.

## Phase 4 reference, step 2: `PenRepository` replaces the `Farm` bean (2026-11-08)
Verified by compiling and running (189 tests green; app started and curled).
- `FarmConfig` now has `@Bean PenRepository penRepository()` (an `InMemoryPenRepository` seeded via `save`: Ring with Lita and Batista, then Cell). No `Farm` bean. Running app: Ring id 1, Lita 2, Batista 3, Cell 4 (pens and alpacas share one counter). `Farm` class and `FarmTest` stay in `alpacas`, unused by `alpacas.web`.
- `PenRepository.getByName(String)` is a `default` method (same message as `Farm.findPen`: "The Pen you are looking for X does not exist", `PenNotFoundException`); tested in `PenRepositoryContract`.
- `AlpacaResponse(Long id, ...)` and `PenSummaryResponse(Long id, ...)` with `PenSummaryResponse.from(Pen)`; `id` is the first JSON member. `FarmResponse` unchanged; farm name is a constant in `FarmController` (no `Farm` object carries it any more).
- Controllers/`ShearingService` end every change with `pens.save(pen)`. `PUT` keeps the id: `replacement.assignId(existing.getId())` (without it the new alpaca has a fresh id; observed red `expected: 42 but was: 43`). Absolute ids in tests are not stable in the shared cached context: compare ids with each other.
- Observed red states (scratch copies): FarmConfig without the bean -> `UnsatisfiedDependencyException ... 'farmController' ... No qualifying bean of type 'alpacas.PenRepository' available`; controller asking for `Farm` without a `Farm` bean -> `No qualifying bean of type 'alpacas.Farm' available`; missing `id` member -> `To match JSON path: "$[0].id"`. A `@SpringBootTest` (FarmConfigTest) fails as long as ANY controller asks for a missing bean.
- `ShearingServiceTest` uses `new InMemoryPenRepository()` and needs no mock. Test helper: `mvc.get().uri(..).exchange().getResponse().getContentAsString()` (throws `UnsupportedEncodingException`) + `com.jayway.jsonpath.JsonPath` (on the test classpath via the web test starter).
- `PUT` on lowercase address wart (stores the address spelling) is unchanged.

## 14. Lessons 17-18 reference: JdbcClient, @JdbcTest, Postgres (verified 2026-11-08, `mentor/reference`, 198 tests green)

- **`@JdbcTest` does provide the `JdbcClient` bean** (autowired in `JdbcPenRepositoryTest`; closes the open item from section 7). `schema.sql` from `src/main/resources` runs on the embedded H2 automatically. Embedded H2 is the default replacement; no `MODE=PostgreSQL` needed for this schema.
- **`update(KeyHolder, "id")` with `GeneratedKeyHolder` works on H2 (tests) and on PostgreSQL 17 (real run)**: `keys.getKey().longValue()` gave the identity value (closes the KeyHolder item). Column list `"id"` lowercase worked on both.
- `.param("keptIds", List<Long>)` expands `:keptIds` into `in (?, ?, ...)` on both; an empty list needs a separate statement.
- Dependencies added: `spring-boot-starter-jdbc`, `org.postgresql:postgresql` (runtime), `spring-boot-starter-jdbc-test` (test), `com.h2database:h2` (test); all versions managed.
- **Trap 1 (observed red):** the contract's `private final PenRepository repository = createRepository();` runs in the superclass constructor, before Spring injects `@Autowired JdbcClient`: NPE `"this.jdbcClient" is null` in all 9 tests. Fix: create in `@BeforeEach`.
- **Trap 2 (observed):** tests *inherited from an abstract contract class* did **not** run in the `@JdbcTest` transaction (`TestTransaction.isActive()` false; a test declared in the subclass itself was active), so rows leaked between tests (`There is already a pen named Ring`). Cause not investigated; adding `@Transactional` to the contract class fixed it, but the reference instead empties both tables in `createRepository()` to keep the contract free of Spring.
- Red states in lesson order: compile error `cannot find symbol class JdbcPenRepository`; NPE above; `Table "ALPACAS" not found` (H2 upper-cases) without `schema.sql`; `UnsupportedOperationException` with stubs.
- **Lesson 18:** with `application.properties` pointing to Postgres (`spring.sql.init.mode=always`), a second `@Bean penRepository` (old `FarmConfig` still in main) gives `BeanDefinitionOverrideException: Invalid bean definition with name 'penRepository'` (Boot forbids overriding; same method name). Old `FarmConfig` moved to `src/test` as `InMemoryFarmConfig` **without** `@Configuration` (otherwise component scan in `@SpringBootTest` finds it); web tests `@Import` it.
- `@SpringBootTest` without `@AutoConfigureTestDatabase` (`org.springframework.boot.jdbc.test.autoconfigure`) connects to the real Postgres from `application.properties`: passes silently with the container up, red with it stopped (`UnsatisfiedDependencyException ... penRepository ... jdbcClient ... Failed to execute database script`). With the annotation both `@SpringBootTest` classes use embedded H2 and `schema.sql` plus the `ApplicationRunner` seed run there.
- Real run on `postgres:17` via `podman compose up -d`: `GET /farm` 2 pens/2 alpacas, Lita id 1; after POST (Triple H id 3), shear and an app restart: 3 alpacas, Lita fleece 0, ids unchanged, no re-seed (`psql` showed 3 rows). Seed = `ApplicationRunner` that saves Ring/Cell when `findAll()` is empty.
- **Environment gotcha (this Mac only):** `podman compose` delegated to `/usr/local/bin/docker-compose` and failed with `error getting credentials ... docker-credential-desktop not found`; worked with `DOCKER_CONFIG=<empty dir>`. Not known whether Windows + Podman Desktop behaves; still to confirm with Ilona's/the learner's machine.
- Not verified: Windows/Podman Desktop; `podman compose down` without `-v` keeping the data (stated in Reflect as a question for the learner, not run); H2-vs-Postgres differences beyond this schema; concurrency claims in Reflect.
