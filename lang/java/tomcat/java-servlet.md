java-servlet



设计的设计模式

1. 模板模式:  ContainerBase，LifecycleBase
2. 观察者模式: Listener
3. 状态模式 : init,  start, stop, 
4. 责任链模式 ：StandardEngine -> StandardHost -> StandardContext

### Servelet

Servlet 是 Server+Applet 的缩写，根据下面可以清楚地看到，Servlet 事实上是一套 web 规范。



```java
public interface Servlet {
	public void init(ServletConfig config) throws ServletException;
	public ServletConfig getServletConfig();
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;
	public String getServletInfo(); // 获取 author, version, copyright
	public void destroy();
}

public interface ServletConfig {
    public String getServletName();
    public ServletContext getServletContext();
    public String getInitParameter(String name);
    public Enumeration<String> getInitParameterNames();
}
    
public interface ServletRequest {
    public ServletInputStream getInputStream() throws IOException;
    public BufferedReader getReader() throws IOException;
    
    public ServletContext getServletContext();
    public RequestDispatcher getRequestDispatcher(String path);
    
    public Object getAttribute(String name);
    public Enumeration<String> getAttributeNames();
    public void setAttribute(String name, Object o);
    public void removeAttribute(String name);
    
    public String getCharacterEncoding();
    public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException;
    public int getContentLength();
    public long getContentLengthLong();
    public String getContentType();
    public String getParameter(String name); //url 中 query 参数
    public Enumeration<String> getParameterNames();
    public String[] getParameterValues(String name);
    public Map<String, String[]> getParameterMap();
    public String getProtocol(); //http1.1
    public String getScheme(); //http, https
    public String getServerName(); //Host 头的 ”:“ 前面部分
    public int getServerPort();
    public String getRemoteAddr();
    public String getRemoteHost();
    public Locale getLocale(); //根据 Accept-Language
    public Enumeration<Locale> getLocales();
    public boolean isSecure(); //是否是安全连接 https
    public String getRealPath(String path);
    public int getRemotePort();
    public String getLocalName();
    public String getLocalAddr();
    public int getLocalPort();
      
    public AsyncContext startAsync() throws IllegalStateException;
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException;
    public boolean isAsyncStarted();
    public boolean isAsyncSupported();
    public AsyncContext getAsyncContext();
    public DispatcherType getDispatcherType();
}    
    
public interface ServletResponse {
    public ServletOutputStream getOutputStream() throws IOException;
    public PrintWriter getWriter() throws IOException;
    
    public String getCharacterEncoding();
    public void setCharacterEncoding(String charset);
    public void setContentLength(int len);
    public void setContentLengthLong(long length);
    public String getContentType();
    public void setContentType(String type);
    public void setBufferSize(int size);
    public int getBufferSize();
    public void flushBuffer() throws IOException;
    public void resetBuffer(); //清除底层数据，不包括 header 和 status code
    public boolean isCommitted(); //将 status code 和  header 已经发送出去是为 True
    public void reset();  //清除底层数据，包括 header 和 status code
    public void setLocale(Locale loc);
    public Locale getLocale();
}   
    
public interface ServletContext {
    public static final String TEMPDIR = "javax.servlet.context.tempdir";
    public static final String ORDERED_LIBS = "javax.servlet.context.orderedLibs";
    public String getContextPath();
    public ServletContext getContext(String uripath);
    public int getMajorVersion();
    public int getMinorVersion();
    public int getEffectiveMajorVersion();
    public int getEffectiveMinorVersion();
    public String getMimeType(String file);
    public Set<String> getResourcePaths(String path);
    public URL getResource(String path) throws MalformedURLException;
    public InputStream getResourceAsStream(String path);
    public RequestDispatcher getRequestDispatcher(String path);
    public RequestDispatcher getNamedDispatcher(String name);
    public Servlet getServlet(String name) throws ServletException;
    public Enumeration<Servlet> getServlets();
    public Enumeration<String> getServletNames();
    public void log(String msg);
    public void log(Exception exception, String msg);
    public void log(String message, Throwable throwable);
    public String getRealPath(String path);
    public String getServerInfo();
    public String getInitParameter(String name); //对应配置文件 init-parameter
    public Enumeration<String> getInitParameterNames();
    public boolean setInitParameter(String name, String value);
    public Object getAttribute(String name);
    public Enumeration<String> getAttributeNames();
    public void setAttribute(String name, Object object);
    public void removeAttribute(String name);
    public String getServletContextName();
    public ServletRegistration.Dynamic addServlet(String servletName, String className);
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet);
    public ServletRegistration.Dynamic addServlet(String servletName,
    public <T extends Servlet> T createServlet(Class<T> c)
    public ServletRegistration getServletRegistration(String servletName);
    public Map<String, ? extends ServletRegistration> getServletRegistrations();
    public FilterRegistration.Dynamic addFilter(String filterName, String className);
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter);
    public FilterRegistration.Dynamic addFilter(String filterName,
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException;
    public FilterRegistration getFilterRegistration(String filterName);
    public Map<String, ? extends FilterRegistration> getFilterRegistrations();
    public SessionCookieConfig getSessionCookieConfig();
    public void setSessionTrackingModes(
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes();
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes();
    public void addListener(String className);
    public <T extends EventListener> void addListener(T t);
    public void addListener(Class<? extends EventListener> listenerClass);
    public <T extends EventListener> T createListener(Class<T> c)
    public JspConfigDescriptor getJspConfigDescriptor();
    public ClassLoader getClassLoader();
    public void declareRoles(String... roleNames);
    public String getVirtualServerName();
}
        

//根据配置文件初始化除 service 之外的方法
public abstract class GenericServlet implements Servlet, ServletConfig, java.io.Serializable {
   	
    private transient ServletConfig config;
    public GenericServlet() {}
    public void destroy() {}
    public String getInitParameter(String name) {
        return getServletConfig().getInitParameter(name);
    }
    public Enumeration<String> getInitParameterNames() {
        return getServletConfig().getInitParameterNames();
    }
    public ServletConfig getServletConfig() {
        return config;
    }
    public ServletContext getServletContext() {
      return this.getServletConfig().getServletContext();
    }
    public String getServletInfo() { return ""; }
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        this.init();
    }
    public void init() throws ServletException {}
    public void log(String msg) { 
        getServletContext().log(getServletName() + ": " + msg);
    }
    public void log(String message, Throwable t) {
    	getServletContext().log(getServletName() + ": " + message, t);
    }
    public abstract void service(ServletRequest req, ServletResponse res)
    public String getServletName() {
        return config.getServletName();
    }
}
        
    
// HTTP协议实现的 Servlet 基类
public abstract class HttpServlet extends GenericServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
 	  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
    //根据 req 的方法不同，调用不同的 do 方法
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletRequest request;
        HttpServletResponse response;
        try {
            request = (HttpServletRequest)req;
            response = (HttpServletResponse)res;
        } catch (ClassCastException var6) {
            throw new ServletException(lStrings.getString("http.non_http"));
        }

        this.service(request, response);
    }
}
```



