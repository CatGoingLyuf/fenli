package com.lyuf.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author tangzy
 * @Title: RedisTemplateUtil
 * @Description: RedisTemplateUtil工具包，封装redisTemplate中的方法实现。
 * @date 2020/8/12 16:59
 */
@Component
@Slf4j
public class RedisTemplateUtil {

    public static final long DAY_OF_SECONDS = 86400;
    public static final long MONTH_OF_SECONDS = 2592000;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Set<String> keys(String keys) {
        try {
            return redisTemplate.keys(keys);
        } catch (Exception e) {
            log.error("keys", e);
            return null;
        }
    }
    public void execute(String key) {
        try {
            redisTemplate.execute(new RedisCallback<Set<String>>() {
                @Override
                public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                    log.info(connection.execute("get *").toString());
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("keys", e);
        }
    }
    /**
     * 指定缓存失效时间
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean expire(String key, long timeout) {
        try {
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("expire", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键； 不能为null
     * @return 时间(秒)； 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
//    public void sort(String key) {
//        redisTemplate.sort(key);
//    }

    /**
     * 判断key是否存在
     *
     * @param key 键；
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("hasKey", e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值， 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键；
     * @return 值；
     */
    public String get(String key) {
        if (key == null) {
            return null;
        }
        Object result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键；
     * @param value 值；
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("set(String key, Object value)", e);
            return false;
        }
    }

    public boolean setnx(String key, Object value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error("setnx(String key, Object value)", e);
            return false;
        }
    }

    /**
     * 不存在插入,并设置超时时间
     *
     * @param key
     * @param value
     * @param timeout timeout 时间(秒)；
     * @return
     */
    public boolean setnx(String key, Object value, long timeout) {
        try {
            boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value);
            if (flag) {
                expire(key, timeout);
            }
            return flag;
        } catch (Exception e) {
            log.error("setnx(String key, Object value)", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)； timeout要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long timeout) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("set(String key, Object value, long timeout)", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键；
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键；
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * HashGet
     *
     * @param key  键； 不能为null
     * @param item 项 不能为null
     * @return 值；
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键；值；
     *
     * @param key 键；
     * @return 对应的多个键；值；
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键；
     * @param map 对应多个键；值；
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("hmset(String key, Map<String, Object> map)", e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key     键；
     * @param map     对应多个键；值；
     * @param timeout 时间(秒)；
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long timeout) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("hmset(String key, Map<String, Object> map, long timeout)", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键；
     * @param item  项
     * @param value 值；
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("hset(String key, String item, Object value)", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键；
     * @param item    项
     * @param value   值；
     * @param timeout 时间(秒)； 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long timeout) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("hset(String key, String item, Object value, long timeout)", e);
            return false;
        }
    }

