
## 数据层面

### RDB


### AOF



## 服务

### 集群

### 主从复制



实现原理

1、第一次同步

2、增量同步



![ha sync](ha_sync.jpg)



1、由于主库同步从库过程中，redis 仍然要处理请求，新增的key 如何处理？ 保存在 replication buffer

2、从库变多，主库压力太大，如何处理？主-从-从级联模式

3、主从复制的增量命令同步通过长连接，避免频繁建立连接。

4、主从断网如何处理？2.8 以前是全量同步。2.8 之后，将断网期间的命令保存在repl_backlog_buffer（环形缓冲区，多个从库共享）中。master_repl_offset，slave_repl_offset

5、由于主从复制临时写命令采用环形缓冲区，因此，可能存在 master_repl_offset - slave_repl_offset 大于环形缓冲区的可能性，此时，就需要全量同步了。



#### 环形缓存器

缓冲空间的计算公式是：缓冲空间大小 = 主库写入命令速度 * 操作大小 - 主从库间网络传输命令速度 * 操作大小

即 repl_backlog_size = 缓冲空间大小 * 2，这也就是 repl_backlog_size 的最终值

![ha_copy](ha_copy.jpg)

### sentiel机制