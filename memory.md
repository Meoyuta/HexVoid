# HexVoid 技术现状

更新：2026-10-21。

项目目标为将 HexVoidLegacy（Forge 1.20.1）迁移至 NeoForge 1.21.1。目前仅启用 neoforge 模块，业务源码与资源已迁入 NeoForge；Legacy 原目录未修改，common/Fabric 不承载业务功能。物品自定义状态组件统一位于 mys.hexvoid.components，ticklib 未引入。

构建使用 Gradle 8.12.1、JDK 21、Architectury Loom 1.14.476、NeoForge 21.1.172、Architectury API 13.0.11。映射为官方 Mojang + Parchment 2024.11.17 附加 layer。命令通过 IDEA MCP 的终端工具调用 pwsh，不使用 cmd。

HexCasting 已改为 Ivy 依赖：参考 https://github.com/loliking-01/hexextra ，固定 Jenkins build 64，坐标 at.petra-k:hexcasting-neoforge:1.21.1-0.12.0-devel-pre-64，使用 implementation。仓库在 neoforge/build.gradle 中受 exclusiveContent 限制；无 Ivy 传递依赖元数据，paucal、Patchouli、Caelus、Inline、KotlinForForge 等前置显式声明。该正式 jar 同时包含 Common 和 NeoForge 内容。

KotlinForForge 5.8.0 同样使用 implementation，保留 NeoForge 原始发布包及其嵌套 Kotlin 库。modImplementation 重映射该包会导致开发启动报 kotlin/jvm/internal/Intrinsics 缺失；已用实际 runData 和客户端启动验证修复。

includeBuild、dependencySubstitution、.gitmodules 和 hexmod gitlink 已移除。本地 hexmod 目录保留并忽略，其三个构建文件存在旧修改；该目录不参与同步或编译。ticklib 未引入本项目。

数据生成：:neoforge:runData 输出 neoforge/src/generated/resources，已接入 main 资源源集；生成器负责的 22 个数据文件只保留生成目录中的版本，.cache 不入 Git/发布包。手写配方/进度采用 1.21.1 的 recipe/advancement 单数目录，ItemStack JSON 使用 id 字段。

IDEA 旧 Java 运行配置曾保存 ASM 9.10.1 模块路径，与 Gradle 的 ASM 9.7 冲突。Loom 的 ideaSyncTask 不覆盖已存在配置的启动参数；已将 NeoForge 三个配置备份至 build/idea-run-config-backup，移除旧文件后执行 :neoforge:ideaSyncTask 重新生成。该任务只生成 Loom 开发启动文件，不等于 IDEA Gradle 模型同步。IDEA MCP 当前未提供同步项目工具。

验证：IDEA Gradle runData 配置成功（四个 provider、22 个文件），Gradle runClient 完成模组初始化、资源重载和配方/进度解析；IDEA 服务端 Java 配置通过引导并用 --initSettings 写出 build/run-server-smoke 下的配置。未接受 EULA、未启动服务端世界；initSettings 返回后驻留的测试 JVM 已定向结束。完整 :neoforge:build --no-daemon 成功，日志 build/neoforge-run-fix-build.log；发布 jar 含全部生成 JSON 且内容一致，无重复资源、旧数据目录或缓存。51 个资源 JSON 解析及 IDEA build_project 通过。

验证范围：没有测试源码；具体法术、进度触发及世界运行尚未测试。Gradle 9 不兼容弃用提示尚在，属于当前构建工具链的后续升级事项。每步执行结果详见 plan.md。

IDEA 源码导航：Jenkins CI 64 的 sources jar 只覆盖 NeoForge Java，不能提供 Common 的 `GuiSpellcasting.kt`。NeoForge build.gradle 固定 GitHub HexMod 1.21 提交 `7d5c1576c6bd1044bfe438ad8a8cc7e39529ccb4`，`prepareHexcastingIdeaSources` 下载并解包 Common/NeoForge Java 源目录至项目根 `ide-sources/hexcasting`，通过 `idea.module.sourceDirs` 关联；不进入 Gradle 编译源集。该目录被 `.gitignore` 忽略，普通 `gradlew clean` 不会删除，删除后运行 `:neoforge:ideaSyncTask` 或 `:neoforge:idea` 会重新下载。
