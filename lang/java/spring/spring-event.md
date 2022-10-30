spring-event





Spring Event 是一个典型的观察者模式。



###  概念

事件：ApplicationEvent

```java
public abstract class ApplicationEvent extends EventObject {
	private static final long serialVersionUID = 7099057708183571937L;
	private final long timestamp;

	public ApplicationEvent(Object source) {
		super(source);
		this.timestamp = System.currentTimeMillis();
	}
	public final long getTimestamp() {
		return this.timestamp;
	}
}
```



监听者：ApplicationListener

```java
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
	void onApplicationEvent(E event);
}
```



发布者：ApplicationEventPublisher

```java
public interface ApplicationEventPublisher {
	default void publishEvent(ApplicationEvent event) {
		publishEvent((Object) event);
	}
	void publishEvent(Object event);
}
```



监听者管理及事件广播：ApplicationEventMulticaster

```java
public interface ApplicationEventMulticaster {
	void addApplicationListener(ApplicationListener<?> listener);

	void addApplicationListenerBean(String listenerBeanName);

	void removeApplicationListener(ApplicationListener<?> listener);

	void removeApplicationListenerBean(String listenerBeanName);

	void removeAllListeners();

	void multicastEvent(ApplicationEvent event);

	void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);
}
```





### Spring 对观察者模式的增强



#### 事件类型支持泛型

如果事件类型要支持泛型，那么，继承 PayloadApplicationEvent 即可。

```java
public class PayloadApplicationEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
	private final T payload;

	public PayloadApplicationEvent(Object source, T payload) {
		super(source);
		this.payload = payload;
	}


	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getPayload()));
	}

	public T getPayload() {
		return this.payload;
	}
}
```



#### 对事件行为的扩展

1、支持事件类型过滤

2、支持事件顺序

3、支持事件泛型

```java
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
	boolean supportsEventType(Class<? extends ApplicationEvent> eventType);

	default boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}

	@Override
	default int getOrder() {
		return LOWEST_PRECEDENCE;
	}
}

public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
	boolean supportsEventType(ResolvableType eventType);

	default boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}

	@Override
	default int getOrder() {
		return LOWEST_PRECEDENCE;
	}
}
```

默认实现为 GenericApplicationListenerAdapter 利用 ApplicationListener 适配 GenericApplicationListener和SmartApplicationListener

> 适配器模式：用已有 A 行为实现新的 B 行为。
>
> 比如，通过 ApplicationListener 实现 GenericApplicationListener。



#### 统一事件处理

所有类型的事件都可以在一个事件处理器中完成，避免针对不同的事件创建不同的事件类型。

保存类型与其对应的监听器到 Map 结构retrieverCache中。

```java
public abstract class AbstractApplicationEventMulticaster
		implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

	private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);

	final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);

	private Object retrievalMutex = this.defaultRetriever;

  private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

		private final ResolvableType eventType;

		@Nullable
		private final Class<?> sourceType;
}
```





#### 支持异步事件

Spring 对异步事件的支持符合直觉，就是初始化线程池，在线程池执行监听器的行为。支持通过注册 Bean 的方式自定义线程池

```java
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

  // 设置异步执行器
 	private Executor taskExecutor;
	
	public void setTaskExecutor(@Nullable Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	
	protected Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	@Override
	public void multicastEvent(ApplicationEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}

	@Override
	public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		Executor executor = getTaskExecutor();
		for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
      // 如何设置了执行器，就是异步的。
			if (executor != null) {
				executor.execute(() -> invokeListener(listener, event));
			}
			else {
				invokeListener(listener, event);
			}
		}
	}
}
```

> 1、异步支持全局异步和局部异步
>
> 2、同步事件，如果有一个因为异常失败，会导致其他监听器也无法正常处理事件

#### 支持自定义错误处理

通过定义错误处理器，在发生异常的时候调用错误处理器。

```java
public interface ErrorHandler {
	void handleError(Throwable t);
}

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
	// 设置错误处理器
  private ErrorHandler errorHandler;

  protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				doInvokeListener(listener, event);
			}
			catch (Throwable err) {
				errorHandler.handleError(err);
			}
		}	
		else {
			doInvokeListener(listener, event);
		}
	}
```



#### 支持注解创建监听器

支持 @EventListener  注解创建监听器

```java

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {
	@AliasFor("classes")
	Class<?>[] value() default {};

	@AliasFor("value")
	Class<?>[] classes() default {};

	String condition() default "";
}

public interface EventListenerFactory {
	boolean supportsMethod(Method method);

	ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method);
}
```

详细实现在 EventListenerMethodProcessor 和 ApplicationListenerMethodAdapter 中。



#### 层级事件的传播



#### 事件的传递



### 可扩展性

1、ApplicationEventMulticaster 的默认实现是 SimpleApplicationEventMulticaster，如果不能满足需求，可以通过注册 Bean 名称为 APPLICATION_EVENT_MULTICASTER_BEAN_NAME 的 Bean 替代默认实现。

