
##数据库

文件

    db.c

```
    /* Redis database representation. There are multiple databases identified
     * by integers from 0 (the default database) up to the max configured
     * database. The database number is the 'id' field in the structure. */
    typedef struct redisDb
        dict *dict;                 /* The keyspace for this DB */
        dict *expires;              /* Timeout of keys with a timeout set */
        dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP) */
        dict *ready_keys;           /* Blocked keys that received a PUSH */
        dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
        struct evictionPoolEntry *eviction_pool;    /* Eviction pool of keys */
        int id;                     /* Database ID */
        long long avg_ttl;          /* Average TTL, just for stats */
```

robj *lookupKey(redisDb *db, robj *key)

    从 db->dict 查找 key 对应的 value. 并返回. 如果 没有 rdb 或 aof 正在运行, 对 val 加锁.

robj *lookupKeyRead(redisDb *db, robj *key)

    在 db->expires 检查 key 是否过期, 从 db->dict 查找 key 对应的 value. 并返回. (如果没有 rdb 或 aof 正在运行, 对 val 加锁)

robj *lookupKeyWrite(redisDb *db, robj *key)

    在 db->expires 检查 key 是否过期, 并返回 db->dict 中 key 对应的值

robj *lookupKeyReadOrReply(client *c, robj *key, robj *reply)

    在 db->expires 检查 key 是否过期, 应答客户端, 并返回 db->dict 中 key 对应的值

robj *lookupKeyWriteOrReply(client *c, robj *key, robj *reply)

    在 db->expires 检查 key 是否过期, 应答客户端, 并返回 db->dict 中 key 对应的值

void dbAdd(redisDb *db, robj *key, robj *val)

    将 key, val 加入 db->dict, 如果 val->type 为 OBJ_LIST, 加入 db->ready_keys,
    将 key 复制到对应的 slot

void dbOverwrite(redisDb *db, robj *key, robj *val)

    将 db->dict 中 key 的值用 val 替代

void setKey(redisDb *db, robj *key, robj *val)

    设置 db->dict 中 key 的值为 val, 如果不存在就创建. 存在就设置, 之后删除
    过期的 key, 并通知所有的 watch key 的客户端的标志为 CLIENT_DIRTY_CAS

int dbExists(redisDb *db, robj *key)

    检查 db->dict 中是否存在 key.

robj *dbRandomKey(redisDb *db)

    从 db->dict 中找到随机的 key 并返回. 如果该 key 过期, 就循环, 直到找到
    可用的 key.


void signalModifiedKey(redisDb *db, robj *key)

    设置所有 watch db->dict 中 key 的客户端的 flag 为 CLIENT_DIRTY_CAS


long long getExpire(redisDb *db, robj *key)

    从 db->expires 中查找 key 对应的 value 并返回.(该值为 key 的过期时间),
    如果没有查到返回 -1

void propagateExpire(redisDb *db, robj *key)

    如果 aof 打开, 将过期 key 的操作写入 aof 文件

robj *dbUnshareStringValue(redisDb *db, robj *key, robj *o)

    如果 o 的编码不是 OBJ_ENCODING_RAW, 将编码为 OBJ_STRING 类型 o 替换原值.
    否则返回 o

long long emptyDb(void(callback)(void*))

    遍历所有的数据库, 将 server->db->dict 和 server.db->expires 数据情况, 并调用对应的回调函数
    并将其同步的所有的 slot

int selectDb(client *c, int id)

    c->db = &server.db[id];

void signalFlushedDb(int dbid)

    遍历所有 client 的所有 wathch 的 keys, 如果 key 属于 dbid, 设置 flags 为 CLIENT_DIRTY_CAS

void flushdbCommand(client *c)

    遍历所有 client 的所有 wathch 的 keys, 如果 key 属于 dbid, 设置 flags 为 CLIENT_DIRTY_CAS
    清空 c->db->dict, c->db->expires
    同步到所有 slot

void flushallCommand(client *c)

    遍历所有 client 的所有 wathch 的 keys, 设置 key 对应的 client 的 flags 为 CLIENT_DIRTY_CAS
    如果 rdb 正在进行, 停止 rdb 并删除临时文件.
    主进程进行 rdb 文件写.

void delCommand(client *c)

    将 c->argv 中的元素从 c->db 中删除

