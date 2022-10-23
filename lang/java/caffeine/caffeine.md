

基本概念

evict

readBuffer：回环数字，每个元素为 Buffer 对象，每个线程占用单独的数组元素，避免锁竞争。

weighter

nodeFactory







如何评估一个缓存

1、功能

2、内存占用

3、性能

4、命中率





容量限制

1、数量

2、权重：



读写策略

同步

异步



淘汰策略

Count-Min Sketch

Freshness Mechanism



1、如果 windowWeightedSize > windowMaximum 时，将 window 的节点加入 probation 区，并同时减少 window 区的权重，直到条件不满足。

2、当 weightedSize > maximum 时，

accessOrderProbationDeqeue 最后一个元素不为空，

1、accessOrderProbationDeqeue

accessOrderProtectedDeqeue、accessOrderWindowDeqeue第一个元素为空时，删除该元素，如果满足条件，不断重复。

2、当 accessOrderProbationDeqeue 不为空且权重不为 0 的第一个元素

3、当 accessOrderProtectedDeqeue 不为空且权重不为 0 的第一个元素

4、当 accessOrderWindowDeqeue 不为空且权重不为 0 的第一个元素



accessOrderProbationDeqeue 最后一个元素为空，









容量

时间：

​	访问时间

​    写时间



过期机制

1、访问过期

2、写过期

3、自定义过期



刷新机制

1、写后刷新



线程池



监控

1、缓存状态：可以监控缓存状态变化

2、监听器：删除、过期、更新、空间满

3、通知回调：



过期控制

1、expireAfterAccess：通过双向队列 AccessOrderDeque，每次访问，将元素放入队尾。

2、expireAfterWrite：通过双向队列 WriteOrderDeque，每次访问，将元素放入队尾。

3、expireAfter：实现 Expiry，通过时间轮



定时任务



对于 ReadBuffer

遍历 ReadBuffer 中的数据，从 ReadBuffer 中删除后，执行如下逻辑。

1、如果开启淘汰机制，更新节点到具体的淘汰队列，参考淘汰机制

2、如果设置 expireAfterAccess，移动到 accessOrderWindowDeque 的队尾。

3、如果设置 expireAfter，根据节点过期时间，将节点移动到时间轮合适的位置。参考时间轮机制。

对于 WriteBuffer

遍历 WriteBuffer 中的数据，从 writeBuffer 中删除（最多执行WRITE_BUFFER_MAX次），执行对应的任务	



对于 KeyReferenceQueue，遍历 KeyReferenceQueue 中的数据，执行淘汰逻辑。

对于 ValueReferenceQueue，遍历 KeyReferenceQueue 中的数据，执行淘汰逻辑。

对于 accessOrderDeque 队列

1、遍历 accessOrderWindowDeque 元素，如果过期就淘汰，继续下一个元素，直到没有过期的元素。

2、遍历 accessOrderProbationDeque 元素，如果过期就淘汰，继续下一个元素，直到没有过期的元素。

3、遍历 accessOrderProtectedDeque 元素，如果过期就淘汰，继续下一个元素，直到没有过期的元素。

对于 writeOrderDeque 队列，遍历 writeOrderDeque	 元素，如果过期就淘汰，继续下一个元素，直到没有过期的元素。

对于时间轮元素，计算当前和上一时刻，决定是否执行时间轮任务。



淘汰机制

1、访问节点的时候

2、



initialCapacity

maximumSize

maximumWeight

weighter：权重

evict：

weakXXX：ruoyinyong

softXXX:

Ticker:

Entry

Writer

CacheStats











1、[An Improved Data Stream Summary: The Count-Min Sketch and its Applications](http://dimacs.rutgers.edu/~graham/pubs/papers/cm-full.pdf)

2、[TinyLFU: A Highly Efficient Cache Admission Policy](https://dl.acm.org/citation.cfm?id=3149371)