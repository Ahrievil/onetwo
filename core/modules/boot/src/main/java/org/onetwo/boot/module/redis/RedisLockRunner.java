package org.onetwo.boot.module.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import org.onetwo.common.exception.BaseException;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author wayshall
 * <br/>
 */
@Slf4j
public class RedisLockRunner {
	
	final private RedisLockRegistry redisLockRegistry;
	private String lockKey;
	private Long time;
	private TimeUnit unit;
	private Consumer<Exception> errorHandler;

	@Builder
	public RedisLockRunner(RedisLockRegistry redisLockRegistry, String lockKey,
			Long time, TimeUnit unit, Consumer<Exception> errorHandler) {
		super();
		this.redisLockRegistry = redisLockRegistry;
		this.lockKey = lockKey;
		this.time = time;
		this.unit = unit;
		this.errorHandler = errorHandler;
	}
	
	public <T> T tryLock(Supplier<T> action){
		Function<Lock, Boolean> lockTryer = null;
		if(time==null && unit!=null){
			lockTryer = lock->{
				try {
					return lock.tryLock(time, unit);
				} catch (InterruptedException e) {
					throw new BaseException("try lock error", e);
				}
			};
		}else{
			lockTryer = lock->lock.tryLock();
		}
		return tryLock(lockTryer, action);
	}
	
	public <T> T tryLock(Function<Lock, Boolean> lockTryer, Supplier<T> action){
		Lock lock = redisLockRegistry.obtain(lockKey);
		if(!lockTryer.apply(lock)){
			log.info("can not obtain task lock, ignore task. lock key: {}", lockKey);
			return null;
		}

		if(log.isDebugEnabled()){
			log.debug("lock with key : {}", lockKey);
		}
		T result = null;
		try {
			result = action.get();
		} catch (Exception e) {
			handleException(e);
		} finally{
			lock.unlock();
			if(log.isDebugEnabled()){
				log.debug("unlock with key : {}", lockKey);
			}
		}
		return result;
	}

	protected void handleException(Exception e){
		if(errorHandler!=null){
			errorHandler.accept(e);
		}else{
			log.error("execute error: "+e.getMessage(), e);
		}
	}

}