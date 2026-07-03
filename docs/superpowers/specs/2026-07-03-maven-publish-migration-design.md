# Maven Publish Migration Design

## Context

The project uses Gradle 8.7 and Android Gradle Plugin 8.6.1. The existing publishing script, `maven-push-release.gradle`, applies the legacy `maven` plugin and configures publishing through `uploadArchives`, `mavenDeployer`, and `MavenDeployment`. That plugin path is no longer supported by the current Gradle version.

The library module has this publishing script referenced from `chat-uikit/build.gradle`, but the line is currently commented:

```gradle
//apply from: "../maven-push-release.gradle"
```

That commented state is intentional. Other release scripts control whether publishing is enabled by toggling or applying this script. The migration must not change that switch behavior.

## Goals

- Replace the legacy `maven` plugin usage with Gradle's `maven-publish` plugin.
- Keep the existing Maven coordinates:
  - groupId: `io.agora.rtc`
  - artifactId: `chat-uikit`
  - version: `project.sdkVersion`
- Publish the Android release AAR from the `release` component.
- Keep sources and javadoc artifacts.
- Keep current POM metadata.
- Keep current OSSRH credential and signing property sources.
- Use standard `maven-publish` task names only, without adding an `uploadArchives` compatibility alias.
- Leave `chat-uikit/build.gradle` publishing switch behavior unchanged.

## Non-Goals

- Do not migrate credentials to environment variables or Gradle properties.
- Do not introduce a convention plugin, `buildSrc`, or publishing module abstraction.
- Do not change the Nexus staging plugin setup beyond what is required by this script.
- Do not force-enable publishing from `chat-uikit/build.gradle`.
- Do not change dependency versions or Android build configuration.

## Proposed Implementation

Update `maven-push-release.gradle` so it applies:

```gradle
apply plugin: 'maven-publish'
apply plugin: 'signing'
```

The script will continue to set:

```gradle
group = "io.agora.rtc"
archivesBaseName = "chat-uikit"
version = project.sdkVersion
```

Credential loading will remain compatible with the existing release environment:

1. Read `local.properties`.
2. Read `maven.dir` from `local.properties`.
3. Read `${maven.dir}/project.properties`.
4. Load `ossrhUsername`, `ossrhPassword`, `signing.keyId`, `signing.secretKeyRingFile`, and `signing.password` from that properties file.

Configure `publishing` after the Android release component exists. The `release` publication will:

- Use `from components.release`.
- Set `groupId`, `artifactId`, and `version`.
- Attach `sourcesJar`.
- Attach `javadocJar`.
- Define POM metadata equivalent to the legacy `pom.project` block.

Configure the Maven repository with the existing Sonatype URLs:

- Release URL: `https://oss.sonatype.org/service/local/staging/deploy/maven2/`
- Snapshot URL: `https://oss.sonatype.org/content/repositories/snapshots/`

The repository URL will be selected from the version suffix. Versions ending in `SNAPSHOT` publish to the snapshot repository; all other versions publish to the staging deploy repository.

Configure signing for the `release` publication:

```gradle
signing {
    sign publishing.publications.release
}
```

Signing properties will preserve the existing secret key ring file behavior, including resolving `signing.secretKeyRingFile` relative to `maven.dir`.

## Task Names

The migration intentionally adopts Gradle standard publishing tasks. Expected task names include:

- `generatePomFileForReleasePublication`
- `signReleasePublication`
- `publishReleasePublicationToOssrhRepository`
- `publish`

There will be no `uploadArchives` compatibility task.

## Error Handling

The script should fail clearly if publishing is enabled but the expected local publishing configuration is missing. Missing `local.properties`, missing `maven.dir`, or missing `project.properties` should surface as configuration errors during publishing setup, matching the current local-release assumption.

Normal development builds are unaffected while `apply from: "../maven-push-release.gradle"` remains disabled.

## Verification

After implementation:

1. Enable the publishing script in the same way existing release scripts do.
2. Run a Gradle task listing or dry configuration command that includes publishing tasks.
3. Confirm Gradle 8.7 no longer fails on `apply plugin: 'maven'`, `uploadArchives`, `mavenDeployer`, or `MavenDeployment`.
4. Confirm standard `maven-publish` tasks are registered.
5. If credentials and signing files are available, run the publish task expected by the release flow.

When publishing remains disabled in `chat-uikit/build.gradle`, normal project configuration should continue to work without requiring OSSRH or signing properties.