void existsCommand(client *c)

    遍历 c->argv 中如果存在于 c->db 的元素数量

void selectCommand(client *c)

    c->db 指向 server->db[c->argv[1]]

void randomkeyCommand(client *c)

    从 c->db 中随机选择一个 key

void keysCommand(client *c)

    从 c->db->dict 中找到和 c->argv[1] 匹配的 key. 其中 * 表示所有的 key

void scanCallback(void *privdata, const dictEntry *de)

    根据 privdata[1]->type 获取 de 对应的 key, value. 加入 privdata[0]

int parseScanCursorOrReply(client *c, robj *o, unsigned long *cursor)


void scanGenericCommand(client *c, robj *o, unsigned long cursor)

void scanCommand(client *c)

void dbsizeCommand(client *c)

    返回 c->db->dict 的元素个数

void lastsaveCommand(client *c)

    返回 server.lastsave

void typeCommand(client *c)

    从 c->db->dict 查找 c->argv[0]　元素对应的类型

void shutdownCommand(client *c)

    调用 prepareForShutdown(flag), 其中 flag 为 SHUTDOWN_NOSAVE 或 SHUTDOWN_SAVE

    /* When SHUTDOWN is called while the server is loading a dataset in
     * memory we need to make sure no attempt is performed to save
     * the dataset on shutdown (otherwise it could overwrite the current DB
     * with half-read data).

void renameGenericCommand(client *c, int nx)

    将 c->db->dict 中的 key 从 c->argv[1] 重命名为 c->argv[2].
    即增加新值删除旧值, 并保留 expire

    注 : 如果 nx == 1, c->argv[2] 存在, 返回错误.
         否则 c->argv[2] 存在, 将从 c->db->dict 中删除, 之后进行重命名

void renameCommand(client *c)

    如果 c->argv[2] 存在, 将从 c->db->dict 中删除

    将 c->db->dict 中的 key 从 c->argv[1] 重命名为 c->argv[2].
    即增加新值删除旧值, 并保留 expire

void renamenxCommand(client *c)

    如果 c->argv[2] 存在, 返回错误.

    将 c->db->dict 中的 key 从 c->argv[1] 重命名为 c->argv[2].
    即增加新值删除旧值, 并保留 expire

void moveCommand(client *c)

    将 c->argv[1] 增加到 c->argv[2] 指向的 db 中之后, 从 c->db 中删除.
    并保留原来的 expire

int removeExpire(redisDb *db, robj *key)

    从 db->expires 中删除 key 对应的元素.

int removeExpire(redisDb *db, robj *key)

    从 db->expires 删除 key, 如果删除成功, 返回 1, 失败返回 0

void setExpire(redisDb *db, robj *key, long long when)

    确保 key 存在于 db->dict, 之后将其从 db->expires 中的 key 的值设置为 when


int dbDelete(redisDb *db, robj *key)

    从 db->expires, db->dict 中删除 key 对应的值. 并从对应的 slot 中删除该 key

int expireIfNeeded(redisDb *db, robj *key)

    如果 slave : 过期, 立即返回 1, 没有过期返回 0
    如果 master : 过期, 同步给 salve, 从 db 中删除该 key.

void expireGenericCommand(client *c, long long basetime, int unit)

    在 c->db->dict 中查找 c->argv[0] 对应的元素.
    如果元素过期, basetime += c->argv[1] * unit 小于 now, 删除该元素.
    否则, 设置该元素的过期时间.

void expireCommand(client *c)

    设置 c->db->dict 中 c->argv[1] 对应元素的 expire 为 now + c->argv[1] * 1000,

void expireatCommand(client *c)

    设置 c->db->dict 中 c->argv[1] 对应元素的 expire 为 c->argv[1] * 1000,
    如果已经过期, 删除之.

void pexpireCommand(client *c)

    设置 c->db->dict 中 c->argv[1] 对应元素的 expire 为 now + c->argv[1]

void pexpireatCommand(client *c)

    设置 c->db->dict 中 c->argv[1] 对应元素的 expire 为 c->argv[1] * 1000,
    如果已经过期, 删除之.

void ttlGenericCommand(client *c, int output_ms)

    如果 c->db->dict 中 c->argv[1] 不存在返回 -2
    如果 c->db->dict 中 c->argv[1] 存在, 不过期, 返回 -1
    如果 c->db->dict 中 c->argv[1] 存在, 过期, 返回 剩余过期时间, 如果 output_ms
    = 1, 返回 ms, 否则返回 sec

void ttlCommand(client *c)

    如果 c->db->dict 中 c->argv[1] 不存在返回 -2
    如果 c->db->dict 中 c->argv[1] 存在, 不过期, 返回 -1
    如果 c->db->dict 中 c->argv[1] 存在, 过期, 返回 剩余过期时间 ms

void pttlCommand(client *c)

    如果 c->db->dict 中 c->argv[1] 不存在返回 -2
    如果 c->db->dict 中 c->argv[1] 存在, 不过期, 返回 -1
    如果 c->db->dict 中 c->argv[1] 存在, 过期, 返回 剩余过期时间 sec

void persistCommand(client *c)

    将 c->db->dict 中 c->argv[1] 的 expire 删除. 使其不会过期

int *getKeysUsingCommandTable(struct redisCommand *cmd,robj **argv, int argc, int *numkeys)

    遍历 cmd->firstkey cmd->keystep cmd->lastkey, 其中 numkeys 为 key 的个数,
    返回包含所有元素的数组.

int *getKeysFromCommand(struct redisCommand *cmd, robj **argv, int argc, int *numkeys)

    if (cmd->getkeys_proc) return cmd->getkeys_proc(cmd,argv,argc,numkeys);
    else return getKeysUsingCommandTable(cmd,argv,argc,numkeys);

void getKeysFreeResult(int *result)

    释放 result

int *zunionInterGetKeys(struct redisCommand *cmd, robj **argv, int argc, int *numkeys)

    argc 为 所有参数个数 = numkeys + 3
    argv[0] 为 ZUNIONSTORE 或 ZINTERSTORE
    argv[1] 为 dstkey
    argv[2] 为 key 的数量
    argv[3...] 为具体的 key

    返回 所有 key 的在 argv 中的索引的数组, numkeys 为 具体 key 的数量(包括 dstkey)

    ZUNIONSTORE <destkey> <num-keys> <key> <key> ... <key> <options>
    ZINTERSTORE <destkey> <num-keys> <key> <key> ... <key> <options> */

