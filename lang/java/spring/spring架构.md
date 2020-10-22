spring 架构理念









启动流程

https://blog.csdn.net/qq_20597727/article/details/82292481

<http://cxis.me/2017/04/06/SpringMVC%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%8F%8A%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90/>

<http://cxis.me/2019/02/22/Spring%E4%B8%AD%E6%89%A9%E5%B1%95%E7%82%B9%E6%B1%87%E6%80%BB/>

<https://blog.csdn.net/FJJ543/article/details/81432419>

<https://weibo.com/ttarticle/p/show?id=2309404329015451191583>

<https://www.iteye.com/blogs/subjects/springMVC?page=2>

https://www.cnblogs.com/xiaoxi/p/8780147.html

<https://muyinchen.github.io/archives/page/3/>

<https://www.cnblogs.com/yulinfeng/category/902338.html>

<https://www.cnblogs.com/xiaoxi/p/8780147.html>

<https://blog.csdn.net/luoshenfu001/article/details/5816408>

https://mp.weixin.qq.com/s/mRKwERpyWF6InyNbp1tP4w

https://blog.csdn.net/jin5203344/article/details/78590111

https://www.cnblogs.com/zhangjianbing/category/1265739.html

https://github.com/wuyouzhuguli/SpringAll

https://blog.csdn.net/u013054715/article/details/77943094

https://blog.csdn.net/u013054715?t=1

https://blog.csdn.net/andy_zhang2007/article/details/99719097

https://blog.csdn.net/andy_zhang2007/article/details/99718010

https://www.cnblogs.com/onlymate/tag/Spring%20Boot/

https://blog.csdn.net/qq_20597727/article/details/82292481

https://www.cnblogs.com/wangjiming/p/11669091.html

1. 有哪些核心组件，各个组件是如何组合到一起的？
2. 应用了哪些设计模式？
3. 



### 问题

1. 哪些方面被反转了？
2. 控制反转和依赖注入的关系？
3. 



### 定位

为开发者提供一个一站式的轻量级应用开发框架

战略上（设计思想、模式）高估，战术（API 抽象、模块化设计、功能的稳定性、可测试性、可扩展性）上低估

### 编程思想

Java 语言特性：反射、动态代理、枚举、泛型、注解

设计思想和模式：OOP、IOC、DDD、TDD、GoF23

Java API 的封装与简化：JDBC 事务 Transaction Servlet、JPA、JMX Bean、Validation

JSR 规范的适配与实现：

第三方框架的整合：ORM 框架、缓存、消息队列、

### 设计理念

IoC 容器依赖反转：把依赖关系的管理从 Java 对象中解放出来，交给 IoC 容器。将对象-对象的关系转换为对象-IoC 容器-对象。

面向接口开发：应用依赖的是 IoC 和 AOP 的接口，而不是具体的服务。

J2EE 的 EJB模式将规范凌驾于需求之上，不符合企业需求。Spring 使用 POJO 将需求重新放到首位。

### 整体架构

从功能特性到编程模型

从设计模式到技术规范

从理论基础到实战演练



#### 核心组件

1. beans：
2. aop：支持 AspectJ 和  JVM 动态代理/CGLIB，实现了一个 AOP 框架
3. context：Context就是一个Bean关系的集合，这个关系集合又叫IOC容器
4. core：发现、建立和维护每 个Bean之间的关系所需要的一些列的工具
5. jms
6. orm
7. web

1.DisPatcherServlet：前端控制器（不需要程序员开发）

用户请求到达前端控制器，它相当于MVC模式中的C（Controller），DispatcherServlet是整个流程控制的中心，由它调用其它组件处理用户的请求，DispatcherServlet的存在降低了组件之间的耦合性。

作用：作为接受请求，相应结果，相当于转发器，中央处理器，减少其他组件之间的耦合度。

2.HandlerMapping：处理器映射器（不需要程序员开发）

HandlerMapping负责根据用户请求找到Handler（即：处理器），SpringMVC提供了不同的映射器实现实现不同的映射方式，例如：配置文件方式、实现接口方式、注解方式等。

作用：根据请求的Url 查找Handler

3.HandLer：处理器（需要程序员开发）

Handler是继DispatcherServlet前端控制器的后端控制器，在DispatcherServlet的控制下，Handler对具体的用户请求进行处理。

