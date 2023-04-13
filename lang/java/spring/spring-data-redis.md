## Spring Data Redis 介绍
Spring Data Redis 是属于 Spring Data 下的一个模块，作用就是简化对于 redis 的操做。

### 模块划分

- RedisTemplate：RedisTemplate 是 Spring Data Redis 中最核心的类之一，提供了与 Redis 数据库交互的基本方法，如 get()、set()、incr() 等。它使用序列化器将 key 和 value 转换成二进制数组，并向 Redis 发送命令。
- StringRedisTemplate：StringRedisTemplate 是 RedisTemplate 的子类，专门用于操作 Redis 中的字符串类型数据。它默认使用 UTF-8 编码来序列化和反序列化字符串数据。
- RedisRepository：RedisRepository 是 Spring Data Redis 的另一个核心组件，用于简化对 Redis 数据库的 CRUD 操作。它定义了常见的 Redis 数据访问操作，如 save()、findAll()、findById() 等。
- RedisConnectionFactory：RedisConnectionFactory 是连接 Redis 数据库的工厂类，它会创建连接到 Redis 数据库的 RedisConnection 对象。Spring Data Redis 提供了多种 RedisConnectionFactory 的实现方式，如 JedisConnectionFactory、LettuceConnectionFactory 等。
- RedisCacheManager：RedisCacheManager 是 Spring 框架提供的缓存管理器，它使用 Redis 作为缓存存储介质。RedisCacheManager 支持多个缓存空间，可以针对不同的数据进行缓存。



1、提供了一个高度封装的“RedisTemplate”类，里面封装了对于Redis的五种数据结构的各种操作，包括：
 - redisTemplate.opsForValue()：操作字符串
 - redisTemplate.opsForHash()：操作hash
 - redisTemplate.opsForList()：操作list
 - redisTemplate.opsForSet()：操作set
 - redisTemplate.opsForZSet()：操作zset
2、SpringBoot2.x后RedisTemplate采用是lettuce(基于netty采用异步非阻塞式lO)进行通信，大并发下比jedis效率更高。
3、RedisTemplate模板使用序列化器操作redis数据。


## RedisTemplate

