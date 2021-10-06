


当一个服务只使用一个数据源
当多个服务只使用一个数据源
当一个服务使用到多个不同的数据源
当多个个服务使用到多个不同的数据源



## 本地事务

### 原子性和持久性

1、Commit Log
2、Shadow Paging
3、WAL(Write Ahead Log)

FORCE：当事务提交后，要求变动数据必须同时完成写入则称为FORCE
STEAL：在事务提交前，允许变动数据提前写入则称为STEAL

```
         NO-STEAL     STEAL
NO-FORCE redo log   redo log/undo log
FORCE    no log     undo log
```

### 隔离性

写锁
读锁
范围锁

读+写：MVCC
写写：乐观锁、悲观锁


## 全局事务

X/Open XA

### 2PC



### 3PC


## 共享事务

很少见

## 分布式事务


### FLP

### CAP

### BASE

基本可用性（Basically Available）
柔性事务（Soft State）
最终一致性（EventuallyConsistent）

可靠事件队列

### TCC

Try
Confirm
Cancel

数据库专家Pat Helland在2007年 [Life beyond DistributedTransactions:An Apostate’s Opinion](https://www-db.cs.wisc.edu/cidr/cidr2007/papers/cidr07p15.pdf)

### Saga

把一个大事务分解为可以交错运行的一系列子事务集合


将大事务拆分成若干个小事务
为每一个子事务设计对应的补偿动作

正向恢复（Forward Recovery）：提交成功
反向恢复（Backward Recovery）：提交失败


1987年普林斯顿大学的Hector Garcia-Molina和Kenneth Salem在ACM发表 [SAGAS](https://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf)




## 参考

[ARIES:A Transaction RecoveryMethod Supporting Fine-Granularity Locking and Partial Rollbacks UsingWrite-Ahead Logging](https://cs.stanford.edu/people/chrismre/cs345/rl/aries.pdf)
[ARIES/KVL:A Key-Value LockingMethod for Concurrency Control of Multiaction Transactions Operating on B-Tree Indexes](http://vldb.org/conf/1990/P392.PDF)
[ARIES/lM:An Efficient and High Concurrency IndexManagement Method Using Write-Ahead Logging](http://madsys.cs.tsinghua.edu.cn/publications/ASPLOS20-ma.pdf)

## 附录

### [ARIES 理论](https://en.wikipedia.org/wiki/Algorithms_for_Recovery_and_Isolation_Exploiting_Semantics)
（Algorithms for Recoveryand Isolation Exploiting Semantic，ARIES），直接翻译过来是“基于语义的恢复与隔离算法”。

### [并发控制（Concurrency Control）理论](https://en.wikipedia.org/wiki/Concurrency_control)
决定了隔离程度与并发能力是相互抵触的，隔离程度越高，并发访问时的吞吐量就越低。
现代数据库一定会提供除可串行化以外的其他隔离级别供用户使用，让用户自主调节隔离级别，根本目的是让用户可以调节数据库
的加锁方式，取得隔离性与吞吐量之间的平衡。


### [多版本并发控制](https://en.wikipedia.org/wiki/Multiversion_concurrency_control)

CREATE_VERSION和DELETE_VERSION，这两个字段记录的值都是事务ID，事务ID是一个全局严格递增的数值，然后根据以下规则写入数据。
·插入数据时：CREATE_VERSION记录插入数据的事务ID，DELETE_VERSION为空。
·删除数据时：DELETE_VERSION记录删除数据的事务ID，CREATE_VERSION为空。
·修改数据时：将修改数据视为“删除旧数据，插入新数据”的组合，即先将原有数据复制一份，原有数据的DELETE_VERSION记录修改数据的事务ID，CREATE_VERSION为空。复制后的新数据的CREATE_VERSION记录修改数据的事务ID，DELETE_VERSION为空。

·隔离级别是可重复读：总是读取CREATE_VERSION小于或等于当前事务ID的记录，在这个前提下，如果数据仍有多个版本，则取最新（事务ID最大）的。
·隔离级别是读已提交：总是取最新的版本即可，即最近被提交的那个版本的数据记录。
