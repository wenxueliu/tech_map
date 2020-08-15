
### 目标

初、中级：扎实的 Java 和计算机科学基础，掌握主流的开源框架使用；
高级或技术专家：系统地掌握Java IO/NIO、并发、虚拟机；并对分布式、安全、性能等领域有深入研究

### 基本知识

基本语法：接口、继承、重载、多态、关键字、 《Java 核心编程卷一》

软引用，弱引用，强引用:

值传递和引用传递：

fast-fail：

异常：

注解：

反射：

https://blog.csdn.net/hanchao5272/column/info/20010

MethHandle
https://www.jianshu.com/p/a9cecf8ba5d9
https://blog.csdn.net/zmx729618/article/details/78968810
https://cloud.tencent.com/developer/article/1005920
https://www.imooc.com/article/details/id/25992
https://blog.csdn.net/qq_31142553/article/details/81638027


泛型：参考书目《Java 核心编程》

IO：参考书目《Java NIO》

#### 并发

CAS

线程：多线程，线程安全

锁

synchronized、读写锁、公平锁、不公平锁、偏向锁、悲观锁、乐观锁、可重入锁、不可重入锁、互斥锁、共享锁、轻量级锁、重量级锁、自旋锁、死锁等）
锁优化、锁消除、锁粗化、ABA问题等。

AQS

参考书目 《Java 并发编程的艺术》《Java 并发编程》