#### 生命周期管理

1. 通过 ServletConfig  初始化 Servlet 实例
2. service 通过 ServletRequest 读取客户端数据，根据 ServletRequest 得到 ServletContext 处理请求，构造 ServletResponse，应答客户端请求。
3. 通过 destroy 销毁

#### 基本信息

核心要素

Servlet ：容器声明周期管理

ServletConfig：获取配置信息，来自 web.xml 中的 init-param 标签配置

ServletContext：上下文，由 ServletConfig 提供，可以获取应用相关的数据。

ServletRequest：请求相关信息，获取输入流，通过输入流的解析，可以获取到 Http 协议相关各种信息。如 content-length, query parameter, Character-Encoding scheme 等等

ServletResponse：应答相关信息





## 异步 Servlet





## tomcat

​	

Container 有一个背景线程专门调用自己及其子 container 的 pipeline, realm, cluster 的 backgroundProcess()

### 初始化



#### 环境变量初始化

设置 catalina.home，优先级从高到低

1. 如果 catalina.home 存在
2. 如果 user.dir 包含 bootstrap.jar
3. 如果 user.dir 

设置 catalina.base ，优先级从高到低

1. 如果没有设置，则设置为 catalina.home



#### 配置加载

1. catalina.config
2. catalina.base/conf/catalina.properties
3. /org/apache/catalina/startup/catalina.properties

#### 支持命令 startd，stopd，start，stop，

如果 startd 命令

1. init()
2. load(args)
3. start()

如果 start 命令

1. setAwait()
2. init()
3. load(args)
4. start()

如果 stop 或 stopd 命令

1. stop()

如果 stop

1. stopServer()



####  启动过程

1. 读 catalina.properties 文件，将里面的变量用系统变量替换，创建类加载器 common.loader, server.loader, shared.loader。
2. 设置当前线程的加载器为 server.loader
3. 创建 org.apache.catalina.startup.Catalina 实例  catalinaDaemon
4. 设置 catalinaDaemon 的父类的类加载器为 sharedLoader
5. 反射调用 catalinaDaemon 的 load 方法
6. 反射调用 catalinaDaemon 的 start 方法

因此，初始化本质上是调用 org.apache.catalina.startup.Catalina 的相应方法。



#### 运行过程

1. Connector 接受请求并将请求封装成 Request 和 Response，最底层是使用 Socket来进行连接的 ， Request 和 Response 是按照 HTTP 协议来封装的 ，所以 Conn ector 同时实现了 TCP/IP 协议和 HTTP 协议 
2. 运行 pipeline 中各个 Value 的 invoke 方法。
3. 运行 FilterChain 的 doFilter
4. Connector 使用 Socket 将处理结果返回给客户端 

这样整个请求就处理完了 。

####  Server 启动过程详细版

init()

1. 解析配置文件 conf/server.xml 或 server-embed.xml，也可以在命令行指定
2. 配置 Server
3. 重定向输入输出流
4. 调用 Server 的 init() 方法进行初始化

start()

1. 调用 Server 的 start()
2. 配置  shutdownHook
3. 调用 Server 的 await()

stop()

1. 检查当前声明周期状态，如果没有在 STOPPING_PREP 和 DESTROYED 之间，调用 Server.stop()  Server.destroy()



```
       	   	  start()
   -----------------------------
   |                           |
   | init()                    |
  NEW -»-- INITIALIZING        |
  | |           |              |     ------------------«-----------------------
  | |           |auto          |     |                                        |
  | |          \|/    start() \|/   \|/     auto          auto         stop() |
  | |      INITIALIZED --»-- STARTING_PREP --»- STARTING --»- STARTED --»---  |
  | |         |                                                            |  |
  | |destroy()|                                                            |  |
  | --»-----«--    ------------------------«--------------------------------  ^
  |     |          |                                                          |
  |     |         \|/          auto                 auto              start() |
  |     |     STOPPING_PREP ----»---- STOPPING ------»----- STOPPED -----»-----
  |    \|/                               ^                     |  ^
  |     |               stop()           |                     |  |
  |     |       --------------------------                     |  |
  |     |       |                                              |  |
  |     |       |    destroy()                       destroy() |  |
  |     |    FAILED ----»------ DESTROYING ---«-----------------  |
  |     |                        ^     |                          |
  |     |     destroy()          |     |auto                      |
  |     --------»-----------------    \|/                         |
  |                                 DESTROYED                     |
  |                                                               |
  |                            stop()                             |
  ----»-----------------------------»------------------------------
```



### 类结构

```
interface Server extends Lifecycle

```





### 服务生命周期

典型的状态模式

#### 服务状态流转

```java
LifecycleState.NEW
LifecycleState.INITIALIZING
initInternal()
LifecycleState.INITIALIZED

LifecycleState.STARTING_PREP
LifecycleState.STARTING	startInternal()
LifecycleState.STARTED

LifecycleState.STOPPING_PREP
LifecycleState.STOPPING 或 LifecycleState.FAILED  stopInternal()
LifecycleState.STOPPED

LifecycleState.DESTROYING
LifecycleState.STOPPED 或 LifecycleState.FAILED 或 LifecycleState.NEW  或 LifecycleState.INITIALIZED 	destroyInternal();
LifecycleState.DESTROYED


start 之前只能是 INITIALIZED，STOPPED
stop 之前只能是 NEW, FAILED, STARTED
```