### 操作
Redis 的类型包括 Value、List、Set、Stream、ZSet、Geo、HyperLogLog 、Cluster 操作。它们之间的关系如下：
![spring data redis](https://img-blog.csdnimg.cn/38a25f4c1c0a410682575ddffa176ed8.png#pic_center)

### 序列化

```java
public interface RedisSerializer<T> {
    @Nullable
    byte[] serialize(@Nullable T var1) throws SerializationException;

    @Nullable
    T deserialize(@Nullable byte[] var1) throws SerializationException;
}
```

![spring data redis serializer](https://img-blog.csdnimg.cn/ceaaa597150b4f5f9b311c7ab3896f4d.png)

### 类型转换
在 RedisCustomConversions 和 Jsr310Converters 定义了常见的类型转换。

RedisCustomConversions 中定义的转换器：
```java
public class RedisCustomConversions extends CustomConversions {
    static {
        List<Object> converters = new ArrayList();
        converters.add(new StringToBytesConverter());
        converters.add(new BytesToStringConverter());
        converters.add(new NumberToBytesConverter());
        converters.add(new BytesToNumberConverterFactory());
        converters.add(new EnumToBytesConverter());
        converters.add(new BytesToEnumConverterFactory());
        converters.add(new BooleanToBytesConverter());
        converters.add(new BytesToBooleanConverter());
        converters.add(new DateToBytesConverter());
        converters.add(new BytesToDateConverter());
        converters.add(new UuidToBytesConverter());
        converters.add(new BytesToUuidConverter());
        converters.addAll(Jsr310Converters.getConvertersToRegister());
        STORE_CONVERTERS = Collections.unmodifiableList(converters);
        STORE_CONVERSIONS = StoreConversions.of(SimpleTypeHolder.DEFAULT, STORE_CONVERTERS);
    }
}
```

Jsr310Converters 中定义的转换器：
```java
public abstract class Jsr310Converters {
    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        if (!JAVA_8_IS_PRESENT) {
            return Collections.emptySet();
        } else {
            List<Converter<?, ?>> converters = new ArrayList();
            converters.add(new Jsr310Converters.LocalDateTimeToBytesConverter());
            converters.add(new Jsr310Converters.BytesToLocalDateTimeConverter());
            converters.add(new Jsr310Converters.LocalDateToBytesConverter());
            converters.add(new Jsr310Converters.BytesToLocalDateConverter());
            converters.add(new Jsr310Converters.LocalTimeToBytesConverter());
            converters.add(new Jsr310Converters.BytesToLocalTimeConverter());
            converters.add(new Jsr310Converters.ZonedDateTimeToBytesConverter());
            converters.add(new Jsr310Converters.BytesToZonedDateTimeConverter());
            converters.add(new Jsr310Converters.InstantToBytesConverter());
            converters.add(new Jsr310Converters.BytesToInstantConverter());
            converters.add(new Jsr310Converters.ZoneIdToBytesConverter());
            converters.add(new Jsr310Converters.BytesToZoneIdConverter());
            converters.add(new Jsr310Converters.PeriodToBytesConverter());
            converters.add(new Jsr310Converters.BytesToPeriodConverter());
            converters.add(new Jsr310Converters.DurationToBytesConverter());
            converters.add(new Jsr310Converters.BytesToDurationConverter());
            return converters;
        }
    }
}
```
![spring data redis convert](https://img-blog.csdnimg.cn/7b1add4299054ac7a48c5251db9ddc3c.png#pic_center)

### 监听器

#### 功能列表
1、支持 Pattern Topic 和 KeyspaceEvent 支持
2、通过 MessageListener 监听Redis Pub/Sub消息
3、监听 KeyspaceEvent  事件 ` __keyevent@*`
4、监听KeyExpirationEvent 事件	 `__keyevent@*__:expire`

```java
@FunctionalInterface
public interface MessageListener {
	void onMessage(Message message, byte[] pattern);
}
```

主流程如下
```java
    public void onMessage(Message message, @Nullable byte[] pattern) {
        try {
            if (this.delegate != this && this.delegate instanceof MessageListener) {
                // 如果是 MessageListener，直接执行并返回
                ((MessageListener)this.delegate).onMessage(message, pattern);
                return;
            }
            Object convertedMessage = this.extractMessage(message);
            // 获取反射方法参数
            String convertedChannel = (String)this.stringSerializer.deserialize(pattern);
            Object[] listenerArguments = new Object[]{convertedMessage, convertedChannel};
            // 反射调用
            this.invokeListenerMethod(this.invoker.getMethodName(), listenerArguments);
        } catch (Throwable var6) {
            this.handleListenerException(var6);
        }

    }
```
监听到消息之后，通过反射执行方法。

### 执行 Lua 脚本

Redis 支持执行 lua 脚本，包括两部分
1、脚本内容
2、脚本执行

#### 脚本内容
```java
public interface RedisScript<T> {
	String getSha1();

	Class<T> getResultType();

	String getScriptAsString();
```
包括脚本内容和返回类型。默认实现类为DefaultRedisScript。

对于脚本内容核心在于内容的来源，通过 ScriptSource 进行抽象，当前默认实现包括 ResourceScriptSource （从 Resource 读取）和 StaticScriptSource（直接为字符串）。

#### 脚本的执行
```java
public interface ScriptExecutor<K> {
	<T> T execute(RedisScript<T> script, List<K> keys, Object... args);

	<T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer,
			List<K> keys, Object... args);
}
```
默认实现为 DefaultScriptExecutor，包括如下几部分
1、参数序列化：抽象为 RedisSerializer
2、执行脚本：依赖 RedisTemplate 进行的 eval 和 evalSha 命令。
3、结果序列化：抽象为 RedisSerializer
4、返回值类：通过 ReturnType 转换




### 连接
支持 Lettuce 和 Jedis 两种客户端。其中 Lettuce 支持 Reactive RedisTemplae
### 操作

#### SessionCallback
```java
public interface SessionCallback<T> {
    @Nullable
    <K, V> T execute(RedisOperations<K, V> var1) throws DataAccessException;
}
```
#### RedisCallback

```java

public interface RedisCallback<T> {
    @Nullable
    T doInRedis(RedisConnection var1) throws DataAccessException;
}
```





## 附录



现在你要写一篇关于 Spring Data Redis 原理分析的博客，内容包括模块划分、序列化、反序列化、Lua 脚本，监听器，类型转换器，SessionCallback