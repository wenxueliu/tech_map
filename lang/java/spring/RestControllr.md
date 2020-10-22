Spring 处理请求的主流程是啥？

Spring 中整个处理请求的主流程是啥？经常进行 spring 开发，大家知道拦截器，过滤器，还有具体的 Controller 处理。大体的顺序也能理清楚。但是不看源码总觉得不踏实。那么 spring 到底是如何处理的呢？事实上，非常简单。用不了 5 分钟，你就达到给别人讲解程度。

```java
public class DispatcherServlet extends FrameworkServlet {

  private List<HandlerAdapter> handlerAdapters;
  
  private List<HandlerMapping> handlerMappings;
  
	private void initHandlerAdapters(ApplicationContext context) {
		this.handlerAdapters = null;

		if (this.detectAllHandlerAdapters) {
			// 从 context 查
			Map<String, HandlerAdapter> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerAdapters = new ArrayList<>(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerAdapters);
			}
		}
		else {
			try {
        // 从 context 配置中找
				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				this.handlerAdapters = Collections.singletonList(ha);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// 默认的
		if (this.handlerAdapters == null) {
			this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
		}
	}  
  
	private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;

		if (this.detectAllHandlerMappings) {
			// 从 context 查
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<>(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
        // 从 context 配置中找
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// 默认的
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
		}
	}  
  
  
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Make framework objects available to handlers and view objects.
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
		request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
		request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
		request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());


	  doDispatch(request, response);

	}
  
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    
		HttpServletRequest processedRequest = request;

    try {
      // 从 handlerMappings 找到 processedRequest 对应的 HandlerExecutionChain
      HandlerExecutionChain mappedHandler = getHandler(processedRequest);

      // 调用 mappedHandler 中每个拦截器的 PreHandler 方法。如果某个拦截器返回 false，请求处理就此完成。
      if (!mappedHandler.applyPreHandle(processedRequest, response)) {
        return;
      }

      // 从 handlerAdapters 根据 mappedHandler 找到对应的 HandlerAdapter
      HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

      // 调用 HandlerAdapter 的 handler 方法处理请求
      mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

      // 生成 ModelAndView
      applyDefaultViewName(processedRequest, mv);

      ModelAndView mv = null;

      // 调用 mappedHandler 中每个拦截器的 PostHandler 方法。
      mappedHandler.applyPostHandle(processedRequest, response, mv);
    } catch (Exception var20) {
    } catch (Throwable var21) {          
    }
    // 调用 mappedHandler 中每个拦截器的 triggerAfterCompletion 方法。
		processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
	}
  
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (this.handlerMappings != null) {
			for (HandlerMapping mapping : this.handlerMappings) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
		}
		return null;
	}
  
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (this.handlerAdapters != null) {
			for (HandlerAdapter adapter : this.handlerAdapters) {
				if (adapter.supports(handler)) {
					return adapter;
				}
			}
		}
		throw new ServletException("No adapter for handler [" + handler +
				"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}
  
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
			@Nullable Exception exception) throws Exception {

		// 生成 ModelAndView
		if (mv != null && !mv.wasCleared()) {
			render(mv, request, response);
		}

		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, null);
		}
	}  

	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		View view;
		String viewName = mv.getViewName();
		if (viewName != null) {
			// 优先使用 viewName
			view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
		}
		else {
			// 其次 View
			view = mv.getView();
		}
    
		if (mv.getStatus() != null) {
			response.setStatus(mv.getStatus().value());
		}
		view.render(mv.getModelInternal(), request, response);
	}  
}
```



### 总结

整个处理流程

1. 初始化 handlerAdapters 和 handlerMappings
2. 根据 mappedHandler 从 handlerMappings 找到 mappedHandler
3. 调用 mappedHandler 中每个拦截器的 PreHandler 方法。如果某个拦截器返回 false，请求处理就此完成。
4. 调用 HandlerAdapter 的 handler 方法处理请求
5. 生成 View
6. 调用 mappedHandler 中每个拦截器的 PostHandler 方法
7. 调用 mappedHandler 中每个拦截器的 triggerAfterCompletion 方法（即使有异常，该方法也确保执行）

所以整个处理流程的核心是  HandlerAdapter 的 handler 方法。关于   HandlerAdapter 的 handler 下篇文章详述。



### HandlerMapping