Bootstrap -> Catalina -> StandardServer -> StandardService

### Server 的生命周期 (默认实现为 StandardServer)

#### init

1. globalNamingResources.init();
2. 注册 onameStringCache，onameMBeanFactory
3. 调用各个 service 的 init() 方法

#### start

1. 设置状态为 LifecycleState.STARTING
2. globalNamingResources.start();
3. 调用各个 Service 的 start() 方法

#### stop

1. 设置状态为 LifecycleState.STOPPING;
2. 调用各个 Service 的 stop() 方法
3. globalNamingResources.stop();

#### destory

1. 调用各个 service 的 destroy() 方法
2. globalNamingResources.destroy();
3. 注销 onameMBeanFactory，onameStringCache

#### await

port = -1 直接返回

port == -2 一直等待，直到设置 awaitStop 为 true，返回

port 为其他，默认监听 8005 端口，收到 'SHUTDOWN' 加 控制字符（小于 32 的 ascii），返回。



### Service 的生命周期（默认 StandardService）

#### init

1. engine.init()
2. 所有 executor 的 init()
3. 所有 connector 的 init()

#### start

1. engine.start()
2. 所有 executor 的 start()
3. 所有 connector 的 start()

#### stop

1. 所有 connectors 的 pause()
2. engine.stop()
3. 所有 connector 的 stop()
4. 所有 executor 的 stop()

#### destroy

1. 所有 connector 的 destroy()
2. 所有 executor 的 destroy()
3. engine.destroy()



### Container 的声明周期(默认 ContainerBase)

Pipeline ： TODO

####  init

初始化 startStopExecutor

#### start

1. 调用所有 Cluster，Realm 的 start 方法
2. 调用所有子 Container 的 start 方法
3. 调用所有 Pipeline 的 start 方法
4. 设置状态为 LifecycleState.STARTING
5. 开启背景线程

#### stop

1. 停止背景线程
2. 设置状态为 LifecycleState.STOPPING
3. 调用 pipeline 的 stop 方法
4. 调用所有子 Children 的 stop 方法
5. 调用所有子 Container 的 stop 方法
6. 调用所有 Cluster，Realm 的 stop 方法

#### destroy

1. 调用所有 Cluster，Realm，Pipeline 的 destroy 方法
2. 删除所有子 Container
3. 停止 startStopExecutor



### Engine(默认 StandardEngine)

顶层 Container，parent 必须为  null，Child 只能是 Host。如果没有配置 Realm，用 nullRealm

Pipeline 的 basic 为 StandardEngineValve

####  init

初始化 startStopExecutor

#### start

同 ContainerBase

#### stop

同 ContainerBase

#### destroy

同 ContainerBase





| 变量        | 类型    | 默认值 |
| ----------- | ------- | ------ |
| defaultHost | String  |        |
| jvmRouteId  | String  |        |
| service     | Service |        |
|             |         |        |



### Host(默认StandardHost)

parent 可以为 Container，Child 只能是 Context。Pipeline 必须包含 errorValve，

Pipeline 的 basic 为 StandardHostValve

生命周期：与 ContainerBase 一样

| 变量                       | 类型                     | 默认值                                         |
| -------------------------- | ------------------------ | ---------------------------------------------- |
| aliases                    | String[]                 |                                                |
| name                       | String                   |                                                |
| workDir                    | String                   |                                                |
| unpackWARs                 | boolean                  | true                                           |
| deployIgnore               | String                   |                                                |
| failCtxIfServletStartFails | boolean                  | False                                          |
| alias                      | List<String>             |                                                |
| copyXML                    | boolean                  | False                                          |
| deployXML                  | boolean                  | false                                          |
| deployOnStartup            | boolean                  | true                                           |
| errorReportValveClass      | String                   | "org.apache.catalina.valves.ErrorReportValve"; |
| contextClass               | String                   | "org.apache.catalina.core.StandardContext"     |
| configClass                | String                   | "org.apache.catalina.startup.ContextConfig"    |
| autoDeploy                 | boolean                  | true                                           |
| createDirs                 | String                   |                                                |
| hostConfigBase             | File                     |                                                |
| xmlBase                    | String                   |                                                |
| appBase                    | String                   | webapps                                        |
| appBaseFile                | File                     |                                                |
| undeployOldVersions        | boolean                  |                                                |
| childClassLoaders          | Map<ClassLoader, String> |                                                |
|                            |                          |                                                |

应用部署的三种方式

