spring-event





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



