spring-async



## 使用

1. `@Async`注解一般用在方法上，如果用在类上，那么这个类**所有的方法**都是异步执行的；
2. `@Async`可以放在任何方法上，哪怕你是`private`的（**若是同类调用**，请务必注意注解失效的情况~~~）
3. 所使用的`@Async`注解方法的类对象应该是Spring容器管理的bean对象
4. `@Async`可以放在接口处（或者接口方法上）。但是只有使用的是JDK的动态代理时才有效，CGLIB会失效。因此建议：`统一写在实现类的方法上`
5. 需要注解`@EnableAsync`来开启异步注解的支持
6. 若你希望得到`异步调用的返回值`，请你的返回值用`Futrue`变量包装起来



## 原理详解

### 概述

1、EnableAsync 引入 ProxyAsyncConfiguration 配置，通过 EnableAsync 注解声明的参数和 AsyncConfigurer 初始化 AsyncAnnotationBeanPostProcessor

2、在 Bean 初始化之后的postProcessAfterInitialization 初始化 Advice 和 Pointcut 拦截器

3、当方法执行的时候，执行拦截器异步执行方法。

注：核心逻辑在 AnnotationAsyncExecutionInterceptor

### 详解

#### EnableAsync 注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {

	 // 默认情况下，要开启异步操作，要在相应的方法或者类上加上@Async注解或者EJB3.1规范下的@Asynchronous注解。
	 // 这个属性使得开发人员可以自己设置开启异步操作的注解(可谓非常的人性化了，但是大多情况下用Spring的就足够了)
	Class<? extends Annotation> annotation() default Annotation.class;
  
	// true表示启用CGLIB代理
	boolean proxyTargetClass() default false;
	
  // PROXY：采用Spring的动态代理（含JDK动态代理和CGLIB）
	// ASPECTJ：使用AspectJ静态代理方式。
	AdviceMode mode() default AdviceMode.PROXY;
  
	// 执行顺序（因为可能有多个@EnableXXX）
	int order() default Ordered.LOWEST_PRECEDENCE;
}
```



AsyncConfigurationSelector

```java
public abstract class AdviceModeImportSelector<A extends Annotation> implements ImportSelector {

	// 默认都叫mode
	public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";
	// 显然也允许子类覆盖此方法
	protected String getAdviceModeAttributeName() {
		return DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME;
	}
	
	// importingClassMetadata：注解的信息
	@Override
	public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
    // 解析泛型参数的类型，对于 AsyncConfigurationSelector 为 EnableAsync
		Class<?> annType = GenericTypeResolver.resolveTypeArgument(getClass(), AdviceModeImportSelector.class);
    // attributes 为 EnableAsync 注解
		AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
		if (attributes == null) {
			throw new IllegalArgumentException(String.format( "@%s is not present annType.getSimpleName(), importingClassMetadata.getClassName()));
		}

		// 解析 EnableAsync 的 mode 属性，导入哪个Bean
		AdviceMode adviceMode = attributes.getEnum(this.getAdviceModeAttributeName());
		String[] imports = selectImports(adviceMode);
		if (imports == null) {
			throw new IllegalArgumentException(String.format("Unknown AdviceMode: '%s'", adviceMode));
		}
		return imports;
	}

	// 子类去实现
	@Nullable
	protected abstract String[] selectImports(AdviceMode adviceMode);
}


public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync> {
	private static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
			"org.springframework.scheduling.aspectj.AspectJAsyncConfiguration";
	@Override
	@Nullable
	public String[] selectImports(AdviceMode adviceMode) {
		// 这里 AdviceMode 进行不同的处理，从而向 Spring 容器注入了不同的Bean
		switch (adviceMode) {
			// 大多数情况下都走这里，ProxyAsyncConfiguration会被注入到Bean容器里面
			case PROXY:
				return new String[] { ProxyAsyncConfiguration.class.getName() };
			case ASPECTJ:
				return new String[] { ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME };
			default:
				return null;
		}
	}
}
```

由此可议看出，`@EnableAsync`最终是向容器内注入了`ProxyAsyncConfiguration`这个Bean。由名字可议看出，它是一个配置类。



```java
@Configuration
public abstract class AbstractAsyncConfiguration implements ImportAware {