由于Handler设计到具体的用户业务请求，所以一般情况需要程序员根据业务需求开发Handler。

注意：编写Handler时按照HandlerAdpter的要求去做，这样才可以去正确执行Handler。

4.HandlerAdapter：处理器适配器

通过HandlerAdapter对处理器进行执行，这是适配器模式的应用，通过扩展适配器可以对更多类型的处理器进行执行。

作用：按照特定的规则（HandlerAdapter要求的规则）去执行Handler

5.ViewResolver：视图解析器（不需要程序员开发）

ViewResolver负责将处理结果生成View视图，ViewResolver首先根据逻辑视图名解析成物理视图名，即具体的页面地址，再生成View视图对象，最后对View进行渲染将处理结果通过页面的展示给用户。SpringMVC框架提供了很多View视图类型，包括：JSTLView、freemarkerView、pdfView等等.

作用：进行视图解析，根据逻辑视图名解析成真正的视图（view）。

6.View视图 （需要程序员开发 jsp）

View是一个接口，实现类支持不同的View类型（jsp、freemarker、pdf）

一般情况下需要通过页面标签或者页面模板技术将模型数据通过页面展示给用户，需要由程序员根据业务需求开发具体的页面。



### 请求类图





HttpRequestHandlerAdapter 适配 handle 的处理到 HttpRequestHandler 的 handleRequest 方法

SimpleServletHandlerAdapter 适配 handle 的处理到 Servlet 的 service 方法

SimpleControllerHandlerAdapter 适配 handle 的处理到 Controller 的 handleRequest 方法

RequestMappingHandlerAdapter 适配 handle 的处理到 HandlerMethod 的 invokeAndHandle

而对于 HttpRequestHandler、Servlet、Controller 由存在多种实现。



### 执行流程

1.用户发送请求至 前端控制器DispatcherServlet。

2.前端控制器DispatcherServlet收到请求后调用处理器映射器HandlerMapping。

3.处理器映射器HandlerMapping根据请求的Url找到具体的处理器，生成处理器对象Handler及处理器拦截器HandlerIntercepter（如果有则生成）一并返回给前端控制器DispatcherServlet。

4.前端控制器DispatcherServlet通过处理器适配器HandlerAdapter调用处理器Controller。

5.执行处理器（Controller，也叫后端控制器）

6.处理器Controller执行完后返回ModelAnView。

7.处理器映射器HandlerAdapter将处理器Controller执行返回的结果ModelAndView返回给前端控制器DispatcherServlet。

8.前端控制器DispatcherServlet将ModelAnView传给视图解析器ViewResolver。

9.视图解析器ViewResolver解析后返回具体的视图View。

10.前端控制器DispatcherServlet对视图View进行渲染视图（即：将模型数据填充至视图中）

11.前端控制器DispatcherServlet响应用户。





#### Bean 组件

Bean组件在Spring的org.springframework.beans包下。这个包下的所有类主要解决了三件事：Bean的定义、Bean 的创建以及对Bean的解析。对Spring的使用者来说唯一需要关心的就是Bean的创建，其他两个由Spring在内部帮你完成了，对你来说是透明的。

SpringBean的创建时典型的工厂模式，他的顶级接口是BeanFactory，下图是这个工厂的继承层次关系：

![Bean 类图]()

BeanFactory有三个子类：ListableBeanFactory、HierarchicalBeanFactory和Autowire Capable Bean Factory。但是从上图中我们可以发现最终的默认实现类是DefaultListableBeanFactory，他实 现了所有的接口。那为何要定义这么多层次的接口呢？查阅这些接口的源码和说明发现，每个接口都有他使用的场合，它主要是为了区分在Spring内部在操作过程中对象的传递和转化过程中，对对象的 数据访问所做的限制。例如ListableBeanFactory接口表示这些Bean是可列表的，而HierarchicalBeanFactory表示的是这些Bean是有继承关系的，也就是每个Bean有可能有父Bean。 AutowireCapableBeanFactory接口定义Bean的自动装配规则。这四个接口共同定义了Bean的集合、Bean之间的关系、以及Bean行为。

Bean的定义主要有BeanDefinition描述，如下图说明了这些类的层次关系：

![Bean 定义的类图]()

Bean的定义就是完整的描述了在Spring的配置文件中你定义的节点中所有的信息，包括各种子节点。当Spring成功解析你定义的一个节点后，在Spring的内部他就被转化 成BeanDefinition对象。以后所有的操作都是对这个对象完成的。

