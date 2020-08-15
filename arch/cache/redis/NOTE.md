


命令文档

http://redisdoc.com/index.html


redisServer

    stat_expiredkeys        : 当 redisDb->dict 中 key 过期, 加1
    stat_keyspace_misses    : 当从 redisDb->dict 中查找 key 没有匹配时加 1
    stat_keyspace_hits      : 当从 redisDb->dict 中查找 key 匹配时加 1

每个 client 只能在 server 其中一个 db. c->db 指向其所属的 server->db, c->argv c->argc 保留的客户端的参数.

##双向链表

文件

adlist.h adlist.c

实现了双向链表. 每个链表, 保存了链表头和链表尾部的指针。可以从头或从尾部进行遍历.

```
    typedef struct listNode {
        struct listNode *prev;
        struct listNode *next;
        void *value;
    } listNode;

    typedef struct list {
        listNode *head;
        listNode *tail;
        void *(*dup)(void *ptr);
        void (*free)(void *ptr);
        int (*match)(void *ptr, void *key);
        unsigned long len;
    } list;
```

##网络框架

文件

ae.c ae.h

```
    /* File event structure */
    typedef struct aeFileEvent {
        int mask; /* one of AE_(READABLE|WRITABLE) */
        aeFileProc *rfileProc;
        aeFileProc *wfileProc;
        void *clientData;
    } aeFileEvent;

    /* Time event structure */
    typedef struct aeTimeEvent {
        long long id; /* time event identifier. */
        long when_sec; /* seconds */
        long when_ms; /* milliseconds */
        aeTimeProc *timeProc;
        aeEventFinalizerProc *finalizerProc;
        void *clientData;
        struct aeTimeEvent *next;
    } aeTimeEvent;

    /* A fired event */
    typedef struct aeFiredEvent {
        int fd;
        int mask;
    } aeFiredEvent;

    /* State of an event based program */
    typedef struct aeEventLoop {
        int maxfd;   /* highest file descriptor currently registered */
        int setsize; /* max number of file descriptors tracked */
        long long timeEventNextId;
        time_t lastTime;     /* Used to detect system clock skew */
        aeFileEvent *events; /* Registered events */
        aeFiredEvent *fired; /* Fired events */
        aeTimeEvent *timeEventHead;
        int stop;
        void *apidata; /* This is used for polling API specific data */
        aeBeforeSleepProc *beforesleep;
    } aeEventLoop;
```

首先, 每个事件循环有一个大小 setsize, 其中文件事件是一个数组, 而时间事件是
一个循环链表, 新添加的元素在链表头(类似一个栈). 文件事件数组的大小在初始化就确定的(为 setsize).

maxfd 记录最大 fd.

timeEventNextId 记录每个 aeTimeEvent 的 id, 随着 aeTimeEvent 的增加, timeEventNextId 递增.

其中 apidata 保存了 aeApiState, aeApiState->event 是 setsize 的 epoll_event
aeApiState->epfd = epoll_create(1024)

aeEventLoop *aeCreateEventLoop(int setsize)

    初始化一个 setsize 的事件循环

void aeDeleteEventLoop(aeEventLoop *eventLoop);

    删除一个的事件循环

void aeStop(aeEventLoop *eventLoop);

    删除一个的事件循环

int aeCreateFileEvent(aeEventLoop *eventLoop, int fd, int mask,
        aeFileProc *proc, void *clientData);

    给事件循环注册一个文件事件回调函数, 其中 fd 为 eventLoop->events 的索引
    mask 是事件类型 AE_READABLE, AE_WRITABLE.

    并给 eventLoop->apidata->epfd 加入 fd 的 mask 的事件.

    clientData 为 TODO

void aeDeleteFileEvent(aeEventLoop *eventLoop, int fd, int mask);

    给事件循环删除 fd 的 mask 事件. 将 eventLoop->events 索引为 fd 对应的
    aeFileEvent 的 mask 置为 ~mask. 并从 eventLoop->apidata 中删除 fd 的
    mask 事件


int aeGetFileEvents(aeEventLoop *eventLoop, int fd);

    获取 eventLoop->events[fd] 对应的事件类型