	// 此注解@EnableAsync的元信息
	protected AnnotationAttributes enableAsync;
	// 异步线程池
	protected Executor executor;
	// 异步异常的处理器
	protected AsyncUncaughtExceptionHandler exceptionHandler;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		// 拿到@EnableAsync注解的元数据信息
		this.enableAsync = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
		if (this.enableAsync == null) {
			throw new IllegalArgumentException("@EnableAsync is not present on importing class " + importMetadata.getClassName());
		}
	}

	 // 把所有的 AsyncConfigurer 的实现类都搜集进来，然后进行类似属性的合并
	 // 备注：虽然这里用的是Collection 但是AsyncConfigurer的实现类只允许有一个
	@Autowired(required = false)
	void setConfigurers(Collection<AsyncConfigurer> configurers) {
    // 从 AsyncConfigurer 中初始化 executor 和 exceptionHandler
    if (!CollectionUtils.isEmpty(configurers)) {
      if (configurers.size() > 1) {
        throw new IllegalStateException("Only one AsyncConfigurer may exist");
      } else {
        AsyncConfigurer configurer = (AsyncConfigurer)configurers.iterator().next();
        this.executor = configurer::getAsyncExecutor;
        this.exceptionHandler = configurer::getAsyncUncaughtExceptionHandler;
      }
    }
	}
}

// 它是一个配置类，角色为ROLE_INFRASTRUCTURE  框架自用的Bean类型
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyAsyncConfiguration extends AbstractAsyncConfiguration {
	// 它的作用就是诸如了一个AsyncAnnotationBeanPostProcessor，它是个BeanPostProcessor
	@Bean(name = TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
		Assert.notNull(this.enableAsync, "@EnableAsync annotation metadata was not injected");
		AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
    bpp.configure(this.executor, this.exceptionHandler);
		// customAsyncAnnotation：自定义的注解类型
		Class<? extends Annotation> customAsyncAnnotation = this.enableAsync.getClass("annotation");
		// 自定义注解优先级高
		if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
			bpp.setAsyncAnnotationType(customAsyncAnnotation);
		}
	  // 用 EnableAsync 的 proxyTargetClass 和 order 初始化 AsyncAnnotationBeanPostProcessor
		bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
		bpp.setOrder(this.enableAsync.<Integer>getNumber("order"));
		return bpp;
	}
}
```

从上可知，ProxyAsyncConfiguration 的作用：

1、解析 EnableAsync

2、依赖 AsyncConfigurer

初始化并注册 Bean `AsyncAnnotationBeanPostProcessor`



#### Async 注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
	// 用来指定执行器的（BeanName名称）
	String value() default "";
}
```



