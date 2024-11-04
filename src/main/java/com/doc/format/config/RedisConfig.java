package com.doc.format.config;

import com.doc.format.util.JedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 *
 * @author cw
 * @date 2020/11/23
 */
@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private Integer port;

    @Value("${redis.timeout}")
    private Integer timeOut;

    @Value("#{'${redis.password}'!=''?'${redis.password}':null}")
    private String password;

    @Value("${redis.db}")
    private Integer db;


    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大能够保持idel状态的对象数
        jedisPoolConfig.setMaxIdle(30);
        //最大分配的对象数
        jedisPoolConfig.setMaxTotal(300);
        //当调用borrow Oject方法时，是否进行有效性检查
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut, password, db);
        return jedisPool;
    }

    @Bean
    public JedisUtil redisUtil(JedisPool jedisPool) {
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setJedisPool(jedisPool);
        return jedisUtil;
    }
}
