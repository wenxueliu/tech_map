Mysql 配置建议



## 假设

1、Mysql 8.0

2、采用 InnoDB 引擎



## 配置建议

```mysql
// 每个InnoDB 表数据存储在一个以 .ibd 为后缀的文件。这样更容易管理，drop table 直接删除文件，释放空间，如果为 OFF，那么，删除表，空间不会释放
innodb_file_per_table=ON
```











## 运维最佳实践





### 索引扫描行数估计不准确

analyze table t 对索引信息重新统计



### Online DDL

对于长期运行的 Mysql 实例，由于大量的增删改导致存在大量的数据空洞。

建议

1、在业务低峰期

2、使用 ghost

补充：

1、在 5.6 之前，DDL 不是 Online 的，通过 copy 表实现。5.6 之后，DDL 是 Online 的，通过拷贝文件实现，也是 inplace 的。Online 和 不是 Online 是对 Server 层来说的。

2、Online DDL 是 inplace DDL。但是 inplace DDL 不一定是 Online 的，比如 alter table t add FULLTEXT(field_name) 和 alter table t add SPATIAL field_name 不是 Online 的。Online 的核心在于 DDL 的时候，还可以对表进行增删改。

3、optimize table t  =  recreate + analysis