int *evalGetKeys(struct redisCommand *cmd, robj **argv, int argc, int *numkeys)

    argc 为 所有参数个数 = numkeys + 3
    argv[0] 为 EVAL 或 EVALSHA
    argv[1] 为 script
    argv[2] 为 key 的数量
    argv[3...] 为具体的 key

    返回 所有 key 的在 argv 中的索引的数组, numkeys 为 具体 key 的数量(包括 dstkey)

    EVAL <script> <num-keys> <key> <key> ... <key> [more stuff]
    EVALSHA <script> <num-keys> <key> <key> ... <key> [more stuff] */

int *sortGetKeys(struct redisCommand *cmd, robj **argv, int argc, int *numkeys)

    argv : SORT <sort-key> limit key1 key2 get key3 by key4 store <store-key> ...

    如果包含 store 返回 2
    如果不包含 store 返回 1

    key[0] = 1
    key[1] = store->key 的索引

int *migrateGetKeys(struct redisCommand *cmd, robj **argv, int argc, int *numkeys)

    如果 argc > 6. 从 argv 的 i(i=6) 开始找到 argv[i] == "keys" 并且 argv[3] 的长度为 0, 返回包含 key 的索引, numkeys 为 key 的数量
    否则 numkeys 为 1, 返回 keys 的元素只包含 3

void slotToKeyAdd(robj *key)

    将 key 插入经过 hash 之后的 server.cluster->slots_to_keys

void slotToKeyDel(robj *key)

    找到 key 对应的 slot, 并将其从 server.cluster->slots_to_keys hash 中删除

void slotToKeyFlush(void)

    释放 server.cluster->slots_to_keys 原有数据, 重新初始化

unsigned int getKeysInSlot(unsigned int hashslot, robj **keys, unsigned int count)

    将 server.cluster->slots_to_keys 中与 hashslot 相同的 key 加入 keys,
    返回加入的个数. 其中 count 表示搜索的个数.

unsigned int delKeysInSlot(unsigned int hashslot)

    将 server.cluster->slots_to_keys 中与 hashslot 相同的 key 从 server.db
    中删除, 返回删除的 key

unsigned int countKeysInSlot(unsigned int hashslot)

    TODO
