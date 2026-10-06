package com.jack.demo01;

public class Client {
    static void main() {
        Host host = new Host();
        // 中介会代理，会有附属操作
        Proxy proxy = new Proxy(host);
        proxy.rent();
    }
}