```java
public abstract class AbstractAdvisingBeanPostProcessor extends ProxyProcessorSupport implements BeanPostProcessor {

	@Nullable
	protected Advisor advisor;
	protected boolean beforeExistingAdvisors = false;
	
	// 缓存合格的Bean们
	private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap<>(256);
  
	// 决定 advisor 的顺序是否为 advised 的第一个。默认在最后面。
	public void setBeforeExistingAdvisors(boolean beforeExistingAdvisors) {
		this.beforeExistingAdvisors = beforeExistingAdvisors;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		// 忽略AopInfrastructureBean的Bean，并且如果没有advisor也会忽略不处理
		if (bean instanceof AopInfrastructureBean || this.advisor == null) {
			// Ignore AOP infrastructure such as scoped proxies.
			return bean;
		}
		
		if (bean instanceof Advised) {
      // 给 bean 增加切面
			Advised advised = (Advised) bean;
			// 注意此advised不能是已经被冻结了的。且源对象必须是Eligible合格的
			if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(bean))) {
				if (this.beforeExistingAdvisors) {
					advised.addAdvisor(0, this.advisor);
				}
				// 否则就是尾部位置
				else {
					advised.addAdvisor(this.advisor);
				}
				// 最终直接返回即可，因为已经没有必要再创建一次代理对象了
				return bean;
			}
		}

		// 如果这个Bean是合格的，这个时候是没有被代理过的
		if (isEligible(bean, beanName)) {
			// 以当前的配置，创建一个ProxyFactory 
			ProxyFactory proxyFactory = prepareProxyFactory(bean, beanName);
			// 如果不是使用CGLIB常见代理，那就去分析出它所实现的接口，然后放进ProxyFactory 里去
			if (!proxyFactory.isProxyTargetClass()) {
				evaluateProxyInterfaces(bean.getClass(), proxyFactory);
			}
			// 切面就是当前持有得advisor
			proxyFactory.addAdvisor(this.advisor);
			// 留给子类，自己还可以对proxyFactory进行自定义~~~~~
			customizeProxyFactory(proxyFactory);
			// 最终返回这个代理对象~~~~~
			return proxyFactory.getProxy(getProxyClassLoader());
		}

		// No async proxy needed.（相当于没有做任何的代理处理,返回原对象）
		return bean;
	}
	
	// 检查这个Bean是否是合格的
	protected boolean isEligible(Object bean, String beanName) {
		return isEligible(bean.getClass());
	}
  
	protected boolean isEligible(Class<?> targetClass) {
		// 如果已经被缓存着了，那肯定靠谱啊
		Boolean eligible = this.eligibleBeans.get(targetClass);
		if (eligible != null) {
			return eligible;
		}
		// 如果没有切面（就相当于没有给配置增强器，那铁定是不合格的）
		if (this.advisor == null) {
			return false;
		}
	
		// 这个重要了：看看这个advisor是否能够切入进targetClass这个类，能够切入进取的也是合格的
		eligible = AopUtils.canApply(this.advisor, targetClass);
		this.eligibleBeans.put(targetClass, eligible);
		return eligible;
	}

	// 子类可以复写。比如`AbstractBeanFactoryAwareAdvisingPostProcessor`覆写了该方法
	protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.copyFrom(this);
		proxyFactory.setTarget(bean);
		return proxyFactory;
	}

	protected void customizeProxyFactory(ProxyFactory proxyFactory) {
	}
}

public class AsyncAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

	public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = AnnotationAsyncExecutionInterceptor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;

	private Class<? extends Annotation> asyncAnnotationType;

	private Executor executor;

	private AsyncUncaughtExceptionHandler exceptionHandler;

	public AsyncAnnotationBeanPostProcessor() {
		setBeforeExistingAdvisors(true);
	}

	public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		this.asyncAnnotationType = asyncAnnotationType;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
		if (this.asyncAnnotationType != null) {
			advisor.setAsyncAnnotationType(this.asyncAnnotationType);
		}
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}
}
```

以上给注入的 Bean 增加切面



```java
public class AsyncAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private AsyncUncaughtExceptionHandler exceptionHandler;
	private Advice advice;
	private Pointcut pointcut;

	public AsyncAnnotationAdvisor() {
		this(null, null);
	}

	// 创建一个AsyncAnnotationAdvisor实例，可以自己指定Executor 和 AsyncUncaughtExceptionHandler 
	@SuppressWarnings("unchecked")
	public AsyncAnnotationAdvisor(@Nullable Executor executor, @Nullable AsyncUncaughtExceptionHandler exceptionHandler) {
		// 这里List长度选择2，应为绝大部分情况下只会支持这两种@Async和@Asynchronous
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<>(2);
		asyncAnnotationTypes.add(Async.class);
		try {
			asyncAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.Asynchronous", AsyncAnnotationAdvisor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// If EJB 3.1 API not present, simply ignore.
		}		

    this.advice = buildAdvice(executor, this.exceptionHandler);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}

	public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<>();
		asyncAnnotationTypes.add(asyncAnnotationType);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (this.advice instanceof BeanFactoryAware) {
			((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
		}
	}

	@Override
	public Advice getAdvice() {
		return this.advice;
	}
	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

	// 这个最终又是委托给`AnnotationAsyncExecutionInterceptor`，它是一个具体的增强器，有着核心内容
	protected Advice buildAdvice(@Nullable Executor executor, AsyncUncaughtExceptionHandler exceptionHandler) {
		return new AnnotationAsyncExecutionInterceptor(executor, exceptionHandler);
	}

	// Calculate a pointcut for the given async annotation types, if any
	protected Pointcut buildPointcut(Set<Class<? extends Annotation>> asyncAnnotationTypes) {
		// 采用一个组合切面：ComposablePointcut （因为可能需要支持多个注解嘛）
		ComposablePointcut result = null;
		for (Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
			// 这里为何new出来两个AnnotationMatchingPointcut
			// 第一个：类匹配（只需要类上面有这个注解，所有的方法都匹配）
			// 第二个：方法匹配。所有的类都可议。但是只有方法上有这个注解才会匹配上
			Pointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
			Pointcut mpc = new AnnotationMatchingPointcut(null, asyncAnnotationType, true);
			if (result == null) {
				result = new ComposablePointcut(cpc);
			}
			else {
				result.union(cpc);
			}
			result = result.union(mpc);
		}
		//  所有类型都木有的情况下，是匹配所有类的所有方法
		return (result != null ? result : Pointcut.TRUE);
	}
}
```



