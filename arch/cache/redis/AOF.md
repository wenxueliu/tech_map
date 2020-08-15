##AOF

文件

    aof.c

```
    /* ----------------------------------------------------------------------------
     * AOF rewrite buffer implementation.
     *
     * The following code implement a simple buffer used in order to accumulate
     * changes while the background process is rewriting the AOF file.
     *
     * We only need to append, but can not just use realloc with a large block
     * because 'huge' reallocs are not always handled as one could expect
     * (via remapping of pages at OS level) but may involve copying data.
     *
     * For this reason we use a list of blocks, every block is
     * AOF_RW_BUF_BLOCK_SIZE bytes.
     * ------------------------------------------------------------------------- */

    typedef struct aofrwblock {
        unsigned long used, free;
        char buf[AOF_RW_BUF_BLOCK_SIZE];
    } aofrwblock;

    /* AOF persistence */
    int aof_state;                  : AOF_(ON|OFF|WAIT_REWRITE) */
    int aof_fsync;                  : AOF_FSYNC_EVERYSEC | AOF_
    char *aof_filename;             : AOF 文件名
    int aof_no_fsync_on_rewrite;    /* Don't fsync if a rewrite is in prog. */
    int aof_rewrite_perc;           /* Rewrite AOF if % growth is > M and... */
    off_t aof_rewrite_min_size;     /* the AOF file is at least N bytes. */
    off_t aof_rewrite_base_size;    /* AOF size on latest startup or rewrite. */
    off_t aof_current_size;         : AOF 大小 byte 为单位
    int aof_rewrite_scheduled;      : 0 : 如果子进程创建成功 1:
    pid_t aof_child_pid;            : 进行 AOF 重写的子进程号
    list *aof_rewrite_buf_blocks;   : 当将一个明细写 AOF 文件时, 如果当前写 AOF 文件正在进行时, 就将该命令写入此 aof_rewrite_buf_blocks.
    sds aof_buf                     : 当 aof_state 为 AOF_ON, 将相关命令字符串写入 aof_buf, 参考 feedAppendOnlyFile
    int aof_fd;                     : 在刷新的时候, 将 aof_buf 的内容写入该 aof_fd
    int aof_selected_db;            : 当前选中的 db id, 参考 feedAppendOnlyFile
    time_t aof_flush_postponed_start; /* UNIX time of postponed AOF flush */
    time_t aof_last_fsync;            /* UNIX time of last fsync() */
    time_t aof_rewrite_time_last;   /* Time used by last AOF rewrite run. */
    time_t aof_rewrite_time_start   : AOF 重写开始时间. 父进程记录
    int aof_lastbgrewrite_status;   /* C_OK or C_ERR */
    unsigned long aof_delayed_fsync;  /* delayed AOF fsync() counter */
    int aof_rewrite_incremental_fsync;/* fsync incrementally while rewriting? */
    int aof_last_write_status;      /* C_OK or C_ERR */
    int aof_last_write_errno;       /* Valid if aof_last_write_status is ERR */
    int aof_load_truncated;         /* Don't stop on unexpected AOF EOF. */
    /* AOF pipes used to communicate between parent and child during rewrite. */
    int aof_pipe_write_data_to_child;
    int aof_pipe_read_data_from_parent;
    int aof_pipe_write_ack_to_parent;
    int aof_pipe_read_ack_from_child;
    int aof_pipe_write_ack_to_child;
    int aof_pipe_read_ack_from_parent;
    int aof_stop_sending_diff       : 如果为 1, 父进程停止发送数据给子进程.  如果为 0, 父进程可以发送数据给子进程
    /* If true stop sending accumulated diffs
                                      to child process. */
    sds aof_child_diff;             /* AOF diff accumulator child side. */
    stat_fork_time  : 创建子进程的时间
    stat_fork_rate  : 创建子进程的时间内, 内存分配的大小.
```

