# sprintboot-init

Spring Boot 入门项目，端口 `38888`。

## 1. 启动

点击 `SprintbootInitApplication` 的 `main` 旁绿色按钮，或执行 `mvn spring-boot:run`。

## 2. DevTools 热重启

要使用这一开发者功能，只需在 `pom.xml` 添加如下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

改代码保存后会自动重启（需先编译到 `target/classes`）。

IDE 需开启自动编译（IntelliJ）：

1. **Settings → Compiler** → 勾选 **Build project automatically**
2. **Settings → Advanced Settings** → 勾选 **Allow auto-make to start even if developed application is currently running**

未生效时可手动 **Ctrl+F9** 编译。

## 3. 多环境配置（Profile）

公共配置放在 `application.yaml`，环境差异放在 `application-{profile}.yaml`（如 `application-dev.yaml` 里配置端口）。

不同电脑端口不同时，无需改代码，在 IntelliJ 启动时指定 profile 即可：

**方式一：Active profiles（推荐）**

配置入口：**Run → Edit Configurations → 选中 SprintbootInitApplication → Active profiles**

```
dev
```

**方式二：VM options**

配置入口：**Run → Edit Configurations → 选中 SprintbootInitApplication → Modify options → Add VM options**

```
-Dspring.profiles.active=dev
```

两种方式效果相同，加载对应配置文件（如 `application-dev.yaml`）。