```java
public abstract class AsyncExecutionAspectSupport implements BeanFactoryAware {
	// 这是备选的。如果找到多个类型为TaskExecutor的Bean，才会备选的再用这个名称去找
	public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";
	// 不同的方法，对应的异步执行器不一样
	private final Map<Method, AsyncTaskExecutor> executors = new ConcurrentHashMap<>(16);
	private volatile Executor defaultExecutor;
	private AsyncUncaughtExceptionHandler exceptionHandler;
	private BeanFactory beanFactory;

	public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor) {
		this(defaultExecutor, new SimpleAsyncUncaughtExceptionHandler());
	}
	public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
		this.defaultExecutor = defaultExecutor;
		this.exceptionHandler = exceptionHandler;
	}
	public void setExecutor(Executor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}
	public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	// 找到一个异步执行器
	@Nullable
	protected AsyncTaskExecutor determineAsyncExecutor(Method method) {
		// 如果缓存中能够找到该方法对应的执行器，就立马返回了
		AsyncTaskExecutor executor = this.executors.get(method);
		if (executor == null) {
			Executor targetExecutor;
			String qualifier = getExecutorQualifier(method);
			if (StringUtils.hasLength(qualifier)) {
				targetExecutor = findQualifiedExecutor(this.beanFactory, qualifier);
			}
			
			// 注解没有指定value值，那就去找默认的执行器
			else {
				targetExecutor = this.defaultExecutor;
				if (targetExecutor == null) {
					// 去找getDefaultExecutor()~~~
					synchronized (this.executors) {
						if (this.defaultExecutor == null) {
							this.defaultExecutor = getDefaultExecutor(this.beanFactory);
						}
						targetExecutor = this.defaultExecutor;
					}
				}
			}
			
			// 若还未null，那就返回null吧
			if (targetExecutor == null) {
				return null;
			}
			
			// 把targetExecutor 包装成一个AsyncTaskExecutor返回，并且缓存起来。
			// TaskExecutorAdapter就是AsyncListenableTaskExecutor的一个实现类
			executor = (targetExecutor instanceof AsyncListenableTaskExecutor ?
					(AsyncListenableTaskExecutor) targetExecutor : new TaskExecutorAdapter(targetExecutor));
			this.executors.put(method, executor);
		}
		return executor;
	}

	// 子类去复写此方法。也就是拿到对应的key，从而方便找bean吧（执行器）
	protected abstract String getExecutorQualifier(Method method);

	protected Executor findQualifiedExecutor(@Nullable BeanFactory beanFactory, String qualifier) {
		if (beanFactory == null) {
			throw new IllegalStateException("BeanFactory must be set on " + getClass().getSimpleName() +
					" to access qualified executor '" + qualifier + "'");
		}
		return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Executor.class, qualifier);
	}

	// 检索或者创建一个默认的executor 
	@Nullable
	protected Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
		if (beanFactory != null) {
			// 这个处理很有意思，它是用用的try catch的技巧去处理的
			try {
				// 如果容器内存在唯一的TaskExecutor（子类），就直接返回了
				return beanFactory.getBean(TaskExecutor.class);
			}
			catch (NoUniqueBeanDefinitionException ex) {
				try {
					return beanFactory.getBean(DEFAULT_TASK_EXECUTOR_BEAN_NAME, Executor.class);
				}
				catch (NoSuchBeanDefinitionException ex2) {
				}
			}
			catch (NoSuchBeanDefinitionException ex) {
				try {
					return beanFactory.getBean(DEFAULT_TASK_EXECUTOR_BEAN_NAME, Executor.class);
				}
				catch (NoSuchBeanDefinitionException ex2) {
				}
				// Giving up -> either using local default executor or none at all...
			}
		}
		return null;
	}

	// 用选定的执行者执行给定任务
	@Nullable
	protected Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
		// 根据不同的返回值类型，来采用不同的方案去异步执行，但是执行器都是executor
		if (CompletableFuture.class.isAssignableFrom(returnType)) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					return task.call();
				}
				catch (Throwable ex) {
					throw new CompletionException(ex);
				}
			}, executor);
		}
		// ListenableFuture接口继承自Future  是Spring自己扩展的一个接口。
		// 同样的AsyncListenableTaskExecutor也是Spring扩展自AsyncTaskExecutor的
		else if (ListenableFuture.class.isAssignableFrom(returnType)) {
			return ((AsyncListenableTaskExecutor) executor).submitListenable(task);
		}
		// 普通的submit
		else if (Future.class.isAssignableFrom(returnType)) {
			return executor.submit(task);
		}
		// 没有返回值的情况下  也用sumitt提交，按时返回null
		else {
			executor.submit(task);
			return null;
		}
	}

	// 处理错误
	protected void handleError(Throwable ex, Method method, Object... params) throws Exception {
	 	// 如果方法的返回值类型是Future,就rethrowException，表示直接throw出去
		if (Future.class.isAssignableFrom(method.getReturnType())) {
			ReflectionUtils.rethrowException(ex);
		} else {
			// Could not transmit the exception to the caller with default executor
			try {
				this.exceptionHandler.handleUncaughtException(ex, method, params);
			} catch (Throwable ex2) {
				logger.error("Exception handler for async method '" + method.toGenericString() +
						"' threw unexpected exception itself", ex2);
			}
		}
	}

}

public class AsyncExecutionInterceptor extends AsyncExecutionAspectSupport implements MethodInterceptor, Ordered {
	...
	// 显然这个方法它直接返回null，因为XML配置嘛~~~~
	@Override
	@Nullable
	protected String getExecutorQualifier(Method method) {
		return null;
	}

	@Override
	@Nullable
	protected Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
		Executor defaultExecutor = super.getDefaultExecutor(beanFactory);
		return (defaultExecutor != null ? defaultExecutor : new SimpleAsyncTaskExecutor());
	}
  
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

  // 解析 Joinpoint 创建任务，提交到执行器。
	@Override
	@Nullable
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
		final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
		if (executor == null) {
			throw new IllegalStateException(
					"No executor specified and no default executor set on AsyncExecutionInterceptor either");
		}
		Callable<Object> task = () -> {
			try {
				Object result = invocation.proceed();
				if (result instanceof Future) {
					return ((Future<?>) result).get();
				}
			}
			catch (ExecutionException ex) {
				handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
			}
			catch (Throwable ex) {
				handleError(ex, userDeclaredMethod, invocation.getArguments());
			}
			return null;
		};
		return doSubmit(task, executor, invocation.getMethod().getReturnType());
	}
}

public class AnnotationAsyncExecutionInterceptor extends AsyncExecutionInterceptor {
	@Override
	@Nullable
	protected String getExecutorQualifier(Method method) {
    // 先找到方法上的 Async，再找类上面的 Async，方法的注解可以覆盖类的 Async 配置
		Async async = AnnotatedElementUtils.findMergedAnnotation(method, Async.class);
		if (async == null) {
			async = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), Async.class);
		}
		return (async != null ? async.value() : null);
	}
}
```

以上初始化切面拦截器

