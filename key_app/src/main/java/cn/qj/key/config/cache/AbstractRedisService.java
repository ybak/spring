package cn.qj.key.config.cache;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * RedisHash服务抽象类
 * 
 * @author Qiujian
 * @date 2018/11/01
 * @param <T>
 */
@Service
public abstract class AbstractRedisService<T> {

	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;

	@Resource
	protected HashOperations<String, String, T> hashOperations;

	/**
	 * 定义Hash表 key名称
	 *
	 * @return
	 */
	protected abstract String getRedisKey();

	/**
	 * 在相应Hash表中添加键值对 key:Object
	 *
	 * @param key
	 * @param doamin
	 * @param expire
	 *            过期时间(单位:秒),传入 -1 时表示不设置过期时间
	 */
	public void put(String key, T doamin, long expire) {
		hashOperations.put(getRedisKey(), key, doamin);
		if (expire != -1) {
			redisTemplate.expire(getRedisKey(), expire, TimeUnit.SECONDS);
		}
	}

	/**
	 * 在相应Hash表中删除key名称的元素
	 *
	 * @param key
	 */
	public void remove(String key) {
		hashOperations.delete(getRedisKey(), key);
	}

	/**
	 * 在相应Hash表中查询key名称的元素
	 *
	 * @param key
	 * @return
	 */
	public T get(String key) {
		return hashOperations.get(getRedisKey(), key);
	}

	/**
	 * 获取在相应Hash表下的所有实体对象
	 *
	 * @return
	 */
	public List<T> getAll() {
		return hashOperations.values(getRedisKey());
	}

	/**
	 * 查询在相应Hash表下的所有key名称
	 *
	 * @return
	 */
	public Set<String> getKeys() {
		return hashOperations.keys(getRedisKey());
	}

	/**
	 * 判断在相应Hash表下key是否存在
	 *
	 * @param key
	 * @return
	 */
	public boolean isKeyExists(String key) {
		return hashOperations.hasKey(getRedisKey(), key);
	}

	/**
	 * 查询相应Hash表的缓存数量
	 *
	 * @return
	 */
	public long count() {
		return hashOperations.size(getRedisKey());
	}

	/**
	 * 清空相应Hash表的所有缓存
	 */
	public void empty() {
		Set<String> set = hashOperations.keys(getRedisKey());
		set.stream().forEach(key -> hashOperations.delete(getRedisKey(), key));
	}

}