# HexVoid 1.21.1 迁移计划

依据 `rules.md`、`plan_temple.md` 与用户最新要求执行：放弃 git submodule/includeBuild，参考 hexextra 通过 Ivy 仓库导入 HexCasting，成功后再把 `HexVoidLegacy` 迁移到 NeoForge。业务实现只放入 NeoForge，不迁入 common/Fabric；Architectury 保留依赖兼容所需部分；不使用 ticklib，改用 Minecraft 1.21.1 原版 tick 实现。每完成一个步骤立即更新本文件。构建及其他高耗时验证安排在最后。

## 步骤

- [已完成] 1. 核对规则、计划模板、项目状态，清理三个模块的旧源码。
- [已完成] 2. 完成 HexCasting Ivy 依赖接入（本步骤替换此前复合构建方案，仍属同一轮问题）。
  - [已完成] 2.1 读取 hexextra 的 Ivy 配置和版本，确认上一轮 Gradle 进程已退出。
  - [已完成] 2.2 核验固定 CI 构建产物及前置依赖，保留 Mojang + Parchment layer。
  - [已完成] 2.3 移除 includeBuild/子模块注册，保留本地 HexMod 修改；配置受限 Ivy 仓库和发布 jar 依赖。
  - [已完成] 2.4 最后解析编译/运行类路径，检查 HexCasting API、NeoForge 实现与 mod 元数据均来自 Ivy jar。
  - [已完成] 2.5 在空业务源码状态执行编译与 API 探针验证，成功后移除临时探针，为后续迁移准备环境。
- [已完成] 3. 盘点 Legacy 源码、资源和构建元数据，按注册、施法、物品/方块、配方、Mixin、数据生成、客户端逻辑分组；标记所有 CompoundTag 持久化状态。
- [已完成] 4. 迁移 NeoForge Java 实现。
  - [已完成] 4.1 迁移主入口、事件与内容注册。
  - [已完成] 4.2 迁移 Hexcasting 图案、施法环境、法术和辅助逻辑。
  - [已完成] 4.3 迁移物品、方块、伤害类型、配方与战利品逻辑。
  - [已完成] 4.4 迁移 Mixin 与客户端交互，按 1.21.1 API 调整签名。
- [已完成] 4.5 将物品自定义状态迁移至 DataComponent；新增组件集中在 `mys.hexvoid.components` 包，移除 ticklib 用法并改用原版 tick 事件/调度能力。
- [已完成] 5. 迁移 NeoForge 资源、数据与元数据，检查 mod id、依赖声明、Mixin 配置和数据格式。
- [已完成] 6. 完成代码/资源一致性检查，并按需修复迁移问题。
  - [已完成] 6.1 修复 NeoForge 与 HexCasting 1.21.1 API 签名和编译错误。
  - [已完成] 6.2 将迁移源码中可替换的已弃用 HexCasting、Patchouli 和 Minecraft 附魔 API 改为当前接口。
  - [已完成] 6.3 保留已迁移的数据资源，移除旧 AdvancementProvider 生成器及其注册。
  - [已完成] 6.4 核对物品状态 DataComponent 包位置、HexCasting Ivy 依赖、Parchment layer 和无 ticklib 引用。
- [已完成] 7. 最后执行 NeoForge Gradle build，记录结果并更新计划。

## 执行记录