```java
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping {
    private Object defaultHandler;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private final List<Object> interceptors = new ArrayList();
    private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList();
  
    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }
  
   	public void setInterceptors(Object... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

   	protected void initInterceptors() {
        if (!this.interceptors.isEmpty()) {
            for(int i = 0; i < this.interceptors.size(); ++i) {
                Object interceptor = this.interceptors.get(i);
                if (interceptor == null) {
                    throw new IllegalArgumentException("Entry number " + i + " in interceptors array is null");
                }

                this.adaptedInterceptors.add(this.adaptInterceptor(interceptor));
            }
        }

    }

    protected HandlerInterceptor adaptInterceptor(Object interceptor) {
        if (interceptor instanceof HandlerInterceptor) {
            return (HandlerInterceptor)interceptor;
        } else if (interceptor instanceof WebRequestInterceptor) {
            return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor)interceptor);
        } else {
            throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
        }
    }  
  
    @Nullable
    protected final HandlerInterceptor[] getAdaptedInterceptors() {
        return !this.adaptedInterceptors.isEmpty() ? (HandlerInterceptor[])this.adaptedInterceptors.toArray(new HandlerInterceptor[0]) : null;
    }

    @Nullable
    protected final MappedInterceptor[] getMappedInterceptors() {
        List<MappedInterceptor> mappedInterceptors = new ArrayList(this.adaptedInterceptors.size());
        Iterator var2 = this.adaptedInterceptors.iterator();

        while(var2.hasNext()) {
            HandlerInterceptor interceptor = (HandlerInterceptor)var2.next();
            if (interceptor instanceof MappedInterceptor) {
                mappedInterceptors.add((MappedInterceptor)interceptor);
            }
        }

        return !mappedInterceptors.isEmpty() ? (MappedInterceptor[])mappedInterceptors.toArray(new MappedInterceptor[0]) : null;
    }

    
    @Nullable
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        // handler 来源1
        Object handler = this.getHandlerInternal(request);
        if (handler == null) {
            // handler 来源2
            handler = this.getDefaultHandler();
        }

        if (handler == null) {
            return null;
        } else {
            if (handler instanceof String) {
                String handlerName = (String)handler;
                // handler 来源3
                handler = this.obtainApplicationContext().getBean(handlerName);
            }

            HandlerExecutionChain executionChain = this.getHandlerExecutionChain(handler, request);
            return executionChain;
        }
    }

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest var1) throws Exception;
  
    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = handler instanceof HandlerExecutionChain ? (HandlerExecutionChain)handler : new HandlerExecutionChain(handler);
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        Iterator var5 = this.adaptedInterceptors.iterator();

        while(var5.hasNext()) {
            HandlerInterceptor interceptor = (HandlerInterceptor)var5.next();
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor)interceptor;
                if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
                    chain.addInterceptor(mappedInterceptor.getInterceptor());
                }
            } else {
                chain.addInterceptor(interceptor);
            }
        }

        return chain;
    }  
}
```





