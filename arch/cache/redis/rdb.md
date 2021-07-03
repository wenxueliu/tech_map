

### 为什么需要 RDB



1、AOF 恢复速度不够快，逐一执行命令的效率太低



### 实现原理

1、保存哪些数据：某时刻的全量数据

2、保存数据的格式：内存快照

3、保存数据的时机：自动：过去 x 秒有 y 次数据修改bgsave。手动 save 或 bgsave

5、保存期间是否可以处理新的请求？如果可以，新数据如何处理？可以。通过 fork 的 COW



![bgsave](bgsave.jpg)



执行流程

1、创建 temp-{pid}.rdb 文件

2、遍历所有 db，对每个 db 遍历所有 key,value 根据不同的数据类型，写入文件。

4、重命名 temp-{pid}.rdb 为 rdb 文件



### RDB 保存

```python

def rdb_save(rsi, filename):
    # 创建新 AOF 文件
    tmpf = create_file("temp-%s.rdb", pid)
    rioInitWithFile(&rdb,fp);
    rdbWriteRaw(rdb,magic,9)
    rdbSaveInfoAuxFields(rdb,flags,rsi)
    rdbSaveModulesAux(rdb, REDISMODULE_AUX_BEFORE_RDB)
    # 遍历数据库
    for db in redisServer.db:
        # 忽略空数据库
        if db.is_empty(): continue
    		rdbSaveType(rdb,RDB_OPCODE_SELECTDB)
        rdbSaveLen(rdb,j)
        rdbSaveType(rdb,RDB_OPCODE_RESIZEDB)
        rdbSaveLen(rdb, dictSize(db->dict);)
        rdbSaveLen(rdb,dictSize(db->expires))
        for key,value in db:
          	expire = getExpire(db,key);
        		rdbSaveKeyValuePair(rdb,key,value,expire)
        
        if (rsi && dictSize(server.lua_scripts))
            for body in lua_scripts:
                rdbSaveAuxField(rdb,"lua",3,body->ptr,sdslen(body->ptr)
        rdbSaveModulesAux(rdb, REDISMODULE_AUX_AFTER_RDB)
        rdbSaveType(rdb,RDB_OPCODE_EOF)
        rioWrite(rdb,rdb->cksum,8)
     fflush(tmpf)
     fsync(tmpf)
     fclose(tmpf)
     rename(tmpf, filename)
                                
        
def rdbSaveKeyValuePair(rdb, key, val, expiretime):                       
		savelru = server.maxmemory_policy & MAXMEMORY_FLAG_LRU;
    savelfu = server.maxmemory_policy & MAXMEMORY_FLAG_LFU;
    if (expiretime != -1):                            
    		rdbSaveType(rdb,RDB_OPCODE_EXPIRETIME_MS)
    		rdbSaveMillisecondTime(rdb,expiretime)
    if (savelru)
        rdbSaveType(rdb,RDB_OPCODE_IDLE)
        rdbSaveLen(rdb,estimateObjectIdleTime(val))
    if (savelfu)
        rdbSaveType(rdb,RDB_OPCODE_FREQ)                        
        rdbWriteRaw(rdb,LFUDecrAndReturn(val),1)                        
    rdbSaveObjectType(rdb,val)
    rdbSaveStringObject(rdb,key)
    rdbSaveObject(rdb,val,key)                         
```

注：rdb 为了减少存储空间，如果配置了 server.rdb_compression还进行了 LZF 压缩



### RDB 加载