    /**
     * 删除hash表中的值；
     *
     * @param key  键； 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值；
     *
     * @param key  键； 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值；返回
     *
     * @param key  键；
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键；
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据key获取Set中的所有值；
     *
     * @param key 键；
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("sGet(String key)", e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键；
     * @param value 值；
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("sHasKey(String key, Object value)", e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键；
     * @param values 值； 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("sSet(String key, Object... values)", e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @param values  值； 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long timeout, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return count;
        } catch (Exception e) {
            log.error("sSetAndTime(String key, long timeout, Object... values)", e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键；
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("sGetSetSize(String key)", e);
            return 0;
        }
    }

    /**
     * 移除值；为value的
     *
     * @param key    键；
     * @param values 值； 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("setRemove(String key, Object... values)", e);
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键；
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值；
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("lGet(String key, long start, long end)", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键；
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("lGetListSize(String key)", e);
            return 0;
        }
    }

    /**
     * 通过索引获取list中的值；。
     *
     * @param key   键；
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("lGetIndex(String key, long index)", e);
            return null;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个值，。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean rPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("rPush(String key, Object value)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个值，。
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean rPush(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("rPush(String key, Object value, long timeout)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个或多个值，。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean rPushAll(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("rPushAll(String key, List<Object> value)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个或多个值，
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean rPushAll(String key, List<Object> value, long timeout) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("rPushAll(String key, List<Object> value, long timeout)", e);
            return false;
        }
    }


    /**
     * 将list放入缓存，将一个值，插入到列表头部。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean lPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("lPush(String key, Object value)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，将一个值，插入到列表头部。
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean lPush(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("lPush(String key, Object value, long timeout)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，将一个或多个值，插入到列表头部。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean lPushAll(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("(String key, List<Object> value)", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，将一个或多个值，插入到列表头部。
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean lPushAll(String key, List<Object> value, long timeout) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("lPushAll(String key, List<Object> value, long timeout)", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据。
     *
     * @param key   键；
     * @param index 索引
     * @param value 值；
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("lUpdateIndex(String key, long index, Object value)", e);
            return false;
        }
    }

    /**
     * 移除N个值，为value。
     *
     * @param key   键；
     * @param count 移除多少个；
     * @param value 值；
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("lRemove(String key, long count, Object value)", e);
            return 0;
        }
    }

    /**
     * 移出并获取列表的第一个元素。
     *
     * @param key 键；
     * @return
     */
    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("lPop(String key)", e);
            return null;
        }
    }

    /**
     * 移出并获取列表的第一个元素。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public Object lPop(String key, long timeout) {
        try {
            return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("lPop(String key, long timeout)", e);
            return null;
        }
    }

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     *
     * @param key 键；
     * @return
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("rPop(String key)", e);
            return null;
        }
    }

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public Object rPop(String key, long timeout) {
        try {
            return redisTemplate.opsForList().rightPop(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("rPop(String key, long timeout)", e);
            return null;
        }
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public Object bLPop(String key, int timeout) {
        try {
            Object obj = redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    //队列没有元素会阻塞操作，直到队列获取新的元素或超时
                    return connection.bLPop(timeout, key.getBytes());
                }
            }, new StringRedisSerializer());
            return obj;
        } catch (Exception e) {
            log.error("bLPop(String key, int timeout)", e);
            return null;
        }
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public Object bRPop(String key, int timeout) {
        try {
            Object obj = redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    //队列没有元素会阻塞操作，直到队列获取新的元素或超时
                    return connection.bRPop(timeout, key.getBytes());
                }
            }, new StringRedisSerializer());
            return obj;
        } catch (Exception e) {
            log.error("bRPop(String key, int timeout)", e);
            return null;
        }
    }

    /**
     * 实现命令 : ZADD key score member
     * 添加一个 成员/分数 对
     *
     * @param key
     * @param value 成员
     * @param score 分数
     * @return
     */
    public boolean zAdd(String key, double score, Object value) {
        Boolean result = redisTemplate.opsForZSet().add(key, value, score);
        if (result == null) {
            return false;
        }
        return result;
    }


    /**
     * 实现命令 : ZRANGEBYSCORE key min max LIMIT offset count
     * 分页获取分数范围内的成员并按从小到大返回
     * 包括min也包括max(未验证)
     *
     * @param key
     * @param min    小分数
     * @param max    大分数
     * @param offset 开始下标，下标从0开始
     * @param count  取多少条
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max, int offset, int count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 实现命令 : ZREM key member [member ...]
     * 删除成员
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRem(String key, Collection<Object> values) {
        Object[] members = values.toArray();
        return redisTemplate.opsForZSet().remove(key, members);
    }

    public Boolean setBit(String key, long offset, boolean value) {
        if (!Optional.ofNullable(key).isPresent()) {
            log.error("key不能为空");
            throw new IllegalArgumentException("redis->key不能为空");
        }
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public Boolean getBit(String key, long offset) {
        if (!Optional.ofNullable(key).isPresent()) {
            log.error("key不能为空");
            throw new IllegalArgumentException("redis->key不能为空");
        }
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * hyperLoglog 新增
     *
     * @param key
     * @param value
     * @return
     */
    public boolean pfadd(String key, Object value) {
        return redisTemplate.opsForHyperLogLog().add(key, value) > 0;
    }

    /**
     * hyperLoglog 查询
     *
     * @param key
     * @param
     * @return
     */
    public long pfcount(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    public List<Object> mget(List<String> keyList) {
        if (!Optional.ofNullable(keyList).isPresent() || keyList.isEmpty()) {
            log.error("key不能为空");
            throw new IllegalArgumentException("redis->key不能为空");
        }
        List<Object> list = redisTemplate.executePipelined((RedisCallback<Object>) redisConnection -> {
            keyList.stream().forEach(key -> {
                RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                byte[] bytes = stringSerializer.serialize(key);
                stringSerializer.deserialize(redisConnection.get(bytes));
            });
            return null;
        });
        return list;
    }

}