- 方案调整（2026-10-20）：用户要求放弃 git submodule，改为参考 `https://github.com/loliking-01/hexextra` 的 Ivy 仓库。步骤 2 的旧复合构建方案停止；以下历史记录仅解释先前状态，不代表现用配置。
- 步骤 2.1：IDEA MCP 正常；通过 IDEA 终端使用 pwsh 读取 hexextra，固定 CI build=64、版本=1.21.1-0.12.0-devel-pre-64，仓库是 BlameJared Jenkins 的 Neoforge/build/libs。未发现残留 Java 进程。本地 hexmod 的 Common/Fabric/Neoforge 构建脚本有修改，解除注册时保留目录内容。
- 步骤 2.2：Jenkins build 64 状态 SUCCESS，正式 jar 同时包含 RenderedSpell、CastingEnvironment、ForgeHexInitializer、Xplat 服务注册和 META-INF/neoforge.mods.toml，Manifest 版本为 0.12.0-devel-pre-64；SHA-256 为 702C2F98160D73975C4535A547ED94AB5C29A496F0E77DC468CE363B198095F8。Ivy 无传递元数据，现有 paucal/Patchouli/Caelus/Inline/KotlinForForge 等依赖保留显式声明。
- 步骤 2.3：已移除 settings.gradle 中的 includeBuild 和替换规则；删除 .gitmodules 并从 Git 索引解除 hexmod gitlink，仅对该路径使用 --cached，保留本地 checkout 与修改并加入忽略。NeoForge 使用受 exclusiveContent 限制的 Ivy 仓库与 implementation at.petra-k:hexcasting-neoforge:1.21.1-0.12.0-devel-pre-64；保留 Mojang + Parchment 映射。
- 步骤 2.4 调整：初次使用 modImplementation 在 Loom 的 mod 重映射阶段失败。完整堆栈显示 Namespace mismatch, expected mojang got named，触发点为发布 jar 的 access widener。改按 hexextra 的 implementation 方式直接消费 NeoForge 1.21.1 的 Mojang 命名发布包；正在重新验证编译/运行 classpath 和 API 编译。
- 步骤 2.4 完成：已移除根 build.gradle 遗留的 Common 坐标。verifyHexcastingIvy 成功，compileClasspath/runtimeClasspath 均仅解析到 Gradle Ivy 缓存的 hexcasting-neoforge-1.21.1-0.12.0-devel-pre-64.jar，包含 Common API、NeoForge 入口、mod 元数据及 Xplat 服务；不再解析本地 hexmod 工程。
- 步骤 2.5 完成（2026-10-20）：JDK 21 下执行 :neoforge:verifyHexcastingIvy、:neoforge:compileJava、:neoforge:compileHexcastingApiProbe，20 秒内 BUILD SUCCESSFUL。探针实际引用 RenderedSpell、CastingEnvironment.getWorld()、ForgeHexInitializer 与 ServerLevel，验证 HexCasting 和 Minecraft 类型可共同编译；临时探针/脚本已移除，验证日志保留于 build/ivy-validation.log。IDEA MCP build_project 对改动的构建文件返回 isSuccess=true、problems=[]；没有声称 IDE Gradle 模型已同步或游戏已启动。