2、可以自定义事件类型，只要继承 ApplicationEvent 即可

3、错误处理器和线程池可以自定义

4、



### 示例



注册 Listener 的方式

1、通过实现 GenericApplicationListener（或  SmartApplicationListener）

2、通过 @EventListener

3、通过 @Bean 的方式

事件

1、普通类型继承 ApplicationEvent

2、泛型事件继承 PayloadApplicationEvent

自定义  ApplicationEventMulticaster

1、注册 Bean APPLICATION_EVENT_MULTICASTER_BEAN_NAME

2、主动添加

异步事件

1、在 onApplicationEvent 中使用线程池

2、注册类型为 Executor， Bean名为 taskExecutor 的 Bean

> 1、谨慎使用全局的异步处理，建议按需增加 @Async
>
> 2、根据事件的规模设置线程池，避免使用默认线程池

自定义错误处理

1、自定义 ErrorHandler 的 Bean



### 设计思考

Spring 对事件的扩展每个点都值得参考学习，抛开 spring 相关内容，对于实现一个通用的发布订阅有非常大的参考价值。



### 思考题

1、你觉得 spring 的事件实现还有哪些优化点？

2、基于 Spring Event 你是否可以实现一个通用的事件发布模型？



### 应用扩展

#### Spring ApplicationEvent 事件

即事件对象为 ApplicationEvent，包括

| 事件                | 流程                                      |
| ------------------- | ----------------------------------------- |
| ContextStartedEvent | Spring 应用上下文启动事件，对应 start()   |
| ContextRefreshEvent | Spring 应用上下文就绪事件，对应 refresh() |
| ContextStopedEvent  | Spring 应用上下文停止事件，对应 stoped()  |
| ContextClosedEvent  | Spring 应用上下文关闭事件，对应 closed()  |

#### Spring Boot 事件

#### Spring Cloud 事件

#### Spring 事务事件



AbstractApplicationContext.java

```java
 		//初始化注册表之后，就会把事件注册到注册表中
    protected void registerListeners() {
        Iterator var1 = this.getApplicationListeners().iterator();

        while(var1.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var1.next();
            this.getApplicationEventMulticaster().addApplicationListener(listener);
        }

        String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
        String[] var7 = listenerBeanNames;
        int var3 = listenerBeanNames.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String listenerBeanName = var7[var4];
            this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (earlyEventsToProcess != null) {
            Iterator var9 = earlyEventsToProcess.iterator();

            while(var9.hasNext()) {
                ApplicationEvent earlyEvent = (ApplicationEvent)var9.next();
                this.getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }

  	}

		public void publishEvent(ApplicationEvent event) {
        this.publishEvent(event, (ResolvableType)null);
    }

    public void publishEvent(Object event) {
        this.publishEvent(event, (ResolvableType)null);
    }

    protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
        Assert.notNull(event, "Event must not be null");
        Object applicationEvent;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent)event;
        } else {
            applicationEvent = new PayloadApplicationEvent(this, event);
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
            }
        }

        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else {
            this.getApplicationEventMulticaster().multicastEvent((ApplicationEvent)applicationEvent, eventType);
        }

        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }

    }

   ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
        } else {
            return this.applicationEventMulticaster;
        }
    }



```



Spring使用反射机制，通过方法getBeansOfType获取所有继承了ApplicationListener接口的监听器，然后把监听器放到注册表中，所以我们可以在Spring配置文件中配置自定义监听器，在Spring初始化的时候，会把监听器自动注册到注册表中去。



```java
   public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
        Iterator var4 = this.getApplicationListeners(event, type).iterator();

        while(var4.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var4.next();
            Executor executor = this.getTaskExecutor();
            if (executor != null) {
                executor.execute(() -> {
                    this.invokeListener(listener, event);
                });
            } else {
                this.invokeListener(listener, event);
            }
        }

    }

   protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler != null) {
            try {
                this.doInvokeListener(listener, event);
            } catch (Throwable var5) {
                errorHandler.handleError(var5);
            }
        } else {
            this.doInvokeListener(listener, event);
        }

    }

    private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        } catch (ClassCastException var6) {
            String msg = var6.getMessage();
            if (msg != null && !this.matchesClassCastMessage(msg, event.getClass())) {
                throw var6;
            }

            Log logger = LogFactory.getLog(this.getClass());
            if (logger.isDebugEnabled()) {
                logger.debug("Non-matching event type for listener: " + listener, var6);
            }
        }

    }
```





GenericApplicationListenerAdapter.java

```java
public void onApplicationEvent(ApplicationEvent event) {
    this.delegate.onApplicationEvent(event);
}

public boolean supportsEventType(ResolvableType eventType) {
    if (this.delegate instanceof SmartApplicationListener) {
        Class<? extends ApplicationEvent> eventClass = eventType.resolve();
        return eventClass != null && ((SmartApplicationListener)this.delegate).supportsEventType(eventClass);
    } else {
        return this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType);
    }
}

public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
    return this.supportsEventType(ResolvableType.forClass(eventType));
}

public boolean supportsSourceType(@Nullable Class<?> sourceType) {
    return !(this.delegate instanceof SmartApplicationListener) || ((SmartApplicationListener)this.delegate).supportsSourceType(sourceType);
}
```





