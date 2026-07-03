# Maven Publish Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `maven-push-release.gradle` 从旧 `maven` 插件迁移到 Gradle 标准 `maven-publish`，同时保留现有发布开关、坐标、POM、sources/javadoc、OSSRH 凭据和签名配置来源。

**Architecture:** 继续把发布逻辑集中在根目录 `maven-push-release.gradle` 中，由 `chat-uikit/build.gradle` 的 `apply from` 引用控制是否启用。脚本应用 `maven-publish` 和 `signing`，用 Android `components.release` 创建 `release` publication，并配置 Sonatype 仓库、POM 元数据和 publication 签名。

**Tech Stack:** Gradle 8.7、Android Gradle Plugin 8.6.1、Groovy Gradle DSL、`maven-publish`、`signing`、Android library release component。

## Global Constraints

- 保持 Maven 坐标：groupId `io.agora.rtc`，artifactId `chat-uikit`，version `project.sdkVersion`。
- 不新增 `uploadArchives` 兼容任务。
- 不修改 `chat-uikit/build.gradle` 中 `//apply from: "../maven-push-release.gradle"` 的发布开关状态。
- 不迁移凭据来源，继续读取 `local.properties` 的 `maven.dir` 和 `${maven.dir}/project.properties`。
- 不修改依赖版本、Android 构建配置或普通开发构建行为。
- 发布脚本未启用时，普通 Gradle 配置不能要求 OSSRH 或签名配置。

---

## File Structure

- Modify: `maven-push-release.gradle`
  - 唯一实现文件。
  - 负责应用 `maven-publish`/`signing`、创建 sources/javadoc jar、配置 `publishing`、配置 POM、配置 signing。
- Read-only: `chat-uikit/build.gradle`
  - 保持第 58 行注释状态不变。
  - 用于验证发布脚本仍由外部流程控制启用。
- Read-only: `docs/superpowers/specs/2026-07-03-maven-publish-migration-design.md`
  - 作为实现约束来源。

### Task 1: 迁移发布脚本到 `maven-publish`

**Files:**
- Modify: `maven-push-release.gradle`
- Read-only: `chat-uikit/build.gradle`

**Interfaces:**
- Consumes: `project.sdkVersion`、`project.stagingRepositoryId`、`local.properties` 的 `maven.dir`、`${maven.dir}/project.properties` 中的 OSSRH 和 signing 属性。
- Produces: Gradle `release` publication、`sourcesJar`、`javadocJar`、`publishReleasePublicationToOssrhRepository`、`generatePomFileForReleasePublication`、`signReleasePublication`。

- [ ] **Step 1: 记录普通构建基线**

Run:

```bash
./gradlew :chat-uikit:tasks --all
```

Expected:

```text
BUILD SUCCESSFUL
```

同时确认输出中不需要 OSSRH 或 signing 配置，因为 `chat-uikit/build.gradle` 仍未启用发布脚本。

- [ ] **Step 2: 替换 `maven-push-release.gradle` 内容**

将 `maven-push-release.gradle` 替换为以下内容：

```gradle
group = "io.agora.rtc"
archivesBaseName = "chat-uikit"
version = project.sdkVersion //发布aar的库版本

apply plugin: 'maven-publish'
apply plugin: 'signing'

def repositoryId = project.stagingRepositoryId

def localProperties = new Properties()
localProperties.load(project.rootProject.file('local.properties').newDataInputStream())

Properties properties = new Properties()
def mavenDir = localProperties.getProperty('maven.dir')
properties.load(file(mavenDir + "./project.properties").newDataInputStream())

def ossrhUsername = properties.getProperty('ossrhUsername')
def ossrhPassword = properties.getProperty('ossrhPassword')

def signingKeyId = properties.getProperty("signing.keyId")
def signingKeyRingFile = mavenDir + properties.getProperty("signing.secretKeyRingFile")
def signingPassword = properties.getProperty("signing.password")

ext."signing.keyId" = signingKeyId
ext."signing.secretKeyRingFile" = signingKeyRingFile
ext."signing.password" = signingPassword

tasks.register('androidJavadoc', Javadoc) {
    options {
        encoding 'UTF-8'
        charSet 'UTF-8'
    }
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
    android.libraryVariants.all { variant ->
        if (variant.name == 'release') {
            owner.source = variant.javaCompileProvider.get().source
            owner.classpath = files(android.bootClasspath.join(File.pathSeparator))
            owner.classpath += variant.javaCompileProvider.get().classpath
        }
    }
    failOnError false
}

tasks.register('javadocJar', Jar) {
    dependsOn tasks.named('androidJavadoc')
    archiveClassifier.set('javadoc')
    from tasks.named('androidJavadoc').map { it.destinationDir }
}

tasks.register('sourcesJar', Jar) {
    archiveClassifier.set('sources')
    from android.sourceSets.main.java.srcDirs
}

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release

                groupId = project.group
                artifactId = project.archivesBaseName
                version = project.version

                artifact tasks.named('sourcesJar')
                artifact tasks.named('javadocJar')

                pom {
                    name = project.archivesBaseName
                    packaging = 'aar'
                    description = 'Agora SDK UIKit'
                    url = 'http://maven.apache.org'

                    scm {
                        connection = 'scm:git:https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android.git'
                        developerConnection = 'scm:git:https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android.git'
                        url = 'https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android'
                    }

                    licenses {
                        license {
                            name = 'AGORA SDK License'
                            url = 'https://github.com/AgoraIO/full-sdk/blob/master/LICENSE'
                        }
                    }

                    developers {
                        developer {
                            name = 'agorabuilder'
                            email = 'zhaoliang@agora.io'
                            organization = 'https://github.com/AgoraIO'
                            url = 'https://www.agora.io/cn'
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = 'Ossrh'
                url = version.toString().endsWith('SNAPSHOT')
                        ? uri('https://oss.sonatype.org/content/repositories/snapshots/')
                        : uri('https://oss.sonatype.org/service/local/staging/deploy/maven2/')
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    signing {
        sign publishing.publications.release
    }
}

if (project.plugins.hasPlugin('io.codearte.nexus-staging')) {
    nexusStaging {
        packageGroup = "$group"
        stagingProfileId = "$group"
        username = ossrhUsername
        password = ossrhPassword
        stagingRepositoryId = repositoryId
    }
}
```

