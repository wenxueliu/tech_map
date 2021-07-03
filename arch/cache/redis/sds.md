sds

### 目标

1. 高存储有效性 
2. 易用

### 实现思路

对于字符串来说，包括字符串本身以及字符串的元数据

### 易用

1. 提供统一的接口，屏蔽底层实现
2. 自动扩容
3. 提供易用的 api

### 存储有效性

1. 针对不同长度的字符串进行优化，避免内存浪费
2. 自动扩容和缩容

## 特点

兼容 C 语言标准字符串：暴露给上层的是 buf，上层可用像读取 C 字符串一样读取 SDS 内容

二进制安全：由于长度有 len，不依赖 '\0'，保证二进制安全（读写字符串时不损害其内容）。

动态字符串：柔性数组

## 数据结构

对于高频使用的字符串，对于亿级缓存，应该本着存储空间是非常重要的思想。

1. 对齐：以 1 字节进行对齐，1）节省空间；2）通过 buf[-1] 方便定位到 flags
2. 不同字符串（长度不同）的长度属性用不同类型表示

```c
struct __attribute__ ((__packed__)) sdshdr5 {
    unsigned char flags; /* 3 lsb of type, and 5 msb of string length */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr8 {
    uint8_t len; /* used */
    uint8_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr16 {
    uint16_t len; /* used */
    uint16_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr32 {
    uint32_t len; /* used */
    uint32_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr64 {
    uint64_t len; /* used */
    uint64_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};

#define SDS_TYPE_5  0
#define SDS_TYPE_8  1
#define SDS_TYPE_16 2
#define SDS_TYPE_32 3
#define SDS_TYPE_64 4
#define SDS_TYPE_MASK 7
#define SDS_TYPE_BITS 3
```



## 核心 API 操作

### 初始化

根据不同长度的字符串使用不同类型的 sds

```c
static inline char sdsReqType(size_t string_size) {
    if (string_size < 1<<5)
        return SDS_TYPE_5;
    if (string_size < 1<<8)
        return SDS_TYPE_8;
    if (string_size < 1<<16)
        return SDS_TYPE_16;
#if (LONG_MAX == LLONG_MAX)
    if (string_size < 1ll<<32)
        return SDS_TYPE_32;
    return SDS_TYPE_64;
#else
    return SDS_TYPE_32;
#endif
}
```



### 动态扩容

1. 如果空间小于 1 M，扩容为 2 倍
2. 如果空间大于 1M，扩容为  newLen + 1MB 
3. 如果 type 变化，重新 malloc；否则 realloc

### 动态缩容

1. 长度变为其有效长度
2. 如果 type 变化，重新 malloc；否则 realloc

### 空间释放

sdsfree：释放字符串内存

sdsclear：重置而不是释放，减少内存分配



## API 概览

```c
// 创建
sds sdsnewlen(const void *init, size_t initlen);
// O(N)
sds sdsnew(const char *init);
// O(1)
sds sdsempty(void);

// 复制 O(N)
sds sdsdup(const sds s);

// 释放 O(N)
void sdsfree(sds s);

// 重置 O(1)
void sdsclear(sds s);

// 连接 O(N)
sds sdscatlen(sds s, const void *t, size_t len);
sds sdscat(sds s, const char *t);
sds sdscatsds(sds s, const sds t);
sds sdscatvprintf(sds s, const char *fmt, va_list ap);
sds sdscatfmt(sds s, char const *fmt, ...);
sds sdsjoin(char **argv, int argc, char *sep);
sds sdsjoinsds(sds *argv, int argc, const char *sep, size_t seplen);

// 替换 O(N)
sds sdscpylen(sds s, const char *t, size_t len);
sds sdscpy(sds s, const char *t);
sds sdsmapchars(sds s, const char *from, const char *to, size_t setlen);

// trim O(N^2)
sds sdstrim(sds s, const char *cset);

// 范围 O(N)
void sdsrange(sds s, ssize_t start, ssize_t end);

// 比较 O(N)
int sdscmp(const sds s1, const sds s2);

// 大小写转换 O(N)
void sdstolower(sds s);
void sdstoupper(sds s);
```



## 总结

sds 是经典的一个高效的易用的动态字符串。

## 思考题

一、什么是二进制安全，sds 是如何实现的？

 所有 SDS API 都会以二进制的方式来处理 SDS 存放的 buf 数组里的数据。程序不会对其中的数据做任何限制、过滤、或者假设。数据在写入时是什么样的，它被读取时就是什么样的。   

二、缓存使用内存的高效性是非常关键的指标，sds 的高效之处在哪？

三、请说明 sds 是如何实现动态字符串的？

四、sds 和 c 字符串的区别是啥？

| C 字符串                                       | SDS                                                          |
| ---------------------------------------------- | ------------------------------------------------------------ |
| 获取字符串长度的复杂度为 O(N)                  | 获取字符串的长度的复杂度为 O(1)                              |
| API是不安全的，可能会造成缓冲区溢出            | API 是安全的，不会造成缓冲区溢出                             |
| 修改字符串长度 N 次必然需要执行 N 次内存重分配 | 修改字符串长度 N 次最多需要执行 N 次内存重分配惰性释放（len + free属性）， |
| 只能保存文本数据                               | 可以保存文本和二进制数据                                     |
| 可以使用所有的<string.h>库中的函数             | 可以使用一部分<string.h>库中的函数                           |