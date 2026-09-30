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

## 5. 多线程中断（学习笔记）

### 5.1 启动线程

```java
Thread t = new MyThread();
t.start();  // 启动新线程，JVM 自动调用 run()
// t.run(); // ❌ 错误：只在当前线程执行，不是多线程
```

### 5.2 中断是什么？

**中断 = 发「请停下来」的信号，线程必须配合检查才会停**（协作式，不是强制杀死）。

| 线程在干什么 | `interrupt()` 的效果 |
|-------------|---------------------|
| 循环空转 | 设置中断标志，`isInterrupted()` 返回 `true` |
| `sleep()` / `join()` / `wait()` | 立刻结束等待，抛出 `InterruptedException` |

```java
while (!isInterrupted()) {
    // 干活的循环
}
```

### 5.3 `interrupt()` 和 `join()` 的区别

| 方法 | 作用 |
|------|------|
| `t.interrupt()` | **通知** t 中断，发完就返回，不等待 |
| `t.join()` | **等待** t 的 `run()` 跑完 |

典型写法：先 `interrupt()` 通知停止，再 `join()` 等它收尾：

```java
t.interrupt();  // 1. 通知停止
t.join();       // 2. 等真正停完
System.out.println("end");
```

### 5.4 三层链式中断（教程经典例子）

```
main ──interrupt──→ t（MyThread）──interrupt──→ hello（HelloThread）
```

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new MyThread();
        t.start();
        Thread.sleep(1000);
        t.interrupt(); // main 通知 t 中断
        t.join();        // main 等 t 收尾
        System.out.println("end");
    }
}

class MyThread extends Thread {
    public void run() {
        Thread hello = new HelloThread();
        hello.start();
        try {
            hello.join(); // t 在这里等 hello
        } catch (InterruptedException e) {
            System.out.println("interrupted from main"); // t 被 main 中断
        }
        hello.interrupt(); // t 再去中断 hello
    }
}

