package com.jack.demo01;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// ============ 1. 接口 ============
interface UserService {
    void save(String name);
    String findById(int id);
}

// ============ 2. 真实实现类 ============
class UserServiceImpl implements UserService {
    @Override
    public void save(String name) {
        System.out.println("【真实逻辑】保存用户：" + name);
    }

    @Override
    public String findById(int id) {
        System.out.println("【真实逻辑】查询用户，id=" + id);
        return "用户-" + id;
    }
}

// ============ 3. 调用处理器（核心） ============
class LogInvocationHandler implements InvocationHandler {

    // 被代理的真实对象
    private final Object target;

    public LogInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * @param proxy  代理对象本身（一般不用）
     * @param method 当前被调用的方法
     * @param args   方法参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(">>> 方法执行前：" + method.getName()
                + "，参数：" + (args == null ? "无" : java.util.Arrays.toString(args)));

        long start = System.currentTimeMillis();

        // 调用真实对象的方法
        Object result = method.invoke(target, args);

        long cost = System.currentTimeMillis() - start;
        System.out.println("<<< 方法执行后：" + method.getName()
                + "，耗时：" + cost + "ms，返回值：" + result);

        return result;
    }
}

// ============ 4. 测试 ============
public class DynamicProxyDemo {
    public static void main(String[] args) {
        // 真实对象
        UserService target = new UserServiceImpl();

        // 创建代理对象
        UserService proxy = (UserService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),   // 类加载器
                target.getClass().getInterfaces(),    // 代理要实现哪些接口
                new LogInvocationHandler(target)      // 调用处理器
        );

        System.out.println("代理对象类型：" + proxy.getClass().getName());
        System.out.println("----------------------------------------");

        // 通过代理调用方法
        proxy.save("张三");
        System.out.println("----------------------------------------");

        String user = proxy.findById(100);
        System.out.println("最终拿到：" + user);
    }
}