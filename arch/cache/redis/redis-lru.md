redis-lru



![redis evction](redis-evction.jpg)





时机：



算法流程

1、采样 N 个数据进行淘汰

`CONFIG SET maxmemory-samples 100`

2、选择随机算法

| 场景                                 | 算法          |
| ------------------------------------ | ------------- |
| 访问频率差别不大，没有明显的冷热之分 | allkey-random |
| 置顶需求                             | allkey-ttl    |
|                                      |               |
| 其余                                 | allkey-lru    |
|                                      |               |

3、对淘汰数据操作：脏数据写数据库后删除，非脏数据直接删除









### LFU 算法





### 思考题

为什么是 双向链表而不是单链表呢？

单链表：

单链表可以实现头部插入新节点、尾部删除旧节点的时间复杂度都是O(1)，但是对于中间节点时间复杂度是O(n)，因为对于中间节点c，我们需要将该节点c移动到头部，此时只知道他的下一个节点，要知道其上一个节点需要遍历整个链表，时间复杂度为O(n)。

