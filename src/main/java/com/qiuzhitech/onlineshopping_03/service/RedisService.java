package com.qiuzhitech.onlineshopping_03.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    @Resource
    private JedisPool jedisPool;
    public void setValue(String key, long value) {
        Jedis resource =jedisPool.getResource();
        resource.set(key, String.valueOf(value));
        resource.close();
    }

    public void setValue(String key, String value) {
        Jedis resource =jedisPool.getResource();
        resource.set(key, value);
        resource.close();
    }
    public String getValue(String key) {
        Jedis resource =jedisPool.getResource();
        String res = resource.get(key);
        resource.close();
        return res;
    }
    public long stockDeduct(String redisKey) {
        Jedis resource = jedisPool.getResource();
        String scripts =
                "if redis.call('exists', KEYS[1]) == 1 then\n" +
                        "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                        "    if (stock<=0) then\n" +
                        "        return -1\n" +
                        "    end\n" +
                        "\n" +
                        "    redis.call('decr', KEYS[1]);\n" +
                        "    return stock - 1;\n" +
                        "end\n" +
                        "\n" +
                        "return -1;";

        Long stockCount = (Long) resource.eval(scripts, Collections.singletonList(redisKey), Collections.emptyList());
        resource.close();
        if (stockCount <0) {
            log.info("There is no stock available");
            return -1;
        } else {
            return stockCount;
        }
    }

    public boolean tryDistribuedLock(String key, String value, int expireTime){
        Jedis resource = jedisPool.getResource();
        String res = resource.set(key, value, "NX", "PX", expireTime);
        resource.close();
        if("OK".equals(res)){
            return true;
        }
        return false;
    }

    public boolean releaseDistribuedLock(String key, String value){
        Jedis resource = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long res = (Long) resource.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        resource.close();
        if(res == 1L){
            return true;
        }
        return false;



    }

    public void revertStock(String redisKey) {
        Jedis resource = jedisPool.getResource();
        resource.incr(redisKey);
        resource.close();
    }
}
