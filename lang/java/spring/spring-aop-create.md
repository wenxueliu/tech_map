spring-aop-create

Spring 中 AOP的生成核心是 AopProxy

```java
public interface AopProxy {
    Object getProxy();

    Object getProxy(@Nullable ClassLoader var1);
}

public interface AopProxyFactory {
    AopProxy createAopProxy(AdvisedSupport var1) throws AopConfigException;
}

class AdvisedSupport extends ProxyConfig implements Advised {
    private static final long serialVersionUID = 2651364800145442165L;
    public static final TargetSource EMPTY_TARGET_SOURCE;
    TargetSource targetSource;
    private boolean preFiltered;
    AdvisorChainFactory advisorChainFactory;
    private transient Map<AdvisedSupport.MethodCacheKey, List<Object>> methodCache;
    private List<Class<?>> interfaces;
    private List<Advisor> advisors;
    private Advisor[] advisorArray;
}

interface Advised
interface Advisor
```

实现类包括CglibAopProxy（ObjenesisCglibAopProxy），JdkDynamicAopProxy。



#### DefaultAopProxyFactory

```java
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {
    public DefaultAopProxyFactory() {
    }

    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        if (!config.isOptimize() && !config.isProxyTargetClass() && !this.hasNoUserSuppliedProxyInterfaces(config)) {
            return new JdkDynamicAopProxy(config);
        } else {
            Class<?> targetClass = config.getTargetClass();
            if (targetClass == null) {
                throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
            } else {
                return (AopProxy)(!targetClass.isInterface() && !Proxy.isProxyClass(targetClass) ? new ObjenesisCglibAopProxy(config) : new JdkDynamicAopProxy(config));
            }
        }
    }

    private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
        Class<?>[] ifcs = config.getProxiedInterfaces();
        return ifcs.length == 0 || ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0]);
    }
}
```



#### JdkDynamicAopProxy

```java
    public Object getProxy() {
        return this.getProxy(ClassUtils.getDefaultClassLoader());
    }

    public Object getProxy(@Nullable ClassLoader classLoader) {
        Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
        this.findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }
```



#### CglibAopProxy

```java
    public Object getProxy() {
        return this.getProxy((ClassLoader)null);
    }

    public Object getProxy(@Nullable ClassLoader classLoader) {
        Class<?> rootClass = this.advised.getTargetClass();
        Class<?> proxySuperClass = rootClass;
        int x;
        // 增加接口
        if (rootClass.getName().contains("$$")) {
            proxySuperClass = rootClass.getSuperclass();
            Class<?>[] additionalInterfaces = rootClass.getInterfaces();
            Class[] var5 = additionalInterfaces;
            int var6 = additionalInterfaces.length;

            for(x = 0; x < var6; ++x) {
                Class<?> additionalInterface = var5[x];
                this.advised.addInterface(additionalInterface);
            }
        }

        this.validateClassIfNecessary(proxySuperClass, classLoader);
        // 
        Enhancer enhancer = this.createEnhancer();
        if (classLoader != null) {
            enhancer.setClassLoader(classLoader);
            if (classLoader instanceof SmartClassLoader && ((SmartClassLoader)classLoader).isClassReloadable(proxySuperClass)) {
                enhancer.setUseCache(false);
            }
        }

        enhancer.setSuperclass(proxySuperClass);
        enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(classLoader));
        Callback[] callbacks = this.getCallbacks(rootClass);
        Class<?>[] types = new Class[callbacks.length];

        for(x = 0; x < types.length; ++x) {
            types[x] = callbacks[x].getClass();
        }

        enhancer.setCallbackFilter(new CglibAopProxy.ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
        enhancer.setCallbackTypes(types);
        return this.createProxyClassAndInstance(enhancer, callbacks);
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        return this.constructorArgs != null && this.constructorArgTypes != null ? enhancer.create(this.constructorArgTypes, this.constructorArgs) : enhancer.create();
    }
```