```c
int rdbLoad(char *filename, rdbSaveInfo *rsi) {
    FILE *fp;
    rio rdb;
    int retval;

    if ((fp = fopen(filename,"r")) == NULL) return C_ERR;
    startLoading(fp);
    rioInitWithFile(&rdb,fp);
    retval = rdbLoadRio(&rdb,rsi,0);
    fclose(fp);
    stopLoading();
    return retval;
}

int rdbLoadRio(rio *rdb, rdbSaveInfo *rsi, int loading_aof) {
	  //校验文件前 9 个字符为 REDIS+版本号
	  while(1) {
	  		dbLoadType(rdb);
	  		if (type == RDB_OPCODE_EXPIRETIME) {
	  				expiretime = rdbLoadTime(rdb);
	  		} else if (type == RDB_OPCODE_EXPIRETIME_MS) {
	  				expiretime = rdbLoadMillisecondTime(rdb,rdbver);
	  		} else if (type == RDB_OPCODE_FREQ) {
	  		    rioRead(rdb,lfu_freq,1);
        } else if (type == RDB_OPCODE_IDLE) {
        		lru_idle = rdbLoadLen(rdb,NULL);
        } else if (type == RDB_OPCODE_EOF) {
            break;
        } else if (type == RDB_OPCODE_SELECTDB)
	  				db = server.db + rdbLoadLen(rdb,NULL);
        } else if (type == RDB_OPCODE_RESIZEDB) {
        		db_size = rdbLoadLen(rdb,NULL);
            expires_size = rdbLoadLen(rdb,NULL)
        } else if (type == RDB_OPCODE_AUX) {
            auxkey = rdbLoadStringObject(rdb);
            auxval = rdbLoadStringObject(rdb); 
        } else if (type == RDB_OPCODE_MODULE_AUX) {
            moduleid = rdbLoadLen(rdb,NULL);
            when_opcode = rdbLoadLen(rdb,NULL);
            when = rdbLoadLen(rdb,NULL);
	      }
        key = rdbLoadStringObject(rdb);
        val = rdbLoadObject(type,rdb,key);
        if (server.masterhost == NULL && !loading_aof && expiretime != -1 && expiretime < now) {
            decrRefCount(key);
            decrRefCount(val);
        } else {
            dbAdd(db,key,val);
            if (expiretime != -1) setExpire(NULL,db,key,expiretime);
            objectSetLRUOrLFU(val,lfu_freq,lru_idle,lru_clock);
            decrRefCount(key);
        }
	  }
    if (rdbver >= 5) {
    	  rioRead(rdb,&cksum,8);
    }
}
```



### 将 rdb 发送给从节点

用画图的方式更加直观





### 总结

数据不允许丢失：Redis 4.0 提出解决方案：RDB 做全量备份 +  AOF 重写将RDB写入AOF  + AOF 做增量

允许分钟级别的丢失：RDB 备份

只用 AOF：使用 everysec 配置。



### 思考题

#### 2核CPU、4GB内存、500G磁盘，Redis实例占用2GB，写读比例为8:2，此时做RDB持久化，产生的风险主要在于 CPU资源 和 内存资源 这2方面：

a、内存资源风险：Redis fork子进程做RDB持久化，由于写的比例为80%，那么在持久化过程中，“写实复制”会重新分配整个实例80%的内存副本，大约需要重新分配1.6GB内存空间，这样整个系统的内存使用接近饱和，如果此时父进程又有大量新key写入，很快机器内存就会被吃光，如果机器开启了Swap机制，那么Redis会有一部分数据被换到磁盘上，当Redis访问这部分在磁盘上的数据时，性能会急剧下降，已经达不到高性能的标准（可以理解为武功被废）。如果机器没有开启Swap，会直接触发OOM，父子进程会面临被系统kill掉的风险。

b、CPU资源风险：虽然子进程在做RDB持久化，但生成RDB快照过程会消耗大量的CPU资源，虽然Redis处理处理请求是单线程的，但Redis Server还有其他线程在后台工作，例如AOF每秒刷盘、异步关闭文件描述符这些操作。由于机器只有2核CPU，这也就意味着父进程占用了超过一半的CPU资源，此时子进程做RDB持久化，可能会产生CPU竞争，导致的结果就是父进程处理请求延迟增大，子进程生成RDB快照的时间也会变长，整个Redis Server性能下降。

c、另外，可以再延伸一下，老师的问题没有提到Redis进程是否绑定了CPU，如果绑定了CPU，那么子进程会继承父进程的CPU亲和性属性，子进程必然会与父进程争夺同一个CPU资源，整个Redis Server的性能必然会受到影响！所以如果Redis需要开启定时RDB和AOF重写，进程一定不要绑定CPU。



#### RDB 是如何高效保存数据的？



