

```java
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalTest {
    public static void dump(String id, ThreadLocal<Integer> threadLocal1, ThreadLocal<String> threadLocal2, ThreadLocal<Map<String, String>> threadLocal3) {
        System.out.println("thread " + id + " threadlocal1 " + threadLocal1.get());
        System.out.println("thread " + id + " threadlocal2 " + threadLocal2.get());
        System.out.println("thread " + id + " threadlocal3 " + threadLocal3.get());
    }

    public static void testThreadLocal() {
        final ThreadLocal threadLocal1 = ThreadLocal.withInitial(() -> 0);
        final ThreadLocal<String> threadLocal2 = java.lang.ThreadLocal.withInitial(() -> "000");
        final ThreadLocal<Map<String, String>> threadLocal3 = java.lang.ThreadLocal.withInitial(HashMap::new);

        Thread t1 = new Thread(() -> {
            threadLocal1.set(10);
            threadLocal2.set("abc");
            dump("t1", threadLocal1, threadLocal2, threadLocal3);
        });

        Thread t2 = new Thread(() -> {
            threadLocal1.set(20);
            threadLocal2.set("ABC");
            dump("t1", threadLocal1, threadLocal2, threadLocal3);
        });

        Thread t3 = new Thread(() -> {
            threadLocal1.set(30);
            Map<String, String> val = new HashMap<>();
            val.put("123", "XXX");
            threadLocal3.set(val);
            dump("t1", threadLocal1, threadLocal2, threadLocal3);
        });

        t1.start();
        t2.start();
        t3.start();
    }

    public static void main(String[] args) {
        testThreadLocal();
    }
}
```

输出

```
thread t2 threadlocal1 20
thread t2 threadlocal2 ABC
thread t1 threadlocal1 10
thread t3 threadlocal1 30
thread t3 threadlocal2 000
thread t1 threadlocal2 abc
thread t2 threadlocal3 {}
thread t1 threadlocal3 {}
thread t3 threadlocal3 {123=XXX}
```

