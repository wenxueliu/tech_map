

### 学习方法

如何多维度地学习 Redis。

顺序式：从基本的数据结构， 命令处理流程，命令实现，持久化，到主从，哨兵和集群，关注系统理解 Redis。《Redis 设计与实现》、《Redis5 设计与源码分析》
主题式：以某个知识点为切入点，结合实践案例掌握该知识点，关注深入理解 Redis 每个知识点。《Redis 核心技术与实战》
拿来式：从Redis的使用角度，从部署、开发、运维角度，根据使用经验给出每一步的最佳实践，关注如何更好地用好 Redis


### 高屋建瓴-架构图

当你在使用 Redis 的时候，你应该思考什么


### 部署

根据业务选择不同的部署形态，每种部署形态的最佳实践



### 使用

常用数据结构

![redis-data-stucture](redis-data-structure.jpg)

各种数据结构的使用场景

为了保证 redis 的高性能，哪些坑不能踩



#### 高性能

高效的数据结构

操作尽量不能阻塞命令执行。比如 RDB 文件要小，一般 4G 左右。
开启RDB+AOF的时候，不要绑定CPU



### 运维

缓存容量预估
1、总数据量的15%-30%，具体根据应用的访问特性(各类数据访问频繁占比)和内存开销



运维 checklist

1、关闭 huge page。huge page 申请大内存导致阻塞

2、高可靠：开启 AOF + RDB。aof-use-rdb-preamble yes yes

3、允许分钟级别的数据丢失，只开启 RDB

4、只用 AOF，推荐使用 everysec 配置

5、内存容量至少是总缓存容量的 2 倍。因为要考虑到写RDB。

6、开启定时RDB和AOF重写，进程一定不要绑定CPU。



Redis 变慢如何定位

#### 高可靠

1. 数据尽量少丢失：RDB + AOF
2. 服务尽量少中断：集群 + HA



### 运维最佳实践



推荐配置

```

// 最大容量 4GB
maxmemory 4gb

// 订阅客户端和服务器间的消息发送方式，不属于阻塞式发送。如果频道消息较多的话，也会占用较多的输出缓冲区空间
client-output-buffer-limit pubsub 8mb 2mb 60

// 普通客户端来说，它每发送完一个请求，会等到请求结果返回后，再发送下一个请求，这种发送方式称为阻塞式发送。
client-output-buffer-limit normal 0 0 0

// 主节点的数据量大小、主节点的写负载压力和主节点本身的内存大小。假设一条写命令数据是 1KB，那么，复制缓冲区可以累积 512K 条（512MB/1KB = 512K）写命令。同时，主节点在全量复制期间，可以承受的写命令速率上限是 2000 条 /s（128MB/1KB/60 约等于 2000）。
client-output-buffer-limit slave 512mb 128mb 60
```

1、RDB 文件大小控制在 2-4G

2、设置 maxmemory，根据情况采用 LRU 和 LFU 淘汰策略

3、如果允许少量丢失，AOF 同步配置为 everysec。如果既需要高性能，又需要高可靠数据保证，我建议你考虑采用高速的固态硬盘作为 AOF 日志的写入设备。

4、不要把一个 redis 实例和一个逻辑核绑定，一个 Redis 绑定到一个物理核。同一 redis 实例，避免跨 socket 访问。

5、redis 进程的 swap 使用情况，避免 swap。

6、关闭 Huge Page。`echo never > /sys/kernel/mm/transparent_hugepage/enabled`

7、打开内存碎片整理开关（`config set activedefrag yes`），并合理配置。

### 使用最佳实践

1、不要在 Redis 主节点做大数据的聚合操作，建议读取到客户端做聚合操作或者如果在从库做聚合操作。

注：如果是在集群模式使用多个key聚合计算的命令，一定要注意，因为这些key可能分布在不同的实例上，多个实例之间是无法做聚合运算的，这样操作可能会直接报错或者得到的结果是错误的！

在从库上操作，可以使用SUNION、SDIFF、SINTER，这些命令做聚合操作，而不是SUNIONSTORE、SDIFFSTORE、SINTERSTORE。

2、不要执行全量查询操作。如SMEMBERS,HGETALL,KEYS，而是用 SCAN 的批量操作

3、key 的过期时间要增加随机值，避免大量 key 同时过期

4、FLUSHDB ASYNC，FLUSHALL ASYNC 清空数据库

5、UNLINK 删除元素而不是 DELETE

13、不要再生产环境中**持续**使用 monitor命令

2、对于排序需求（比如评论、排行榜）可以用 List 或 Sorted Set，对于分页场景，推荐使用 Sorted Set

3、对于二值状态（比如签到、商品有没有、用户是否存在），推荐使用 bitmap。对于连续签到场景，可以使用 bitmap 的与操作。

4、基数操作（统计网页的 UV），对于准确度要求高的场景，可以用 Set，Hash，对于准确度要求低的场景，推荐使用HyperLogLog

5、对于时间序列，方案一、 Hash + Sorted Set。方案二、TimeSeries



### 版本关键特性

1、5.0 引入 Stream

2、4.0 引入 lazy free 机制

3、6.0 引入 lazyfree-lazy-user-del，del 和 unlink 没有区别了

4、6.0 支持绑核配置

5、4.0.RC3 版本之后，引入内存碎片整理清理技术

### 应用场景

1、时间序列

2、在线用户、登录用户

3、签到

4、消息队列

5、LBS

6、

使用Sorted Set可以实现统计一段时间内的在线用户数：用户上线时使用zadd online_users $timestamp $user_id把用户添加到Sorted Set中，使用zcount online_users $start_timestamp $end_timestamp就可以得出指定时间段内的在线用户数。

如果key是以天划分的，还可以执行zinterstore online_users_tmp 2 online_users_{date1} online_users_{date2} aggregate max，把结果存储到online_users_tmp中，然后通过zrange online_users_tmp 0 -1 withscores就可以得到这2天都在线过的用户，并且score就是这些用户最近一次的上线时间。







附录

时间序列的需求

1、快速写入

2、单点查询

3、范围查找

4、聚合计算

消息队列的需求

1、消息的有序

2、消息的重复

3、消息的可靠