1. xml 描述文件 conf/{enginename}/{hostname}/*.xml
2. War 或 app 文件夹放入 appBase 文件夹



### Context(StandardContext)

parent 可以为 Container，Child 只能是 Wrapper。Pipeline 必须包含 errorValve，

Pipeline 的 basic 为  StandardContextValve

| 变量                                     | 类型                                           | 默认值                                   |
| ---------------------------------------- | ---------------------------------------------- | ---------------------------------------- |
| allowCasualMultipartParsing              | boolean                                        | false                                    |
| swallowAbortedUploads                    | boolean                                        | true                                     |
| altDDName                                | String                                         |                                          |
| InstanceManager                          | InstanceManager                                |                                          |
| antiResourceLocking                      | boolean                                        | false                                    |
| applicationListeners                     | String[]                                       |                                          |
| noPluggabilityListeners                  | Set< Object>                                   |                                          |
| applicationEventListenersList            | List< Object >                                 |                                          |
| applicationLifecycleListenersObjects     | Object[]                                       |                                          |
| initializers                             | Map<ServletContainerInitializer,Set<Class<?>>> |                                          |
| applicationParameters                    | ApplicationParameter[]                         |                                          |
| broadcaster                              | NotificationBroadcasterSupport                 |                                          |
| charsetMapper                            | CharsetMapper                                  |                                          |
| charsetMapperClass                       | String                                         | "org.apache.catalina.util.CharsetMapper" |
| configFile                               | URL                                            |                                          |
| configured                               | boolean                                        | false                                    |
| constraints                              | SecurityConstraint[]                           |                                          |
| context                                  | ApplicationContext                             |                                          |
| noPluggabilityServletContext             | NoPluggabilityServletContext                   |                                          |
| cookies                                  | boolean                                        | true                                     |
| crossContext                             | boolean                                        | false                                    |
| encodedPath                              | String                                         |                                          |
| path                                     | String                                         |                                          |
| delegate                                 | boolean                                        | false                                    |
| denyUncoveredHttpMethods                 | boolean                                        |                                          |
| displayName                              | String                                         |                                          |
| defaultContextXml                        | String                                         |                                          |
| defaultWebXml                            | String                                         |                                          |
| distributable                            | boolean                                        |                                          |
| docBase                                  | String                                         |                                          |
| exceptionPages                           | HashMap<String, ErrorPage>                     |                                          |
| filterConfigs                            | HashMap<String, ApplicationFilterConfig>       |                                          |
| filterDefs                               | HashMap<String, FilterDef>                     |                                          |
| filterMaps                               | ContextFilterMaps                              |                                          |
| ignoreAnnotations                        | boolean                                        | False                                    |
| loader                                   | Loader                                         | WebappLoader                             |
| loginConfig                              | LoginConfig                                    |                                          |
| manager                                  | Manager                                        |                                          |
| namingContextListener                    | NamingContextListener                          |                                          |
| namingResources                          | NamingResources                                |                                          |
| messageDestinations                      | HashMap<String, MessageDestination>            |                                          |
| mimeMappings                             | HashMap<String, String>                        |                                          |
| parameters                               | ConcurrentMap<String, String>                  |                                          |
| paused                                   | boolean                                        | false                                    |
| publicId                                 | String                                         |                                          |
| reloadable                               | boolean                                        | false                                    |
| unpackWAR                                | boolean                                        | true                                     |
| copyXML                                  | boolean                                        | false                                    |
| override                                 | boolean                                        | false                                    |
| originalDocBase                          | String                                         |                                          |
| privileged                               | boolean                                        | false                                    |
| replaceWelcomeFiles                      | Boolean                                        | false                                    |
| roleMappings                             | HashMap<String, String>                        |                                          |
| securityRoles                            | String[]                                       |                                          |
| servletMappings                          | HashMap<String, String>                        |                                          |
| sessionTimeout                           | int                                            | 30                                       |
| sequenceNumber                           | AtomicLong                                     |                                          |
| statusPages                              | HashMap<Integer, ErrorPage>                    |                                          |
| swallowOutput                            | boolean                                        | false                                    |
| unloadDelay                              | long                                           | 2000                                     |
| watchedResources                         | String[]                                       |                                          |
| welcomeFiles                             | String                                         |                                          |
| wrapperLifecycles                        | String[]                                       |                                          |
| wrapperListeners                         | String[]                                       |                                          |
| workDir                                  | String                                         |                                          |
| wrapperClassName                         | String                                         |                                          |
| wrapperClass                             | Class<?>                                       |                                          |
| useNaming                                | boolean                                        | true                                     |
| namingContextName                        | String                                         |                                          |
| resources                                | WebResourceRoot                                | StandardRoot                             |
| startupTime                              | long                                           |                                          |
| startTime                                | long                                           |                                          |
| tldScanTime                              | long                                           |                                          |
| j2EEApplication                          | String                                         |                                          |
| j2EEServer                               | String                                         |                                          |
| webXmlValidation                         | boolean                                        |                                          |
| webXmlNamespaceAware                     | boolean                                        |                                          |
| xmlBlockExternal                         | boolean                                        |                                          |
| tldValidation                            | boolean                                        |                                          |
| sessionCookieName                        | String                                         |                                          |
| useHttpOnly                              | boolean                                        |                                          |
| sessionCookieDomain                      | String                                         |                                          |
| sessionCookiePath                        | String                                         |                                          |
| sessionCookiePathUsesTrailingSlash       | Boolean                                        | false                                    |
| jarScanner                               | JarScanner                                     |                                          |
| clearReferencesRmiTargets                | boolean                                        | true                                     |
| clearReferencesStopThreads               | boolean                                        | false                                    |
| clearReferencesStopTimerThreads          | boolean                                        | false                                    |
| clearReferencesHttpClientKeepAliveThread | boolean                                        | true                                     |
| renewThreadsWhenStoppingContext          | boolean                                        | true                                     |
| clearReferencesObjectStreamClassCaches   | boolean                                        | true                                     |
| logEffectiveWebXml                       | boolean                                        | false                                    |
| effectiveMajorVersion                    | int                                            | 3                                        |
| effectiveMinorVersion                    | int                                            | 0                                        |
| jspConfigDescriptor                      | JspConfigDescriptor                            |                                          |
| resourceOnlyServlets                     | Set<String>                                    |                                          |
| webappVersion                            | String                                         |                                          |
| addWebinfClassesResources                | boolean                                        |                                          |
| fireRequestListenersOnForwards           | boolean                                        |                                          |
| createdServlets                          | Set<Servlet>                                   |                                          |
| preemptiveAuthentication                 | boolean                                        | false                                    |
| sendRedirectBody                         | boolean                                        | false                                    |
| jndiExceptionOnFailedWrite               | boolean                                        | true                                     |
| postConstructMethods                     | Map<String, String>                            |                                          |
| preDestroyMethods                        | Map<String, String>                            |                                          |
| containerSciFilter                       | String                                         |                                          |
| failCtxIfServletStartFails               | Boolean                                        |                                          |
| namingToken                              | Object                                         |                                          |
| cookieProcessor                          | CookieProcessor                                | Rfc6265CookieProcessor                   |
| validateClientProvidedNewSessionId       | boolean                                        | true                                     |
| mapperContextRootRedirectEnabled         | boolean                                        | true                                     |
| mapperDirectoryRedirectEnabled           | boolean                                        | false                                    |
| useRelativeRedirects                     | boolean                                        |                                          |
| dispatchersUseEncodedPaths               | boolean                                        | true                                     |
| requestEncoding                          | String                                         |                                          |
| responseEncoding                         | String                                         |                                          |
| allowMultipleLeadingForwardSlashInPath   | boolean                                        |                                          |
|                                          |                                                |                                          |



#### start

1. 设置 Resource，并调用 start 方法

2. 设置加载器为 WebappLoader

3. 如果 cookieProcessor 为空，设置为 Rfc6265CookieProcessor

4. 设置 charsetMapperClass

5. 检查依赖

6. 增加NamingContextListener

7. 调用 loader 的 start 方法

8. Realm 的 start 方法

9. children 的 start 方法

10. pipeline 的 start 方法

11. 设置 contextManager

12. context 设置 InstanceManager 和 JarScanner

13. 开启 applicationListeners

14. Manager 的 start 方法

15. filterDefs 加入 filterConfigs

16. 调用每个 children  的 load

17. 调用 resource 的 gc 方法

18. parameters 和 applicationParameters 合并，设置 ServletContext 的初始化参数

    

#### stop

1. 停止 ContainerBackgroundProcessor
2. children 的 stop 方法
3. filterConfigs 的 release方法，filterDefs 清空
4. Manager 的 stop 方法
5. pipeline 的 stop 方法
6. context 清除属性
7. realm 的 stop
8. loader 的 stop



#### Wrapper(默认 StandardWrapper)

parent 可以为 Container，不能有 Child。

Pipeline 的 basic 为  StandardContextValve

| 变量                   | 类型                           | 默认  |
| ---------------------- | ------------------------------ | ----- |
| available              | long                           | 0     |
| broadcaster            | NotificationBroadcasterSupport |       |
| countAllocated         | AtomicInteger                  |       |
| facade                 | StandardWrapperFacade          |       |
| instance               | Servlet                        |       |
| instanceInitialized    | boolean                        | false |
| loadOnStartup          | int                            | -1    |
| mappings               | ArrayList< String >            |       |
| parameters             | HashMap<String, String>        |       |
| references             | HashMap<String, String>        |       |
| runAs                  | String                         |       |
| sequenceNumber         | long                           |       |
| servletClass           | String                         |       |
| singleThreadModel      | boolean                        | false |
| unloading              | boolean                        | false |
| maxInstances           | int                            | 20    |
| nInstances             | int                            | 0     |
| instancePool           | Stack<Servlet>                 |       |
| unloadDelay            | long                           | 2000  |
| isJspServlet           | boolean                        |       |
| jspMonitorON           | ObjectName                     |       |
| swallowOutput          | boolean                        |       |
| swValve                | StandardWrapperValve           |       |
| loadTime               | long                           |       |
| classLoadTime          | int                            |       |
| multipartConfigElement | MultipartConfigElement         |       |
| asyncSupported         | boolean                        | false |
| enabled                | boolean                        | true  |
| overridable            | boolean                        | true  |





#### start

等同于 ContainerBase

#### stop

1. unload



BaseValue

通过 StandardXXXValue 的 invoke 方法将各个 StandardXXXValue 的 pipeline 串联起来。

#### StandardEngineValve

```
Host host = request.getHost();
host.getPipeline().getFirst().invoke(request, response);
```



#### StandardHostValve

```
context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
Context context = request.getContext();
context.getPipeline().getFirst().invoke(request, response);
context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);

```

#### StandardContextValve

```java
MessageBytes requestPathMB = request.getRequestPathMB();
Wrapper wrapper = request.getWrapper();
response.sendAcknowledgement();
wrapper.getPipeline().getFirst().invoke(request, response);
```



### StandardWrapperValve

```java
StandardWrapper wrapper = (StandardWrapper) getContainer();
Servlet servlet = null;
Context context = (Context) wrapper.getParent();
servlet = wrapper.allocate();

MessageBytes requestPathMB = request.getRequestPathMB();
DispatcherType dispatcherType = DispatcherType.REQUEST;
request.setAttribute(Globals.DISPATCHER_TYPE_ATTR,dispatcherType);
request.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR,requestPathMB);
ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain(request, wrapper, servlet);


如果是异步
request.getAsyncContextInternal().doInternalDispatch();
如果是同步
filterChain.doFilter(request.getRequest(),response.getResponse());


filterChain.release();
wrapper.deallocate(servlet);
wrapper.unload();
```





#### Executor（默认 StandardThreadExecutor）

维护一个线程池，不过感觉完全可以用 JDK 内置的线程池来实现。参数可以在 server.xml 中配置

```xml
   <Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
```



#### MapperListener

Container，Host，Wrapper，Context 等事件

Host 包含 Context

Context 包含 Container，

Contex 包含 Wrapper



#### Pipeline

每个 Value  之间通过 getNext 相互关联，类似链表，每个 Value 代表一个处理请求的组件，接受 Request，返回 Response。Pipeline 中保存的是 Value，可以理解为 Value 的管理类。

```Java
public interface Valve {

    public Valve getNext();
    public void setNext(Valve valve);
    public void backgroundProcess();
    public void invoke(Request request, Response response)
        throws IOException, ServletException;
    public boolean isAsyncSupported();
}

public interface Pipeline extends Contained {
    public Valve getBasic();
    public void setBasic(Valve valve);
    public void addValve(Valve valve);
    public Valve[] getValves();
    public void removeValve(Valve valve);
    public Valve getFirst();
    public boolean isAsyncSupported();
    public void findNonAsyncValves(Set<String> result);
}
```



实现 Value 接口包括

```
ValveBase
StandardEngineValve
Lifecycle
JmxEnabled
```

实现 Pipeline 接口的包括

```
StandardPipeine : first 指向第一个 Value， basic 指向最后 Value
```



责任链模式

```
EnginePipeline
	EngineValue1 -> EngineValue2 -> ... -> StandardEngineValue
	
HostPipeline
-> HostValue1 -> HostValue2 -> ... -> StandardHostValue
	
ContextPipeline
->	ContextValue1 -> ContextValue2 -> ... -> StandardContextValue
    
WrapperPipeline
->	WrapperValue1 -> WrapperValue2 -> ... -> StandardWrapperValue
```



StandardEngine，StandardHost，StandardWrapper 的 pipeline 来自 ContainerBase

```
StandardEngineValve.invoke(request, response)
	host.getPipeline().getFirst().invoke(request, response);
		StandardHost.getPipeline().getFirst().invoke()
		
		
StandardHostValve.invoke(request, response)
	context.getPipeline().getFirst().invoke(request, response);
	

```



StandardHost 的 pipeline 的 basic 为 StandardHostValve

StandardWrapper 的 pipeline 的 basic 为 StandardWrapperValve



#### jar 包扫描（StandardJarScanner）





### Filter

```Java
     public static ApplicationFilterChain createFilterChain(ServletRequest request,
            Wrapper wrapper, Servlet servlet) {
		ApplicationFilterChain filterChain = null;
        if (request instanceof Request) {
            Request req = (Request) request;
            if (Globals.IS_SECURITY_ENABLED) {
                // Security: Do not recycle
                filterChain = new ApplicationFilterChain();
            } else {
                filterChain = (ApplicationFilterChain) req.getFilterChain();
                if (filterChain == null) {
                    filterChain = new ApplicationFilterChain();
                    req.setFilterChain(filterChain);
                }
            }
        } else {
            // Request dispatcher in use
            filterChain = new ApplicationFilterChain();
        }

        filterChain.setServlet(servlet);
        filterChain.setServletSupportsAsync(wrapper.isAsyncSupported());
        StandardContext context = (StandardContext) wrapper.getParent();
        FilterMap filterMaps[] = context.findFilterMaps();
        DispatcherType dispatcher =
                (DispatcherType) request.getAttribute(Globals.DISPATCHER_TYPE_ATTR);

        String requestPath = null;
        Object attribute = request.getAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR);
        if (attribute != null){
            requestPath = attribute.toString();
        }

        String servletName = wrapper.getName();

		//找到与 dispather 和 requestPath 匹配的 filterConfig
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchDispatcher(filterMaps[i] ,dispatcher)) {
                continue;
            }
            if (!matchFiltersURL(filterMaps[i], requestPath))
                continue;
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
                context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                // FIXME - log configuration problem
                continue;
            }
            filterChain.addFilter(filterConfig);
        }

        //找到与 dispather 和 servletName 匹配的 filterConfig
        // Add filters that match on servlet name second
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchDispatcher(filterMaps[i] ,dispatcher)) {
                continue;
            }
            if (!matchFiltersServlet(filterMaps[i], servletName))
                continue;
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
                context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                // FIXME - log configuration problem
                continue;
            }
            filterChain.addFilter(filterConfig);
        }
     }


    private void internalDoFilter(ServletRequest request,
                                  ServletResponse response)
        throws IOException, ServletException {

        // 调用 filter 的 doFilter 方法
        if (pos < n) {
            ApplicationFilterConfig filterConfig = filters[pos++];
            try {
                Filter filter = filterConfig.getFilter();

                if (request.isAsyncSupported() && "false".equalsIgnoreCase(
                        filterConfig.getFilterDef().getAsyncSupported())) {
                    request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR, Boolean.FALSE);
                }
                if( Globals.IS_SECURITY_ENABLED ) {
                    final ServletRequest req = request;
                    final ServletResponse res = response;
                    Principal principal =
                        ((HttpServletRequest) req).getUserPrincipal();

                    Object[] args = new Object[]{req, res, this};
                    SecurityUtil.doAsPrivilege ("doFilter", filter, classType, args, principal);
                } else {
                    filter.doFilter(request, response, this);
                }
            } catch (IOException | ServletException | RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                e = ExceptionUtils.unwrapInvocationTargetException(e);
                ExceptionUtils.handleThrowable(e);
                throw new ServletException(sm.getString("filterChain.filter"), e);
            }
            return;
        }

        // 调用 server 方法
        try {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                lastServicedRequest.set(request);
                lastServicedResponse.set(response);
            }

            if (request.isAsyncSupported() && !servletSupportsAsync) {
                request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR,
                        Boolean.FALSE);
            }
            // Use potentially wrapped request from this point
            if ((request instanceof HttpServletRequest) &&
                    (response instanceof HttpServletResponse) &&
                    Globals.IS_SECURITY_ENABLED ) {
                final ServletRequest req = request;
                final ServletResponse res = response;
                Principal principal =
                    ((HttpServletRequest) req).getUserPrincipal();
                Object[] args = new Object[]{req, res};
                SecurityUtil.doAsPrivilege("service",
                                           servlet,
                                           classTypeUsedInService,
                                           args,
                                           principal);
            } else {
                servlet.service(request, response);
            }
        } catch (IOException | ServletException | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            e = ExceptionUtils.unwrapInvocationTargetException(e);
            ExceptionUtils.handleThrowable(e);
            throw new ServletException(sm.getString("filterChain.servlet"), e);
        } finally {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                lastServicedRequest.set(null);
                lastServicedResponse.set(null);
            }
        }
    }
```





### 系统变量

jvmRoute



### 调优



1. corePoolSize
2. maximumPoolSize
3. maxQueueSize : 默认最大整数
4. maxThreads
5. maxIdleTime
6. minSpareThreads
7. 

## 附录

文件操作

```java
public class File
    implements Serializable, Comparable<File>
	private static final FileSystem fs = DefaultFileSystem.getFileSystem();
	public String getAbsolutePath() {
        return fs.resolve(this);
    }
	
    private File(String pathname, int prefixLength) {
        this.path = pathname;
        this.prefixLength = prefixLength;
    }

	public File getAbsoluteFile() {
        String absPath = getAbsolutePath();
        return new File(absPath, fs.prefixLength(absPath));
	}
	
   	public String getCanonicalPath() throws IOException {
        if (isInvalid()) {
            throw new IOException("Invalid file path");
        }
        return fs.canonicalize(fs.resolve(this));
    }
    
    public File getCanonicalFile() throws IOException {
        String canonPath = getCanonicalPath();
        return new File(canonPath, fs.prefixLength(canonPath));
    }
    
}

class DefaultFileSystem {

    /**
     * Return the FileSystem object for Unix-based platform.
     */
    public static FileSystem getFileSystem() {
        return new UnixFileSystem();
    }
}
   


    public int prefixLength(String pathname) {
        if (pathname.length() == 0) return 0;
        return (pathname.charAt(0) == '/') ? 1 : 0;
    }

    public String resolve(String parent, String child) {
        if (child.equals("")) return parent;
        if (child.charAt(0) == '/') {
            if (parent.equals("/")) return child;
            return parent + child;
        }
        if (parent.equals("/")) return parent + child;
        return parent + '/' + child;
    }

    public String normalize(String pathname) {
        int n = pathname.length();
        char prevChar = 0;
        for (int i = 0; i < n; i++) {
            char c = pathname.charAt(i);
            if ((prevChar == '/') && (c == '/'))
                return normalize(pathname, n, i - 1);
            prevChar = c;
        }
        if (prevChar == '/') return normalize(pathname, n, n - 1);
        return pathname;
    }

    private ExpiringCache cache = new ExpiringCache();

    //useCanonCaches 和 useCanonPrefixCache 默认都是 True
    public String canonicalize(String path) throws IOException {
        if (!useCanonCaches) {
            return canonicalize0(path);
        } else {
            String res = cache.get(path);
            if (res == null) {
                String dir = null;
                String resDir = null;
                if (useCanonPrefixCache) {
                    // Note that this can cause symlinks that should
                    // be resolved to a destination directory to be
                    // resolved to the directory they're contained in
                    dir = parentOrNull(path);
                    if (dir != null) {
                        resDir = javaHomePrefixCache.get(dir);
                        if (resDir != null) {
                            // Hit only in prefix cache; full path is canonical
                            String filename = path.substring(1 + dir.length());
                            res = resDir + slash + filename;
                            cache.put(dir + slash + filename, res);
                        }
                    }
                }
                if (res == null) {
                    res = canonicalize0(path);
                    cache.put(path, res);
                    if (useCanonPrefixCache &&
                        dir != null && dir.startsWith(javaHome)) {
                        resDir = parentOrNull(res);
                        // Note that we don't allow a resolved symlink
                        // to elsewhere in java.home to pollute the
                        // prefix cache (java.home prefix cache could
                        // just as easily be a set at this point)
                        if (resDir != null && resDir.equals(dir)) {
                            File f = new File(res);
                            if (f.exists() && !f.isDirectory()) {
                                javaHomePrefixCache.put(dir, resDir);
                            }
                        }
                    }
                }
            }
            return res;
        }
    }
    private native String canonicalize0(String path) throws IOException;

	//如果 path 的存在连续两字符是 .. ，返回  null，比如 abg/bgsg/..agg/agg
    //如果 path 最后两个字符是 /.，返回 null，比如 /.
    //如果 path 最后两个个字符是 //，返回 null，比如 abg/bgsg//
    //如果 出现连续两个 // 字符，返回  null，比如 abg//bgsg/
    //返回第一个字符到最后一个 / 之间的内容。比如 /abg/gbng/gadg 返回 /abg/gbng
    static String parentOrNull(String path) {
        if (path == null) return null;
        char sep = File.separatorChar;
        int last = path.length() - 1;
        int idx = last;
        int adjacentDots = 0;
        int nonDotCount = 0;
        while (idx > 0) {
            char c = path.charAt(idx);
            if (c == '.') {
                if (++adjacentDots >= 2) {
                    // Punt on pathnames containing . and ..
                    return null;
                }
            } else if (c == sep) {
                if (adjacentDots == 1 && nonDotCount == 0) {
                    // Punt on pathnames containing . and ..
                    return null;
                }
                if (idx == 0 ||
                    idx >= last - 1 ||
                    path.charAt(idx - 1) == sep) {
                    // Punt on pathnames containing adjacent slashes
                    // toward the end
                    return null;
                }
                return path.substring(0, idx);
            } else {
                ++nonDotCount;
                adjacentDots = 0;
            }
            --idx;
        }
        return null;
    }
