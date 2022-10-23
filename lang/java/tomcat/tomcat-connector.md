tomcat-connector



Connector 接受请求并将请求封装成 Request 和 Response，最底层是使用 Socket来进行连接的 ， Request 和 Response 是按照 HTTP 协议来封装的 ，所以 Connector 同时实现了 TCP/IP 协议和 HTTP 协议 

Connector 初始化的时候根据 protocolHandlerClassName 加载对应的 ProtocolHandler 类。而 ProtocolHandler 是一个接口，实现方式可以自定义。ProtocolHandler 最重要的组件是 endpoint, processor, adapter。Endpoint 用于处理底层的 Socket 网络连接，Processor 用于将 Endpoint 接收的 Socket 封装成 Request，Adapter 用于将封装好的 Request 交个 Container 进行具体的处理。也就是说 Endpoint 用于实现 TCP/IP 的底层通信，Processor 用于实现 HTTP 协议，Adapter 将请求适配到 Servlet 容器进行具体业务处理。





```
protocolHandler(CoyoteAdapter(Connector))
```



### Lifecycle

```
before_init
init()
after_init

before_start
start()
after_start

before_stop
stop()
after_stop

before_destroy
destroy()
after_destroy

configure_start
configure_stop



```



#### 状态流转

    NEW(false, null),
    INITIALIZING(false, Lifecycle.BEFORE_INIT_EVENT),
    INITIALIZED(false, Lifecycle.AFTER_INIT_EVENT),
    STARTING_PREP(false, Lifecycle.BEFORE_START_EVENT),
    STARTING(true, Lifecycle.START_EVENT),
    STARTED(true, Lifecycle.AFTER_START_EVENT),
    STOPPING_PREP(true, Lifecycle.BEFORE_STOP_EVENT),
    STOPPING(false, Lifecycle.STOP_EVENT),
    STOPPED(false, Lifecycle.AFTER_STOP_EVENT),
    DESTROYING(false, Lifecycle.BEFORE_DESTROY_EVENT),
    DESTROYED(false, Lifecycle.AFTER_DESTROY_EVENT),
    FAILED(false, null);


###  Connector

上连 service，下接 protocolHandler

主要工作

1. 属性设置与获取：包括自身属性和 ProtocolHandler  的属性
2. 创建 Request
3. 创建 Response
4. 状态处理：ProtocolHandle  的 init，start，stop，destroy，



注意：这里的异常处理是捕获所有异常，用 LifecycleException 进行封装。



支持属性，以下参数都可以在 conf/server.xml 中配置

```
Connector 属性      默认值

acceptCount			backlog
connectionLinger	soLinger
connectionTimeout	soTimeout
rootFile			rootfile


ProtocolHandler 属性

nameIndex			
enableLookups		false
allowTrace			false
asyncTimeout		30000 ms
maxParameterCount	10000
maxPostSize			2 * 1024 * 1024 Byte
maxSavePostSize		4 * 1024 Byte
parseBodyMethods	POST(用逗号分隔，不能有 TRACE)
port				8080
localPort			
protocol			HTTP/1.1 或 AJP/1.3
proxyName			null
proxyPort			0
redirectPort		8443
secure				false
uRIEncoding			ISO-8859-1 或 UTF-8，根据 STRICT_SERVLET_COMPLIANCE 设置
useBodyEncodingForURI	false
xpoweredBy			false
useIPVHosts			false


schema				http
secure				false
maxCookieCount		200
protocolHandlerClassName	org.apache.coyote.http11.Http11NioProtocol
uriCharset			UTF-8
executor			

SSLHostConfig


 
```



通过 setProperty 设置 ProtocolHandler 的

```
org.apache.coyote.http11.Http11NioProtocol：没有安装 Apr，protocol 为 HTTP1.1(默认实现)
org.apache.coyote.http11.Http11AprProtocol：安装 Apr，protocol 为 HTTP1.1
org.apache.coyote.ajp.AjpNioProtocol：没有安装 Apr，protocol 为 AJP/1.3
org.apache.coyote.ajp.AjpAprProtocol：安装 Apr，protocol 为 AJP/1.3
```

注：