Bean的解析过程非常复杂，功能被分的很细，因为这里需要被扩展的地方很多，必须保证有足够的灵活性，以应对可能的变化。Bean的解析主要就是对Spring配置文件的解析。这个解析过程主要通过 下图中的类完成：

![Bean 的解析]()



### Context组件

Context在Spring的org.springframework.context包下，给Spring提供一个运行时的环境，用以保存各个对象的状态。

ApplicationContext是Context的顶级父类，他除了能标识一个应用环境的基本信息外，他还继承了五个接口，这五个接口主要是扩展了Context的功能。下面是Context的类结构图：

![Context类图]()

从上图中可以看出ApplicationContext继承了BeanFactory，这也说明了Spring容器中运行的主体对象是Bean，另外ApplicationContext继承了ResourceLoader接口，使得ApplicationContext可以访 问到任何外部资源，这将在Core中详细说明。



ApplicationContext的子类主要包含两个方面：

ConfigurableApplicationContext表示该Context是可修改的，也就是在构建Context中用户可以动态添加或修改已有的配置信息，它下面又有多个子类，其中最经常使用的是可更新的Context，即 AbstractRefreshableApplicationContext类。

WebApplicationContext顾名思义，就是为web准备的Context他可以直接访问到ServletContext，通常情况下，这个接口使用的少。

再往下分就是按照构建Context的文件类型，接着就是访问Context的方式。这样一级一级构成了完整的Context等级层次。

总体来说ApplicationContext必须要完成以下几件事：

1. 标识一个应用环境
2. 利用BeanFactory创建Bean对象
3. 保存对象关系表
4. 能够捕获各种事件

Context作为Spring的Ioc容器，基本上整合了Spring的大部分功能，或者说是大部分功能的基础。



#### Core组件

Core组件作为Spring的核心组件，他其中包含了很多的关键类，其中一个重要组成部分就是定义了资源的访问方式。这种把所有资源都抽象成一个接口的方式很值得在以后的设计中拿来学习。下面就 重要看一下这个部分在Spring的作用。

![Resource 类图]()

从上图可以看出Resource接口封装了各种可能的资源类型，也就是对使用者来说屏蔽了文件类型的不同。对资源的提供者来说，如何把资源包装起来交给其他人用这也是一个问题，我们看到Resource 接口继承了InputStreamSource接口，这个接口中有个getInputStream方法，返回的是InputStream类。这样所有的资源都被可以通过InputStream这个类来获取，所以也屏蔽了资源的提供者。另外还有一 个问题就是加载资源的问题，也就是资源的加载者要统一，从上图中可以看出这个任务是由ResourceLoader接口完成，他屏蔽了所有的资源加载者的差异，只需要实现这个接口就可以加载所有的资源， 他的默认实现是DefaultResourceLoader。



下面看一下Context和Resource是如何建立关系的？首先看一下他们的类关系图：

![Resource与 Context类图]()

从上图可以看出，Context是把资源的加载、解析和描述工作委托给了ResourcePatternResolver类来完成，他相当于一个接头人，他把资源的加载、解析和资源的定义整合在一起便于其他组件使用。 Core组件中还有很多类似的方式。





Ioc容器如何工作



从使用者角度看一下他们是如何运行的，以及我们如何让Spring完成各种功能，Spring到底能有那些功能，这些功能是如何得来的。



如何创建BeanFactory工厂



Ioc容器实际上就是Context组件结合其他两个组件共同构建了一个Bean关系网，如何构建这个关系网？构建的入口就在AbstractApplicationContext类的refresh方法中

```

```

这个方法就是构建整个Ioc容器过程的完整的代码，了解了里面的每一行代码基本上就了解大部分Spring的原理和功能了。

这段代码主要包含这样几个步骤：

1. 构建BeanFactory
2. 注册可能感兴趣的事件
3. 创建Bean实例对象
4. 触发被监听的事件

Bean的register相关。这在refreshBeanFactory方法中有一行loadBeanDefinitions(beanFactory)将找到答案，这个方法将开始加载、解析 Bean的定义，也就是把用户定义的数据结构转化为Ioc容器中的特定数据结构。



Bean的解析和登记流程时序图如下：

![流程时序图]()



### 附录

