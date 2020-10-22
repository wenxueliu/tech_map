

spring-interview

### 基础

#### spring 核心模块有哪些？

spring core 事件驱动

spring context：

spring bean：依赖注入、依赖查找

spring aop：

spring expression：



#### Spring 的优点和缺点





### Spring Bean

#### 谈谈 Spring Bean 的生命周期和作用域？

Spring Bean 生命周期比较复杂，可以分为创建和销毁两个过程。首先，创建 Bean 会经过一系列的步骤，主要包括：

* 实例化 Bean 对象。
* 设置 Bean 属性。
* 如果我们通过各种 Aware 接口声明了依赖关系，则会注入 Bean 对容器基础设施层面的依赖。具体包括 BeanNameAware、BeanFactoryAware 和 ApplicationContextAware，分别会注入 Bean ID、Bean Factory 或者 ApplicationContext。
* 调用 BeanPostProcessor 的前置初始化方法 postProcessBeforeInitialization。
* 如果实现了 InitializingBean 接口，则会调用 afterPropertiesSet 方法。
* 调用 Bean 自身定义的 init 方法。
* 调用 BeanPostProcessor 的后置初始化方法 postProcessAfterInitialization。
* 创建过程完毕。

第二，Spring Bean 的销毁过程会依次调用 DisposableBean 的 destroy 方法和 Bean 自身定制的 destroy 方法。



Spring Bean 有五个作用域，其中最基础的有下面两种：

* Singleton，这是 Spring 的默认作用域，也就是为每个 IOC 容器创建唯一的一个 Bean 实例。

* Prototype，针对每个 getBean 请求，容器都会单独创建一个 Bean 实例。 

从 Bean 的特点来看，Prototype 适合有状态的 Bean，而 Singleton 则更适合无状态的情况。另外，使用 Prototype 作用域需要经过仔细思考，毕竟频繁创建和销毁 Bean 是有明显开销的。

如果是 Web 容器，则支持另外三种作用域：

* Request，为每个 HTTP 请求创建单独的 Bean 实例。
* Session，很显然 Bean 实例的作用域是 Session 范围。
* GlobalSession，用于 Portlet 容器，因为每个 Portlet 有单独的 Session，GlobalSession 提供一个全局性的 HTTP Session。



#### Spring Bean 为什么需要别名？





#### 简述Spring Bean 从配置到实例化的过程

1. 将所有 BeanDefinition 数据来源抽象为 Resource。如配置文件（或注解
2. BeanDefinitionResource 实现了 Resource
3. BeanDefinitionReader 解析 Resource 生成 BeanDefinition
4. 





### Spring IoC

#### 什么是 IoC

Inverse of Control，控制反转，与好莱坞原则。两种实现方式：依赖查找，依赖注入。

在 Spring 中将对象的初始化由容器来控制，而不是调用方主动创建。

#### 什么是依赖查找和依赖注入

#### 

| 类型     | 依赖处理 | 实现便利性 | 代码侵入性   | API 依赖性     | 可读性 |
| -------- | -------- | ---------- | ------------ | -------------- | ------ |
| 依赖查找 | 主动获取 | 相对繁琐   | 侵入业务逻辑 | 依赖容器 API   | 良好   |
| 依赖注入 | 被动提供 | 相对便利   | 低侵入性     | 不依赖容器 API | 一般   |

#### 构造器注入 VS Setter 注入

构造器注入：

1. 确保依赖不为空，依赖对象已经完成初始化。
2. 如果有大量的依赖需要注入说明是代码的坏味道，即一个类的职责不单一。
3. 构造器注入不允许循环依赖，如果出现循环依赖说明代码设计不合理。

Setter 注入：用于可选依赖，即依赖可以重新配置重新注入。每次必须检查依赖是否为空

因此，在合适的场景下使用合适的注入方式。

#### 什么是 Spring IoC 容器



#### Spring 作为 IoC 容器有什么优势？

AOP 抽象

事务抽象

事件机制

SPI 扩展

强大的第三方整合

易测试性

更好的抽象

#### 哪些方面被反转了？

责任反转了，依赖对象获得被反转了，基于这个结论，为依赖反转创造一个更好的名字：依赖注入。应用由两个或多个类通过彼此合作来实现业务逻辑，每个对象都需要依赖其合作对象的引用。如果自己实现，那么就是导致高耦合并且难以测试。

#### 控制反转和依赖注入的关系？

依赖反转和依赖注入表述的是一个意思。依赖反转的实现方式有很多种，IoC容器是实现这个模式的载体。

#### 循环依赖处理

三层缓存处理循环依赖

#### ObjectFactory, FactoryBean, BeanFactory 的区别

BeanFactory 用于创建 Bean 的基础类

FactoryBean 是一个接口，用于创建 Bean 对象。实现者实现对应的接口，在创建 Bean 的过程会生成对应 

ObjectFactory：一个接口，用于实现类

#### 延时查找和实时查找的区别？



#### ApplicationContext 和 BeanFactory 的区别？

这里使用了代理模式，ApplicationContext Bean 的功能由 BeanFactory 代理。AppilcationContext 提供了比 BeanFactory 更加丰富的功能

1. 面向切面（AOP）
2. 配置元信息
3. 资源管理
4. 事件
5. 国际化
6. 注解
7. Environment 抽象

### Spring AOP



### 其他

