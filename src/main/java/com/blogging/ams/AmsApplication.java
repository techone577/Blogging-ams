package com.blogging.ams;

import com.blogging.ams.netty.NettyClient;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.blogging.ams.persistence")
public class AmsApplication {

    private static final Logger LOG = LoggerFactory.getLogger(AmsApplication.class);

    public static void main (String[] args) {

        SpringApplication.run(AmsApplication.class, args);

        try {
            NettyClient.connect();
        } catch (Exception e) {
            LOG.info("连接netty服务器失败...");
        }
        LOG.info("netty启动结束...");
    }
}
