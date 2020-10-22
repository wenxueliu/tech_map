spring-dogetbean



### 属性说明

mergedBeanDefinitions：key 为beanName，value 为已经合并的RootBeanDefinition。缓存已经合并的 Bean。

dependentBeanMap：key 为beanName，value 为依赖 key 的 beanName

dependenciesForBeanMap：key 为 beanName，value 为 key 依赖的 beanName，与 dependentBeanMap正好相反

singletonObjects ：保存已经创建完成的 Bean

earlySingletonObjects：正在创建的 Bean

singletonFactories ：保存 Bean 的创建工厂

inCreationCheckExclusions：bean 没有在创建过程中，则加入该集合。与 singletonsCurrentlyInCreation相反。

singletonsCurrentlyInCreation：创建开始加入该结合，创建结束从该集合删除，是解决循环依赖的关键



### 获取 Bean 实例

```java
    protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
      /*
       * 1. 通过 name 获取 beanName。
       * 这里不使用 name 直接作为 beanName 有两点原因：
       * 1）name 可能会以 & 字符开头，表明调用者想获取 FactoryBean 本身，而非 FactoryBean 
       *    实现类所创建的 bean。在 BeanFactory 中，FactoryBean 的实现类和其他的 bean 存储
       *    方式是一致的，即 <beanName, bean>，beanName 中是没有 & 这个字符的。所以我们需要
       *    将 name 的首字符 & 移除，这样才能从缓存里取到 FactoryBean 实例。
       * 2）若 name 是一个别名，则应将别名转换为具体的实例名，也就是 beanName。
       */
        String beanName = transformedBeanName(name);
        /**
         * 2. 依次从缓存singletonObjects、earlySingletonObjects、singletonFactories中获取单例 bean。
         * Bean 可能包含三种情况。
         * 1. singletonObjects 包含已经创建完成的 Bean
         * 2. earlySingletonObjects 包含正在创建的 Bean
         * 3. singletonFactories：ObjectFactory getBean方法创建 Bean，因为依赖注入的原因，spring 不等bean 创建完成，就将bean的ObjectFactory提前曝光，一旦其他 bean 需要依赖的时候，可以获取到。
         */
        Object sharedInstance = getSingleton(beanName);
        Object bean;
        /*
         * 
         * 如果 sharedInstance = null，则说明缓存里没有对应的实例，表明这个实例还没创建。
         * BeanFactory 并不会在一开始就将所有的单例 bean 实例化好，而是在调用 getBean 获取 
         * bean 时再实例化，也就是懒加载。
         */
        if (sharedInstance != null && args == null) {
            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, (RootBeanDefinition)null);
        } else {
            // beanName 不能是正在创建的 Prototype 类型的 Bean。
            if (this.isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }

            // 如果 sharedInstance = null，则到父容器中查找 bean 实例
            BeanFactory parentBeanFactory = getParentBeanFactory();
          
            //1. 如果 bean 没有在当前 factory 中定义，并且父容器不为空，那么从父容器查找对应的 Bean          
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                // 得到 name 对应的 beanName，如果为 FactoryBean，返回 &+beanName
                String nameToLookup = originalBeanName(name);
                if (parentBeanFactory instanceof AbstractBeanFactory) {
                    // 递归调用父 factory
                    return ((AbstractBeanFactory)parentBeanFactory).doGetBean(nameToLookup, requiredType, args, typeCheckOnly);
                } else if (args != null) {
                    return parentBeanFactory.getBean(nameToLookup, args);
                } else if (requiredType != null) {
                    return parentBeanFactory.getBean(nameToLookup, requiredType);
                } else {
                  	return parentBeanFactory.getBean(nameToLookup);
                }
            }

            // 从 mergedBeanDefinitions 中删除，添加到 alreadyCreated
            if (!typeCheckOnly) {
                markBeanAsCreated(beanName);
            }

            try {
                // 1. 合并父 BeanDefinition 与子 BeanDefinition
                final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                this.checkMergedBeanDefinition(mbd, beanName, args);
              
                // 2. 检查是否有 dependsOn 依赖，如果有则先初始化所依赖的 bean
                String[] dependsOn = mbd.getDependsOn();
                String[] var11;
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                      // 检测是否存在 depends-on 循环依赖。通过递归的方式实现。面试高频考点
                      if (isDependent(beanName, dep)) {
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                            "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                      }
                      //将 dep, beanName 加入
                      registerDependentBean(dep, beanName);
                      try {
                        //初始化依赖的 bean
                        getBean(dep);
                      }
                      catch (NoSuchBeanDefinitionException ex) {
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                            "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
                      }
                    }
                }
                /*
                 * 3.1 创建 scope 为 singleton 类型的 bean
                 * 3.1.1 从单例 bean 的缓存 singletonObjects 中查找 bean，如果找到，就返回。
                 * 3.1.2 如果没有找到，
                 * 3.1.2.1 beforeSingletonCreation hook
                 * 3.1.2.2 通过 createBean 调用对应的构造函数创建 Bean。
                 * 3.1.2.3 afterSingletonCreation
                 * 3.1.2.4 如果成功创建 Bean。将其加入缓存 singletonObjects，同时将 beanName 从 singletonFactories 和 earlySingletonObjects 中删除。
                 * 
                 */
                if (mbd.isSingleton()) {
                    sharedInstance = this.getSingleton(beanName, () -> {
                        try {
                            return this.createBean(beanName, mbd, args);
                        } catch (BeansException var5) {
                            this.destroySingleton(beanName);
                            throw var5;
                        }
                    });
                    
                    // 如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例，并缓存。否则直接返回 bean 实例
                    bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                }
                /*
                 * 3.2 创建 scope 为 prototype 类型的 bean
                 * 3.2.1 将 bean 加入 prototypesCurrentlyInCreation
                 * 3.2.2 创建 bean
                 * 3.2.3 将 bean 从 prototypesCurrentlyInCreation 中删除。
                 */
                else if (mbd.isPrototype()) {
                    var11 = null;

                    Object prototypeInstance;
                    try {
                        this.beforePrototypeCreation(beanName);
                        prototypeInstance = this.createBean(beanName, mbd, args);
                    } finally {
                        this.afterPrototypeCreation(beanName);
                    }
										// 如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例，并缓存。否则直接返回 bean 实例
                    bean = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                } else {
                   /** 
                     * 3.3 创建其他 scope 类型的 bean
                     * 3.2.1 将 bean 加入 prototypesCurrentlyInCreation
                     * 3.2.2 创建 bean
                     * 3.2.3 将 bean 从 prototypesCurrentlyInCreation 中删除。
                     */
                    String scopeName = mbd.getScope();
                    Scope scope = (Scope)this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }

                    try {
                        Object scopedInstance = scope.get(beanName, () -> {
                            this.beforePrototypeCreation(beanName);

                            Object var4;
                            try {
                                var4 = this.createBean(beanName, mbd, args);
                            } finally {
                                this.afterPrototypeCreation(beanName);
                            }

                            return var4;
                        });
                      
                        // 如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例，并缓存。否则直接返回 bean 实例
                        bean = this.getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    } catch (IllegalStateException var23) {
                        throw new BeanCreationException(beanName, "Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", var23);
                    }
                }
            } catch (BeansException var26) {
                this.cleanupAfterBeanCreationFailure(beanName);
                throw var26;
            }
        }

        // 如果需要，对 bean 进行类型转换，转为 requiredType
        if (requiredType != null && !requiredType.isInstance(bean)) {
            try {
                T convertedBean = this.getTypeConverter().convertIfNecessary(bean, requiredType);
                if (convertedBean == null) {
                    throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
                } else {
                    return convertedBean;
                }
            } catch (TypeMismatchException var25) {
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        } else {
            return bean;
        }
    }
```



