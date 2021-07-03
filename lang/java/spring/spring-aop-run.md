spring-aop-run



### Spring Aop 代理的执行

检查当前方法是否被代理，

1、如果没有被代理直接执行方法

2、如果被代理，执行对应的切面方法

这里需要说明的是，代理方法的执行类 ReflectiveMethodInvocation 是一个典型的调用链实现，值得参考。此外，tomcat 的 filter 也采用类似实现。

#### cglib 方法执行

1、如果 target,proxy,method 满足MethodMatcher的 match 方法，调用拦截器，否则继续下一个拦截器

```java
public interface MethodInterceptor extends Callback {
    Object intercept(Object var1, Method var2, Object[] var3, MethodProxy var4) throws Throwable;
}

class InterceptorAndDynamicMethodMatcher {
	final MethodInterceptor interceptor;
	final MethodMatcher methodMatcher;
	public InterceptorAndDynamicMethodMatcher(MethodInterceptor interceptor, MethodMatcher methodMatcher) {
		this.interceptor = interceptor;
		this.methodMatcher = methodMatcher;
	}
}

public class ReflectiveMethodInvocation implements ProxyMethodInvocation, Cloneable {
   protected final Object proxy;
   protected final Object target;
   protected final Method method;
   protected Object[] arguments;
   private final Class<?> targetClass;

   private Map<String, Object> userAttributes;
   protected final List<?> interceptorsAndDynamicMethodMatchers;
   private int currentInterceptorIndex = -1;	
  //如果 this.method, this.targetClass, this.arguments与InterceptorAndDynamicMethodMatcher匹配，就进执行切面方法，否则继续下一个切面方法。
	@Override
	public Object proceed() throws Throwable {
		// We start with an index of -1 and increment early.
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return invokeJoinpoint();
		}

		Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
			// Evaluate dynamic method matcher here: static part will already have
			// been evaluated and found to match.
			InterceptorAndDynamicMethodMatcher dm =
					(InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
			Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
			if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
				return dm.interceptor.invoke(this);
			}
			else {
				// Dynamic matching failed.
				// Skip this interceptor and invoke the next in the chain.
        // 递归的方式
				return proceed();
			}
		}
		else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}

	@Nullable
	protected Object invokeJoinpoint() throws Throwable {
		return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
	}
}
```



#### Jdk Aop 实现

```java
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object oldProxy = null;
		boolean setProxyContext = false;

		TargetSource targetSource = this.advised.targetSource;
		Object target = null;
		try {
      // 
			if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
					method.getDeclaringClass().isAssignableFrom(Advised.class)) {
				// Service invocations on ProxyConfig with the proxy config...
				return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
			}
			Object retVal;
      // exposeProxy 用于内部方法之间相互调用时，无法代理的问题。
			if (this.advised.exposeProxy) {
				// Make invocation available if necessary.
				oldProxy = AopContext.setCurrentProxy(proxy);
				setProxyContext = true;
			}

			target = targetSource.getTarget();
			Class<?> targetClass = (target != null ? target.getClass() : null);
			// Get the interception chain for this method.
			List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
			
			if (chain.isEmpty()) {
				Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
        //没有定义切面，直接调用方法
				retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
			}
			else {
        // 用 ReflectiveMethodInvocation 封装，实现拦截器的链式调用
				MethodInvocation invocation =
						new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
				// Proceed to the joinpoint through the interceptor chain.
				retVal = invocation.proceed();
			}
      // 返回结果
			Class<?> returnType = method.getReturnType();
			if (retVal != null && retVal == target &&
					returnType != Object.class && returnType.isInstance(proxy) &&
					!RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
				// Special case: it returned "this" and the return type of the method
				// is type-compatible. Note that we can't help if the target sets
				// a reference to itself in another returned object.
				retVal = proxy;
			}
			else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
				throw new AopInvocationException(
						"Null return value from advice does not match primitive return type for: " + method);
			}
			return retVal;
		}
		finally {
			if (target != null && !targetSource.isStatic()) {
				// Must have come from TargetSource.
				targetSource.releaseTarget(target);
			}
			if (setProxyContext) {
				// Restore old proxy.
				AopContext.setCurrentProxy(oldProxy);
			}
		}
	}
```

