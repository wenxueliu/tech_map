观察者模式



# 观察者模式

观察者模式，Observer Pattern也叫作发布订阅模式Publish/Subscribe。定义对象间一对多的依赖关系，使得每当一个对象改变状态，则所有依赖与它的对象都会得到通知，并被自动更新。

观察者模式的几角色名称：

- Subject被观察者，定义被观察者必须实现的职责，它能动态的增加取消观察者，它一般是抽象类或者是实现类，仅仅完成作为被观察者必须实现的职责：管理观察者并通知观察者。
- Observer观察者，观察者接受到消息后，即进行更新操作，对接收到的信息进行处理。
- ConcreteSubject具体的被观察者，定义被观察者自己的业务逻辑，同时定义对哪些事件进行通知。
- ConcreteObserver具体的观察者，每个观察者接收到消息后的处理反应是不同的，每个观察者都有自己的处理逻辑。

## 观察者模式的优点

- 观察者和被观察者之间是抽象耦合，不管是增加观察者还是被观察者都非常容易扩展。
- 建立一套触发机制。

## 观察者模式的缺点

观察者模式需要考虑开发效率和运行效率问题

1. 一个被观察者，多个观察者，开发和调试比较复杂
2. 一个观察者卡壳，会影响整体的执行效率，一般考虑异步的方式。



## 使用场景

- 关联行为场景，关联是可拆分的。
- 事件多级触发场景。
- 跨系统的消息交换场景，如消息队列的处理机制。



### 实例 tomcat

[来源](https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/util/LifecycleBase.java)



```java
public abstract class LifecycleBase implements Lifecycle {
  	private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }
    
    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycleListeners.toArray(new LifecycleListener[0]);
    }
    
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    protected void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }

}
```



#### 实例二

[来源](https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/core/StandardServer.java#L642)

```
public final class StandardServer extends LifecycleMBeanBase implements Server {

    
    
    final PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

        support.addPropertyChangeListener(listener);

    }


    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {

        support.removePropertyChangeListener(listener);

    }
```



实例三

[MapperListener](https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/mapper/MapperListener.java#L508)



