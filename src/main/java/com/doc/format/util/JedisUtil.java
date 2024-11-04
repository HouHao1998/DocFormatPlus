package com.doc.format.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author chengwei
 */
@Slf4j
public class JedisUtil {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    @Resource
    private JedisPool jedisPool;

    /**
     * 字符串转object
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static Object str2Object(String str) throws Exception {

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getByte(str)));
        return ois.readObject();
    }

    /**
     * 字符串转byte
     *
     * @param str
     * @return
     */
    public static byte[] getByte(String str) {
        byte[] bt = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream objos = null;
        try {
            if (str != null) {
                objos = new ObjectOutputStream(baos);
                objos.writeObject(str);
                bt = baos.toByteArray();
            }
        } catch (Exception e) {
            bt = (byte[]) null;
            log.error("getByte str {}", str, e);
        } finally {
            try {
                if (null != objos) {
                    objos.close();
                }
                baos.close();
            } catch (IOException e) {
                log.error("getByte close error", e);
            }
        }
        return bt;
    }

    public static byte[] serialize(Object object) throws Exception {
        if (object == null) {
            return new byte[0];
        }
        String s = JSONObject.toJSONString(object);
        return JSONObject.toJSONString(object).getBytes(DEFAULT_CHARSET);
    }

    public static Object deserialize(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);

