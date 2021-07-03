redis-obj-size





其中：

type：类型

```
#define OBJ_ENCODING_RAW 0     /* Raw representation */
#define OBJ_ENCODING_INT 1     /* Encoded as integer */
#define OBJ_ENCODING_HT 2      /* Encoded as hash table */
#define OBJ_ENCODING_ZIPMAP 3  /* Encoded as zipmap */
#define OBJ_ENCODING_LINKEDLIST 4 /* No longer used: old list encoding. */
#define OBJ_ENCODING_ZIPLIST 5 /* Encoded as ziplist */
#define OBJ_ENCODING_INTSET 6  /* Encoded as intset */
#define OBJ_ENCODING_SKIPLIST 7  /* Encoded as skiplist */
#define OBJ_ENCODING_EMBSTR 8  /* Embedded sds string encoding */
#define OBJ_ENCODING_QUICKLIST 9 /* Encoded as linked list of ziplists */
#define OBJ_ENCODING_STREAM 10 /* Encoded as a radix tree of listpacks */
```





扩展数据类型

HyperLoglog

Geo

BloomFilter

Stream

Bitmap



### 字符串

1、小于等于 44 ：embstr 编码

2、大于 44：raw 编码



Raw

```
type-4:OBJ_STRING
encoding-4:OBJ_ENCODING_RAW
lru-24:
refcount-32:1
ptr: 指向字符串首地址
```



Embedded

```
type-4:OBJ_STRING
encoding-4:OBJ_ENCODING_EMBSTR
lru-24:
refcount-32:1
ptr:指向 sdshdr8 对象头，与对象头内存连续
    len-32:实际长度
    alloc-32:实际长度
    flags-1:SDS_TYPE_8
    buf:实际内容
```





当元素不多的时候，可以利用 List 和 Hash 在元素个数有限时为 ziplist 可以节省内存。

使用ziplist方式存储时，虽然可以利用CPU高速缓存，但也不适合存储过多的数据（hash-max-ziplist-entries和zset-max-ziplist-entries不宜设置过大），否则查询性能就会下降比较厉害。整体来说，这样的方案就是时间换空间，我们需要权衡使用。



查看键值内存大小的工具：

http://www.redis.cn/redis_memory/



### 思考题

1、redis把sds使用raw编码还是embstr编码的临界值是44, 这个44是如何计算出来的呢? 

64 -16（ReadObj头部）- 3（sds头部）-1（buf末尾\0）= 44