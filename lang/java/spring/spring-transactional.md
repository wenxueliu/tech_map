Spring-transactional



@Transactional 注解

使用地方：

1. 类定义和类方法（不建议用接口，cglib 不支持）

2. public 方法

   ​	

JTA 事务

非 JTA 事务





## 事务的传播行为

Spring事务有7种传播行为:

1. PROPAGATION_REQUIRED 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。
2. PROPAGATION_SUPPORTS 支持当前事务，如果当前没有事务，就以非事务方式执行。
3. PROPAGATION_MANDATORY 支持当前事务，如果当前没有事务，就抛出异常。
4. PROPAGATION_REQUIRES_NEW 新建事务，如果当前存在事务，把当前事务挂起。
5. PROPAGATION_NOT_SUPPORTED 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
6. PROPAGATION_NEVER 以非事务方式执行，如果当前存在事务，则抛出异常。
7. PROPAGATION_NESTED 如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务, 则按PROPAGATION_REQUIRED 属性执行

## 事务隔离级别

Spring事务有5种隔离级别

1. ISOLATION_DEFAULT 这是一个PlatfromTransactionManager默认的隔离级别，使用数据库默认的事务隔离级别.
2. ISOLATION_READ_UNCOMMITTED 这是事务最低的隔离级别，允许读取尚未提交的更改。可能导致脏读、幻影读或不可重复读。
3. ISOLATION_READ_COMMITTED 允许从已经提交的并发事务读取。可防止脏读，但幻影读和不可重复读仍可能会发生。
4. ISOLATION_REPEATABLE_READ 对相同字段的多次读取的结果是一致的，除非数据被当前事务本身改变。可防止脏读和不可重复读，但幻影读仍可能发生。
5. ISOLATION_SERIALIZABLE 这是花费最高代价但是最可靠的事务隔离级别。事务被处理为顺序执行。除了防止脏读，不可重复读外，还避免了幻像读。



运行配置@Transactional注解的测试类的时候，具体会发生如下步骤
1)事务开始时，通过AOP机制，生成一个代理connection对象，并将其放入DataSource实例的某个与DataSourceTransactionManager相关的某处容器中。在接下来的整个事务中，客户代码都应该使用该connection连接数据库，执行所有数据库命令[不使用该connection连接数据库执行的数据库命令，在本事务回滚的时候得不到回滚]
2)事务结束时，回滚在第1步骤中得到的代理connection对象上执行的数据库命令，然后关闭该代理connection对象


根据上面所述，我们所使用的客户代码应该具有如下能力：
1)每次执行数据库命令的时候
如果在事务的上下文环境中，那么不直接创建新的connection对象，而是尝试从DataSource实例的某个与DataSourceTransactionManager相关的某处容器中获取connection对象；在非事务的上下文环境中，直接创建新的connection对象
2)每次执行完数据库命令的时候
如果在事务的上下文环境中，那么不直接关闭connection对象，因为在整个事务中都需要使用该connection对象，而只是释放本次数据库命令对该connection对象的持有；在非事务的上下文环境中，直接关闭该connection对象

 

 

在service类前加上@Transactional，声明这个service所有方法需要事务管理。每一个业务方法开始时都会打开一个事务。

Spring默认情况下会对运行期例外(RunTimeException)进行事务回滚。这个例外是unchecked

如果遇到checked意外就不回滚。

如何改变默认规则：

1 让checked例外也回滚：在整个方法前加上 @Transactional(rollbackFor=Exception.class)

2 让unchecked例外不回滚： @Transactional(notRollbackFor=RunTimeException.class)

3 不需要事务管理的(只查询的)方法：@Transactional(propagation=Propagation.NOT_SUPPORTED)

4 如果不添加rollbackFor等属性，Spring碰到Unchecked Exceptions都会回滚，不仅是RuntimeException，也包括Error。

 

注意： 如果异常被try｛｝catch｛｝了，事务就不回滚了，如果想让事务回滚必须再往外抛try｛｝catch｛throw Exception｝。





https://www.jianshu.com/p/a4229aa79ace