1. 当用户调用 BGREWRITEAOF, 创建父子进程的管道, 子进程将 server.db 的数据写入
temp-rewriteaof-bg-[子进程pid].aof 参考 rewriteAppendOnlyFileBackground

2. 服务端的 ServerCon 线程中, 如果 pid = server.aof_child_pid, 将
server.aof_rewrite_buf_blocks 的数据写入 server.aof_filename
(文件名为 temp-rewriteaof-bg-[server.aof_child_pid].aof, fd 是 aof_fd),
参考 backgroundRewriteDoneHandler

3. 将 server.aof_filename 的数据初始化一个 fakeClient->argv

4.  如果 aof_buf = AOF_ON, 将 cmd, argv, argc 相关信息写入 server.aof_buf;
    如果子进程正在写, 将相关信息写如 server.aof_rewrite_buf_blocks

5.  在刷新的时候, 将 server.aof_buf 内容写入 server.aof_fd

server.aof_rewrite_buf_blocks 保存一个链表, 链表的元素保存 aofrwblock 元素.

其中 used 保存了 buf 已经使用的字节数, free 保存 buf 没有使用的字节数.

写的时候将 server.aof_rewrite_buf_blocks 写入 server.aof_pipe_write_data_to_child

aof 文件名: temp-rewriteaof-bg-[child_pid].aof 其中 child_pid 为子进程 id.

fsync 同步策略: AOF_FSYNC_EVERYSEC


void aofRewriteBufferReset(void)

    重置 aofrwblock

unsigned long aofRewriteBufferSize(void)

    遍历 server.aof_rewrite_buf_blocks 的每个元素 aofrwblock, 返回 used 的块的数量

void aofChildWriteDiffData(aeEventLoop *el, int fd, void *privdata, int mask)

    遍历 server.aof_rewrite_buf_blocks 的每个元素, 将每个元素的 used
    部分数据写入 server.aof_pipe_write_data_to_child

    遍历 server.aof_rewrite_buf_blocks 中每个元素 block:
    如果 block 为 null 或者 server.aof_stop_sending_diff 不为 0, 删除 server.aof_pipe_write_data_to_child 的可写事件, 之后返回.
    否则, 从 server.aof_rewrite_buf_blocks 的第一个元素开始, 将其数据写入 server.aof_pipe_write_data_to_child

    问题, block 为 null, 是否应该停止写 aof 返回?

void aofRewriteBufferAppend(unsigned char *s, unsigned long len)

    将长度为 len 的 s 写入 server.aof_rewrite_buf_blocks

    找到 server.aof_rewrite_buf_blocks 最后一个 block.
    1. 如果该 block free 的部分不够 len, 就新分配一个 aofrwblock
    2. 如果该 block free 的部分够 len, 直接将 s 拷贝到 block 的 free 部分.
    重复 1,2 直到复制完成.


ssize_t aofRewriteBufferWrite(int fd)

    将 server.aof_rewrite_buf_blocks 的数据写入 fd

    将 server.aof_rewrite_buf_blocks 的每个 block 的 used 写入 fd.
    这里问题: 为什么其中一个 write 一次写不完全就返回?

void stopAppendOnly(void)

    appendonly yes 变为 appendonly no
    重置 aofrwblock, 杀死子进程, 停止 AOF 写, 子进程和父进程的 pipe.

    删除文件事件 server.aof_pipe_read_ack_from_child 的读事件
    删除文件事件 server.aof_pipe_write_data_to_child 的写事件

    关闭如下 fd.
    server.aof_pipe_write_data_to_child
    server.aof_pipe_read_data_from_parent
    server.aof_pipe_write_ack_to_parent
    server.aof_pipe_read_ack_from_child
    server.aof_pipe_write_ack_to_child
    server.aof_pipe_read_ack_from_parent

    server.aof_child_pid = -1;
    server.aof_rewrite_time_start = -1;

int startAppendOnly(void)

    appendonly no 变为 appendonly yes

    server.aof_last_fsync
    server.aof_fd = server.aof_filename,

    rewriteAppendOnlyFileBackground