long long aeCreateTimeEvent(aeEventLoop *eventLoop, long long milliseconds,
        aeTimeProc *proc, void *clientData,
        aeEventFinalizerProc *finalizerProc);

    给事件循环注册一个时间事件回调函数. 创建一个 aeTimeEvent 事件加入
    eventLoop->timeEventHead 的头部.

int aeDeleteTimeEvent(aeEventLoop *eventLoop, long long id);

    将 eventLoop->timeEventHead 中 id 为的 aeTimeEvent 的 id 设置为 AE_DELETED_EVENT_ID

int aeProcessEvents(aeEventLoop *eventLoop, int flags);

    If flags is 0, the function does nothing and returns.
    if flags has AE_ALL_EVENTS set, all the kind of events are processed.
    if flags has AE_FILE_EVENTS set, file events are processed.
    if flags has AE_TIME_EVENTS set, time events are processed.
    if flags has AE_DONT_WAIT :
        设置该位, 如果没有文件事件, 立即调用 processTimeEvents
        没有设置该位, 如果没有文件事件, 会等待文件事件, 之后才触发事件事件.
        这样时间事件可能不会很及时

    1. 只要 flags 包括 AE_TIME_EVENTS, 就一定会调用 processTimeEvents
    2. 等待 eventLoop->events 中的文件时间, 加入 eventLoop->fired,

int aeWait(int fd, int mask, long long milliseconds);

    等待 milliseconds 毫秒 fd 的 mask 事件. 返回等待的事件类型

void aeMain(aeEventLoop *eventLoop);

    如果 eventLoop->stop 为 0, 一直调用 aeProcessEvents, 监控所有的
    文件和时间事件.

void aeSetBeforeSleepProc(aeEventLoop *eventLoop, aeBeforeSleepProc *beforesleep);

    设置 eventLoop 执行事件前的回调函数.

int aeGetSetSize(aeEventLoop *eventLoop);

    获取 aeEventLoop 的元素数量

int aeResizeSetSize(aeEventLoop *eventLoop, int setsize);

    重新分配 aeEventLoop 的元素数量

static int processTimeEvents(aeEventLoop *eventLoop)

    1. 删除 id 为 AE_DELETED_EVENT_ID 的 aeTimeEvent, 并调用对应的 finalizerProc
    2. 如果当前时间事件时间点到达, 调用对应的时间处理函数. 需要注意的是,
    该时间时间是否继续处理, 取决于时间处理函数的返回值 retval. 如果不是 -1. 就继续
    等待 retval 时间, 继续触发该时间处理函数

static int aeApiPoll(aeEventLoop *eventLoop, struct timeval *tvp)

    等待 eventLoop->apidata 的 events 的事件, 并加入 eventLoop->fired 的事件

static aeTimeEvent *aeSearchNearestTimer(aeEventLoop *eventLoop)

    搜索所有 eventLoop->timeEventHead, 中的最近需要的时间事件.













##dict

1. 最关键的需要理解 dict 的扩容. 当扩容的时候, 将现有的内存移动.
2. 提供了字典的迭代器, 其中包括 safe 和 unsafe 两种. 前者在迭代过程中可以增加, 查找, 后者只能是遍历.
3. 字典初始化元素大小是 4
4. value 的泛型是通过 union 实现的.
5. key 的 hash 算法 Thomas Wang
6. rehash 既提供了全部的粗粒度的也提供了单步的或基于时间的细粒度的接口.
7. 默认是可以禁止 rehash 的. 但是当 元素的个数/bucket 的个数 > 5 的时候, 仍然会 rehash.
8. 扩容仍然是传统的 2 倍扩容. 新的大小是 2 的 n 次方(该数最接近当前已经使用的元素 * 2)

## rio

对文件, 内存, 和 socket 进行的基本操作的封装.(read, write, tell, flush, update_cksum)

这个很简单, 略.

## bio

简称 Background I/O

维护一个 bio_jobs 的链表数组. 即由两 list 组成的数组.
其中第一个数组, 调用 close 关闭每个元素中对应的文件.
第二个数组,  调用 aof_fsync 同步对应的文件

创建两个线程, 每个线程的分别从每个 list 中取元素, 然后执行元素任务. 其中一个是 close, 另外一个是 aof_fsync