        return JSONObject.parseObject(str, Object.class);
    }

    /**
     * String类型的增加操作
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Map类型的增加操作
     *
     * @param key Redis的键
     * @param map 要存储的Map数据
     */
    public void setMap(String key, Map<String, String> map) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hmset(key, map);
        } catch (Exception e) {
            log.error("setMap key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取 Map 类型的数据
     *
     * @param key Redis 的键
     * @return 存储在 Redis 中的 Map 数据，如果键不存在则返回 null
     */
    public Map<String, String> getMap(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("getMap key {} error.", key, e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 字符串
     *
     * @param key
     * @param value
     * @param expireTimeInSec
     */
    public void set(String key, String value, int expireTimeInSec) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, expireTimeInSec, value);
        } catch (Exception e) {
            log.error("set key {} ex error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * key模糊查询
     *
     * @param key
     */
    public Set<String> keys(String key) {
        Jedis jedis = null;
        Set<String> keys = null;
        try {
            jedis = jedisPool.getResource();
            keys = jedis.keys(key);
        } catch (Exception e) {
            log.error("keys key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return keys;
    }

    public void subscribe(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                log.info("Received message: " + message + " from channel: " + channel);
            }
        }, key);
    }

    public String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = jedisPool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            log.error("get key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public List<String> mget(String... key) {
        Jedis jedis = null;
        List<String> value = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            Response<List<String>> response = pipeline.mget(key);
            pipeline.sync();
            value = response.get();
        } catch (Exception e) {
            log.error("get key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public Set<String> getKeysByPattern(String pattern) {
        Jedis jedis = null;
        Set<String> retSet = new HashSet<>();
        int scanCount = 20;
        String scanRet = "0";
        try {
            jedis = jedisPool.getResource();
            do {
                ScanParams scanParams = new ScanParams();
                scanParams.match(pattern + "*");
                scanParams.count(scanCount);
                ScanResult<String> scanResult = jedis.scan(scanRet, scanParams);
                scanRet = scanResult.getStringCursor();
                retSet.addAll(scanResult.getResult());
            } while (!"0".equals(scanRet));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return retSet;
    }

    /**
     * 删除某个key
     *
     * @param key
     */
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            log.error("del key {} ", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * 模糊删除
     *
     * @param key
     * @return
     */
    public Long batchDel(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String> keys = jedis.keys("*" + key + "*");
            for (String ckey : keys) {
                jedis.del(ckey);
            }
            return 1L;
        } catch (Exception e) {
            log.error("batchDel key {} ", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * 存入对象之前进行序列化
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public String setObj(String key, Object value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key.getBytes(), serialize(value));

            log.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            log.error("setObject {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObj(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key.getBytes(), serialize(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            log.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            log.warn("setObject {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * getObj
     *
     * @param key 键
     * @return
     */
    public Object getObj(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes());
            if (null != bytes && bytes.length > 0) {
                return deserialize(bytes);
            }

            return null;
        } catch (Exception e) {
            log.error("getobj key {} error.", key, e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除key
     */
    public Long delObj(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key.getBytes());
        } catch (Exception e) {
            log.error("delObj key {} error.", key, e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 判断某个key值是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        Boolean flag = false;

        try {
            jedis = jedisPool.getResource();
            flag = jedis.exists(key);
        } catch (Exception e) {
            log.error("exists key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return flag;
    }

    /**
     * setNX
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setNx(String key, String value) {
        Long setnx = null;
        try (Jedis jedis = jedisPool.getResource()) {
            setnx = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("setNX key {} error.", key, e);
        }
        return Long.valueOf(1L).equals(setnx);
    }

    public String setLock(String lockKey, String requestId, long expireTime) {
        Jedis jedis = null;
        String result = null;
        try {

            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            }
        } catch (Exception e) {
            log.error("redis连接异常", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public Object releaseLock(String lockKey, String requestId) {
        Jedis jedis = null;
        Object result = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            }
        } catch (Exception e) {
            log.error("redis连接异常", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 递减
     *
     * @param key
     * @return
     */
    public Long decr(String key) {
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = jedisPool.getResource();
            value = jedis.decr(key);
        } catch (Exception e) {
            log.error("decr key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * 递增
     *
     * @param key
     * @return
     */
    public Long incr(String key) {
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = jedisPool.getResource();
            value = jedis.incr(key);
        } catch (Exception e) {
            log.error("incr key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.sadd(key, value);
            log.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            log.error("setSetAdd {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 向Set获取所有成员
     *
     * @param key 键
     * @return
     */
    public Set smembers(String key) {
        Set<String> result = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.smembers(key);
            log.debug("smembers {} = {}", key);
        } catch (Exception e) {
            log.error("smembers {} = {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 向Set获取所有成员
     *
     * @param key 键
     * @return
     */
    public Long srem(String key, String... members) {
        Long result = 0L;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.srem(key, members);
            log.debug("smembers {} = {}", key);
        } catch (Exception e) {
            log.error("smembers {} = {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, value.toArray(new String[value.size()]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            log.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            log.error("setSet {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取set缓存
     *
     * @param key 键
     * @return 值
     */
    public Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                log.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            log.error("getSet {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key.getBytes())) {
                jedis.del(key);
            }
            Set<byte[]> set = new HashSet();
            for (Object o : value) {
                set.add(serialize(o));
            }
            result = jedis.sadd(key.getBytes(), set.toArray(new byte[set.size()][]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            log.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            log.error("setObjectSet {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key.getBytes())) {
                value = new HashSet();
                Set<byte[]> set = jedis.smembers(key.getBytes());
                for (byte[] bs : set) {
                    value.add(deserialize(bs));
                }
                log.debug("getObjectSet {} = {}", key, value);
            }
        } catch (Exception e) {
            log.error("getObjectSet {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<byte[]> set = new HashSet();
            for (Object o : value) {
                //防止传数组，有null报错
                if (o == null) {
                    continue;
                }
                set.add(serialize(o));

            }
            result = jedis.sadd(key.getBytes(), set.toArray(new byte[set.size()][]));
            log.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            log.error("setSetObjectAdd {} = {}", key, value, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取给定key中元素个数
     *
     * @param key
     * @return
     */
    public long scard(String key) {
        Jedis jedis = null;
        long len = 0;
        try {
            jedis = jedisPool.getResource();
            len = jedis.scard(key);
        } catch (Exception e) {
            log.error("scard key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return len;
    }

    /**
     * 判断KEY关联的SET集合是否存在对应的成员
     *
     * @param key   Redis里面实际的KEY
     * @param value 要查找的成员
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            flag = jedis.sismember(key, value);
        } catch (Exception e) {
            log.error("sismember key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return flag;
    }

    /**
     * 从集合中删除指定成员
     *
     * @param key
     * @param value
     * @return
     */
    public long srem(String key, String value) {
        Jedis jedis = null;
        long flag = 0;
        try {
            jedis = jedisPool.getResource();
            flag = jedis.srem(key, value);
        } catch (Exception e) {
            log.error("srem key {} error.", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return flag;
    }

    /**
     * List放入多个对象
     *
     * @param name
     * @param ts
     * @param <T>
     */
    public <T extends Serializable> void push(String name, T... ts) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (T t : ts) {
                jedis.lpush(name, JSON.toJSONString(t));
            }
        } catch (Exception e) {
            log.error("push key {} error.", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * List放入单个值
     *
     * @param name
     * @param json
     * @return
     */
    public long lpush(String name, String json) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(name, json);
        } catch (Exception e) {
            log.error("lpush key {} error.", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }

    /**
     * List放入集合
     *
     * @param name
     * @param collection
     * @param <T>
     */
    public <T extends Serializable> void push(String name, Collection<T> collection) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (T t : collection) {
                jedis.lpush(name, JSON.toJSONString(t));
            }
        } catch (Exception e) {
            log.error("push collection {} error.", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从列表最后一位开始移除并获取列表该元素
     *
     * @param name
     * @param size
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> List<T> pop(String name, int size) {
        if (size < 1) {
            return null;
        }

        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(name)) {
                String value = jedis.rpop(name);
                for (int i = 1; i <= size && value != null; i++) {
                    list.add((T) JedisUtil.str2Object(value));
                    value = jedis.rpop(name);
                }
            }
        } catch (Exception e) {
            log.error("pop key {} ", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return list;
    }

    /**
     * list 从左往右获取
     *
     * @param name
     * @param start
     * @param end
     * @param <T>
     * @return
     */
    public <T extends Serializable> List<T> lrange(String name, int start, int end) {
        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(name)) {
                List<String> strList = jedis.lrange(name, start, end);
                int size = strList == null ? 0 : strList.size();
                for (int i = 0; i < size; i++) {
                    list.add((T) JedisUtil.str2Object(strList.get(i)));
                }
            }
        } catch (Exception e) {
            log.error("lrange ", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return list;
    }

    /**
     * list 从左到右获取
     *
     * @param name
     * @param start
     * @param end
     * @param classes
     * @param <T>
     * @return
     */
    public <T extends Serializable> List<T> lrange(String name, int start, int end, Class<T> classes) {
        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(name)) {
                List<String> strList = jedis.lrange(name, start, end);
                int size = strList == null ? 0 : strList.size();
                ObjectMapper objectMapper = new ObjectMapper();
                for (int i = 0; i < size; i++) {
                    list.add(objectMapper.readValue(strList.get(i), classes));
                }
            }
        } catch (Exception e) {
            log.error("lrange key {}", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return list;
    }

    /**
     * list 从右到左获取
     *
     * @param name
     * @param start
     * @param end
     * @param <T>
     * @return
     */
    public <T extends Serializable> List<T> rrange(String name, int start, int end) {
        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(name)) {
                List<String> strList = jedis.lrange(name, start, end);
                int size = strList == null ? 0 : strList.size();
                for (int i = size - 1; i >= 0; i--) {
                    list.add((T) JedisUtil.str2Object(strList.get(i)));
                }
            }
        } catch (Exception e) {
            log.error("rrange key {} ", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return list;
    }

    /**
     * list 从右到左获取
     *
     * @param name
     * @param start
     * @param end
     * @param classes
     * @param <T>
     * @return
     */
    public <T extends Serializable> List<T> rrange(String name, int start, int end, Class<T> classes) {
        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(name)) {
                List<String> strList = jedis.lrange(name, start, end);
                int size = strList == null ? 0 : strList.size();
                ObjectMapper objectMapper = new ObjectMapper();
                for (int i = size - 1; i >= 0; i--) {
                    list.add(objectMapper.readValue(strList.get(i), classes));
                }
            }
        } catch (Exception e) {
            log.error("rrange key {} ", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return list;
    }

    /**
     * list length获取
     *
     * @param name
     * @return
     */
    public long length(String name) {
        Jedis jedis = null;
        long length = 0;
        try {
            jedis = jedisPool.getResource();
            length = jedis.llen(name);
        } catch (Exception e) {
            log.error("length key {}", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return length;
    }

    /**
     * 移除列表中的元素
     *
     * @param name
     * @param count count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     *              count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     *              count = 0 : 移除表中所有与 VALUE 相等的值。
     */
    public void lrem(String name, int count, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lrem(name, count, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error("lrem key {} ", name, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 向有序不重复集合添加数据
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public long zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            log.error("zadd key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 向有序不重复集合添加数据
     *
     * @param key
     * @param scoreMembers
     * @return
     */
    public long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            log.error("zadd key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 移除有序集合中给定的字典区间的所有成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            log.error("zremrangeByRank key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 移除有序集合中的成员
     *
     * @param key
     * @param members
     * @return
     */
    public long zrem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrem(key, members);
        } catch (Exception e) {
            log.error("zremrangeByRank key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 通过索引区间返回有序集合成指定区间内的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            log.error("zrang key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 通过索引区间返回有序集合成指定区间内的成员(倒序)
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            log.error("zrang key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取有序集合的成员数
     *
     * @param key
     * @return
     */
    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            log.error("zcard key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 获取有序集合的成员数
     *
     * @param key
     * @return
     */
    public Long zrank(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrank(key, member);
        } catch (Exception e) {
            log.error("zcard key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取有序集合的成员数
     *
     * @param key
     * @return
     */
    public long zinterstore(String key, String key1, String key2, double key1Weight, double key2Weight) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ZParams zParams = new ZParams();
            zParams.weightsByDouble(key1Weight, key2Weight);
            return jedis.zinterstore(key, zParams, key1, key2);
        } catch (Exception e) {
            log.error("zcard key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 将哈希表 key 中的字段 field 的值设为 value
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("hset key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error("hget key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取大key下的所有键值对数据
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("hgetAll key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取当前key的长度
     *
     * @param key
     * @return
     */
    public Long hlen(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hlen(key);
        } catch (Exception e) {
            log.error("hcard key {}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param field
     * @return
     */
    public Long hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hdel(key, field);
        } catch (Exception e) {
            log.error("hdel key {} ", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