- 步骤 1（2026-10-19）：IDEA MCP 连接正常并成功读取项目规则、模板、配置；`sync_project` 工具未提供，故不宣称已由 MCP 同步。检查到一个 Gradle daemon 进程，无活动构建；三个模块的旧 `src` 已清空，HexVoidLegacy 与 hexmod 内容保留。
- 步骤 2.1-2.2：根设置使用 `includeBuild('hexmod')` 并替换 Hexcasting Common/NeoForge 模块坐标；根构建已恢复 Mojang + Parchment layered mappings，NeoForge 为唯一启用模块，使用 JDK 21 的 pwsh wrapper。
- 步骤 2.3 进度：NeoForge build 声明了 HexMod Common 与 NeoForge 依赖。先前诊断只发现 NeoForge 输出目录，缺少 Common API；当前声明尚未重新验证，故 2.4、2.5 仍待执行。
- 步骤 2.4 进度（2026-10-19）：用 JDK 21 成功运行离线 `:neoforge:dependencies --configuration compileClasspath`；报告将 Common 与 NeoForge 两个 Hexcasting 坐标分别替换为 `project :hexmod:Common`、`project :hexmod:Neoforge`。具体解析文件与 NeoForge mod 启动配置仍待核实。
- 步骤 3-5：尚未迁移 Legacy；之前临时复制的 44 个 Java 文件已撤回，Legacy 原目录保持不变。
- 2026-10-21：按用户要求删除 neoforge/build.gradle 第 7-8 行的无效注释，开始第 3 步。原有源码注释非必要不增删；自定义 DataComponent 统一放在 components 包。
- 步骤 3 完成（2026-10-21）：Legacy 含 44 个 Java 文件，分为入口/注册、6 个施法动作与 VM、物品/方块/配方、9 个 Mixin、数据生成和客户端辅助；另有手写资源及生成数据。未发现 ticklib 引用。`MindStaffItem` 的储能、最大储能、可储能标志，以及编织标志属于物品持久化状态，迁移到 DataComponent；法术参数、玩家持久数据和配方临时 NBT 按各自 1.21.1 API 处理。Legacy 原目录保留。
- 步骤 4.1 完成：Legacy Java 源码复制到 NeoForge 模块，入口、命令事件、创意标签及基础内容注册切换至 NeoForge 1.21.1 API；后续分组继续调整其依赖类型，最终统一编译验证。
- 步骤 4.2 完成：图案解析切换 `fromAngleString`，六个法术移除已废弃的 userdata 接口，清除图案从 HexCasting 当前 VM/平台接口读取；治疗与编织效果延后到媒质验证后，爆炸参数改为每次施法独立保存。待统一编译及运行验证。
- 步骤 4.3 完成：NeoForge 注册的物品、方块、伤害和战利品运行时逻辑已迁移；编织配方切换到 1.21.1 `MapCodec`/`StreamCodec`，Patchouli 查询改用 `RecipeHolder`。物品状态组件实现随步骤 4.5 收尾，数据生成器留到步骤 5。
- 步骤 4.4 完成：9 个 Mixin 已迁入，附魔名称、等级上限、击退、法杖 VM 和客户端 GUI 注入按 1.21.1 签名调整；移除已废弃的 ItemStack/EnchantmentHelper NBT 附魔注入逻辑。运行时 Mixin 生效情况待后续运行检查。
- 步骤 4.5 完成：法杖储能/最大储能/可储能标志和苹果编织标志迁至 `mys.hexvoid.components.HexvoidComponents`；更新组件注册、物品读写及描述。Legacy 不含 ticklib 引用，故无 ticklib 逻辑需要替换。
- 步骤 5 完成：迁入 Legacy 手写和生成资源，NeoForge 元数据替代 Forge `mods.toml`、移除 ticklib 声明并登记 HexCasting/NeoForge/Minecraft 依赖；Mixin 声明 Java 21 并加入 NeoForge 元数据，资源 pack format 升至 48；全局战利品 modifier 路径、条件名称和 DataComponent 配方输出已更新。代码生成器已切换到 NeoForge/1.21.1 API，待最终构建验证其签名。
- 步骤 6 完成（2026-10-21）：修复 NeoForge 创意物品接收、AdvancementHolder 查找、`ItemStack.hurtAndBreak`、HexCasting `OperatorUtils.getEntity` 参数顺序、damage bootstrap、loot modifier MapCodec、Patchouli registry-aware 变量、物品属性、附魔 DataComponent 等 1.21.1 API 调用；移除 HexCasting SpellAction/RenderedSpell 旧 DefaultImpls 覆盖。IDEA lint 分批检查 33 个文件，没有剩余错误或已弃用方法警告；保留弱警告和原有未使用字段。PowerShell `ConvertFrom-Json -AsHashtable` 成功验证 51 个资源 JSON。未发现 `net.minecraftforge` API、ItemStack `getOrCreateTag` 用法或 ticklib 引用；自定义 DataComponent 均位于 `mys.hexvoid.components`。
- 步骤 7 完成（2026-10-21）：IDEA MCP `build_project(rebuild=true)` 成功；JDK 21 下 `:neoforge:build --no-daemon` 成功，完成 compileJava、资源处理、jar/remapJar/sourcesJar/remapSourcesJar，`BUILD SUCCESSFUL`。该模块当前没有测试源码（`test NO-SOURCE`）；未执行游戏启动测试。Gradle 仍报告插件/脚本使用的 Gradle 9 不兼容弃用特性，该提示与源码 API 检查分开记录。

## 本轮问题：NeoForge 运行与数据生成任务不可用

- [已完成] 8. 核对 IDEA MCP 连接、规则、Gradle 配置和现有运行配置；确认当前没有待执行的关机任务。
- [已完成] 9. 复现 `:neoforge:runData` 失败，区分任务配置、运行依赖和模组初始化问题。
- [已完成] 10. 根据日志修复运行配置与依赖接入，检查客户端/服务端共用启动路径。
- [已完成] 11. 修复数据生成器的运行错误及生成资源接入，保留原有业务行为与注释。
- [已完成] 12. 最后实际执行 runData、核对生成文件并验证完整 build；检查其他运行任务的启动结果。
  - [已完成] 12.1 从 IDEA 的 Gradle runData 配置复测，确认四个 provider 和 22 个数据文件。
  - [已完成] 12.2 从 IDEA 的 Gradle runClient 配置启动，检查模组加载、资源重载与配方/进度解析。
  - [已完成] 12.3 修复 IDEA Java 运行配置中的 ASM 版本冲突，再在 build 下独立目录执行服务端 --initSettings。
  - [已完成] 12.4 执行完整 Gradle build，核对发布 jar 的资源路径、生成文件和缓存排除。