如果系统默认 stacksize < REDIS_THREAD_STACK_SIZE, 就 stacksize = stacksize*2

###预备知识

* pthread
* sds.h

###工作流

1. bioInit()
2. bioCreateBackgroundJob() : 给 bio_jobs[type] 增加任务.
3.



###要点:

* 对 list 的增加与删除元素都需要加锁.
* 当链表中的元素为空, 就阻塞. 每当添加元素就发送信号给阻塞的线程.

static pthread_t bio_threads[BIO_NUM_OPS] : 保存每个创建的线程变量.
static pthread_mutex_t bio_mutex[BIO_NUM_OPS] :
static pthread_cond_t bio_condvar[BIO_NUM_OPS]:
static list *bio_jobs[BIO_NUM_OPS]  :
static unsigned long long bio_pending[BIO_NUM_OPS] : 保存每个列表中元素个数
#define REDIS_THREAD_STACK_SIZE (1024*1024*4)

struct bio_job
    time_t time : job 创建时间
    void *arg1, *arg2, *arg3 : 传给 job 的参数, 如果多余一个参数, 可以将参数以 struct 的方式传递


void bioInit(void)

    1. 初始化 bio_jobs, bio_pending, bio_mutex, bio_condvar
    2. 设置线程栈大小
    3. 创建 BIO_NUM_OPS 个线程执行 bioProcessBackgroundJobs

void bioCreateBackgroundJob(int type, void *arg1, void *arg2, void *arg3)

    将 bio_job 增加到 bio_jobs[type], 并发送通知给阻塞的线程.

void *bioProcessBackgroundJobs(void *arg)

    1. 设置当前线程是可以被取消的.
    2. 循环从 bio_jobs[arg] 中取一个元素, 执行其中的任务. 删除该元素. 然后继续取, 如果当前
    list 没有元素就阻塞.

unsigned long long bioPendingJobsOfType(int type)

    获取 bio_jobs[type] 对应 list 元素个数

void bioKillThreads(void)

    给 bio_threads 中的所有线程发送取消线程请求, 并检查取消是否成功.

##

文件

    blocked.c

int getTimeoutFromObjectOrReply(client *c, robj *object, mstime_t *timeout, int unit)

    从 robj 中解析值初始化 timeout, 根据 unit 转为 ms.

void blockClient(client *c, int btype)

    c->flags |= CLIENT_BLOCKED;
    c->btype = btype;
    server.bpop_blocked_clients++;

void processUnblockedClients(void)

    遍历 server.unblocked_clients 的每一个元素:
    1. 将其从链表中删除
    2. 去掉 CLIENT_UNBLOCKED 标志.
    3. 如果 c->flags 不包含 CLIENT_BLOCKED, 并且 c->querybuf 不为 null,
    c->reqtype = PROTO_REQ_INLINE, 初始化 client->argc client->argv, 之后调用 processCommand 处理
    c->reqtype == PROTO_REQ_MULTIBULK : 初始化 client->argc client->argv, 之后调用 processCommand 处理

void unblockClient(client *c)

    1. c->btype == BLOCKED_LIST : 将 c->bpop.keys 中每个元素在 c->db->blocking_keys 中对应的节点删除.
       c->btype == BLOCKED_WAIT : 将 server.clients_waiting_acks 中 c 对应的节点删除
    2. 设置 c->flags = ~CLIENT_BLOCKED | CLIENT_UNBLOCKED, c->btype = BLOCKED_NONE;
    3. 将 c 加入 server.unblocked_clients

    c->bpop.keys 中每个元素对应 c->db->blocking_keys 的 key,
    在 c->db->blocking_keys 的每个 value 为 list, 从 list 中删除 c 对应的元素.
    c->bpop.target = NULL;

void replyToBlockedClientTimedOut(client *c)

    当一个阻塞的客户端超时之后, 应答调用该函数

void disconnectAllBlockedClients(void)

    当由 master 变为 slave 的时候, 调用该函数

    遍历 server.clients 的每个元素 client, 如果该 client 是阻塞的, 将其转为
    非阻塞的, 加入　server.unblocked_clients, 并设置 CLIENT_CLOSE_AFTER_REPLY
