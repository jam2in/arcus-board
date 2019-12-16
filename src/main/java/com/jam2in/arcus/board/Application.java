package com.jam2in.arcus.board;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    static ArcusClientPool arcusClient;
    static public final boolean CACHE = true;

    public static void main(String[] args) {
        if (CACHE) {
            arcusClient = ArcusClient.createArcusClientPool("10.34.35.122:7289", "test", new ConnectionFactoryBuilder(), 8);
            System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
        }
        SpringApplication.run(Application.class, args);
    }
}