```

getAbsolutePath 和 getCanonicalPath 只区别在 canonicalize。



```

# ObjectCreateRule

Server  org.apache.catalina.core.StandardServer   className
Server/GlobalNamingResources	org.apache.catalina.deploy.NamingResourcesImpl
Server/Listener	null	className
Server/Service	org.apache.catalina.core.StandardService	className
Server/Service/Listener	null	className
Server/Service/Executor	org.apache.catalina.core.StandardThreadExecutor	className
Server/Service/Connector/SSLHostConfig	org.apache.tomcat.util.net.SSLHostConfig
Server/Service/Connector/Listener	null	className
Server/Service/Connector/UpgradeProtocol


# SetPropertiesRule

Server
Server/GlobalNamingResources
Server/Listener
Server/Service
Server/Service/Listener
Server/Service/Executor
Server/Service/Connector/SSLHostConfig
Server/Service/Connector/Listener
Server/Service/Connector/UpgradeProtocol


# SetNextRule

Server	setServer	org.apache.catalina.Server
Server/GlobalNamingResources	setGlobalNamingResources	org.apache.catalina.deploy.NamingResourcesImpl
Server/Listener	addLifecycleListener	org.apache.catalina.LifecycleListener
Server/Service	addService	org.apache.catalina.Service
Server/Service/Listener	addLifecycleListener	org.apache.catalina.LifecycleListener
Server/Service/Executor	addExecutor	org.apache.catalina.Executor
Server/Service/Connector	addConnector	org.apache.catalina.connector.Connector
Server/Service/Connector/SSLHostConfig	addSslHostConfig	org.apache.tomcat.util.net.SSLHostConfig
Server/Service/Connector/Listener	addLifecycleListener	org.apache.catalina.LifecycleListener
Server/Service/Connector/UpgradeProtocol	addUpgradeProtocol	org.apache.coyote.UpgradeProtocol

