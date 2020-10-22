相信你 N 次在代码中写下了 @ResponseBody，可是否层级关注过ResponseBody 到底有什么用？它的底层原理是啥？那么，静下心来，5 分钟，你将掌握，spring 返回值处理的核心。

### 处理器适配器

```java
public interface HandlerAdapter {
    boolean supports(Object var1);

  	//整个 HTTP 处理的最核心，接收 HttpServletRequest，HttpServletResponse，返回ModelAndView
    @Nullable
    ModelAndView handle(HttpServletRequest var1, HttpServletResponse var2, Object var3) throws Exception;

    long getLastModified(HttpServletRequest var1, Object var2);
}

// 典型模板模式的使用
public abstract class AbstractHandlerMethodAdapter extends WebContentGenerator implements HandlerAdapter, Ordered {
    @Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.handleInternal(request, response, (HandlerMethod)handler);
    }
  
  	@Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest var1, HttpServletResponse var2, HandlerMethod var3) throws Exception;
}


public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware, InitializingBean {

    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {    
        ModelAndView mav;
        mav = this.invokeHandlerMethod(request, response, handlerMethod);
        return mav;
    }
}

    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        // 创建 web 请求
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        ModelAndView var15;
        try {
            WebDataBinderFactory binderFactory = this.getDataBinderFactory(handlerMethod);
            // 模型工厂
            ModelFactory modelFactory = this.getModelFactory(handlerMethod, binderFactory);
          
            // 创建方法调用对象
            ServletInvocableHandlerMethod invocableMethod = this.createInvocableHandlerMethod(handlerMethod);
            if (this.argumentResolvers != null) {
               // 设置参数解析器
              invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            }

            if (this.returnValueHandlers != null) {
              // 设置返回值处理器
                invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
            }

            invocableMethod.setDataBinderFactory(binderFactory);
            invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
            // 创建 ModelView 容器
            ModelAndViewContainer mavContainer = new ModelAndViewContainer();
            mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
            modelFactory.initModel(webRequest, mavContainer, invocableMethod);
            mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);
            Object result;
            // 调用方法
            invocableMethod.invokeAndHandle(webRequest, mavContainer, new Object[0]);
            // 封装为 ModelAndView
            var15 = this.getModelAndView(mavContainer, modelFactory, webRequest);
        } finally {
            webRequest.requestCompleted();
        }

        return var15;
    }

    private ModelAndView getModelAndView(ModelAndViewContainer mavContainer, ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {
        modelFactory.updateModel(webRequest, mavContainer);
        if (mavContainer.isRequestHandled()) {
            return null;
        } else {
            ModelMap model = mavContainer.getModel();
            ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
            return mav;
        }
    }
}
```

注：从简化代码的角度，对非核心逻辑进行了删除。

上面的代码层级非常清晰

1. AbstractHandlerMethodAdapter 实现了 HandlerAdapter 的 handle 方法。
2. AbstractHandlerMethodAdapter 在 handle 方法调用了自己的 handleInternal 方法（模板模式）。
3. RequestMappingHandlerAdapter 实现了 AbstractHandlerMethodAdapter 的 handleInternal 方法。
4. RequestMappingHandlerAdapter 的 handleInternal方法调用了 invokeHandlerMethod



下面关注 invokeHandlerMethod 的实现。

1. 创建 ServletInvocableHandlerMethod 对象 invocableMethod，设置参数、返回值
2. 创建 web 请求对象
3. 创建 ModelAndView 容器
4. 调用方法 invokeAndHandle 获取返回值，处理返回值
5. 返回ModelAndView

由上可知，关于返回值的处理都在 invokeAndHandle  里面。



### 处理返回值

其中 invokeAndHandle 处理如下

```java

public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
		private HandlerMethodReturnValueHandlerComposite returnValueHandlers; 
  
		public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        // 获取返回值
        Object returnValue = this.invokeForRequest(webRequest, mavContainer, providedArgs);
				// 找到 returnValue 的 handler，并处理之
        this.returnValueHandlers.handleReturnValue(returnValue, this.getReturnValueType(returnValue), mavContainer, webRequest);
    }
}
```

其中 returnValueHandlers 为 HandlerMethodReturnValueHandlerComposite

```java
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
 
		public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 遍历所有的 HandlerMethodReturnValueHandler，根据 returnType 找到对应的 Handler
        HandlerMethodReturnValueHandler handler = this.selectHandler(returnValue, returnType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        } else {
            // 处理返回值
            handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }
    }

    // 遍历所有的 HandlerMethodReturnValueHandler，根据 returnType 找到对应的 Handler
    @Nullable
    private HandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {
 
        Iterator var4 = this.returnValueHandlers.iterator();
				// 遍历所有返回值 handler，找到支持 returnType 的 handler
        HandlerMethodReturnValueHandler handler;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            handler = (HandlerMethodReturnValueHandler)var4.next();
        // 每个 handler 都实现了 supportsReturnType 方法。
        } while(!handler.supportsReturnType(returnType));
        return handler;
    } 
}
```