![threadlocal1](https://img-blog.csdnimg.cn/1386f29b89ae458ca61565e6dffdfa15.png#pic_center)


### 内存模型

![threadlocal 内存分布](https://img-blog.csdnimg.cn/c1ffda2dd9d84b5f87fd296cb812ca10.png#pic_center)

#### Thread

```java
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```
#### ThreadLocalMap

```java
    static class ThreadLocalMap {
        static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;
            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
        private static final int INITIAL_CAPACITY = 16;
        private Entry[] table;
        private int size = 0;
        private int threshold;
        
        ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY];
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }
```
#### ThreadLocal

```java
public class ThreadLocal<T> {
    /**
     * ThreadLocals rely on per-thread linear-probe hash maps attached
     * to each thread (Thread.threadLocals and
     * inheritableThreadLocals).  The ThreadLocal objects act as keys,
     * searched via threadLocalHashCode.  This is a custom hash code
     * (useful only within ThreadLocalMaps) that eliminates collisions
     * in the common case where consecutively constructed ThreadLocals
     * are used by the same threads, while remaining well-behaved in
     * less common cases.
     */
    private final int threadLocalHashCode = nextHashCode();

    private static AtomicInteger nextHashCode = new AtomicInteger();

    /**
     * The difference between successively generated hash codes - turns
     * implicit sequential thread-local IDs into near-optimally spread
     * multiplicative hash values for power-of-two-sized tables.
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }
```


### 基本操作

* get()
* set()
* remove()

### 关键设计点
#### 初始大小
初始大小为 16
#### hash 算法
HASH_INCREMENT 也不是随便取的，它转化为十进制是 1640531527，2654435769 转换成 int 类型就是 -1640531527，2654435769 等于 (√5-1)/2 乘以 2 的 32 次方。(√5-1)/2 就是黄金分割数，近似为 0.618，也就是说 0x61c88647 理解为一个黄金分割数乘以 2 的 32 次方，它可以保证 nextHashCode 生成的哈希值，均匀的分布在 2 的幂次方上，且小于 2 的 32 次方。

0x61c88647 是一个“魔数”，即一个特殊的常量，这个特定的数值被选定的原因在于它是黄金分割比（Golden Ratio）的近似整数表示。

这个常数的二进制表示形式是：0110 0001 1100 1000 1000 0110 0100 0111，可以看到，它是一个31位的数，最高位为0，这保证了当它用作增量时，计算出的 hash 值总是正数。

那么，为什么要选择黄金分割比的近似表示呢？这是因为黄金分割比有一个特性：它可以帮助实现更好的散列分布。当我们用这个数值作为增量时，即使原始的 hash 值存在规律（例如，都是偶数），经过增量计算后的 hash 值也将尽可能均匀地分布在整个 hash 表中。

此外，选择这个特定的数也是因为它与 2^32 的除数接近黄金分割比（0.6180339887），可以为 hash 值提供良好的分布。

在 ThreadLocal 类中，每个线程都有一个唯一的线程局部变量，并且每个线程局部变量都需要一个唯一的 hash 值。通过将这个“魔数”作为 hash 值的增量，可以确保每个线程局部变量的 hash 值都是独一无二的，同时也可以尽可能地减少 hash 冲突。

```java
public class HashTest {
    private static final int HASH_INCREMENT = 0x61c88647;

    public static void dumpHashElements() {
        int n = 4;
        int max = 2 << (n - 1);
        for (int i = 0; i < max; i++) {
            System.out.print(i * HASH_INCREMENT & (max - 1));
            System.out.print(" ");

        }
    }

    public static void main(String[] args) {
        dumpHashElements();
    }
}
```

#### hash 冲突解决

采用线性探测法

```
        private Entry getEntry(ThreadLocal<?> key) {
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)
                return e;
            else
                return getEntryAfterMiss(key, i, e);
        }

        private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                ThreadLocal<?> k = e.get();
                if (k == key)
                    return e;
                if (k == null)
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }
  
        // 线性探测法
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }
```

ThreadLocalMap 其实就是一个简单的 Map 结构，底层是数组，有初始化大小，也有扩容阈值大小，数组的元素是 Entry，「Entry 的 key 就是 ThreadLocal 的引用，value 是 ThreadLocal 的值」。ThreadLocalMap 解决 hash 冲突的方式采用的是「线性探测法」，如果发生冲突会继续寻找下一个空的位置。

#### 扩容条件是什么？如何扩容？

1、容量达到阈值的 3/4
2、每次扩容为当前的 2 倍

```java
        private void rehash() {
            expungeStaleEntries();

            // 容量大于等于 3/4 时触发扩容
            if (size >= threshold - threshold / 4)
                resize();
        }

        /**
         * Double the capacity of the table.
         */
        private void resize() {
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            // 容量为之前的 2 倍
            int newLen = oldLen * 2;
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            for (int j = 0; j < oldLen; ++j) {
                Entry e = oldTab[j];
                if (e != null) {
                    ThreadLocal<?> k = e.get();
                    if (k == null) {
                        e.value = null; // Help the GC
                    } else {
                        int h = k.threadLocalHashCode & (newLen - 1);
                        while (newTab[h] != null)
                            h = nextIndex(h, newLen);
                        newTab[h] = e;
                        count++;
                    }
                }
            }

            setThreshold(newLen);
            size = count;
            table = newTab;
        }
```


### 应用场景
条件：每个线程 threadlocal 数量笔记少

1、全链路追踪中的 traceId 或者流程引擎中上下文的传递一般采用 ThreadLocal
2、Spring 事务管理器采用了 ThreadLocal
3、Spring MVC 的 RequestContextHolder 的实现使用了 ThreadLocal
### 疑问
1、ThreadLocalMap 的 Entry 为什么继承自 WeakReference？
2、每个线程 threadlocal 的数量很多会有什么问题？如何处理？ 速度变慢（线性探测法，数据清理）
3、如果当前线程创建子线程，当前线程的 ThreadLocal 数据可以传递给子线程的 ThreadLocal 么？为什么？
4、thread 如果退出，threadlocalMap 可以回收么？
5、thread 长时间运行（比如线程池），threadlocalMap 中的值没有引用了，如何处理，不处理会有什么问题？
6、theadlocal 中 Entry 中为什么需要 key 为 threadlocal？
### 内存泄漏

ThreadLocalMap 的 Entry 为什么继承自 WeakReference？

假设为强引用，threadlocal 不再使用，但是 TheadLocalMap 到 Entry，Entry 到 key 的强引用一直存在，那么，就导致内存泄漏。

我们应用中，线程常常是以线程池的方式来使用的，比如 Tomcat 的线程池处理了一堆请求，而线程池中的线程一般是不会被清理掉的，所以这个引用链就会一直在，那么 ThreadLocal 对象即使没有用了，也会随着线程的存在，而一直存在着！
![threadlocal weakreference](https://img-blog.csdnimg.cn/0516be6dcb1e4fd98a3e645531ee9d26.png#pic_center)
```java
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }
```


### FastThreadLocal
![fastthreadlocal](https://img-blog.csdnimg.cn/c8a0e40767ed4ae9855250674d6c0f00.png#pic_center)

1. 每个 FastThread 包含一个 FastThreadLocalMap，每个 FastThreadLocalThread 中的多个 FastThreadLocal 占用不同的索引。
2. 每个 InternalThreadLocalMap 的第一个元素保存了所有的 ThreadLocal 对象。之后的元素保存了每个 ThreadLocal 对应的 value
### 基本操作

#### get()
当前线程中
1. 找到 ThreadLocal 的  index，value 加入 InternalThreadLocalMap 对应索引的位置
2. 
####  set()
当前线程中
 1. 找到 ThreadLocal 的  index，value 加入 InternalThreadLocalMap 对应索引的位置
 2. 将 ThreadLocal 加入当前线程 InternalThreadLocalMap  的第一个元素对应的集合
 
#### remove()
当前线程中
1. 找到 ThreadLocal 的  index，InternalThreadLocalMap 中 index 对应的 value 置为 UNSET
2. 将 ThreadLocal 从当前线程 InternalThreadLocalMap 的第一个元素集合中删除
### 关键设计点

#### 兼容 ThreadLocal

当线程没有使用 FastThreadLocal 的时候，默认走 ThreadLocal 的逻辑。
#### 初始大小
初始大小为 32
#### hash 算法

直接使用全局的自增，不存在Hash 冲突，以空间换时间
#### 扩容条件是什么？如何扩容？
* 扩容条件：当前线程元素超出容量
* 扩容：元素数量扩展为向上取最近 2 次幂整数，比如，5 取8，31 取 64。

```java
    public boolean setIndexedVariable(int index, Object value) {
        Object[] lookup = indexedVariables;
        // index 大于容量
        if (index < lookup.length) {
            Object oldValue = lookup[index];
            lookup[index] = value;
            return oldValue == UNSET;
        } else {
            // 扩容
            expandIndexedVariableTableAndSet(index, value);
            return true;
        }
    }
    
    private void expandIndexedVariableTableAndSet(int index, Object value) {
        Object[] oldArray = indexedVariables;
        final int oldCapacity = oldArray.length;
        int newCapacity;
        // 当小于 2的30次方时，容量扩展为向上取最近 2 次幂整数，比如，5 取8，31 取 64。
        if (index < ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD) {
            newCapacity = index;
            newCapacity |= newCapacity >>>  1;
            newCapacity |= newCapacity >>>  2;
            newCapacity |= newCapacity >>>  4;
            newCapacity |= newCapacity >>>  8;
            newCapacity |= newCapacity >>> 16;
            newCapacity ++;
        } else {
            newCapacity = ARRAY_LIST_CAPACITY_MAX_SIZE;
        }

        Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
        Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
        newArray[index] = value;
        indexedVariables = newArray;
    }
```

#### 如何防止内存泄漏

使用  

```java
final class FastThreadLocalRunnable implements Runnable {
    private final Runnable runnable;

    private FastThreadLocalRunnable(Runnable runnable) {
        this.runnable = (Runnable)ObjectUtil.checkNotNull(runnable, "runnable");
    }

    public void run() {
        try {
            this.runnable.run();
        } finally {
            // 如果用的是 FastThreadLocalRunnable ，默认会做清理
            FastThreadLocal.removeAll();
        }

    }

    static Runnable wrap(Runnable runnable) {
        return (Runnable)(runnable instanceof FastThreadLocalRunnable ? runnable : new FastThreadLocalRunnable(runnable));
    }
}

```

### 存在什么问题

1、空间浪费，所有线程的 ThreadLocalMap 数组大小是一样的
比如，线程1 创建 100 个 ThreadLocal 对象。线程 1 里面有一个长度为 100 的数组。
此时，第二个线程需要调用 ThreadLocal 100 的 get 方法，第二个线程需要分配 100 个 Object 对象。

```java
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.ArrayList;
import java.util.List;

public class FastThreadLocalTest {

    private List<FastThreadLocal<String>> fastThreadLocals = new ArrayList<>();

    private List<ThreadLocal<String>> threadLocals = new ArrayList<>();

    void thread1Init() {
        new Thread (() -> {
            for (int i = 0; i < 31; i++) {
                ThreadLocal<String> threadLocal = new ThreadLocal<>();
                threadLocal.get();
                threadLocals.add(threadLocal);
            }
        }).start();

    }

    void thread2Init() {
        new Thread(() -> {
            threadLocals.get(threadLocals.size() - 1).get();
        });
    }

    void fastThread1Init() {
        new FastThreadLocalThread (() -> {
            for (int i = 0; i < 33; i++) {
                FastThreadLocal<String> fastThreadLocal = new FastThreadLocal<>();
                fastThreadLocal.get();
                fastThreadLocals.add(fastThreadLocal);
            }
        }).start();

    }

    void fastThread2Init() {
        new FastThreadLocalThread(() -> {
            fastThreadLocals.get(fastThreadLocals.size() - 1).get();
        });
    }

    public static void main(String[] args) {
        FastThreadLocalTest test = new FastThreadLocalTest();
        test.fastThread1Init();
        test.fastThread2Init();

        test.thread1Init();
        test.thread2Init();
    }
}
```

2、FastThreadLocal 需要配套 FastThreadLocalThread 使用，不然还不如原生 ThreadLocal。
3、FastThreadLocal 使用最好配套 FastThreadLocalRunnable，这样执行完任务后会主动调用 removeAll 来移除所有 
### 性能压测 

[netty 官方 mincrobench](https://github.com/netty/netty/tree/2822dfdba3651cfda433dcb1842b98f5580b05d5/microbench/src/main/java/io/netty/microbench/concurrent)

### InheritableThreadLocal

```java
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
    
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
    }
```

1、变量的传递是发生在线程创建的时候，如果不是新建线程，而是用了线程池里的线程，就不灵了
2、变量的赋值就是从主线程的map复制到子线程，它们的value是同一个对象，如果这个对象本身不是线程安全的，那么就会有线程安全问题

### TransmittableThreadLocal

如何往线程池内的线程传递 ThreadLocal

ThreadLocal的需求场景即TransmittableThreadLocal的潜在需求场景，如果你的业务需要『在使用线程池等会池化复用线程的执行组件情况下传递ThreadLocal值』则是TransmittableThreadLocal目标场景。

下面是几个典型场景例子。
* 分布式跟踪系统 或 全链路压测（即链路打标）
* 日志收集记录系统上下文
* Session级Cache
* 应用容器或上层框架跨应用代码给下层SDK传递信息

```java

```
### 最后

赶紧打开你们项目的代码，看看 ThreadLocal 的使用是不是有问题