总结

1. 通过 name 获取 beanName

2. 依次从缓存singletonObjects、earlySingletonObjects、singletonFactories中获取单例 bean，如果找到，继续步骤 3。如果没有找到，继续步骤 4

3. 如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例，并缓存。否则直接返回 bean 实例

4. 如果父容器不为空，调用父容器创建对应的 Bean  

5. 合并父 BeanDefinition 与子 BeanDefinition

6. 检查是否有 dependsOn 依赖，如果有则先初始化所依赖的 bean

7. 创建 scope 为 singleton 类型的 bean

   7.1 创建 scope 为 singleton 类型的 bean

   7.2 创建 scope 为 prototype 类型的 bean

   7.3 创建其他 scope 类型的 bean

8. 如果需要，对 bean 进行类型转换

   

整个 Bean 创建的核心在 createBean，由于 createBean 实现逻辑较为复杂，另起一篇。

### 附录

#### 获取 bean 名

```java
    protected String transformedBeanName(String name) {
        return this.canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }
    
    //如果 name 前面包含 &，去掉前面的 &。比如 &&&Hello 变为 Hello
    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        return !name.startsWith("&") ? name : (String)transformedBeanNameCache.computeIfAbsent(name, (beanName) -> {
            do {
                beanName = beanName.substring("&".length());
            } while(beanName.startsWith("&"));

            return beanName;
        });
    }
    
		//如果 name 为别名，找到实际的名称。
    public String canonicalName(String name) {
        String canonicalName = name;

        String resolvedName;
        do {
            /*
             * 使用 while 循环找到归一化名称
             * 原因是：可能会存在多重别名的问题，即别名指向别名。比如下面配置：
             *   <bean id="hello" class="service.Hello"/>
             *   <alias name="hello" alias="aliasA"/>
             *   <alias name="aliasA" alias="aliasB"/>
             * 上面的别名指向关系为 aliasB -> aliasA -> hello，对于上面的别名配置，aliasMap 中数据
             * 视图为：
             * aliasMap = [<aliasB, aliasA>, <aliasA, hello>]。通过下面的循环解析别名
             * aliasB 最终指向的 beanName
             */
            resolvedName = (String)this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        } while(resolvedName != null);

        return canonicalName;
    }
```