void flushAppendOnlyFile(int force)

    1. 强制模式 :
    2. 非强制模式 :
    如果 AOF 同步策略是 AOF_FSYNC_EVERYSEC, 并且在同步过程中,

    1. 将 aof_buf 的内容写入 aof_fd.
    2. 将 写入的长度增加到 server.aof_current_size 中
    3. 每次最短 2 s 刷新一次

sds catAppendOnlyGenericCommand(sds dst, int argc, robj **argv)

    dst + "*" + 1+argc + "\r\n"
        + "$" + len(argv[0]) + argv[0] + "\r\n"
        + "$" + len(argv[1]) + argv[1] + "\r\n"

    将 argc, argv 的每个元素增加到 dst 后面,　格式见后

sds catAppendOnlyExpireAtCommand(sds buf, struct redisCommand *cmd, robj *key, robj *seconds)

    返回 buf + "*3\r\n$9PEXPIREAT\r\n$" + len(key) + key + "\r\n$" + len(seconds) + seconds*1000 + now_ms

void feedAppendOnlyFile(struct redisCommand *cmd, int dictid, robj **argv, int argc)

    设置 server.aof_selected_db 为 dictid

    如果是 EXPIRE, PEXPIRE, EXPIREAT (Translate EXPIRE/PEXPIRE/EXPIREAT into PEXPIREAT)
    "*2\r\n$6SELECT\r\n$" + len(dictid) + "\r\n" + dictid + "\r\n"
    + "*3\r\n$9PEXPIREAT\r\n$" + len(argv[1]) + argv[1] + "\r\n$" + len(argv[2]) + argv[2]*1000 + now_ms

    如果是 setexCommand, psetexCommand, (Translate SETEX/PSETEX to SET and PEXPIREAT)
    "*2\r\n$6SELECT\r\n$" + len(dictid) + "\r\n" + dictid + "\r\n"
    + "*4\r\n$3SET\r\n$" + len(argv[1]) + argv[1] + "\r\n$" + len(argv[3]) + argv[3] + "\r\n"
    + "*3\r\n$9PEXPIREAT\r\n$" + len(argv[1]) + argv[1] + "\r\n$" + len(argv[2]) + argv[2]*1000 + now_ms

    否则
    "*2\r\n$6SELECT\r\n$" + len(dictid) + "\r\n" + dictid + "\r\n"
        + "*" + 1+argc + "\r\n"
        + "$" + len(argv[0]) + argv[0] + "\r\n"
        + "$" + len(argv[1]) + argv[1] + "\r\n"

    如果 server.aof_state == AOF_ON 将如上字符串写入 server.aof_buf,
    如果子进程在写, 将如实字符串写入 server.aof_rewrite_buf_blocks

struct client *createFakeClient(void)

    创建并初始化一个 client 对象

void freeFakeClientArgv(struct client *c)

    是否 client->argv 对象

void freeFakeClient(struct client *c)

    是否 client 对象

int loadAppendOnlyFile(char *filename)

    主要用于恢复

    读文件 filename 初始化 fakeClient 的 argc, argv,
    如果 argv[0] 不在 server->commands 中, 退出, 否则 调用 argv[0]->proc(fakeClient)
    关闭 filename, 是否 fakeClient

    期间每 1000 次循环, 处理一次客户端请求.

ssize_t aofReadDiffFromParent(void)

    将 server.aof_pipe_read_data_from_parent, buf 读的数据加入 server.aof_child_diff,
    返回读到的数据的大小