- [已完成] 13. 更新技术现状文档与计划，暂存本轮必要改动，记录未覆盖的运行验证范围。

### 本轮执行记录

- 步骤 8：IDEA MCP 可用，`runData` 与 Client/Server 配置存在；此前只验证了 build，没有验证开发启动。`shutdown /a` 返回 1116，表示当前没有待执行的关机任务。
- 步骤 9：JDK 21 实际启动 runData，在 KotlinLanguageLoader 加载阶段出现 `NoClassDefFoundError: kotlin/jvm/internal/Intrinsics`。KotlinForForge 被 modImplementation 重映射后失去嵌套库的正常加载；正在改用原始 NeoForge 发布包验证。两个本项目失败后仍驻留的 runData Java 进程已定向结束，释放日志锁；其他 Java 进程保留。
- 步骤 10：KotlinForForge 改为 `implementation`，NeoForge 已发现原包中的 Kotlin stdlib/reflect/coroutines/serialization 嵌套库，Kotlin loader 与 HexVoid 注册正常。实际 `:neoforge:runData` 返回 BUILD SUCCESSFUL，四个 provider 写出 22 个数据文件（日志 `build/runData-fixed.log`）。检查产物另发现 src/generated/resources 未加入资源源集，main 资源仍有旧版 recipes/advancements 路径，步骤 11 一并修复资源接入。
- 步骤 11：将 src/generated/resources 接入 main 资源源集并排除 .cache；22 个生成器负责的数据从 main 移除重复副本。其余手工配方/进度迁至 1.21.1 的 recipe/advancement 目录，并将结果/进度图标 ItemStack 字段改为 id；没有更改物品、媒质或掉落业务数值。生成缓存加入 Git 忽略规则。
- 步骤 12.1：IDEA 配置 HexVoid [:neoforge:runData] 实际返回 BUILD SUCCESSFUL（29 秒）；四个 provider 完成，22 个文件保持稳定，二次生成没有内容变化。
- 步骤 12.2：IDEA 配置 HexVoid [:neoforge:runClient] 完成 KotlinForForge/HexVoid 初始化、资源重载及 1430 个配方、1551 个进度解析；无启动崩溃。测试客户端通过 CloseMainWindow 正常关闭；未验证具体法术及进度触发行为。
- 步骤 12.3 诊断：IDEA 的 Minecraft Server (:neoforge) Java 配置委托 Gradle 执行时，保存的模块路径使用 ASM 9.10.1，而 Gradle 类路径为 ASM 9.7，BootstrapLauncher 因同名模块位置冲突退出；正在重新生成 Loom IDEA 运行配置。此问题与已成功的 Gradle runClient/runData 分开验证。
- 步骤 12.3 完成：核对 Loom IdeaSyncTask 源码，确认只为不存在的配置写入启动参数。将 NeoForge 三个旧配置备份至 build/idea-run-config-backup，重新生成后 ASM 模块路径已改为 9.7。IDEA 服务端 Java 配置已通过 NeoForge 模组发现/Mixin 引导并写出独立目录下的 server.properties 和 eula.txt；未接受 EULA，也未启动世界。--initSettings 返回后开发启动器线程池仍驻留，定向结束两个本轮测试 JVM；没有将此验证记为完整服务端运行或正常退出。
- 步骤 12.4 完成：与 IDEA 相同的 G:\.gradle 缓存、JDK 21 下执行 :neoforge:build --no-daemon，29 秒 BUILD SUCCESSFUL（日志 build/neoforge-run-fix-build.log）。发布 jar 的 22 个生成 JSON 与源文件逐一一致，手写 recipe/advancement 存在，没有重复 entry、旧复数目录或 .cache；共 51 个资源 JSON 解析通过。IDEA build_project 也通过；无测试源码，未覆盖世界运行和法术业务。Gradle 9 弃用提示仍来自构建工具链。
- 步骤 13 完成：更新 memory.md 的迁移现状、依赖加载方式、生成资源接入、IDEA 配置恢复方法和验证边界；暂存本轮 NeoForge 构建/数据资源、生成 JSON、plan.md，以及 .gitignore 中仅本轮新增的缓存忽略规则。其他已有暂存/未暂存改动保留，未创建提交；构建日志、生成缓存、IDEA 本地配置不暂存。本轮计划完成。

