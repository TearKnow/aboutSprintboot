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

## 4. Maven 依赖 scope

| scope | 编译 | 运行 | 打进 jar | 典型场景 |
|-------|------|------|----------|----------|
| **compile**（默认） | ✅ | ✅ | ✅ | Spring、大部分依赖 |
| **provided** | ✅ | 环境自带 | ❌ | Servlet API（外部 Tomcat 部署） |
| **runtime** | ❌ | ✅ | ✅ | JDBC 驱动（如 MySQL） |
| **test** | 仅测试 | 仅测试 | ❌ | JUnit |

**怎么区分 provided 和 runtime？**

- **provided**：源码里**直接 import** 这个 jar 的类，但运行时由服务器/JDK 提供，不必打进包。
  - 例：WAR 部署到 Tomcat 时用 `jakarta.servlet.http.HttpServletRequest`

- **runtime**：源码里**不 import 该依赖 jar 里的类**，运行时才加载。

**JDBC 驱动为什么是 runtime？（import 疑问）**

代码里看起来有 import，但 import 的是 **JDK 自带** 的类，不是 MySQL jar 里的类：

```java
import java.sql.Connection;      // JDK 自带，编译时不需要 MySQL jar
import java.sql.DriverManager;   // JDK 自带，编译时不需要 MySQL jar

Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/db", user, pass
);
// 不会出现：import com.mysql.cj.jdbc.Driver;
```

| 类 | 来自 | 编译时需要 |
|----|------|-----------|
| `Connection`、`DriverManager` | JDK（`java.sql.*`） | JDK 即可 |
| `com.mysql.cj.jdbc.Driver` | `mysql-connector-j.jar` | 不需要出现在源码里 |

运行时 `DriverManager.getConnection()` 会根据 URL 里的 `jdbc:mysql://` 自动从 classpath 加载 MySQL 驱动（SPI 机制），所以 MySQL jar **运行时必须**，**编译时不必** → scope 用 **runtime**。

**快速判断：**

```
源码里直接 import 这个依赖的类？
├─ 是 → 编译需要
│      └─ 运行时环境自带？→ provided；自己带？→ compile
└─ 否 → 运行才需要 → runtime
```

Spring Boot 内嵌 Tomcat 的项目，大部分依赖用默认 **compile** 即可。
