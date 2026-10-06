package com.jack.demo01;

public class Proxy implements Rent {
    private Host host;

    public Proxy() {
    }

    public Proxy(Host host) {
        this.host = host;
    }

    @Override
    public void rent() {
        seeHouse();
        host.rent();
        hetong();
    }

    public void seeHouse(){
        System.out.println("中介带你看房");
    }

    public void hetong() {
        System.out.println("带你签合同");
    }
}