AbstractApplicationEventMulticaster.java

```java
   
   public void addApplicationListener(ApplicationListener<?> listener) {
        synchronized(this.retrievalMutex) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }

            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    public void addApplicationListenerBean(String listenerBeanName) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    public void removeApplicationListener(ApplicationListener<?> listener) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    public void removeApplicationListenerBean(String listenerBeanName) {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    public void removeAllListeners() {
        synchronized(this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        synchronized(this.retrievalMutex) {
            return this.defaultRetriever.getApplicationListeners();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        AbstractApplicationEventMulticaster.ListenerCacheKey cacheKey = new AbstractApplicationEventMulticaster.ListenerCacheKey(eventType, sourceType);
        AbstractApplicationEventMulticaster.ListenerRetriever retriever = (AbstractApplicationEventMulticaster.ListenerRetriever)this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        } else if (this.beanClassLoader == null || ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader))) {
            synchronized(this.retrievalMutex) {
                retriever = (AbstractApplicationEventMulticaster.ListenerRetriever)this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getApplicationListeners();
                } else {
                    retriever = new AbstractApplicationEventMulticaster.ListenerRetriever(true);
                    Collection<ApplicationListener<?>> listeners = this.retrieveApplicationListeners(eventType, sourceType, retriever);
                    this.retrieverCache.put(cacheKey, retriever);
                    return listeners;
                }
            }
        } else {
            return this.retrieveApplicationListeners(eventType, sourceType, (AbstractApplicationEventMulticaster.ListenerRetriever)null);
        }
    }

    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable AbstractApplicationEventMulticaster.ListenerRetriever retriever) {
        List<ApplicationListener<?>> allListeners = new ArrayList();
        LinkedHashSet listeners;
        LinkedHashSet listenerBeans;
        synchronized(this.retrievalMutex) {
            listeners = new LinkedHashSet(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet(this.defaultRetriever.applicationListenerBeans);
        }

        Iterator var7 = listeners.iterator();

        while(var7.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var7.next();
            if (this.supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.applicationListeners.add(listener);
                }

                allListeners.add(listener);
            }
        }

        if (!listenerBeans.isEmpty()) {
            BeanFactory beanFactory = this.getBeanFactory();
            Iterator var15 = listenerBeans.iterator();

            while(var15.hasNext()) {
                String listenerBeanName = (String)var15.next();

                try {
                    Class<?> listenerType = beanFactory.getType(listenerBeanName);
                    if (listenerType == null || this.supportsEvent(listenerType, eventType)) {
                        ApplicationListener<?> listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener) && this.supportsEvent(listener, eventType, sourceType)) {
                            if (retriever != null) {
                                if (beanFactory.isSingleton(listenerBeanName)) {
                                    retriever.applicationListeners.add(listener);
                                } else {
                                    retriever.applicationListenerBeans.add(listenerBeanName);
                                }
                            }

                            allListeners.add(listener);
                        }
                    }
                } catch (NoSuchBeanDefinitionException var13) {
                }
            }
        }

        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null && retriever.applicationListenerBeans.isEmpty()) {
            retriever.applicationListeners.clear();
            retriever.applicationListeners.addAll(allListeners);
        }

        return allListeners;
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        if (!GenericApplicationListener.class.isAssignableFrom(listenerType) && !SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
            return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
        } else {
            return true;
        }
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener)listener : new GenericApplicationListenerAdapter(listener);
        return ((GenericApplicationListener)smartListener).supportsEventType(eventType) && ((GenericApplicationListener)smartListener).supportsSourceType(sourceType);
    }

    private class ListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet();
        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                Iterator var3 = this.applicationListenerBeans.iterator();

                while(var3.hasNext()) {
                    String listenerBeanName = (String)var3.next();

                    try {
                        ApplicationListener<?> listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    } catch (NoSuchBeanDefinitionException var6) {
                    }
                }
            }

            if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }

            return allListeners;
        }
    }

    private static final class ListenerCacheKey implements Comparable<AbstractApplicationEventMulticaster.ListenerCacheKey> {
        private final ResolvableType eventType;
        @Nullable
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
            Assert.notNull(eventType, "Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                AbstractApplicationEventMulticaster.ListenerCacheKey otherKey = (AbstractApplicationEventMulticaster.ListenerCacheKey)other;
                return this.eventType.equals(otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
            }
        }

        public int hashCode() {
            return this.eventType.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        public int compareTo(AbstractApplicationEventMulticaster.ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return other.sourceType == null ? 0 : -1;
                }

                if (other.sourceType == null) {
                    return 1;
                }

                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }

            return result;
        }
    }
```