- [ ] **Step 3: 检查未误改发布开关**

Run:

```bash
sed -n '50,70p' chat-uikit/build.gradle
```

Expected includes:

```gradle
//apply from: "../maven-push-release.gradle"
```

- [ ] **Step 4: 验证普通构建仍不读取发布凭据**

Run:

```bash
./gradlew :chat-uikit:tasks --all
```

Expected:

```text
BUILD SUCCESSFUL
```

Expected negative checks:

```text
No error mentioning missing local.properties
No error mentioning missing maven.dir
No error mentioning missing project.properties
```

- [ ] **Step 5: 临时启用发布脚本并验证任务注册**

临时将 `chat-uikit/build.gradle` 最后一行从：

```gradle
//apply from: "../maven-push-release.gradle"
```

改为：

```gradle
apply from: "../maven-push-release.gradle"
```

然后运行：

```bash
./gradlew :chat-uikit:tasks --all
```

Expected includes:

```text
generatePomFileForReleasePublication
signReleasePublication
publishReleasePublicationToOssrhRepository
publish
```

Expected not includes:

```text
uploadArchives
```

- [ ] **Step 6: 临时启用状态下验证 POM 生成任务**

Run:

```bash
./gradlew :chat-uikit:generatePomFileForReleasePublication
```

Expected:

```text
BUILD SUCCESSFUL
```

Expected file:

```text
chat-uikit/build/publications/release/pom-default.xml
```

Expected POM coordinates:

```xml
<groupId>io.agora.rtc</groupId>
<artifactId>chat-uikit</artifactId>
```

- [ ] **Step 7: 恢复发布开关为注释状态**

将 `chat-uikit/build.gradle` 最后一行恢复为：

```gradle
//apply from: "../maven-push-release.gradle"
```

Run:

```bash
git diff -- chat-uikit/build.gradle
```

Expected:

```text
no output
```

- [ ] **Step 8: 最终验证发布脚本中不再包含旧 API**

Run:

```bash
rg -n "apply plugin: 'maven'|uploadArchives|mavenDeployer|MavenDeployment|signPom|configurations\\.archives|deployerJars" maven-push-release.gradle
```

Expected:

```text
no matches
```

- [ ] **Step 9: 提交迁移实现**

Run:

```bash
git status --short
git add maven-push-release.gradle
git commit -m "build: migrate release publishing to maven-publish"
```

Expected commit contains only:

```text
maven-push-release.gradle
```

### Task 2: 最终回归验证

**Files:**
- Read-only: `maven-push-release.gradle`
- Read-only: `chat-uikit/build.gradle`

**Interfaces:**
- Consumes: Task 1 产出的 `maven-push-release.gradle`。
- Produces: 最终验证结果，证明普通构建不受影响，发布脚本迁移完成。

- [ ] **Step 1: 确认工作区只剩预期状态**

Run:

```bash
git status --short
```

Expected:

```text
no output
```

- [ ] **Step 2: 验证普通开发任务**

Run:

```bash
./gradlew :chat-uikit:tasks --all
```

Expected:

```text
BUILD SUCCESSFUL
```

Expected negative checks:

```text
No publish tasks from maven-publish are required while apply from remains commented
No OSSRH or signing property errors
```

- [ ] **Step 3: 验证最终脚本差异摘要**

Run:

```bash
git show --stat --oneline HEAD
```

Expected includes:

```text
build: migrate release publishing to maven-publish
maven-push-release.gradle
```

- [ ] **Step 4: 记录无法执行的外部发布验证**

如果本地没有真实 OSSRH 凭据或签名文件，不执行远程 publish。最终汇报中明确说明只验证到任务注册和 POM 生成，未向 Sonatype 上传。
