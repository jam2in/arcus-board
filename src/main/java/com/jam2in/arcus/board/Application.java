package com.jam2in.arcus.board;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ConnectionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    static ArcusClient arcusClient;
    public static void main(String[] args) {
        arcusClient = ArcusClient.createArcusClient("1.255.51.181:8080", "test", new ConnectionFactoryBuilder());
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
        SpringApplication.run(Application.class, args);
    }
}
