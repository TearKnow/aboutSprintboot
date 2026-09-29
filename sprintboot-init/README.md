# sprintboot-init

Spring Boot 入门项目，端口 `38888`。

## 启动

点击 `SprintbootInitApplication` 的 `main` 旁绿色按钮，或执行 `mvn spring-boot:run`。

## DevTools 热重启

要使用这一开发者功能，只需在 `pom.xml` 添加如下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

改代码保存后会自动重启（需先编译到 `target/classes`）。

IDE 需开启自动编译（PhpStorm / IntelliJ）：

1. **Settings → Compiler** → 勾选 **Build project automatically**
2. **Settings → Advanced Settings** → 勾选 **Allow auto-make to start even if developed application is currently running**

未生效时可手动 **Ctrl+F9** 编译。
