![IKASAN](../docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Maven Archetypes
The basic constituents of an Ikasan Integration Module (IM) are the same. 
Due to this we have created some out of the box IM archetypes based on common technical integration problems.

- [DB (SQL) to JMS / JMS to DB (SQL) Integration Module Archetype](ikasan-standalone-db-jms-im/Readme.md)
- [Local File System to JMS / JMS to Local File System Integration Module Archetype](ikasan-standalone-filesystem-im/Readme.md)
- [FTP to JMS / JMS to FTP Integration Module Archetype](ikasan-standalone-ftp-jms-im/Readme.md)
- [Hibernate DB to JMS / JMS to Hibernate DB Integration Module Archetype](ikasan-standalone-hibernate-jms-im/Readme.md)
- [JMS to JMS Integration Module Archetype](ikasan-standalone-jms-im/Readme.md)
- [SFTP to JMS / JMS to SFTP Integration Module Archetype](ikasan-standalone-sftp-jms-im/Readme.md)
- [Vanilla Integration Module Archetype](ikasan-standalone-vanilla-im/Readme.md)

## Versions
The archetypes do not define their own dependency or plugin versions. All versions come from the
`ikasan-build` parent pom (`ikasaneip/pom.xml`):

- **Dependencies** - generated projects import `ikasan-eip-standalone-bom`, which inherits the
  `ikasan-build` dependency management, so the `jar` and `distribution` poms declare no versions.
- **Plugins and Java level** - the generated parent pom references `ikasan-build` properties such as
  `${version.maven-compiler-plugin}` and `${maven.compiler.source}`. These are filled in when the
  archetype is built.

To change a version used by generated projects, change it in `ikasan-build` and rebuild the archetypes.

## Testing the Archetypes
Each archetype has an integration test (`*ArchetypeIT`) that:

1. generates a new project from the freshly built archetype
2. builds the generated project with `mvn clean verify`, including its own unit and flow tests,
   the dependency convergence check and the distribution assembly
3. checks that the integration module jar and the distribution zip were produced

The tests are slow, roughly 1-4 minutes per archetype, so they are not part of the default build.
They run only when the `archetype-it` profile is enabled.

### Prerequisites
- **Maven 3.9 or later** - the nested builds use `maven.repo.local.tail` to read your normal local repository.
- **JDK 25** - the generated projects compile for Java 25.
- **Ikasan installed locally** - the generated projects depend on the current Ikasan snapshot, so build it
  first (or run the tests as part of a full build, see below):

```
cd ikasaneip
mvn clean install -DskipTests
```

### Run all archetype tests
From this directory (`ikasaneip/developer/mvn-archetype`):

```
mvn clean verify -Parchetype-it
```

Add `-fae` (fail at end) to run every archetype even if one fails, and `-Dmaven.javadoc.skip` to save a little time:

```
mvn -fae clean verify -Parchetype-it -Dmaven.javadoc.skip
```

### Run the tests for a single archetype
```
cd ikasan-standalone-jms-im
mvn clean verify -Parchetype-it
```

or from this directory:

```
mvn clean verify -Parchetype-it -pl ikasan-standalone-jms-im
```

### Run as part of a full Ikasan build
From `ikasaneip`:

```
mvn clean install -Parchetype-it
```

With the profile enabled, the archetype modules depend on the Ikasan artifacts the generated projects
use, so Maven builds those first.

### Offline builds
`-o` is passed through to the nested builds. This works once everything the generated projects
need has been downloaded to your local repository at least once.

### Where to look when a test fails
For an archetype module, for example `ikasan-standalone-jms-im`:

| Location | Contents |
|---|---|
| `target/failsafe-reports/` | Test result. The failure message includes the tail of the nested build output; the full output is in the `*-output.txt` / `TEST-*.xml` file. |
| `target/it-projects/<id>/` | The generated project, left in place so you can inspect it or rebuild it by hand with `mvn clean verify`. |
| `target/it-repo/` | Staging local repository holding the freshly built archetype and the Ikasan artifacts used by the nested builds. |

Do not build, clean or install the archetype modules, `ikasan-build` or `ikasan-mvn-util` from another
terminal while the tests are running. The nested builds work in `target/it-projects` and read from your
local repository, so concurrent changes can break them.

### How it works
- In `pre-integration-test`, the `maven-invoker-plugin` `install` goal stages the archetype and the
  Ikasan artifacts it needs into `target/it-repo`.
- The `maven-failsafe-plugin` runs the `*IT` tests. They use `ArchetypeBuildVerifier` from
  `ikasan-mvn-util` to run `archetype:generate` and then build the generated project with the
  Maven Invoker. `target/it-repo` is the local repository and your normal local repository is
  added as a read-only tail.
- A nested Maven build that does not succeed fails the test.

Both are configured in the `archetype-it` profile of this module's `pom.xml`.