#### 获取单例 bean

```java
   @Nullable
    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, true);
    }

    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 先从 singletonObjects 获取对象
        Object singletonObject = this.singletonObjects.get(beanName);
        // 如果 singletonObject = null，表明还没完全创建好。
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
            synchronized(this.singletonObjects) {
                // 如果 beanName 正在创建，从 earlySingletonObjects 中获取
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    // 如果允许引用，从 singletonFactories 获取 beanName 对应的工厂类
                    ObjectFactory<?> singletonFactory = (ObjectFactory)this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 通过 getObject 创建对象
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }

        return singletonObject;
    }
```

其中

1. singletonObjects 保存已经实例化的 Bean 对象
2. earlySingletonObjects 保存正在创建的 Bean 对象，用于解决循环依赖
3. singletonFactory 保存创建 Bean 对象的工厂，通过 getObject 创建 Bean 对象。bean 工厂所产生的 bean 是还未完成初始化的 bean。

问题：singletonObjects、earlySingletonObjects 和 singletonFactory 的设计是为什么？



#### 合并父 BeanDefinition 与子 BeanDefinition



```java
   protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
     		// 如果 beanName 已经合并，直接返回
        RootBeanDefinition mbd = (RootBeanDefinition)this.mergedBeanDefinitions.get(beanName);
        return mbd != null ? mbd : this.getMergedBeanDefinition(beanName, this.getBeanDefinition(beanName));
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
        return this.getMergedBeanDefinition(beanName, bd, (BeanDefinition)null);
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd) throws BeanDefinitionStoreException {
        synchronized(this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;
            if (containingBd == null) {
                mbd = (RootBeanDefinition)this.mergedBeanDefinitions.get(beanName);
            }

            if (mbd == null) {
                // 没有 Parent 配置，当前为 RootBeanDefinition
                if (bd.getParentName() == null) {
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition)bd).cloneBeanDefinition();
                    } else {
                        mbd = new RootBeanDefinition(bd);
                    }
                } else {
                    
                    BeanDefinition pbd;
                    try {
                        // 找到父对象的 bean 名称，递归调用父 Bean 的 getMergedBeanDefinition
                        String parentBeanName = this.transformedBeanName(bd.getParentName());
                        if (!beanName.equals(parentBeanName)) {
                            pbd = this.getMergedBeanDefinition(parentBeanName);
                        } else {
                            BeanFactory parent = this.getParentBeanFactory();
                            if (!(parent instanceof ConfigurableBeanFactory)) {
                                throw new NoSuchBeanDefinitionException(parentBeanName, "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName + "': cannot be resolved without an AbstractBeanFactory parent");
                            }
														//递归找到父 BeanDefinition
                            pbd = ((ConfigurableBeanFactory)parent).getMergedBeanDefinition(parentBeanName);
                        }
                    } catch (NoSuchBeanDefinitionException var10) {
                        throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, "Could not resolve parent bean definition '" + bd.getParentName() + "'", var10);
                    }

                    mbd = new RootBeanDefinition(pbd);
                    // 用子 BeanDefinition 中的属性覆盖父 BeanDefinition 中的属性
                    mbd.overrideFrom(bd);
                }
              
                // 设置 bean 的 Scope
                if (!StringUtils.hasLength(mbd.getScope())) {
                    mbd.setScope("singleton");
                }

                //一个 bean 如果包含一个不是 singleton 的 bean，那么该 bean 本身不能是 singleton 的 Bean
                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }

                if (containingBd == null && this.isCacheBeanMetadata()) {
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }

            return mbd;
        }
    }

    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        String beanName = this.transformedBeanName(name);
        return (BeanDefinition)(!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.getParentBeanFactory()).getMergedBeanDefinition(beanName) : this.getMergedLocalBeanDefinition(beanName));
    }
```



