spring-dependency-source



## 依赖查找来源

| 来源                  | 配置元数据                   |
| --------------------- | ---------------------------- |
| Spring BeanDefinition | xml 配置                     |
|                       | 注解（@Bean）                |
|                       | API（BeanDefinitionBuilder） |
| 单例对象              | API                          |



Spring 内建依赖查找来源

| Bean 名称                                | Bean实例                                   | 场景                                               |
| ---------------------------------------- | ------------------------------------------ | -------------------------------------------------- |
| internalConfigurationAnnotationProcessor | ConfigurationClassPostProcesso r 对象      | 处理 Spring 配置类                                 |
| internalAutowire dAnnotationProcessor    | AutowiredAnnotationBeanPostPro cessor 对象 | 处理 @Autowired 以及 @Value 注解                   |
| internalCommonAnnotationProcessor        | CommonAnnotationBeanPostProces sor 对象    | (条件激活)处理 JSR-250 注解， 如 @PostConstruct 等 |
| internalEventListenerProcessor           | EventListenerMethodProcessor 对象          | 处理标注 @EventListener 的 Spring 事件监听方法     |



Spring 內建单例对象 

| Bean 名称                   | Bean实例                          | 场景                    |
| --------------------------- | --------------------------------- | ----------------------- |
| environment                 | Environment 对象                  | 外部化配置以及 Profiles |
| systemProperties            | java.util.Properties 对象         | Java 系统属性           |
| systemEnvironment           | java.util.Map 对象                | 操作系统环境变量        |
| MessageSource               | MessageSource 对象                | 国际化                  |
| lifecycleProcessor          | LifecycleProcessor 对象           | Lifecycle Bean 处理器   |
| applicationEventMulticaster | ApplicationEventMulticaster 对 象 | Spring 事件广播器       |





## 依赖注入来源



​	

| 来源                   | 配置元数据                   |
| ---------------------- | ---------------------------- |
| Spring BeanDefinition  | xml 配置                     |
|                        | 注解（@Bean）                |
|                        | API（BeanDefinitionBuilder） |
| 单例对象               | API                          |
| 非 Spring 容器管理对象 |                              |



| 维度             | Spring BeanDefinition  | 单例对象          | Resolvable Dependency        |
| ---------------- | ---------------------- | ----------------- | ---------------------------- |
| Spring Bean 对象 | 是                     | 是                | 否                           |
| 生命周期管理     | 是                     | 否                | 否                           |
| 配置元信息       | 有                     | 无                | 无                           |
| 延迟初始化       | 是                     | 无                | 无                           |
| 依赖查找         | 是                     | 是                | 无                           |
| 依赖注入         | 是                     | 是                | 是                           |
| API              | registerBeanDefinition | registerSingleton | registerResolvableDependency |
|                  |                        |                   |                              |







## 面试题

1、依赖查找和依赖注入的来源一样么？

2、单例对象可以在 Ioc 容器启动之后注册么？