1. 目前 Tomcat 8.x 默认情况下全部是运行在 nio 模式下，而 apr 的本质就是使用 jni 技术调用操作系统底层的IO接口，从操作系统级别解决异步 IO 问题，大幅度提高服务器的并发处理性能，也是 Tomcat 生产环境运行的首选方式。更多参考[这里](http://tomcat.apache.org/tomcat-7.0-doc/apr.html)
2. Ajp 是一个私有协议，仅仅当 tomcat 的前端是 apache 时有用，因此，之后的分析将其忽略

### ProtocolHandler

具体协议处理器，支持 AJP 和 HTTP 协议。实际由 endpoint 来实现。此外，还有一个 asyncTimeout 线程，检查每个 waitingProcessors 中的 Processor 是否 timeout

```
ProtocolHandler
	AbstractProtocol
		AbstractHttp11Protocol
			AbstractHttp11JsseProtocol
				Http11NioProtocol
					Http11Protocol  用 NioEndpoint
				Http11Nio2Protocol  用 Nio2Endpoint 
			Http11AprProtocol		用 AprEndpoint
			
		AbstractAjpProtocol
			AjpAprProtocol
			AjpNio2Protocol
			AjpNioProtocol
				AjpProtocol
				

AbastracProtocol
	endpoint
	handler
	Processor
	AsyncTimeout
```



属性

```
AbastracProtocol 属性

adapter					
processorCache			200
clientCertProvider


Endpoint 属性

AcceptorThreadCount
MaxHeaderCount
ConnectionCount
AcceptorThreadPriority
SoTimeout
ConnectionTimeout
LocalPort
Port
Address
KeepAliveTimeout
SoLinger
TcpNoDelay
Backlog
ThreadPriority
MinSpareThreads
MaxConnections
MaxThreads
Executor



AbstractHttp11Protocol 的属性

relaxedPathChars
relaxedQueryChars
allowHostHeaderMismatch
rejectIllegalHeaderName		false
maxSavePostSize				4 * 1024
maxHttpHeaderSize			8 * 1024
connectionUploadTimeout		300000
disableUploadTimeout		true
restrictedUserAgents
server
serverRemoveAppProvidedValues	false
maxTrailerSize				8192
maxExtensionSize			8192
maxSwallowSize				2 * 1024 * 1024
secure						false


其中 CompressionConfig 属性
	
Compression
NoCompressionUserAgents
CompressableMimeType
CompressionMinSize


其中 Endpoint 属性中与 HTTP 相关的

SSLEnabled
UseSendfile
MaxKeepAliveRequests

其中 SSLHostConfig 属性

SslEnabledProtocols
SSLProtoco
KeystoreFile
SSLCertificateChainFile
SSLCertificateFile
SSLCertificateKeyFile
Algorithm
ClientAuth
SSLVerifyClient
TrustMaxCertLength
SSLVerifyDepth
UseServerCipherSuitesOrder
SSLHonorCipherOrder
Ciphers
SSLCipherSuite
KeystorePass
KeyPass
SSLPassword
CrlFile
SSLCARevocationFile
SSLCARevocationPath
KeystoreType
KeystoreProvider
KeyAlias
TruststoreAlgorithm
TruststoreFile
TruststorePass
TruststoreType
TruststoreProvider
SslProtocol
SessionCacheSize
SessionTimeout
SSLCACertificatePath
SSLCACertificateFile
SSLDisableCompression
SSLDisableSessionTickets
TrustManagerClassName


AbstractHttp11JsseProtocol 属性

SslImplementationShortName
SniParseLimit


Http11NioProtocol

其中 Endpoint 配置
PollerThreadCount
SelectorTimeout
PollerThreadPriority


Http11AprProtocol

其中 Endpoint 配置
PollTime
SendfileSize
DeferAccept
```









Nio2 与 Nio 的区别



### AbstractEndpoint

Acceptor 线程

```
AbstractEndpoint 属性

useSendfile
executorTerminationTimeoutMillis
acceptorThreadCount					1    acceptor 线程的数量。
acceptorThreadPriority
maxConnections						10000	（监控点）
executor							处理器
port								0
address								
acceptCount							100
keepAliveTimeout					null
SSLEnabled
minSpareThreads						10
maxThreads							200
threadPriority						NORM_PRIORITY
maxKeepAliveRequests				100
maxHeaderCount						100
name								'TP'
domain
handler


SocketProperties 属性

TcpNoDelay
ConnectionLinger
SoLinger
ConnectionTimeout

```

Tomcat 有 Thread.sleep 阻塞，这是性能杀手。

核心功能：

1. SocketProcessorBase 保存在 SynchronizedStack，处理的时候，多个线程每次从 SynchronizedStack 取出一个 SocketProcessorBase，调用 Executor 执行。支持同步与异步执行。参考 processSocket

2. 创建 acceptorThreadCount 个 Acceptor 处理进来的请求。每个 Acceptor 调用 ServerSocke accept 创建 socket，从 nioChannels 中提取 NioChnnel 或者创建 NioChannel 与 socket 关联起来，并监听可读事件。

   

init

1. bind
2. 注册 SSLHostConfig

start

1. 如果没有  unBind，进行 bind 
2. startInternal  由子类决定

stop

1. unbind
2. 更新状态

pause

1. handler  调用 pause
2. unlockAccept
3. paused 为 true

resume

1. paused 为 false

destroy

1. unbind
2. 注销 sslHostConfig 的 jmx 配置



### NioEndpoint

```
useInheritedChannel
pollerThreadPriority
pollerThreadCount
selectorTimeout
pollerThreadPriority
acceptorThreadCount
```

核心实现

Poller：pollerThreadCount 个线程执行 Poller

NioSelectorPool

eventCache：默认 128，回收完成处理的 PollEvent，**128 对于大量连接肯定是不够的**。

nioChannels: 默认 128，回收完成处理的 NioChannel，**128 对于大量连接肯定是不够的**。由 NioSocketWrapper 封装。

processorCache ： 

Executor

Acceptor : acceptorThreadCount 个线程执行  Acceptor



Acceptor 线程

Acceptor 调用 ServerSocke accept 创建 socket，从 nioChannels 中提取 NioChnnel 或者创建 NioChannel 与 socket 关联起来。



SocketProcessor 线程

1. 根据 socket 获取 SelectionKey
2. handshake，如果  handshake 完成，设置 event 为 OPEN_READ；如果  handshake 为 OP_READ 注册读事件，如果  handshake 为 OP_WRITE 注册写事件，调用 Handler 的 process 方法处理 socket 的 event 事件。
3. 将自己加入 ProcessorCache



Poller 线程

Selector

events 保存即将被处理的 PollerEvent，eventCache 保存处理过的 PollerEvent，也即可以被回收的 PollerEvent

1. 注册 socket，如果 eventCache 中有，就用 eventCache 中的，如果没有就创建新的 PollerEvent
2. 创建 PollerEvent 并将其加入 events
3. 从 events 中取 PollerEvent，调用 run, reset 方法 之后，加入 eventCache；
4. selector 监听事件，如果所有事件，就创建 SocketProcessorBase，用 Executor 执行
5. 检查事件是否过期，继续从 3 开始。



bind:

 	1. socket 进行 bind
 	2. 打开 selectorPool



startInternal

1. 创建 processorCache，eventCache，nioChannels
2. 创建 executor
3. 创建 pollerThreadCount 个 Poller 线程，处理读写事件。这里 eventCache 对所有已经执行的PollerEvent 回收。
4. 创建 acceptorThreadCount 个 acceptor 线程， 处理收的请求，Acceptor 调用 ServerSocke accept 创建 socket，从 nioChannels 中提取 NioChnnel 或者创建 NioChannel 与 socket 关联起来，并监听可读事件。

stopInternal

1. hander 进行 pause
2. poller 停止
3. executor 停止
4. 清除 processorCache，eventCache，nioChannels



unbind

1. 关闭 socket
2. 回收 handler 
3. 关闭 selectorPool













###  NioSocketWrapper 

```
pool
Poller
socketBufferHandler

```

channel.register 的 attr。





#### startInternal

1. 初始化 eventCache，nioChannels，processorCache
2. 创建 Executor
3. pollerThreadCount 个线程执行 Poller
4. acceptorThreadCount 个线程执行  Acceptor



#### stopInternal

1. Acceptor 关闭
2. Poller destroy
3. 关闭 Executor
4. 清除 eventCache，nioChannels，processorCache



### unbind

1. 关闭 ServerSocke
2. 消耗 SSL
3. unbind
4. 回收 Handler
5. 关闭  selectorPool



###  close

将 socket 加入 nioChannels











### CoyoteAdapter

### asyncDispatch

 检查请求是否是读写分发的，如果是，进行读写分发。TODO



###service

1. 如果 request 不存在，创建 request 和 response。
2. 设置请求处理器名称
3. 解析 URL（参考 prepare）
4. 如果解析成功，检查支持异步支持，调用 pipeline 的第一个 Value 的 invoke 方法
5. 如果是异步的，读监听器

####  prepare

1. 设置 schema ： http 或 https
2. 设置代理：代理名，代理端口
3. 获取解码的 URI：URI 解码，解析路径中的 query 参数，转换，归一化，转换字符集
4. 从 cookie 和 SSL  中解析 session id
5. 检查重定向
6. 检查 TRACE 请求
7. 认证



### Request

```
org.apache.coyote.Request coyoteRequest
Cookie []cookies 
Map<String, Object> attributes
InputBuffer inputBuffer
CoyoteInputStream inputStream
CoyoteReader reader
ParameterMap<String, String[]> parameterMap
Session session
MappingData mappingData 
ApplicationMapping applicationMapping 
HttpServletRequest applicationRequest
Connector connector
FilterChain filterChain
RequestFacade facade

org.apache.catalina.connector.Response response

```

实现了接口 HttpServletRequest，RequestFacade 是其代理。

1. 属性，依次从 specialAttributes，attributes，coyoteRequest
2. AsyncContextImpl 为异步实现
3. 各种 http 头的设置获取



Response

```
org.apache.coyote.Response coyoteResponse
OutputBuffer outputBuffer;
CoyoteOutputStream outputStream
CoyoteWriter writer
List<Cookie> cookies
HttpServletResponse applicationResponse 
ResponseFacade facade
```

实现了接口 HttpServletResponse，ResponseFacade 是其代理。

1. 各种 http 头的设置获取







### 基础结构



#### MessageByte

支持三种数据类型 byte，char，string 来表示 http，并且可以回收。T_NULL 表示为空，T_BYTES 表示用 byte 数组存储数据，T_STR 表示用 字符串存储数据，T_CHARS 用 char 数组存储数据。只能用一种。

### InputBuffer

实现了 java.io.Reader 接口，底层读数据的 buffer，通过 CharBuff 和 ByteBuffer 二选一的读写方式，为了对编码的支持，支持从 Byte 到 Char 的转化。此外支持，读写监听器。

### OutputBuffer

实现了 java.io.Reader 接口，底层读数据的 buffer，通过 CharBuff 和 ByteBuffer 二选一的读写方式，为了对编码的支持，支持从 char 到 byte 的转化。此外支持，读写监听器。

### CoyoteReader

继承 BufferedReader，对行的读写用 char 数组，其他用 InputBuffer

### CoyoteWriter

继承自 PrintWriter，用 OutputBuffer 进行写。

### SynchronizedStack

只扩容不缩容的栈实现。元素保存在数组。



### SocketWrapperBase





```
enum SendfileKeepAliveState
	NONE，PIPELINED，OPEN
	
enum SocketEvent
	OPEN_READ，OPEN_WRITE，STOP，TIMEOUT，DISCONNECT，ERROR
	
enum SendfileState
	PENDING，DONE，ERROR
```

```
SocketProcessorBase<S> implements Runnable
	SocketWrapperBase<S> socketWrapper
	SocketEvent event
	
	
SocketProcessor extends SocketProcessorBase<NioChannel>
```



```
AbstractEndpoint<S>
	SocketProperties socketProperties : socket 属性
	SynchronizedStack<SocketProcessorBase<S>> processorCache : 
	executor : ThreadPoolExecutor 或 ResizableExecutor
	Handler<S> handler : 
	HashMap<String, Object> attributes : 
	

1. 设置线程池参数，用 executor 执行 
2. 从 processorCache 弹出元素，调用 executor 执行，或在当前线程执行(processSocket)
3. 创建 acceptorThreadCount 个 acceptor 线程， 处理收的请求，Acceptor 调用 ServerSocke accept 创建 socket，从 nioChannels 中提取 NioChnnel 或者创建 NioChannel 与 socket 关联起来，并监听可读事件。



AbstractJsseEndpoint<S> extends AbstractEndpoint<S>
	SSLImplementation sslImplementation
	
增加对 SSL 的支持


NioEndpoint extends AbstractJsseEndpoint<NioChannel> 	
	NioSelectorPool selectorPool
	ServerSocketChannel serverSock
	SynchronizedStack<PollerEvent> eventCache
	SynchronizedStack<NioChannel> nioChannels
	Poller[] pollers
	


Nio2Channel implements AsynchronousByteChannel
	AsynchronousSocketChannel sc : 读写 ByteBuffer
	SocketWrapperBase<Nio2Channel> socket :
	SocketBufferHandler bufHandler: 
	ApplicationBufferHandler appReadBufHandler
	
NioChannel implements ByteChannel
	SocketChannel sc : 读写 ByteBuffer
	SocketWrapperBase<NioChannel> socketWrapper
	SocketBufferHandler bufHandler
	Poller poller : 获取 SelectionKey 的 attachment
	ApplicationBufferHandler appReadBufHandler
	
	
```



```
SocketBufferHandler

两个 ByteBuffer 一个读 buffer，一个写 buffer。可以配置 buffer 可读，可写。具体的读写通过 getReadBuffer 和 getWriteBuffer 来执行。


SocketWrapperBase<E>
	E socket : 为 NioChannel 或 Nio2Channel
	AbstractEndpoint<E> endpoint : NioEndpoint
	SocketBufferHandler socketBufferHandler : 来自  socket.getBufferHandler
	LinkedBlockingDeque<ByteBufferHolder> bufferedWrites ：缓冲读写数据
	
对 socket，endpoint，SocketBufferHandler 的封装。由 socketBufferHandler 的 writeBuffer 和 readBuffer 进行读写。processSocket 由 endpoint 处理。

对读写进行了优化：如果写的时候，buffer 是空的，直接写，否则加到 bufferedWrites。
```



```
BlockPoller extends Thread

1. 增加，删除读写事件
2. 线程运行的时候，对每个事件的增加和删除，之后监听事件，对于 SelectionKey 的 read，write 进行执行。


NioBlockingSelector
	SynchronizedStack<KeyReference> keyReferenceStack
	Selector sharedSelector
	BlockPoller poller
	
读的时候，从 keyReferenceStack 弹出元素，用 RunnableAdd 注册事件，读完，用 RunnableRemove 删除事件，归还到 keyReferenceStack
写的时候，从 keyReferenceStack 弹出元素，用 RunnableAdd 注册事件，读完，用 RunnableRemove 删除事件，归还到 keyReferenceStack

所有操作都线程 BlockPoller 中执行。

NioSelectorPool
	NioBlockingSelector blockingSelector
	Selector SHARED_SELECTOR
	ConcurrentLinkedQueue<Selector> selectors
支持共享的 Selector 和池化的 Selector，并且支持读写 ByteBuffer，其中共享的读写由 blockingSelector 完成，其他读写由读写的接口参数由 NioChannel 执行。



NioSocketWrapper extends SocketWrapperBase<NioChannel>
	NioSelectorPool pool
	Poller poller
	
读写事件的增加与删除由 Poller 来决定
读写由 NioChannel 或 Nio2Chnnnel 来处理


class Poller implements Runnable
	 Selector selector;
	 SynchronizedQueue<PollerEvent> events: 保存事件，将运行之后的事件加入 eventCache
	 
1. events 函数处理所有的事件。
2. 遍历 selector 的 selectedKeys，对每个 SelectionKey 的事件类型（读或写）创建 SocketProcessorBase，调用 Executor 进行执行。

```



读写事件由线程 BlockPoller 来处理。

具体的读写根据配置可以使共享的阻塞的 blockingSelector 读写，也可以是 NioChannel 进行读写。





读的路径

```
NioSocketWrapper.read
```





问题：keyReferenceStack 为什么不直接用 SelectionKey 而要用包装之后的 KeyReference？