####  获取 Bean 实例

如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例，并缓存。否则直接返回 bean 实例

```java
   protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
        // 以 & 开头，但是不是 FactoryBean，报错。
        if (BeanFactoryUtils.isFactoryDereference(name)) {
          if (beanInstance instanceof NullBean) {
            return beanInstance;
          }
          if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
          }
        }

        // 不是 FactoryBean
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
          return beanInstance;
        }

        Object object = null;
        if (mbd == null) {
          // 从缓存 factoryBeanObjectCache 中获取
          object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
          FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
          // 有 beanName 对应的 bean，但是 RootBeanDefinition 为空，获取 RootBeanDefinition
          if (mbd == null && containsBeanDefinition(beanName)) {
            mbd = getMergedLocalBeanDefinition(beanName);
          }
          boolean synthetic = (mbd != null && mbd.isSynthetic());
          // 从缓存 factoryBeanObjectCache 中获取，如有直接返回，如果没有，
          object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

   protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
     
        /**
         * FactoryBean 也有单例和非单例之分，针对不同类型的 FactoryBean，这里有两种处理方式：
         * 1. 单例 FactoryBean 生成的 bean 实例也认为是单例类型。需放入缓存中，供后续重复使用
         * 2. 非单例 FactoryBean 生成的 bean 实例则不会被放入缓存中，每次都会创建新的实例
         */
        if (factory.isSingleton() && this.containsSingleton(beanName)) {
            synchronized(this.getSingletonMutex()) {
                // 从缓存中获取
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    // factory.getObject() 生成 object
                    object = this.doGetObjectFromFactoryBean(factory, beanName);
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    } else {
                        if (shouldPostProcess) {
                            if (this.isSingletonCurrentlyInCreation(beanName)) {
                                return object;
                            }

                            this.beforeSingletonCreation(beanName);

                            try {
                                object = this.postProcessObjectFromFactoryBean(object, beanName);
                            } catch (Throwable var14) {
                                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", var14);
                            } finally {
                                this.afterSingletonCreation(beanName);
                            }
                        }
                        //单例的 bean，加入 factoryBeanObjectCache
                        if (this.containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }

                return object;
            }
        } else {
            Object object = this.doGetObjectFromFactoryBean(factory, beanName);
            if (shouldPostProcess) {
                try {
                    object = this.postProcessObjectFromFactoryBean(object, beanName);
                } catch (Throwable var17) {
                    throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", var17);
                }
            }

            return object;
        }
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
        Object object;
        try {
             object = factory.getObject();
        } catch (FactoryBeanNotInitializedException var7) {
            throw new BeanCurrentlyInCreationException(beanName, var7.toString());
        } catch (Throwable var8) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", var8);
        }

        if (object == null) {
            if (this.isSingletonCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
            }

            object = new NullBean();
        }

        return object;
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }
```



1. 检测参数 beanInstance 的类型，如果是非 FactoryBean 类型的 bean，直接返回
2. 检测 FactoryBean 实现类是否单例类型，针对单例和非单例类型进行不同处理
3. 对于单例 FactoryBean，先从缓存里获取 FactoryBean 生成的实例
4. 若缓存未命中，则调用 FactoryBean.getObject() 方法生成实例，并放入缓存中
5. 对于非单例的 FactoryBean，每次直接创建新的实例即可，无需缓存
6. 如果 shouldPostProcess = true，不管是单例还是非单例 FactoryBean 生成的实例，都要进行后置处理



#### 初始化单例的 Bean



```java
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");
		synchronized (this.singletonObjects) {
      //1. 查询缓存
			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null) {
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}
        //确保将 beanName 不在 singletonsCurrentlyInCreation，并且加入 singletonsCurrentlyInCreation 中
				beforeSingletonCreation(beanName);
				boolean newSingleton = false;
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}
				try {
          // 创建 bean，实际上是 createBean
					singletonObject = singletonFactory.getObject();
					newSingleton = true;
				}
				catch (IllegalStateException ex) {
					// Has the singleton object implicitly appeared in the meantime ->
					// if yes, proceed with it since the exception indicates that state.
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}
				catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
          // 确保将 beanName 不在 singletonsCurrentlyInCreation，并且从singletonsCurrentlyInCreation 中删除
					afterSingletonCreation(beanName);
				}
				if (newSingleton) {
          // 从中间状态的 singletonFactories 和 earlySingletonObjects 中删除，加入 singletonObjects 
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}
```

