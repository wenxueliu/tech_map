spring-circular-references



## 预备知识

Bean 的初始化

Bean 初始化包括如下三个阶段，循环依赖发生在 Bean 的属性设置阶段。

1）Bean 的实例化

2）Bean 的属性设置

3）Bean 的初始化



## 依赖注入方式



### 构造器注入

```java
@Service
public class A {
    public A(B b) {
    }
}
@Service
public class B {
    public B(A a) {
    }
}
```

结果：抛出异常 BeanCurrentlyInCreationException。

根本原因：Spring 解决循环依赖，依赖 Bean 初始化的中间态，而构造器注入的 Bean 是必须已经实例化的。

解决办法：重新整理依赖关系，避免循环依赖。

### Filed 注入

```java
@Service
public class A {
    @Autowired
    private B b;
}

@Service
public class B {
    @Autowired
    private A a;
}
```

### Setter 注入

```java
@Service
public class A {
    private B b;
    
    @Autowired
    void setB(B b) {
    	this.b = b;
    }
}

@Service
public class B {
    private A a;
    
    @Autowired
    void setA(A a) {
    	this.a = a;
    }
}
```



### prototype 注入

```java
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class A {
    @Autowired
    private B b;
}

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class B {
    @Autowired
    private A a;
}
```



如果一个单例的 Bean 依赖了一个 prototype 的 Bean 就会报循环依赖的错误。

```java
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class A {
    @Autowired
    private B b;
}

@Service
public class B {
    @Autowired
    private A a;
}
```

### 总结

| 注入类型        | 结果                   | 根因       | 解决办法     |
| --------------- | ---------------------- | ---------- | ------------ |
| constructor注入 | 启动报错               | 已经初始化 | 避免循环依赖 |
| Filed 注入      | 正常工作               |            |              |
| Setter 注入     | 正常工作               |            |              |
| Prototype       | 启动不报错，运行时报错 | 运行时     | 避免循环依赖 |



## 原理分析

### 问题

1、对象的初始化主流程是啥？

2、对象时如何依次放入各级缓存的？

3、对象又是如何从缓存中读取的？



### 单例 Bean 的管理

```java
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    // 一级缓存：完全初始化好的 Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    // 三级缓存：存放 Bean 的工厂对象，用于解决循环依赖
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    // 二级缓存：Bean 仅仅调用完构造函数，还没有注入属性依赖，此时可以被其他 Bean 引用
    private final Map<String, Object> earlySingletonObjects = new HashMap(16);
    private final Set<String> registeredSingletons = new LinkedHashSet(256);
    // 开始创建存入，创建完成删除
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));
    // 
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap(16));
    @Nullable
    private Set<Exception> suppressedExceptions;
    private boolean singletonsCurrentlyInDestruction = false;
    private final Map<String, Object> disposableBeans = new LinkedHashMap();
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap(16);
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap(64);
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap(64);

    public DefaultSingletonBeanRegistry() {
    }

    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(singletonObject, "Singleton object must not be null");
        synchronized(this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            } else {
                this.addSingleton(beanName, singletonObject);
            }
        }
    }

    // 加入一级缓存，从二级和三级缓存删除
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized(this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized(this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }

        }
    }

    @Nullable
    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, true);
    }

    // 加入二级缓存
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        // 不在一级缓存
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
            synchronized(this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                // 不在二级缓存
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = (ObjectFactory)this.singletonFactories.get(beanName);
                    // 在三级缓存（此时已经调用构造函数）
                    if (singletonFactory != null) {
                        // Bean 工厂创建对象
                        singletonObject = singletonFactory.getObject();
                        // 加入二级缓存
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        // 从三级缓存删除
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        // 完全实例化或者调用完构造函数，但是没有注入属性，或者还没有 Bean 工厂
        return singletonObject;
    }
  
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized(this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                // 加入二级缓存
                this.beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } finally {
                    // 从二级缓存删除		
                    this.afterSingletonCreation(beanName);
                }
                if (newSingleton) {
                    // 加入一级缓存，从二级和三级缓存删除
                    this.addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return !this.inCreationCheckExclusions.contains(beanName) && this.isActuallyInCreation(beanName);
    }
}
```

关键属性介绍

| 属性                  | 状态 | 阶段                                           |
| --------------------- | ---- | ---------------------------------------------- |
| singletonObjects      |      | 完成初始化，取出的 bean 可以直接使用           |
| earlySingletonObjects |      | 完成构造，可以被其他对象引用，未填充属性       |
| singletonFactories    |      | 对象不存在，需要工厂创建，不可以被其他对象引用 |



### 调试

根据如下函数进行调试

| ExampleA                                       | ExampleB                                        | ExampleA                                           |
| ---------------------------------------------- | ----------------------------------------------- | -------------------------------------------------- |
| refresh                                        |                                                 |                                                    |
| finishBeanFactoryInitialization                |                                                 |                                                    |
| preInstantiateSingletons                       |                                                 |                                                    |
| getBean                                        |                                                 |                                                    |
| doGetBean                                      |                                                 |                                                    |
| getSingleton                                   |                                                 |                                                    |
| createBean                                     |                                                 |                                                    |
| doCreateBean                                   |                                                 |                                                    |
| createBeanInstance                             |                                                 |                                                    |
| instantiateBean   反射无参构造                 |                                                 |                                                    |
| addSingletonFactory  加入singletonFactories    |                                                 |                                                    |
| populateBean                                   |                                                 |                                                    |
| postProcessProperties                          |                                                 |                                                    |
| mate.inject                                    |                                                 |                                                    |
| resolveDependency                              |                                                 |                                                    |
| doResolveDependency                            |                                                 |                                                    |
| resolveCandidate                               |                                                 |                                                    |
|                                                | getBean                                         |                                                    |
|                                                | doGetBean                                       |                                                    |
|                                                | getSingleton                                    |                                                    |
|                                                | createBean                                      |                                                    |
|                                                | doCreateBean                                    |                                                    |
|                                                | createBeanInstance                              |                                                    |
|                                                | instantiateBean   反射无参构造                  |                                                    |
|                                                | addSingletonFactory  加入singletonFactories     |                                                    |
|                                                | populateBean                                    |                                                    |
|                                                | postProcessProperties                           |                                                    |
|                                                | mate.inject                                     |                                                    |
|                                                | resolveDependency                               |                                                    |
|                                                | doResolveDependency                             |                                                    |
|                                                | resolveCandidate                                |                                                    |
|                                                |                                                 | getBean                                            |
|                                                |                                                 | doGetBean                                          |
|                                                |                                                 | getSingleton 加入 exampleA 到earlySingletonObjects |
|                                                |                                                 | 结束                                               |
|                                                | addSingleton  加入  exampleB 到singletonObjects |                                                    |
| addSingleton  加入 exampleA 到singletonObjects |                                                 |                                                    |



### 附录



![sprinng-getsingleton](spring-bean-getSingleton.png)