## 本轮问题：IDEA 无法跳转 HexCasting 源码

- [已完成] 14. 核对 Ivy/Jenkins 发布物与 GitHub 1.21 分支的源码覆盖范围。
- [已完成] 15. 配置 IDEA 专用 HexCasting 源码下载/解包目录，不将源码加入 NeoForge 编译源集。
- [已完成] 16. 重新生成 IDEA 模型并验证 `GuiSpellcasting` 等源码可见，记录网络或版本限制。

### 本轮执行记录

- 步骤 14：Jenkins CI 64 提供 `hexcasting-neoforge-...-sources.jar`，但仅含 NeoForge 侧 46 个 Java 文件；`GuiSpellcasting.kt`、`RenderedSpell.kt` 等 Common 源码不在其中。GitHub `FallingColors/HexMod` 的 `1.21` 分支 ZIP 可访问，包含 Common 与 NeoForge 源码。
- 步骤 15：在 `neoforge/build.gradle` 增加固定提交 `7d5c1576c6bd1044bfe438ad8a8cc7e39529ccb4` 的 HexMod 源码下载任务，解包 Common/NeoForge `src/main/java`（含 Kotlin 源文件）至项目根 `ide-sources/hexcasting`，并仅通过 IDEA `sourceDirs` 关联；该目录加入 `.gitignore`，不进入 Gradle `sourceSets.main`。
- 步骤 16：执行 `:neoforge:prepareHexcastingIdeaSources :neoforge:idea` 成功，生成 562 个源码文件（约 1.82 MB），`neoforge.iml` 写入绝对路径 source root；IDEA MCP 可读取 `GuiSpellcasting.kt` 的真实源码。执行 `:neoforge:clean` 后目录仍存在，随后 `:neoforge:ideaSyncTask` 保持成功；IDEA `build_project` 与清理后的 `:neoforge:compileJava` 均成功。网络不可用或完整 `git clean -fdx` 删除忽略目录时，运行 IDEA sync/idea 任务会按固定提交重新下载。

## 本轮问题：修复 GuiSpellcastingMixin 的局部物品判断

- [已完成] 17. 对照 HexCasting `GuiSpellcasting.tick` 的 `heldItem` 局部变量和 Legacy 关闭逻辑。
- [已完成] 18. 使用 `WrapWithCondition` 捕获 `heldItem`，替换旧的重复取手物品判断。
- [已完成] 19. 启动 NeoForge 客户端验证 Mixin 局部变量捕获及运行时注入。
- [已完成] 20. 更新计划并暂存本轮必要修改。

### 本轮执行记录

- 步骤 17：确认 HexCasting 1.21 `tick` 在 `closeForReal()` 前创建唯一 `ItemStack heldItem = player.getItemInHand(handOpenedWith)`；Legacy 逻辑允许 Mind Staff、木棍或 `HexTags.Items.STAVES`，虚弱心智仍强制关闭。
- 步骤 18：`GuiSpellcastingMixin` 改为 `@Local(ordinal = 0) ItemStack heldItem`，条件直接比较该局部变量；移除无用的 `handOpenedWith`/`closeForReal` Shadow 和注入参数。IDEA `build_project` 成功；静态检查仅有外部 Kotlin 目标解析提示及 `@Local` 建议，无编译错误。
- 步骤 19：通过 IDEA `HexVoid [:neoforge:runClient]` 启动 NeoForge 客户端；Mixin 子系统、MixinExtras、HexVoid 客户端初始化和资源加载均成功，没有局部变量捕获异常、注入失败或启动崩溃。测试 JVM 已定向结束。
- 步骤 20：本轮修改已完成，待暂存文件为 `GuiSpellcastingMixin.java` 与 `plan.md`；保留其他既有工作区和索引改动，不创建提交。
