package com.boco.redisUntil;

import com.boco.commdevinfocache.TDevVarInfoCacheMap;
import com.boco.controller.DevInfoSendController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisPoolUntil {
    private static final Logger logger= LoggerFactory.getLogger(DevInfoSendController.class);

//    //连接安装redis服务器的IP地址
//    private static String ADDR = "127.0.0.1";
//    //Redis的端口号
//    private static int PORT = 6379;

   /* //可用连接实例的最大数目，默认值为8；
     //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;*//*

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    //访问密码
    private static String AUTH = "123456";
    */
   private static JedisPoolUntil singleton = null;

    /**
     * 静态工厂方法
     */
    public static JedisPoolUntil getInstance(){
        if (singleton == null){
            singleton = new JedisPoolUntil();
        }
        return singleton;
    }

    private static int MAXTOTAL = 20; //最大闲置数
    private static int MINIDLE = 10;  //最小闲置数
    private static int MAXIDLE = 15;  //最大连接数
    private static JedisPool jedisPool = null;

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public Jedis getJedis(String redisAddr,String redisPort) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAXTOTAL);
            config.setMaxIdle(MINIDLE);
            config.setMinIdle(MAXIDLE);
            //config.setMaxWaitMillis(MAX_WAIT);
            //config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config,redisAddr,Integer.valueOf(redisPort));
            //jedisPool = new JedisPool(config,ADDR,PORT,TIMEOUT,AUTH);
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                logger.error("redis连接失败");
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("redis连接异常------->"+ex.toString());
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null) {
            //jedisPool.returnResource(jedis);
        }
    }

}
