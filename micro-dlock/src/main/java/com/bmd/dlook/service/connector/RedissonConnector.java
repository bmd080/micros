package com.bmd.dlook.service.connector;

import org.redisson.Redisson;
import org.springframework.stereotype.Component;
import org.redisson.api.RedissonClient;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 获取RedissonClient连接类
 */
@Component
public class RedissonConnector {
    RedissonClient redisson;

    @PostConstruct
    public void init(){
        redisson = Redisson.create();
    }

    public RedissonClient getClient(){
        return redisson;
    }

}