Java8 ： 实例代码 [java8-lambda](https://github.com/RichardWarburton/java-8-lambdas-exercises)，[Java8InAction](https://github.com/java8/Java8InAction)  https://github.com/CarpenterLee/JavaLambdaInternals       参考书目《Java 8 in action》

API vs SPI



最佳实践：参考《Effective Java》

TODO 梳理

[JavaInterView](https://github.com/crossoverJie/JCSprout)

<https://github.com/Luckylau/The-Art-of-Architect>

<https://github.com/hollischuang/toBeTopJavaer>



### JSR

Servlet

SPI

### JDK

https://github.com/seaswalker/JDK

http://cxis.me/archives/page/8/

数据结构：Collections，Arrays，Map，List，Set系列

https://www.cnblogs.com/xiaoxi/category/929860.html

类加载：Class

并发：Condition，CountDownLatch，CyclicBarrier，ReadWriteLock，ReentrantLock

Socket：

IO：Buffer，FileChannel，AsynchronousFileChannel


NIO： https://www.cnblogs.com/xiaoxi/category/961993.html

线程：ThreadPool，Executors， https://www.cnblogs.com/xiaoxi/category/961349.html

反射：

语法糖：switch 支持 String 与枚举、泛型、自动装箱与拆箱、方法变长参数、枚举、内部类、条件编译、 断言、数值字面量、for-each、try-with-resource、Lambda表达式

序列化\反序列化：jackson，gson，kyro

[JDK 源码阅读](https://github.com/seaswalker/JDK)

### 设计模式

https://github.com/iluwatar/java-design-patterns

### JVM

参考 JVM.md

#### spring

https://github.com/wuyouzhuguli/SpringAll

https://github.com/seaswalker/spring-analysis

https://blog.csdn.net/uftjtt/article/list/3?t=1&  spring, mybatis, java
源码，设计模式

AOP

IOC

注解

filter

拦截器

[源码分析](https://github.com/seaswalker/spring-analysis)

参数校验 Hibernate Validation
https://blog.wuwii.com/springboot-10.html

#### mybatis

执行引擎

配置管理

缓存

插件机制

#### tomcat

启动流程

https://www.jianshu.com/p/76ff17bc6dea



#### dubbo



#### spring cloud

https://www.cnblogs.com/li3807/tag/

注册中心：eureka，consul

服务发现：

服务间调用：feign，grpc

网关：zuul，spring gateway

配置中心：apolle

链路跟踪：jaeger，skywalking，zipkin，pinpoint，

熔断：hystrix，sentinel

负载均衡：ribbon

监控：

分布式事务：TCC  参考 [seata](https://github.com/seata/seata)

参考书目：《重新定义 SpringCloud 实战》



实战

https://github.com/wxiaoqi/Spring-Cloud-Admin

https://github.com/Luckylau/Redis-Learning

https://github.com/xujianguo/mybatis-redis

https://github.com/ggj2010/redis

https://github.com/shuzheng/zheng

#### 工具链

性能诊断：[Arthas](https://github.com/alibaba/arthas)，[Btrace](https://github.com/btraceio/btrace)

问题分析：jstat，jps，jstack, jmap、jstat, jconsole, jinfo, jhat, javap

编译打包：maven，gradle，javac  参考 《maven 实战》

开发工具：Intellij IDEA

好的博客：https://www.hollischuang.com/


#### 模块

定时器 :
认证鉴权：Shiro


### 参考

https://github.com/akullpp/awesome-java#readme

### 附录

### 第三方包

netty： [源码解读](https://github.com/yongshun/learn_netty_source_code)

reactor : https://github.com/reactor/reactor

guava

#### 建议阅读书目

https://github.com/LingCoder/OnJava8
https://github.com/Snailclimb/JavaGuide



#### Apache Commons

- [BCEL](http://commons.apache.org/proper/commons-bcel) - Byte Code Engineering Library - analyze, create, and manipulate Java class files.
- [BeanUtils](http://commons.apache.org/proper/commons-beanutils) - Easy-to-use wrappers around the Java reflection and introspection APIs.
- [BeanUtils2](http://commons.apache.org/sandbox/commons-beanutils2) - Redesign of Commons BeanUtils.
- [BSF](http://commons.apache.org/proper/commons-bsf) - Bean Scripting Framework - interface to scripting languages, including JSR-223.
- [Chain](http://commons.apache.org/proper/commons-chain) - Chain of Responsibility pattern implementation.
- [ClassScan](http://commons.apache.org/sandbox/commons-classscan) - Find Class interfaces, methods, fields, and annotations without loading.
- [CLI](http://commons.apache.org/proper/commons-cli) - Command-line arguments parser.
- [CLI2](http://commons.apache.org/sandbox/commons-cli2) Redesign of Commons CLI.
- [Codec](http://commons.apache.org/proper/commons-codec) - General encoding/decoding algorithms (for example phonetic, base64, URL).
- [Collections](http://commons.apache.org/proper/commons-collections) - Extends or augments the Java Collections Framework.
- [Compress](http://commons.apache.org/proper/commons-compress) - Defines an API for working with tar, zip and bzip2 files.
- [Configuration](http://commons.apache.org/proper/commons-configuration) - Reading of configuration/preferences files in various formats.
- [Convert](http://commons.apache.org/sandbox/commons-convert) - Commons-Convert aims to provide a single library dedicated to the task of converting an object of one type to another.
- [CSV](http://commons.apache.org/proper/commons-csv) - Component for reading and writing comma separated value files.
- [Daemon](http://commons.apache.org/proper/commons-daemon) - Alternative invocation mechanism for unix-daemon-like java code.
- [DBCP](http://commons.apache.org/proper/commons-dbcp) - Database connection pooling services.
- [DbUtils](http://commons.apache.org/proper/commons-dbutils) - JDBC helper library.
- [Digester](http://commons.apache.org/proper/commons-digester) - XML-to-Java-object mapping utility.
- [Email](http://commons.apache.org/proper/commons-email) - Library for sending e-mail from Java.
- [Exec](http://commons.apache.org/proper/commons-exec) - API for dealing with external process execution and environment management in Java.
- [FileUpload](http://commons.apache.org/proper/commons-fileupload) - File upload capability for your servlets and web applications.
- [Finder](http://commons.apache.org/sandbox/commons-finder) - Java library inspired by the UNIX find command.
- [Flatfile](http://commons.apache.org/sandbox/commons-flatfile) - Java library for working with flat data structures.
- [Functor](http://commons.apache.org/proper/commons-functor) - A functor is a function that can be manipulated as an object, or an object representing a single, generic function.
- [Graph](http://commons.apache.org/sandbox/commons-graph) - A general purpose Graph APIs and algorithms.
- [I18n](http://commons.apache.org/sandbox/commons-i18n) - Adds the feature of localized message bundles that consist of one or many localized texts that belong together.
- [Id](http://commons.apache.org/sandbox/commons-id) - Id is a component used to generate identifiers.
- [Imaging](http://commons.apache.org/proper/commons-imaging) - A pure-Java image library.
- [IO](http://commons.apache.org/proper/commons-io) - Collection of I/O utilities.
- [Javaflow](http://commons.apache.org/sandbox/commons-javaflow) - Continuation implementation to capture the state of the application.
- [JCI](http://commons.apache.org/proper/commons-jci) - Java Compiler Interface.
- [JCS](http://commons.apache.org/proper/commons-jcs) - Java Caching System.
- [Jelly](http://commons.apache.org/proper/commons-jelly) - XML based scripting and processing engine.
- [Jexl](http://commons.apache.org/proper/commons-jexl) - Expression language which extends the Expression Language of the JSTL.
- [JNet](http://commons.apache.org/sandbox/commons-jnet) - JNet allows to use dynamically register url stream handlers through the java.net API.
- [JXPath](http://commons.apache.org/proper/commons-jxpath) - Utilities for manipulating Java Beans using the XPath syntax.
- [Lang](http://commons.apache.org/proper/commons-lang) - Provides extra functionality for classes in java.lang.
- [Logging](https://en.wikipedia.org/wiki/Apache_Commons_Logging) Wrapper around a variety of logging API implementations.
- [Math](http://commons.apache.org/proper/commons-math) - Lightweight, self-contained mathematics and statistics components.
- [Monitoring](http://commons.apache.org/sandbox/commons-monitoring) - Monitoring aims to provide a simple but extensible monitoring solution for Java applications.
- [Nabla](http://commons.apache.org/sandbox/commons-nabla) - Nabla provides automatic differentiation classes that can generate derivative of any function implemented in the Java language.
- [Net](http://commons.apache.org/proper/commons-net) - Collection of network utilities and protocol implementations.
- [OGNL](http://commons.apache.org/proper/commons-ognl) - An Object-Graph Navigation Language.
- [OpenPGP](http://commons.apache.org/sandbox/commons-openpgp) - Interface to signing and verifying data using OpenPGP.
- [Performance](http://commons.apache.org/sandbox/commons-performance) - A small framework for microbenchmark clients, with implementations for Commons DBCP and Pool.
- [Pipeline](http://commons.apache.org/sandbox/commons-pipeline) - Provides a set of pipeline utilities designed around work queues that run in parallel to sequentially process data objects.
- [Pool](http://commons.apache.org/proper/commons-pool) - Generic object pooling component.
- [Proxy](http://commons.apache.org/proper/commons-proxy) - Library for creating dynamic proxies.
- [RDF](https://commons.apache.org/proper/commons-rdf) - Common implementation of RDF 1.1 that could be implemented by systems on the JVM.
- [RNG](https://commons.apache.org/proper/commons-rng) - Commons Rng provides implementations of pseudo-random numbers generators.
- [SCXML](http://commons.apache.org/proper/commons-scxml) - An implementation of the State Chart XML specification aimed at creating and maintaining a Java SCXML engine.
- [Validator](http://commons.apache.org/proper/commons-validator) - Framework to define validators and validation rules in an xml file.
- [VFS](http://commons.apache.org/proper/commons-vfs) - Virtual File System component for treating files, FTP, SMB, ZIP and such like as a single logical file system.
- [Weaver](http://commons.apache.org/proper/commons-weaver) - Provides an easy way to enhance (weave) compiled bytecode.
