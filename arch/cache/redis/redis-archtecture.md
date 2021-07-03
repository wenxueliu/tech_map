redis-arch 



### 逻辑架构



网络模型：多路复用

通信协议：RESP 2、 RESP 3

### 索引模型

Hash 模型

![redis_data_arch](redis_data_arch.jpg)

说明

1、全局哈希表有两个，主要原因是当 Hash 冲突过高的时候，需要重哈希。

2、全局 Hash 表中的元素都是指针，因此，重哈希并不会导致大量的数据拷贝。

### 数据模型

K-V 存储。其中 值包括 6 种底层数据结构（ziplist,list,array,skiplist,dict），5 中数据结构（string,set,hash,sorted set,stream），3 种扩展结构(hypelog, geo,)

![redis data structure](redis-data-structure.jpg)

### 线程模型

单线程

操作接口：GET\PUT\DELETE\SCAN



存储模型：内存分配、磁盘读写、数据压缩

主从复制

哨兵机制

集群复制：

数据统计：

淘汰策略：

过期策略：

### 物理架构

单机架构

特点

 1、无法高可用；2、读写都在同一台机器，处理能力有限；





主从架构



1、无法保证高可用；3、读写分离，减轻读压力；但没有解决 master 写的压力；



哨兵架构



1、自动故障迁移，保证高可用；3、读写分离，减轻读压力，但没有解决 master 写的压力



集群代理架构



集群直连架构



1、自动故障迁移，保证高可用；3、写和读的压力都分散到各个节点