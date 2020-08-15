
端口 26379

static void redisAeReadEvent(aeEventLoop *el, int fd, void *privdata, int mask)
static void redisAeWriteEvent(aeEventLoop *el, int fd, void *privdata, int mask)
static void redisAeAddRead(void *privdata)
static void redisAeDelRead(void *privdata)
static void redisAeAddWrite(void *privdata)
static void redisAeDelWrite(void *privdata)
static void redisAeCleanup(void *privdata)
static int redisAeAttach(aeEventLoop *loop, redisAsyncContext *ac)
