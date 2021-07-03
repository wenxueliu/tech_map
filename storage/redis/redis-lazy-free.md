开启lazy-free时，有4个选项可以控制，分别对应不同场景下，要不要开启异步释放内存机制：
a) lazyfree-lazy-expire：key在过期删除时尝试异步释放内存
b) lazyfree-lazy-eviction：内存达到maxmemory并设置了淘汰策略时尝试异步释放内存
c) lazyfree-lazy-server-del：执行RENAME/MOVE等命令或需要覆盖一个key时，删除旧key尝试异步释放内存
d) replica-lazy-flush：主从全量同步，从库清空数据库时异步释放内存



即使开启了lazy-free，如果直接使用DEL命令还是会同步删除key，只有使用UNLINK命令才会可能异步删除key。

开启lazy-free后，Redis在释放一个key的内存时，首先会评估代价，如果释放内存的代价很小，那么就直接在主线程中操作了，没必要放到异步线程中执行（不同线程传递数据也会有性能消耗）。除了replica-lazy-flush之外，其他情况都只是**可能**去异步释放key的内存，并不是每次必定异步释放内存的。



###  什么情况才会真正异步释放内存？

这和key的类型、编码方式、元素数量都有关系（详细可参考源码中的lazyfreeGetFreeEffort函数）：

1、当Hash/Set底层采用哈希表存储（非ziplist/int编码存储）时，并且元素数量超过64个
2、当ZSet底层采用跳表存储（非ziplist编码存储）时，并且元素数量超过64个
3、当List链表节点数量超过64个（注意，不是元素数量，而是链表节点的数量，List的实现是在每个节点包含了若干个元素的数据，这些元素采用ziplist存储）

只有以上这些情况，在删除key释放内存时，才会真正放到异步线程中执行，其他情况一律还是在主线程操作。

也就是说String（不管内存占用多大）、List（少量元素）、Set（int编码存储）、Hash/ZSet（ziplist编码存储）这些情况下的key在释放内存时，依旧在主线程中操作。

可见，即使开启了lazy-free，String类型的bigkey，在删除时依旧有阻塞主线程的风险。所以，即便Redis提供了lazy-free，我建议还是尽量不要在Redis中存储bigkey。



#### 思考题

异步处理，redis出现异常重启，会不会导致数据丢失？