```java
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {
   protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = this.getUrlPathHelper().getLookupPathForRequest(request);

        HandlerMethod handlerMethod = this.lookupHandlerMethod(lookupPath, request);
        var4 = handlerMethod != null ? handlerMethod.createWithResolvedBean() : null;
        return var4;
    }
    
   protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<AbstractHandlerMethodMapping<T>.Match> matches = new ArrayList();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
        if (directPathMatches != null) {
            this.addMatchingMappings(directPathMatches, matches, request);
        }

        if (matches.isEmpty()) {
            this.addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
        }

        if (!matches.isEmpty()) {
            Comparator<AbstractHandlerMethodMapping<T>.Match> comparator = new AbstractHandlerMethodMapping.MatchComparator(this.getMappingComparator(request));
            matches.sort(comparator);
            AbstractHandlerMethodMapping<T>.Match bestMatch = (AbstractHandlerMethodMapping.Match)matches.get(0);
            if (matches.size() > 1) {
                if (CorsUtils.isPreFlightRequest(request)) {
                    return PREFLIGHT_AMBIGUOUS_MATCH;
                }

                AbstractHandlerMethodMapping<T>.Match secondBestMatch = (AbstractHandlerMethodMapping.Match)matches.get(1);
                if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                    Method m1 = bestMatch.handlerMethod.getMethod();
                    Method m2 = secondBestMatch.handlerMethod.getMethod();
                    String uri = request.getRequestURI();
                    throw new IllegalStateException("Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");
                }
            }

            request.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, bestMatch.handlerMethod);
            this.handleMatch(bestMatch.mapping, lookupPath, request);
            return bestMatch.handlerMethod;
        } else {
            return this.handleNoMatch(this.mappingRegistry.getMappings().keySet(), lookupPath, request);
        }
    }    

    protected void handleMatch(T mapping, String lookupPath, HttpServletRequest request) {
        request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, lookupPath);
    }
  
    protected HandlerMethod handleNoMatch(Set<T> mappings, String lookupPath, HttpServletRequest request) throws Exception {
        return null;
    }

    private class Match {
        private final T mapping;
        private final HandlerMethod handlerMethod;

        public Match(T mapping, HandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        public String toString() {
            return this.mapping.toString();
        }
    }

    private static class MappingRegistration<T> {
        private final T mapping;
        private final HandlerMethod handlerMethod;
        private final List<String> directUrls;
        @Nullable
        private final String mappingName;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, @Nullable List<String> directUrls, @Nullable String mappingName) {
            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directUrls = directUrls != null ? directUrls : Collections.emptyList();
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public List<String> getDirectUrls() {
            return this.directUrls;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }
    }

    class MappingRegistry {
        private final Map<T, AbstractHandlerMethodMapping.MappingRegistration<T>> registry = new HashMap();
        private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap();
        private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap();
        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap();
        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap();
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        MappingRegistry() {
        }

        public Map<T, HandlerMethod> getMappings() {
            return this.mappingLookup;
        }

        @Nullable
        public List<T> getMappingsByUrl(String urlPath) {
            return (List)this.urlLookup.get(urlPath);
        }

        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return (List)this.nameLookup.get(mappingName);
        }

        public CorsConfiguration getCorsConfiguration(HandlerMethod handlerMethod) {
            HandlerMethod original = handlerMethod.getResolvedFromHandlerMethod();
            return (CorsConfiguration)this.corsLookup.get(original != null ? original : handlerMethod);
        }

        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();

            try {
                HandlerMethod handlerMethod = AbstractHandlerMethodMapping.this.createHandlerMethod(handler, method);
                this.assertUniqueMethodMapping(handlerMethod, mapping);
                this.mappingLookup.put(mapping, handlerMethod);
                List<String> directUrls = this.getDirectUrls(mapping);
                Iterator var6 = directUrls.iterator();

                while(var6.hasNext()) {
                    String url = (String)var6.next();
                    this.urlLookup.add(url, mapping);
                }

                String name = null;
                if (AbstractHandlerMethodMapping.this.getNamingStrategy() != null) {
                    name = AbstractHandlerMethodMapping.this.getNamingStrategy().getName(handlerMethod, mapping);
                    this.addMappingName(name, handlerMethod);
                }

                CorsConfiguration corsConfig = AbstractHandlerMethodMapping.this.initCorsConfiguration(handler, method, mapping);
                if (corsConfig != null) {
                    this.corsLookup.put(handlerMethod, corsConfig);
                }

                this.registry.put(mapping, new AbstractHandlerMethodMapping.MappingRegistration(mapping, handlerMethod, directUrls, name));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }

        }

        private void assertUniqueMethodMapping(HandlerMethod newHandlerMethod, T mapping) {
            HandlerMethod handlerMethod = (HandlerMethod)this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException("Ambiguous mapping. Cannot map '" + newHandlerMethod.getBean() + "' method \n" + newHandlerMethod + "\nto " + mapping + ": There is already '" + handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }

        private List<String> getDirectUrls(T mapping) {
            List<String> urls = new ArrayList(1);
            Iterator var3 = AbstractHandlerMethodMapping.this.getMappingPathPatterns(mapping).iterator();

            while(var3.hasNext()) {
                String path = (String)var3.next();
                if (!AbstractHandlerMethodMapping.this.getPathMatcher().isPattern(path)) {
                    urls.add(path);
                }
            }

            return urls;
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = (List)this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }

            Iterator var4 = oldList.iterator();

            HandlerMethod current;
            do {
                if (!var4.hasNext()) {
                    List<HandlerMethod> newList = new ArrayList(oldList.size() + 1);
                    newList.addAll(oldList);
                    newList.add(handlerMethod);
                    this.nameLookup.put(name, newList);
                    return;
                }

                current = (HandlerMethod)var4.next();
            } while(!handlerMethod.equals(current));

        }

        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();

            try {
                AbstractHandlerMethodMapping.MappingRegistration<T> definition = (AbstractHandlerMethodMapping.MappingRegistration)this.registry.remove(mapping);
                if (definition != null) {
                    this.mappingLookup.remove(definition.getMapping());
                    Iterator var3 = definition.getDirectUrls().iterator();

                    while(var3.hasNext()) {
                        String url = (String)var3.next();
                        List<T> list = (List)this.urlLookup.get(url);
                        if (list != null) {
                            list.remove(definition.getMapping());
                            if (list.isEmpty()) {
                                this.urlLookup.remove(url);
                            }
                        }
                    }

                    this.removeMappingName(definition);
                    this.corsLookup.remove(definition.getHandlerMethod());
                    return;
                }
            } finally {
                this.readWriteLock.writeLock().unlock();
            }

        }

        private void removeMappingName(AbstractHandlerMethodMapping.MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name != null) {
                HandlerMethod handlerMethod = definition.getHandlerMethod();
                List<HandlerMethod> oldList = (List)this.nameLookup.get(name);
                if (oldList != null) {
                    if (oldList.size() <= 1) {
                        this.nameLookup.remove(name);
                    } else {
                        List<HandlerMethod> newList = new ArrayList(oldList.size() - 1);
                        Iterator var6 = oldList.iterator();

                        while(var6.hasNext()) {
                            HandlerMethod current = (HandlerMethod)var6.next();
                            if (!current.equals(handlerMethod)) {
                                newList.add(current);
                            }
                        }

                        this.nameLookup.put(name, newList);
                    }
                }
            }
        }
    }  
}
```





@RestController 

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {
    @AliasFor(
        annotation = Controller.class
    )
    String value() default "";
}
```

@RestController = @Controller + @ResponseBody。

因此，理解 RestController 必须先理解 Controller 和 RestponseBody



### Controller



```java
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    public SimpleControllerHandlerAdapter() {
    }

  	// Controller 的处理为当前函数
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Nullable
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ((Controller)handler).handleRequest(request, response);
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        return handler instanceof LastModified ? ((LastModified)handler).getLastModified(request) : -1L;
    }
}
```

