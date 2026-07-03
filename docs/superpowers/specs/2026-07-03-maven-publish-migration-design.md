# Maven Publish 迁移设计

## 背景

当前工程使用 Gradle 8.7 和 Android Gradle Plugin 8.6.1。现有发布脚本 `maven-push-release.gradle` 依赖旧的 `maven` 插件，并通过 `uploadArchives`、`mavenDeployer`、`MavenDeployment` 配置发布流程。这个插件路径已经不被当前 Gradle 版本支持。

库模块在 `chat-uikit/build.gradle` 中保留了发布脚本引用，但这一行当前是注释状态：

```gradle
//apply from: "../maven-push-release.gradle"
```

这个注释状态是有意保留的。现有发布流程会通过其他脚本控制是否启用发布脚本。因此本次迁移不能改变这个开关行为。

## 目标

- 将旧的 `maven` 插件迁移为 Gradle 标准 `maven-publish` 插件。
- 保持现有 Maven 坐标：
  - groupId: `io.agora.rtc`
  - artifactId: `chat-uikit`
  - version: `project.sdkVersion`
- 发布 Android `release` component 产出的 AAR。
- 保留 sources 和 javadoc 附加 artifact。
- 保留现有 POM 元数据。
- 保留现有 OSSRH 凭据和签名配置来源。
- 只使用 `maven-publish` 标准任务名，不新增 `uploadArchives` 兼容别名。
- 保持 `chat-uikit/build.gradle` 中发布开关的行为不变。

## 非目标

- 不把凭据迁移到环境变量或 Gradle properties。
- 不引入 convention plugin、`buildSrc` 或新的发布抽象层。
- 不做 Nexus staging 插件相关重构，除非当前脚本必须调整。
- 不在 `chat-uikit/build.gradle` 中强制启用发布脚本。
- 不修改依赖版本或 Android 构建配置。

## 实现方案

更新 `maven-push-release.gradle`，改为应用：

```gradle
apply plugin: 'maven-publish'
apply plugin: 'signing'
```

脚本继续设置：

```gradle
group = "io.agora.rtc"
archivesBaseName = "chat-uikit"
version = project.sdkVersion
```

凭据读取方式保持兼容现有发布环境：

1. 读取 `local.properties`。
2. 从 `local.properties` 读取 `maven.dir`。
3. 读取 `${maven.dir}/project.properties`。
4. 从该 properties 文件中读取 `ossrhUsername`、`ossrhPassword`、`signing.keyId`、`signing.secretKeyRingFile`、`signing.password`。

在 Android release component 可用后配置 `publishing`。`release` publication 需要：

- 使用 `from components.release`。
- 设置 `groupId`、`artifactId`、`version`。
- 附加 `sourcesJar`。
- 附加 `javadocJar`。
- 定义与旧 `pom.project` 块等价的 POM 元数据。

Maven 仓库继续使用现有 Sonatype 地址：

- Release URL: `https://oss.sonatype.org/service/local/staging/deploy/maven2/`
- Snapshot URL: `https://oss.sonatype.org/content/repositories/snapshots/`

仓库 URL 按版本后缀选择。版本以 `SNAPSHOT` 结尾时发布到 snapshot 仓库；其他版本发布到 staging deploy 仓库。

签名配置改为签名 `release` publication：

```gradle
signing {
    sign publishing.publications.release
}
```

签名属性继续保持原来的 secret key ring file 方式，包括将 `signing.secretKeyRingFile` 按 `maven.dir` 进行路径拼接。

## 任务名

迁移后采用 Gradle 标准发布任务。预期任务包括：

- `generatePomFileForReleasePublication`
- `signReleasePublication`
- `publishReleasePublicationToOssrhRepository`
- `publish`

不会新增 `uploadArchives` 兼容任务。

## 错误处理

如果启用了发布脚本，但本地发布配置缺失，脚本应清晰失败。缺少 `local.properties`、缺少 `maven.dir` 或缺少 `project.properties` 时，应在发布配置阶段暴露为配置错误。这与当前脚本依赖本地发布配置的假设保持一致。

当 `apply from: "../maven-push-release.gradle"` 仍保持禁用时，普通开发构建不应要求 OSSRH 或签名属性，也不应受发布脚本影响。

## 验证

实现后需要验证：

1. 用现有发布脚本相同的方式启用 `maven-push-release.gradle`。
2. 运行 Gradle 任务列表或配置阶段命令，确认发布任务可注册。
3. 确认 Gradle 8.7 不再因为 `apply plugin: 'maven'`、`uploadArchives`、`mavenDeployer` 或 `MavenDeployment` 失败。
4. 确认标准 `maven-publish` 任务已注册。
5. 如果本地具备凭据和签名文件，再运行发布流程实际使用的 publish 任务。

当发布脚本在 `chat-uikit/build.gradle` 中保持禁用时，普通工程配置应继续正常工作，不需要 OSSRH 或签名配置。