那 returnValueHandlers 包含哪些 Handler 呢？具体细节在 RequestMappingHandlerAdapter 中

```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware, InitializingBean {

   // 可以手动设置返回值处理器
   public void setReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
        } else {
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
            this.returnValueHandlers.addHandlers(returnValueHandlers);
        }

    }

	public void afterPropertiesSet() {
    this.initControllerAdviceCache();
    List handlers;
    if (this.argumentResolvers == null) {
        handlers = this.getDefaultArgumentResolvers();
        this.argumentResolvers = (new HandlerMethodArgumentResolverComposite()).addResolvers(handlers);
    }

    if (this.initBinderArgumentResolvers == null) {
        handlers = this.getDefaultInitBinderArgumentResolvers();
        this.initBinderArgumentResolvers = (new HandlerMethodArgumentResolverComposite()).addResolvers(handlers);
    }

    // 设置默认的返回值处理器
    if (this.returnValueHandlers == null) {
        handlers = this.getDefaultReturnValueHandlers();
        // HandlerMethodReturnValueHandlerComposite 与返回值相关的组合器
        this.returnValueHandlers = (new HandlerMethodReturnValueHandlerComposite()).addHandlers(handlers);
    }

    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList();
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new ResponseBodyEmitterReturnValueHandler(this.getMessageConverters(), this.reactiveAdapterRegistry, this.taskExecutor, this.contentNegotiationManager));
        handlers.add(new StreamingResponseBodyReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new HttpHeadersReturnValueHandler());
        handlers.add(new CallableMethodReturnValueHandler());
        handlers.add(new DeferredResultMethodReturnValueHandler());
        handlers.add(new AsyncTaskMethodReturnValueHandler(this.beanFactory));
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(this.getMessageConverters(), this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());
        if (this.getCustomReturnValueHandlers() != null) {
            handlers.addAll(this.getCustomReturnValueHandlers());
        }

        if (!CollectionUtils.isEmpty(this.getModelAndViewResolvers())) {
            handlers.add(new ModelAndViewResolverMethodReturnValueHandler(this.getModelAndViewResolvers()));
        } else {
            handlers.add(new ModelAttributeMethodProcessor(true));
        }

        return handlers;
    }
}
```



1. 注册返回值处理器。所有的返回值处理器都实现了 HandlerMethodReturnValueHandler 接口，所有 handler 都保存在 HandlerMethodReturnValueHandlerComposite 对象中。如果默认没有设置 returnValueHandlers，会调用 getDefaultReturnValueHandlers 来初始化 
2. 处理完请求之后，获取返回值
3. 遍历返回值找到对应的返回值处理器：所有的返回值处理器都实现了 supportsReturnType 方法。
4. 处理返回值：所有的返回值处理器都实现了 handleReturnValue 方法来处理返回值。

#### 总结

1. 所有的返回值都实现了 HandlerMethodReturnValueHandler 接口。

```java
public interface HandlerMethodReturnValueHandler {
    // 寻找匹配的返回值处理器，根据该方法进行判断
    boolean supportsReturnType(MethodParameter var1);

    // 每个返回值处理器具体处理返回值的方法
    void handleReturnValue(@Nullable Object var1, MethodParameter var2, ModelAndViewContainer var3, NativeWebRequest var4) throws Exception;
}
```

2. 先注册所有的 handler
3. 根据 supportsReturnType 找到匹配的返回值处理器
4. 用 handleReturnValue 处理返回值。

这里里面充分体现了设计原则的开放-封闭的思想。



### ResponseBody

了解了返回值的处理，那么很自然的想法是，我们平时 spring 开发中经常用的 ResponseBody 是如何处理的呢？

ResponseBody

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
```



在所有的 HandlerMethodReturnValueHandler 中，RequestResponseBodyMethodProcessor 与 ResponseBody 相关。原因是在 RequestResponseBodyMethodProcessor 中有如下代码

```java
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {

  	//参数为 RequestBody
		public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }
    
    //类或方法有 ResponseBody 注解
    public boolean supportsReturnType(MethodParameter returnType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) || returnType.hasMethodAnnotation(ResponseBody.class);
    }
}
```

也就是请求或应答中包含 RequestBody 的都由 RequestResponseBodyMethodProcessor 处理。RequestResponseBodyMethodProcessor 也有 handleReturnValue 方法，相信此刻，你已经急不可耐地想打开 IDEA 一看究竟。