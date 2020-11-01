





数据类型



```c
typedef struct redisObject {
    unsigned type:4;
    unsigned encoding:4;
    unsigned lru:LRU_BITS; /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    int refcount;
    void *ptr;
} robj;

typedef struct redisDb {
    dict *dict;                 /* The keyspace for this DB */
    dict *expires;              /* Timeout of keys with a timeout set */
    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP)*/
    dict *ready_keys;           /* Blocked keys that received a PUSH */
    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
    int id;                     /* Database ID */
    long long avg_ttl;          /* Average TTL, just for stats */
    list *defrag_later;         /* List of key names to attempt to defrag one by one, gradually. */
} redisDb;
```



需要注意的是，redisDb 中的 dict 中存储的 key 为 string，value 为 redisObject，而 redisObject 的 ptr 才是真正的底层数据结构，比如 ziplist, string, intset 等。



支持的五种数据类型

```
#define OBJ_STRING 0    /* String object. */
#define OBJ_LIST 1      /* List object. */
#define OBJ_SET 2       /* Set object. */
#define OBJ_ZSET 3      /* Sorted set object. */
#define OBJ_HASH 4      /* Hash object. */
```





#### object 命令

从 client->db->dict 中查找值 key 对应的 redisObject，获取其中的属性。比如 refcount, encoding, idletime, freq 等

源码参考 object.c/objectCommand

```c
robj *objectCommandLookup(client *c, robj *key) {
    dictEntry *de;

    if ((de = dictFind(c->db->dict,key->ptr)) == NULL) return NULL;
    return (robj*) dictGetVal(de);
}

robj *objectCommandLookupOrReply(client *c, robj *key, robj *reply) {
    robj *o = objectCommandLookup(c,key);

    if (!o) addReply(c, reply);
    return o;
}
```



#### type 命令

**格式**：type key 

**返回**： key 所储存的值的类型。

**时间复杂度** ：O(1)

**实现原理**

从 client->db->dict 中查找值 key，获取其type属性

如果已经过期：

对于 master，如果 key 已经过期，根据配置 server.lazyfree_lazy_expire，通知 slave 同步或异步删除。

对于 slave，返回 1，并更新 

源码参考 db.c/typeCommand。注意这里会对 key 进行是否过期判断。



#### pttl/ttl 命令

从 client->db->expires 中查找 key 对应的值，如果不存在（没有设置过期时间），返回 -1，如果存在，计算 expire 与当前时间，返回剩余过期时间。如果已经过期返回 -1。

源码参考 expire.c/ttlGenericCommand



#### pexpire/pexpireat/expireat/expire 命令

格式：expire key seconds 

返回：设置成功返回 1。当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中 你尝试更新 key 的生存时间)，返回 0 。 

时间复杂度：O(1)

实现原理：找到 key 对应的值，如果 seconds 在将来，将 key 加入 client->db->expires 中，并设置值为 seconds

源码参考 expire.c/expireGenericCommand



#### persist命令

格式：pexpire key milliseconds 

返回值: 设置成功，返回 1 ；key 不存在或设置失败，返回 0 

时间复杂度：O(1)

实现原理：从 client->db->expires 中删除 key

源码参考 expire.c/persistCommand



#### rename

格式：rename key newkey

​	将 key 改名为 newkey 。 

​	当 key 和 newkey 相同，返回 OK

​    当 key 不存在时，返回一个错误。 

​	当 newkey 已经存在时， RENAME 命令将覆盖旧值。 

时间复杂度：O(1) 

返回值: 改名成功时提示 OK ，失败时候返回一个错误。 

实现原理：

1、如果 newkey 存在，删除 newkey

2、增加 newkey

3、将 key 的 expire 赋值给 newkey

4、删除 key

源码参考 db.c/renameGenericCommand



#### renamenx 命令

格式：renamenx key newkey

​		当 key 等于 newkey，返回 0。

​		当且仅当 newkey 不存在时，将 key 改名为 newkey 。 

​		当 key 不存在时，返回一个错误。 

时间复杂度：O(1) 

返回值：修改成功时，返回 1 。 如果 newkey 已经存在，返回 0 。 

实现原理：

1、如果 newkey 存在，引用计数减一

2、增加 newkey

3、将 key 的 expire 赋值给 newkey

4、删除 key

源码参考 db.c/renameGenericCommand



#### touch 命令

格式

时间复杂度：O(1) 

返回值:

实现原理

找到 key，更新 client->db->dict 中 key 对应 redisObject 的 lru。

源码参考 expire.c/touchCommand



#### exists 命令

用于判断指定的key是否存在，并返回key存在的数量。使用频率较高。

格式：exists key1 key2 ... keyN

说明：检查key是否存在，返回key存在的数量。

实现原理：查询 key 对应的值，并更新 LFU 信息

源码参考： db.c/existsCommand



#### keys 命令

说明：匹配合适的key并一次性返回，如果匹配的键较多，则可能阻塞服务器，因此该命令一般禁止在线上使用。

格式：keys pattern

备注：keys命令的作用是查找所有符合给定模式“pattern”的key，使用该命令处理大数据库时，可能会造成服务器长时间阻塞（秒级）。其中 * 表示返回所有的 key，pattern 支持 ? [] * 等正则字符。

实现原理：检查 key 是否过期，如果没有过期，并且匹配 pattern。就加入返回对应的 key。

源码参考：db.c/keysCommand



#### scan命令

说明：遍历数据库中几乎所有的键，并且不用担心阻塞服务器。使用频率较低。

格式：scan cursor [MATCH pattern] [COUNT count]

备注：scan命令和hscan、sscan、zscan命令都用于增量迭代，每次只返回少量数据，不会有像keys命令堵塞服务器的隐患。

实现原理：

源码参考：db.c/scanCommand





### 超时处理机制

源码参考 db.c/expireIfNeeded

1、如果 db->expires 中查找的 key 对应的值，如果不存在，返回 0。如果与当前时间比较，过期返回 1

2、如果过期，server.stat_expiredkeys加 1

3、AOF 同步

4、slave 传播

5、删除 key：如果开启 lazyfree_lazy_expire ，后台线程 lazy 删除；同步从 db->dict、 db->expires、集群的 slot 中删除 key

6、更新server.stat_keyspace_misses







### zset



充分利用 dict 和 skiplist 的特性

对于 dict 查询需要 O(1)，zrange，zrank 需要 O(N*logN)

对于 skiplist，查询需要 O(logN)，zrange，zrank 需要 O(logN)

## 思考题

1、OBJECT_ENCODING_EMBSTR 和 OBJ_ENCODING_RAW 都表示简单动态字符串，两种编码的区别是啥？

```c
typedef struct redisObject {
    unsigned type:4;
    unsigned encoding:4;
    unsigned lru:LRU_BITS; /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    int refcount;
    void *ptr;
} robj;
```

当字符串比较短时，底层使用OBJECT_ENCODING_EMBSTR，robj 和 sds一起分配，连续存储，充分利用cpu高速缓存，提升内存分配效率与数据访问效率。