class HelloThread extends Thread {
    public void run() {
        int n = 0;
        while (!isInterrupted()) {
            n++;
            System.out.println(n + " hello!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
```

流程：

1. t 启动 hello，自己堵在 `hello.join()` 上等 hello 结束
2. main `sleep` 后调用 `t.interrupt()`，t 从 `join()` 醒来，抛出 `InterruptedException`
3. t 在 `catch` 里收到信号，再调用 `hello.interrupt()` 通知 hello 停
4. hello 在 `sleep` 或循环里退出
5. t 结束 → main 的 `t.join()` 返回 → 打印 `end`

**为什么不写 `t.interrupt()` 会卡死？**  
t 永远堵在 `hello.join()`，hello 无限循环，main 永远等不到 t。

**`hello.join()` 为什么要 try-catch？**  
`join()` 声明了 `throws InterruptedException`（受检异常）；且 t 在等待中被 main 中断时，就靠这里捕获。

### 5.5 标志位停止 vs `interrupt()`

除了 `interrupt()`，也可以用共享标志位：

```java
public volatile boolean running = true;

while (running) { /* 干活 */ }

// 别的线程
t.running = false;
```

| | `interrupt()` | `volatile boolean` |
|---|--------------|-------------------|
| 机制 | JVM 标准中断 API | 自己定义标志 |
| 阻塞方法 | 能打断 `sleep`/`join` | 不能打断阻塞，只在循环检查时生效 |
| 可见性 | JVM 保证 | 必须加 `volatile`（或 `AtomicBoolean`） |

**注意：** 没写 `volatile` 有时测试也能停，但 Java 不保证可见性，生产环境可能死循环。测试能过 ≠ 写法正确。

### 5.6 快速对照

```
start()           → 启动线程
join()            → 等待线程结束（可被 interrupt 打断）
interrupt()       → 发中断信号
isInterrupted()   → 检查是否被中断（循环里用）
InterruptedException → 在 sleep/join/wait 中被中断时抛出
volatile          → 保证多线程读写标志位可见
```

## 6. synchronized 锁

多个线程同时改同一个变量（如 `count++`）会丢数据，因为 `count++` 实际是「读 → 加 1 → 写」三步，可能互相覆盖。

`synchronized` 保证：**同一时刻只有一个线程能执行被锁住的代码**。

### 6.1 两种写法

**写法一：加在方法上**

```java
public synchronized void increment() {
    count++;
}
```

等价于整个方法体被锁住。实例方法锁的是 `this`；静态方法锁的是类的 `Class` 对象。

**写法二：代码块（更灵活）**

```java
synchronized(Counter.lock) {
    Counter.count += 1;
}
```

可以指定锁哪个对象，也可以只锁需要保护的一小段代码。

### 6.2 经典例子：一增一减

```java
class Counter {
    public static final Object lock = new Object();
    public static int count = 0;
}

class AddThread extends Thread {
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized(Counter.lock) {
                Counter.count += 1;
            }
        }
    }
}

class DecThread extends Thread {
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized(Counter.lock) {
                Counter.count -= 1;
            }
        }
    }
}
```

两个线程各执行 10000 次，有加有减，最终 `count` 应为 **0**。不加锁则结果随机。

### 6.3 为什么要有 `lock` 变量？

因为 AddThread 和 DecThread 是**两个不同的类**，如果各自在 `run()` 上写 `synchronized`：

```java
class AddThread extends Thread {
    public synchronized void run() { ... }  // 锁的是 AddThread 的 this
}
class DecThread extends Thread {
    public synchronized void run() { ... }  // 锁的是 DecThread 的 this
}
```

两把不同的锁，**互不干扰**，`count` 仍然会乱。

必须让两个线程抢**同一把锁**：

```java
synchronized(Counter.lock) { ... }   // add 和 dec 都用这个
// 或
synchronized(Counter.class) { ... }  // 静态变量也可用类锁
```

### 6.4 锁的对象必须是对象

`synchronized()` 括号里必须是**对象引用**，不能是基本类型：

```java
synchronized(Counter.count) { ... }  // ❌ int 不是对象，编译错误
```

不一定非 `new Object()`，只要是**同一个对象实例**即可。常见选择：

| 锁对象 | 说明 |
|--------|------|
| `new Object()` 专用变量 | 推荐，意图清晰，和业务数据分离 |
| `Counter.class` | 保护静态变量时常用，可省略 lock 字段 |
| 随便一个字符串 `"lock"` | 语法可以，但可能和其他代码意外锁到同一对象，不推荐 |

### 6.5 和 volatile 的区别

| | `volatile` | `synchronized` |
|---|-----------|----------------|
| 解决什么 | 可见性（一个线程改了，另一个能看见） | 互斥（同一时刻只有一个线程能改） |
| `count++` 安全吗 | ❌ 不安全 | ✅ 安全 |
| 典型场景 | 停止标志 `running = false` | 多个线程改同一计数器 |

### 6.6 死锁

需要同时锁两个对象时，如果不同方法**拿锁顺序相反**，会死锁：

```java
public void add(int m) {
    synchronized(lockA) {
        this.value += m;
        synchronized(lockB) {
            this.another += m;
        }
    }
}

public void dec(int m) {
    synchronized(lockB) {   // ❌ 先 B
        this.another -= m;
        synchronized(lockA) {  // 再 A，和 add 顺序相反
            this.value -= m;
        }
    }
}
```

两个线程同时执行时：

```
add 线程：拿到 lockA ✅ → 等 lockB ⏳（dec 占着，且 dec 不会先放 lockB）
dec 线程：拿到 lockB ✅ → 等 lockA ⏳（add 占着，且 add 不会先放 lockA）
→ 互相等，永远卡住
```

**常见误解：** 以为「拿到锁后马上释放，另一个线程就能接着拿」。  
实际上 `synchronized` 是**进块加锁、出块才释放**——外层锁会一直持有到整个 `{ }` 执行完，内层才去抢第二把锁：

```java
synchronized(lockA) {    // 拿到 A，一直握着
    ...
    synchronized(lockB) {  // 仍握着 A 的情况下去拿 B
        ...
    }                       // 这里才释放 B
}                           // 这里才释放 A
```

**解决办法：**

1. **统一拿锁顺序**（都先 A 后 B）：

```java
public void dec(int m) {
    synchronized(lockA) {
        this.value -= m;
        synchronized(lockB) {
            this.another -= m;
        }
    }
}
```

2. **尽量只用一把锁**（`value` 和 `another` 总是一起改时更简单）：

```java
synchronized(lock) {
    this.value -= m;
    this.another -= m;
}
```

### 6.7 快速对照

```
synchronized 方法       → 锁 this（实例）或 Class（静态）
synchronized(对象) { }  → 锁指定的对象，可控制锁的范围
同一把锁                → 所有要互斥的线程必须 synchronized 同一个对象
进块加锁、出块释放       → 嵌套锁时外层锁不会「用完就还」
死锁                    → 多把锁 + 拿锁顺序不一致 → 互相等待
```