int rewriteAppendOnlyFile(char *filename)

    0. 创建临时文件 temp-rewriteaof-[getpid()].aof
    1. 遍历所有的 db, 将每个 db 的数据依次写入 . 刷新写操作到磁盘
    2. 如果超过 1 s 或 1s 内有 20 ms 超时(连续的每次 1 ms), 将 server.aof_pipe_read_data_from_parent,buf
    读的数据加入 server.aof_child_diff, 退出
    3. 给父进程发送 "!" 通知父进程不要发送增量数据.
    4. 超时 10 s，等待父进程的 "!" 应答
    5. 如果超过 1 s 或 1s 内有 20 ms 超时(连续的每次 1 ms), 将 server.aof_pipe_read_data_from_parent,buf
    读的数据加入 server.aof_child_diff, 退出
    6. 将 server.aof_child_diff 中的数据写入临时文件
    7. 刷新文件缓存数据到磁盘, 并关闭临时文件
    8. 重命名临时文件为 filename

    格式: "*2\r\n$6\r\nSELECT\r\n$" + len(j) + "\r\n" + j + "\r\n"
    (j 为数据库的索引) + (server.db + j)->dict 的所有元素





void aofChildPipeReadable(aeEventLoop *el, int fd, void *privdata, int mask)

    父进程读到子进程发送的 "!", 父进程应答 "!", 表明停止写数据给子进程.
    删除该读事件

int aofCreatePipes(void)

    创建 3 个管道,
    1. 父进程写数据到子进程, 子进程读数据从父进程, 并且是异步的.
    2.
    3.

    注册事件

    server.aof_pipe_write_data_to_child : 父进程写 AOF 数据的到子进程
    server.aof_pipe_read_data_from_parent : 子进程读 AOF 数据从父进程
    server.aof_pipe_write_ack_to_parent : 子进程写 "!" 给父进程
    server.aof_pipe_read_ack_from_child :
    server.aof_pipe_write_ack_to_child  : 父进程写 "!" 给子进程
    server.aof_pipe_read_ack_from_parent : 读父进程的 "!" 应答
    server.aof_stop_sending_diff = 0;

void aofClosePipes(void)

    关闭 aofCreatePipes 创建的管道

int rewriteAppendOnlyFileBackground(void)

    1. 如果当前有背景进程在进行 aof 写操作, 返回.(server.aof_child_pid 不为 -1)
    2. 创建父进程和子进程的管道
    3. 子进程将 server.db 的数据写入 temp-rewriteaof-bg-[子进程pid].aof, 退出子进程
       父进程记录 fork 时间, 速率, 子进程号, 禁止 dict 的重哈希, 设置 aof_selected_db
       为 -1.

        dictEmpty(server.repl_scriptcache_dict,NULL);
        listRelease(server.repl_scriptcache_fifo);
        server.repl_scriptcache_fifo = listCreate();

void bgrewriteaofCommand(client *c)

    如果 server.aof_child_pid != -1, 返回 rewrite 已经在进行中
    如果 server.rdb_child_pid != -1, server.aof_rewrite_scheduled = 1,
    如果 写 aof 成功, 返回 aof 写开始
    否则返回错误

void aofRemoveTempFile(pid_t childpid)

    unlink 文件 temp-rewriteaof-bg-[childpid].aof


void aofUpdateCurrentSize(void)

    设置 server.aof_current_size 为 server.aof_fd 文件的大小

void backgroundRewriteDoneHandler(int exitcode, int bysignal)

    1. 将 server.aof_rewrite_buf_blocks 的数据写入
    temp-rewriteaof-bg-[server.aof_child_pid].aof 文件
    2. 将 temp-rewriteaof-bg-[server.aof_child_pid].aof 重命名为
    server.aof_filename
    3. 将 server.aof_fd 为 temp-rewriteaof-bg-[server.aof_child_pid].aof 的 fd
    4. 根据 server.aof_fsync 的刷新策略对 temp-rewriteaof-bg-[server.aof_child_pid].aof 的 fd 进行刷新
    5. 设置 server.aof_current_size 和 server.aof_rewrite_base_size 为 server.aof_fd 文件的大小
    6. 清除 server.aof_buf
    7. 如果 server.aof_state 是 AOF_WAIT_REWRITE 设置为 AOF_ON;
    8. 如果 修订文件 fd 不为 -1, 通过另外一个线程关闭.
