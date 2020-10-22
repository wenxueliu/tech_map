### 拦截器

拦截器接口

```java
public interface HandlerInterceptor {
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
```

[参考前面的文章](https://blog.csdn.net/wenxueliu/article/details/103485229)，我们复习下整个处理流程。

整个处理流程

1. 初始化 handlerAdapters 和 handlerMappings
2. 根据 mappedHandler 从 handlerMappings 找到 mappedHandler
3. 调用 mappedHandler 中每个拦截器的 PreHandler 方法。如果某个拦截器返回 false，请求处理就此完成。
4. 调用 HandlerAdapter 的 handler 方法处理请求
5. 生成 View
6. 调用 mappedHandler 中每个拦截器的 PostHandler 方法
7. 调用 mappedHandler 中每个拦截器的 triggerAfterCompletion 方法（即使有异常，该方法也确保执行）

那么，具体拦截器的执行顺序是咋样的呢？

```java
public class HandlerExecutionChain {
  
	private final Object handler;

	@Nullable
	private HandlerInterceptor[] interceptors;

	@Nullable
	private List<HandlerInterceptor> interceptorList;

	private int interceptorIndex = -1;
  
  // 调用每个拦截器的 PreHandler 方法：
  // 1. 如果返回 true，继续下一个拦截器。
  // 2. 如果返回 false，调用当前拦截器的 afterCompletion，请求处理就此完成。
	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				if (!interceptor.preHandle(request, response, this.handler)) {
					triggerAfterCompletion(request, response, null);
					return false;
				}
				this.interceptorIndex = i;
			}
		}
		return true;
	}
  
	// 从最后一个拦截器开始，依次调用每个拦截器的 PostHandler 方法。
	void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
			throws Exception {

		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = interceptors.length - 1; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				interceptor.postHandle(request, response, this.handler, mv);
			}
		}
	}
  
	void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex)
			throws Exception {

		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = this.interceptorIndex; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				try {
					interceptor.afterCompletion(request, response, this.handler, ex);
				}
				catch (Throwable ex2) {
					logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
				}
			}
		}
	}  
}	
```



情况一：处理请求之前依次调用 preHandle，处理之后请求依次调用 postHandle，生成 View 之后依次调用 afterCompletion

情况二：在处理正式的请求之前依次调用 preHandle，到任何拦截器返回 false，仅执行当前拦截器的 afterCompletion方法，请求处理完成。

情况三：处理请求之前依次调用 preHandle 的过程中抛出异常，执行**之前所有** handler 的 afterCompletion方法。

情况四：处理请求之前依次调用 preHandle，处理请求之后依次调用 postHandle，当抛出异常的时候，执行**所有** handler 的 afterCompletion方法。

举例说明：假设有 1，2，3，4 个 handler

1. 依次执行 1，2，3，4 的 preHandler，afterHandler，afterCompletion
2. 在 2  的  prehandler 返回 false，调用 2 的 afterCompletion， 请求处理完成。
3. 在 3 的  prehandler 抛出异常，调用 2， 1  拦截器的 afterCompletion， 请求处理完成。
4. 在 2 的  posthandler 抛出异常，调用 4，3，2， 1  拦截器的 afterCompletion， 请求处理完成。



#### 自定义拦截器

实现自定义拦截器，只需要实现 HandlerInterceptor 接口即可。

spring 已经实现了很多拦截器，但是为了防止误用，preHandler 尽量避免返回 false，导致部分拦截器无法执行的问题，继承 HandlerInterceptorAdapter

```java
class MappedInterceptor implements HandlerInterceptor
interface AsyncHandlerInterceptor extends HandlerInterceptor
class WebContentInterceptor extends WebContentGenerator implements HandlerInterceptor  

class WebRequestHandlerInterceptorAdapter implements AsyncHandlerInterceptor  
abstract class HandlerInterceptorAdapter implements AsyncHandlerInterceptor
class ConversionServiceExposingInterceptor extends HandlerInterceptorAdapter
class LocaleChangeInterceptor extends HandlerInterceptorAdapter
class CorsInterceptor extends HandlerInterceptorAdapter  
class PathExposingHandlerInterceptor extends HandlerInterceptorAdapter
class ResourceUrlProviderExposingInterceptor extends HandlerInterceptorAdapter
class ThemeChangeInterceptor extends HandlerInterceptorAdapter
class UriTemplateVariablesHandlerInterceptor extends HandlerInterceptorAdapter
class UserRoleAuthorizationInterceptor extends HandlerInterceptorAdapter
```



#### 总结

Spring 原生的拦截器只能在请求的之前和之后做一些处理。应付一般的校验、鉴权是 OK 的。但是，粒度是请求级别，无法满足更细粒度的需求。那么，是否可以对某个方法的之前和之后做一些控制呢？且看下回的MethodInterceptor



思考题：

拦截器的 PreHandler 为什么要返回 boolean，而且如果返回 false，整个请求流程就中断了？