# ConnectorCreateRule

Server/Service/Connector

# SetAllPropertiesRule
Server/Service/Connector
```



## 问题

1. Naming 是什么？
2. Rules,CallMethodRule,FactoryCreateRule,ObjectCreateRule,SetPropertiesRule,SetNextRule
3. StandardServer 的 service 为什么用数组而不用 List？
4. StandardHost 的 alias 为什么用数组而不用 List？
5. ServerCookies 为什么用 数组而不是 List 存储 ServerCookie
6. ExtensionValidator 有什么用？
7. onameStringCache，onameMBeanFactory 的用处？
8. findXXX 方法如 findExecutors 为什么返回数组，而不是List？
9. Realm 是什么？
10. jvmRoutId 什么意思，有什么用？
11. SecurityConstraint 有什么用？
12. StandardPipeline 的 Value 为什么不用 List 或 Queue 来实现，而是用数组



### 配置



```xml
<?xml version="1.0" encoding="UTF-8"?>
<Server port="8005" shutdown="SHUTDOWN">
  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <!-- Security listener. Documentation at /docs/config/listeners.html
  <Listener className="org.apache.catalina.security.SecurityListener" />
  -->
  <!--APR library loader. Documentation at /docs/apr.html -->
  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
  <!-- Prevent memory leaks due to use of particular java/javax APIs-->
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />

  <!-- Global JNDI resources
       Documentation at /docs/jndi-resources-howto.html
  -->
  <GlobalNamingResources>
    <!-- Editable user database that can also be used by
         UserDatabaseRealm to authenticate users
    -->
    <Resource name="UserDatabase" auth="Container"
              type="org.apache.catalina.UserDatabase"
              description="User database that can be updated and saved"
              factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
              pathname="conf/tomcat-users.xml" />
  </GlobalNamingResources>

  <!-- A "Service" is a collection of one or more "Connectors" that share
       a single "Container" Note:  A "Service" is not itself a "Container",
       so you may not define subcomponents such as "Valves" at this level.
       Documentation at /docs/config/service.html
   -->
  <Service name="Catalina">

    <!--The connectors can use a shared executor, you can define one or more named thread pools-->
    <!--
    <Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
    -->


    <!-- A "Connector" represents an endpoint by which requests are received
         and responses are returned. Documentation at :
         Java HTTP Connector: /docs/config/http.html
         Java AJP  Connector: /docs/config/ajp.html
         APR (HTTP/AJP) Connector: /docs/apr.html
         Define a non-SSL/TLS HTTP/1.1 Connector on port 8080
    -->
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    <!-- A "Connector" using the shared thread pool-->
    <!--
    <Connector executor="tomcatThreadPool"
               port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    -->
    <!-- Define a SSL/TLS HTTP/1.1 Connector on port 8443
         This connector uses the NIO implementation. The default
         SSLImplementation will depend on the presence of the APR/native
         library and the useOpenSSL attribute of the
         AprLifecycleListener.
         Either JSSE or OpenSSL style configuration may be used regardless of
         the SSLImplementation selected. JSSE style configuration is used below.
    -->
    <!--
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true">
        <SSLHostConfig>
            <Certificate certificateKeystoreFile="conf/localhost-rsa.jks"
                         type="RSA" />
        </SSLHostConfig>
    </Connector>
    -->
    <!-- Define a SSL/TLS HTTP/1.1 Connector on port 8443 with HTTP/2
         This connector uses the APR/native implementation which always uses
         OpenSSL for TLS.
         Either JSSE or OpenSSL style configuration may be used. OpenSSL style
         configuration is used below.
    -->
    <!--
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11AprProtocol"
               maxThreads="150" SSLEnabled="true" >
        <UpgradeProtocol className="org.apache.coyote.http2.Http2Protocol" />
        <SSLHostConfig>
            <Certificate certificateKeyFile="conf/localhost-rsa-key.pem"
                         certificateFile="conf/localhost-rsa-cert.pem"
                         certificateChainFile="conf/localhost-rsa-chain.pem"
                         type="RSA" />
        </SSLHostConfig>
    </Connector>
    -->

    <!-- Define an AJP 1.3 Connector on port 8009 -->
    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />


    <!-- An Engine represents the entry point (within Catalina) that processes
         every request.  The Engine implementation for Tomcat stand alone
         analyzes the HTTP headers included with the request, and passes them
         on to the appropriate Host (virtual host).
         Documentation at /docs/config/engine.html -->

    <!-- You should set jvmRoute to support load-balancing via AJP ie :
    <Engine name="Catalina" defaultHost="localhost" jvmRoute="jvm1">
    -->
    <Engine name="Catalina" defaultHost="localhost">

      <!--For clustering, please take a look at documentation at:
          /docs/cluster-howto.html  (simple how to)
          /docs/config/cluster.html (reference documentation) -->
      <!--
      <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"/>
      -->

      <!-- Use the LockOutRealm to prevent attempts to guess user passwords
           via a brute-force attack -->
      <Realm className="org.apache.catalina.realm.LockOutRealm">
        <!-- This Realm uses the UserDatabase configured in the global JNDI
             resources under the key "UserDatabase".  Any edits
             that are performed against this UserDatabase are immediately
             available for use by the Realm.  -->
        <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
               resourceName="UserDatabase"/>
      </Realm>

      <Host name="www.example.com"  appBase="webapps"
            unpackWARs="true" autoDeploy="true">

        <Alias> example.com </Alias>
        <!-- SingleSignOn valve, share authentication between web applications
             Documentation at: /docs/config/valve.html -->
        <!--
        <Valve className="org.apache.catalina.authenticator.SingleSignOn" />
        -->

        <!-- Access log processes all example.
             Documentation at: /docs/config/valve.html
             Note: The pattern used is equivalent to using pattern="common" -->
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="localhost_access_log" suffix=".txt"
               pattern="%h %l %u %t &quot;%r&quot; %s %b" />

      </Host>
    </Engine>
  </Service>
</Server>
```

1. defaultHost : 当 Host 中没有匹配的主机的时候或者使用 ip 访问的时候
2. Alias 作为主机别名
3. 同一 Service 下的所有站点共享 Connector，如果需要不同的端口，需要配置多个 Service
4. Wrapper 在 web.xml 文件中配置 Servlet
5. Context 和 Host 有对应的 ContextConfig 和  HostConfig



Context 三种配置方法：

1. 配置文件
2. war 包放在 Host 文件夹下
3. 应用文件夹放在 Host 文件夹下

其中配置文件可以在如下 5 个任一位置

* conf/server.xml : tomcat 重启之后才加载
* conf/[enginename]/[hostname]/*.xml
* 应用/META-INF/context.xml
* conf/context.xml
* conf/[enginename]/[hostname]/context.xml.default