java-jdk-concurrenthashmap8



### 设计目标

1. 维护并发的可读性，最小化更新操作的竞争
2. 空间消耗与 HashMap 相当或更好
3. 支持初始化时多线程并发高速插入

功能属性

1. 支持 Map 的接口

非功能属性

2. 高并发读
3. 空间消耗与 HashMap 相当或更好

### 核心要素

初始化容量

初始容量都为2 的 N 次方。 原因参考附录



```java
final int MAXIMUM_CAPACITY = 1 << 30;

final int DEFAULT_CAPACITY = 16;

final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

final int DEFAULT_CONCURRENCY_LEVEL = 16;

final float LOAD_FACTOR = 0.75f;

final int TREEIFY_THRESHOLD = 8;

final int UNTREEIFY_THRESHOLD = 6;

final int MIN_TREEIFY_CAPACITY = 64;

final int MIN_TRANSFER_STRIDE = 16;

final static int RESIZE_STAMP_BITS = 16;

final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

final int MOVED     = -1;

final int TREEBIN   = -2;

final int RESERVED  = -3;

final int HASH_BITS = 0x7fffffff;

// 用来控制表初始化和扩容的，默认值为0，当在初始化的时候指定了大小，将容量保存在sizeCtl中。
// tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1))
// tableSizeFor((long)(1.0 + (long)initialCapacity / loadFactor))
// -1表示初始化,-(1+n) n:表示活动的扩张线程
volatile int sizeCtl;

// 从 table.length 开始，递减
volatile int transferIndex;

volatile int cellsBusy;

volatile CounterCell[] counterCells;

KeySetView<K,V> keySet;

ValuesView<K,V> values;

EntrySetView<K,V> entrySet;

transient volatile Node<K,V>[] table;

// 扩容的时候为 table 的 2 倍
private transient volatile Node<K,V>[] nextTable;

```





Node

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K,V> next;

    Node(int hash, K key, V val, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.val = val;
        this.next = next;
    }

    public final K getKey()       { return key; }
    public final V getValue()     { return val; }
    public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
    public final String toString(){ return key + "=" + val; }
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public final boolean equals(Object o) {
        Object k, v, u; Map.Entry<?,?> e;
        return ((o instanceof Map.Entry) &&
                (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                (v = e.getValue()) != null &&
                (k == key || k.equals(key)) &&
                (v == (u = val) || v.equals(u)));
    }

    /**
     * Virtualized support for map.get(); overridden in subclasses.
     */
    // 遍历列表，直到找到匹配的第一个元素或者没有找到，返回空
    Node<K,V> find(int h, Object k) {
        Node<K,V> e = this;
        if (k != null) {
            do {
                K ek;
                if (e.hash == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    return e;
            } while ((e = e.next) != null);
        }
        return null;
    }
}
```



TreeNodes



ForwardingNodes

```java
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
    ForwardingNode(Node<K,V>[] tab) {
        super(MOVED, null, null, null);
        this.nextTable = tab;
    }

    Node<K,V> find(int h, Object k) {
        // loop to avoid arbitrarily deep recursion on forwarding nodes
        outer: for (Node<K,V>[] tab = nextTable;;) {

            Node<K,V> e; int n;
            // 判空
            if (k == null || tab == null || (n = tab.length) == 0 ||
                    (e = tabAt(tab, (n - 1) & h)) == null)
                return null;
            //
            for (;;) {
                int eh; K ek;
                // 判断当前 bucket 的某个元素与 h,k 是否匹配
                if ((eh = e.hash) == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    return e;
                // TODO eh < 0 表明啥？
                if (eh < 0) {
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode<K,V>)e).nextTable;
                        continue outer;
                    }
                    else
                        // e 为某个 bucket 的首元素
                        // 依次遍历 e 中的元素。e 为链表或红黑树。但都有关 next 指针
                        return e.find(h, k);
                }
                if ((e = e.next) == null)
                    return null;
            }
        }
    }
}
```

ForwardingNode 主要为扩容使用
1、ForwardingNode类继承了Node类，所以ForwardingNode对象也是Node类型对象
2、ForwardingNode在扩容中使用。每一个ForwardingNode对象都包含扩容后的表的引用（新表保存在nextTable属性中）。
ForwardingNode对象的key，value，next属性值全部为null，它的hash值为-1
3、ForwardingNode对象中也定义了find的方法,它是从扩容后的新表中查询结点，而不是以自身为头结点进行查找。
4、在每个位置扩容时，会对头结点加锁，避免其它线程在该位置进行put及remove操作，这个位置扩容结束时会将头结点设置成ForwardingNode，然后释放锁。


ReservationNodes



TableStack

To ensure that no intervening nodes are skipped even when moved out of order,  a stack (see class TableStack) is created on first encounter of a forwarding node during a traversal, to maintain its place if later processing the current table. 



### 初始化

1. 初始化会用传参除以负载因子，这点不同于 HashMap。这样设置初始化容量的时候，不需要自己手动除以负载因子

```java
public ConcurrentHashMap(int initialCapacity,
                         float loadFactor, int concurrencyLevel) {
    if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    if (initialCapacity < concurrencyLevel)   // Use at least as many bins
        initialCapacity = concurrencyLevel;   // as estimated threads
    long size = (long)(1.0 + (long)initialCapacity / loadFactor);
    int cap = (size >= (long)MAXIMUM_CAPACITY) ?
        MAXIMUM_CAPACITY : tableSizeFor((int)size);
    // 注意 sizeCtl 的值的计算方式
    this.sizeCtl = cap;
}

   private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            // 如果有其他线程在初始化，自旋
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                // 设置 sizeCtl 为 -1，成功，当前线程初始化 table
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        // 设置为容量的 0.75
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 此时 sizeCtl 为原来的 0.75
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```

初始化
1、如果有其他线程在初始化，自旋
2、初始化 table，容量为 cap 或 DEFAULT_CAPACITY(16)


### Hash 函数

```java
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
```

1. 高 16 位参与不到 hash 计算，因此将高 16 位放到低 16 位
2. 本身 hash 函数已经足够离散
3. 即使冲突高，也有红黑树兜底
4. 元素容量是 2 的 n 次方
5. 鉴于以上原因，hash 函数的重要性不是那么关键了，

### get

```java
    public V get(Object key) {
        Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (e = tabAt(tab, (n - 1) & h)) != null) {
            // 检查当前 bucket 第一个元素是否满足
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            }
            // 红黑树
            else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null;
            // 链表
            while ((e = e.next) != null) {
                if (e.hash == h &&
                    ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
```

可以分成两种情况讨论
1）该位置的头结点是Node类型对象，直接get，即使这个桶正在进行迁移，在get方法未完成前，迁移操作已完成，
即槽被设置成了ForwordingNode对象，也没关系，并不影响get的结果。因为get线程仍然持有旧链表的引用，
可以从当前结点位置访问到所有的后续结点。这是因为新表中的节点是通过复制旧表中的结点得到的，所以新表的
结点的next值不会影响旧表中对应结点的next值。当get方法结束后，旧链表就出于不可达的状态，会被垃圾回收线程回收。
2）该位置的头结点是ForwordingNode类型对象（头结点的hash值 ==
-1），头结点是ForwordingNode类型的对象，调用该对象的find方法，在新表中查找。
所以无论哪种情况，都能get到正确的值。


### containsValue

借助于 Traverser

```java
public boolean containsValue(Object value) {
    if (value == null)
        throw new NullPointerException();
    Node<K,V>[] t;
    if ((t = table) != null) {
        Traverser<K,V> it = new Traverser<K,V>(t, t.length, 0, t.length);
        for (Node<K,V> p; (p = it.advance()) != null; ) {
            V v;
            if ((v = p.val) == value || (v != null && value.equals(v)))
                return true;
        }
    }
    return false;
}
```



TODO



### put

```java
public V put(K key, V value) {
    return putVal(key, value, false);
}


final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        // 1. 初始化
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        // 2. 如果为空，就创建一个 Node，设置为该 bucket 的首节点
        // 每次循环都会重新计算槽的位置，因为可能刚好完成扩容操作
        // 扩容完成后会使用新表，槽的位置可能会发生改变
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // 槽为空，先尝试用CAS方式进行添加结点
            if (casTabAt(tab, i, null,
                    new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin
        }
        // 3. 第一个元素的 hash 为 -1，帮助 rehash
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        // 4.
        else {
            V oldVal = null;
            // 加锁操作，防止其它线程对此桶同时进行put,remove,transfer操作
            synchronized (f) {
                // 第一个元素为 f。多个线程进入锁，只有第一个线程为 true，其他线程都为 false
                //头结点发生改变，就说明当前链表（或红黑树）的头节点已不是f了
                //可能被前面的线程remove掉了或者迁移到新表上了
                //如果被remove掉了，需要重新对链表新的头节点加锁
                if (tabAt(tab, i) == f) {
                    // 如果是链表：第一个元素的 hash 值大于等于 0
                    if (fh >= 0) {
                        // 增加元素，所以为 1
                        binCount = 1;
                        // 遍历当前 bucket，如果找到，替换；如果没有找到，加到尾部。
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            // hash 和 key 匹配，更新值
                            if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                            (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            // 没有在当前 bucket，创建一个新的节点。
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key,
                                        value, null);
                                break;
                            }
                        }
                    }
                    // 如果为红黑树，加入 红黑树（红黑树的根结点的hash值为-2）
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                // 超过阈值，树化
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    // 判断是否需要扩容操作。如果容量超过阀值了，就由这个线程发起扩容操作。如果已经处于扩容状态（sizeCtl -1），
    // 根据剩余迁移的数据和已参加到扩容中的线程数来判断是否需要当前线程来帮助扩容。>
    addCount(1L, binCount);
    return null;
}

static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                    Node<K,V> c, Node<K,V> v) {
    return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
}

@SuppressWarnings("unchecked")
static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
    return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
}


// tab 为 table, f 为 table 的某个 bucket 的首元素
final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
    Node<K,V>[] nextTab; int sc;
    if (tab != null && (f instanceof ForwardingNode) &&
            (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
        int rs = resizeStamp(tab.length);
        while (nextTab == nextTable && table == tab &&
                (sc = sizeCtl) < 0) {
            // TODO
            if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                    sc == rs + MAX_RESIZERS || transferIndex <= 0)
                break;
            // 每增加一个线程，sc 加 1
            if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                transfer(tab, nextTab);
                break;
            }
        }
        return nextTab;
    }
    return table;
}

// 2^15(32768) + n前面的0的位数
static final int resizeStamp(int n) {
    return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
}
```

put 包括如下情况：
1、首次加入元素
2、元素为当前槽位的第一个元素，CAS 增加元素
3、正在扩容，扩容完之后，增加元素
4、除以上条件之外，加锁，将元素加入当前槽



### 扩容

1、扩容为原来的 2 倍
2、不同槽的扩容可以使用不同的线程，避免加锁，多线程扩容加快扩容的速度
4、从旧表的最后一个索引，一批一批地迁移，每批迁移 stride 个槽位，每批用一个线程
5、

```java
private final void treeifyBin(Node<K,V>[] tab, int index) {
    Node<K,V> b; int n, sc;
    if (tab != null) {
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            tryPresize(n << 1);
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) {
                // 双重检测
                if (tabAt(tab, index) == b) {
                    // hd 为 head，tl 为 tail 的简称，从命名的角度来说，jdk 的确不咋样，多两个字母能提升可读性，难道会对性能产生影响？
                    TreeNode<K,V> hd = null, tl = null;
                    // 将 Node 转为 TreeNode，并用链表将所有元素串起来
                    for (Node<K,V> e = b; e != null; e = e.next) {
                        TreeNode<K,V> p =
                                new TreeNode<K,V>(e.hash, e.key, e.val,
                                        null, null);
                        if ((p.prev = tl) == null)
                            hd = p;
                        else
                            tl.next = p;
                        tl = p;
                    }
                    // 构造红黑树，并设置 tab[index] 为红黑树
                    setTabAt(tab, index, new TreeBin<K,V>(hd));
                }
            }
        }
    }
}


private final void tryPresize(int size) {
    int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
            tableSizeFor(size + (size >>> 1) + 1);
    int sc;
    while ((sc = sizeCtl) >= 0) {
        Node<K,V>[] tab = table; int n;
        if (tab == null || (n = tab.length) == 0) {
            n = (sc > c) ? sc : c;
            // 初始化
            if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    // 双重检测
                    if (table == tab) {
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = nt;
                        // 原来容量的 0.75
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
            }
        }
        else if (c <= sc || n >= MAXIMUM_CAPACITY)
            break;
        else if (tab == table) {
            int rs = resizeStamp(n);
            if (sc < 0) {
                Node<K,V>[] nt;
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                    transfer(tab, nt);
            }
            else if (U.compareAndSwapInt(this, SIZECTL, sc,
                    (rs << RESIZE_STAMP_SHIFT) + 2))
                transfer(tab, null);
        }
    }
}

static final int resizeStamp(int n) {
    return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
}

private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride;
    // 1. 对任务进行分片。stride 最小为 MIN_TRANSFER_STRIDE。
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
    // 2. 第一个线程条件成立，其他线程，条件都不成立
    if (nextTab == null) {            // initiating
        try {
            @SuppressWarnings("unchecked")
            // 两倍容量
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
            nextTab = nt;
        } catch (Throwable ex) {      // try to cope with OOME
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;
        transferIndex = n;
    }
    int nextn = nextTab.length;
    ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
    boolean advance = true;
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) {
        Node<K,V> f; int fh;
        while (advance) {
            int nextIndex, nextBound;
            // --i >= bound 说明 i, bound 被初始化
            if (--i >= bound || finishing)
                advance = false;
            // transferIndex 每次减少 stride，transferIndex <= 0 的时候，停止循环
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }
            // 获取当前线程分片范围，并将 transferIndex 减少 stride
            else if (U.compareAndSwapInt
                    (this, TRANSFERINDEX, nextIndex,
                            nextBound = (nextIndex > stride ?
                                    nextIndex - stride : 0))) {
                // 某次循环的开始索引
                bound = nextBound;
                // 某次循环的最后索引
                i = nextIndex - 1;
                advance = false;
            }
            // 不满足上述三种情况的，继续循环
        }
        // 
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            // 收尾工作，只有最后一个线程执行
            if (finishing) {
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1);
                return;
            }
            // 对 sc 减一
            if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }
        else if ((f = tabAt(tab, i)) == null)
            // 设置 fwd 为 tab[i] 的首元素
            advance = casTabAt(tab, i, null, fwd);
        else if ((fh = f.hash) == MOVED) //正在初始化，TODO 啥时候设置的？
            advance = true; // already processed
        else {
            synchronized (f) {
                // 双重检测
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn;
                    if (fh >= 0) {
                        // n 为 table.length
                        int runBit = fh & n;
                        Node<K,V> lastRun = f;
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }
                        // 将 f 拆成两部分， ln，低维给 hn
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0)
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn);
                        }
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                    else if (f instanceof TreeBin) {
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> lo = null, loTail = null;
                        TreeNode<K,V> hi = null, hiTail = null;
                        int lc = 0, hc = 0;
                        // 将 f 对应的树分为两部分，高位部分，低位部分。
                        for (Node<K,V> e = t.first; e != null; e = e.next) {
                            int h = e.hash;
                            TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                if ((p.prev = loTail) == null)
                                    lo = p;
                                else
                                    loTail.next = p;
                                loTail = p;
                                ++lc;
                            }
                            else {
                                if ((p.prev = hiTail) == null)
                                    hi = p;
                                else
                                    hiTail.next = p;
                                hiTail = p;
                                ++hc;
                            }
                        }
                        // 检查低位部分是否需要去掉树
                        ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                        // 检查高位部分是否需要去掉树
                        hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                        // nextTab[i] = ln
                        setTabAt(nextTab, i, ln);
                        // nextTab[i + n] = hn
                        setTabAt(nextTab, i + n, hn);
                        // tab[i] = fwd
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }
}
```



### remove

```java
final V replaceNode(Object key, V value, Object cv) {
    int hash = spread(key.hashCode());
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        // 空表
        if (tab == null || (n = tab.length) == 0 ||
                (f = tabAt(tab, i = (n - 1) & hash)) == null)
            break;
        // 正在扩容，那么，下一个循环再处理
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            boolean validated = false;
            // 
            synchronized (f) {
                // 没有被其他线程修改
                if (tabAt(tab, i) == f) {
                    // 链表
                    if (fh >= 0) {
                        validated = true;
                        for (Node<K,V> e = f, pred = null;;) {
                            K ek;
                            if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                            (ek != null && key.equals(ek)))) {
                                V ev = e.val;
                                if (cv == null || cv == ev ||
                                        (ev != null && cv.equals(ev))) {
                                    oldVal = ev;
                                    if (value != null)
                                        e.val = value;
                                    else if (pred != null)
                                        pred.next = e.next;
                                    else
                                        setTabAt(tab, i, e.next);
                                }
                                break;
                            }
                            pred = e;
                            if ((e = e.next) == null)
                                break;
                        }
                    }
                    // 红黑树
                    else if (f instanceof TreeBin) {
                        validated = true;
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> r, p;
                        if ((r = t.root) != null &&
                                (p = r.findTreeNode(hash, key, null)) != null) {
                            V pv = p.val;
                            if (cv == null || cv == pv ||
                                    (pv != null && cv.equals(pv))) {
                                oldVal = pv;
                                if (value != null)
                                    p.val = value;
                                else if (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
            }
            if (validated) {
                if (oldVal != null) {
                    if (value == null)
                        addCount(-1L, -1);
                    return oldVal;
                }
                break;
            }
        }
    }
    return null;
}
```





### Size



```
// LongAddr
final long sumCount() {
    CounterCell[] as = counterCells; CounterCell a;
    long sum = baseCount;
    if (as != null) {
        for (int i = 0; i < as.length; ++i) {
            if ((a = as[i]) != null)
                sum += a.value;
        }
    }
    return sum;
}

/**
* A padded cell for distributing counts.  Adapted from LongAdder
* and Striped64.  See their internal docs for explanation.
*/
@sun.misc.Contended static final class CounterCell {
		volatile long value;
		CounterCell(long x) { value = x; }
}
```







### Java 1.7 vs 1.8



1. 使用红黑树
2. 1.7及以前，链表采用的是头插法，1.8后改成了尾插法；
3. Segment+ReentrantLock改成了CAS+synchronized

https://mp.weixin.qq.com/s?src=11&timestamp=1587309981&ver=2288&signature=GxpKHk1aAZjxpi*u3z-KkhwInEfmPedmRL6IXg0aiTOe6cN*296UYt2oTjACYCRPmGjj8gKjqSCPaQjl7Ui6okjVe7JIH*fpJKo5ISOmqJ4GU1HLvt3QAPSh7F0G3133&new=1

### 问题

**1. sizeCtrl 的作用？**

1.
2. -1 表明正在初始化，初始化化完成之后变为原理的 0.75
3. -(1 + resize线程数)：表明正在 resize

**2. 当前线程如何感知其他线程也在参与迁移工作？**



**3. 任务按照何规则进行分片？**



**4. 如何记录目前已经分出去的任务？**

transferIndex

**5.get 为什么不需要加锁**



**6. concurrentHashMap 会死循环么？如果会请模拟，并分析原理**

复现条件：

1. computeIfAbsent key 对应的 bucket， 第一次被放入 map
2. 在 mappingFunction 函数中，存在 put 操作，put 操作的 key 与 computeIfAbsent 的key 的 bucket 相同

情况一

```java
ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
map.computeIfAbsent(12, (k) -> {
    map.put(k, k);
    return k;
});

System.out.println(map);
```

情况二

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>(16);
map.computeIfAbsent(
    "AAAA",
    v ->  map.computeIfAbsent("AAAA", v2 -> 1)
);
```

核心原因是在 computeIfAbsent 中没有判断 ReservationNode 类型的情况

```java
public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    if (key == null || mappingFunction == null)
        throw new NullPointerException();
    int h = spread(key.hashCode());
    V val = null;
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        // 第一次执行到这里
        else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
            Node<K,V> r = new ReservationNode<K,V>();
            synchronized (r) {
                if (casTabAt(tab, i, null, r)) {
                    binCount = 1;
                    Node<K,V> node = null;
                    try {
                        if ((val = mappingFunction.apply(key)) != null)
                            node = new Node<K,V>(h, key, val, null);
                    } finally {
                        setTabAt(tab, i, node);
                    }
                }
            }
            if (binCount != 0)
                break;
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            // 第二次之后执行这里，由于 mappingFunction 中也存在 put 操作，并且 key 与当前函数参数 key 的 hashCode 相同，在 table 中的索引相同
            boolean added = false;
            // 
            synchronized (f) {
                // ReservationNode 的 f 置为 -3，下面条件都不满足，所以一直执行 for 循环
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek; V ev;
                            if (e.hash == h &&
                                    ((ek = e.key) == key ||
                                            (ek != null && key.equals(ek)))) {
                                val = e.val;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                if ((val = mappingFunction.apply(key)) != null) {
                                    added = true;
                                    pred.next = new Node<K,V>(h, key, val, null);
                                }
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {
                        binCount = 2;
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> r, p;
                        if ((r = t.root) != null &&
                                (p = r.findTreeNode(h, key, null)) != null)
                            val = p.val;
                        else if ((val = mappingFunction.apply(key)) != null) {
                            added = true;
                            t.putTreeVal(h, key, val);
                        }
                    }
                }
            }
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (!added)
                    return val;
                break;
            }
        }
    }
    if (val != null)
        addCount(1L, binCount);
    return val;
}
```



### 总结

1、初始化同步问题
CAS 设置sizeCtrl 和 Thread.yield()

2、put和remove的同步问题
同一个槽位是互斥的，不同槽位没有同步问题

3、put和get的同步问题
无同步问题

4、get和扩容的同步问题
可以分成两种情况讨论
1）该位置的头结点是Node类型对象，直接get，即使这个桶正在进行迁移，在get方法未完成前，迁移操作已完成，
即槽被设置成了ForwordingNode对象，也没关系，并不影响get的结果。因为get线程仍然持有旧链表的引用，
可以从当前结点位置访问到所有的后续结点。这是因为新表中的节点是通过复制旧表中的结点得到的，所以新表的
结点的next值不会影响旧表中对应结点的next值。当get方法结束后，旧链表就出于不可达的状态，会被垃圾回收线程回收。
2）该位置的头结点是ForwordingNode类型对象（头结点的hash值 ==
-1），头结点是ForwordingNode类型的对象，调用该对象的find方法，在新表中查找。
所以无论哪种情况，都能get到正确的值。

5、put(或remove)方法和扩容操作的同步问题
同样可以分为两种情况讨论：
1）该位置的头结点是Node类型对象，那就看谁先获取锁，如果put操作先获取锁，则先将Node对象放入到旧表中，然后调用addCount方法，判断是否需要帮助扩容。
2）该位置的头结点是ForwordingNode类型对象，那就会先帮助扩容，然后在新表中进行put操作。

使用的技巧

1. 用位运算代替直接的乘除法
2. 重复扫描
3. 分离锁：锁的粒度为 table 的元素
4. 检测冲突
5. 局部扩容
6. 延迟加载(lazy-load)
7. volatile 保证可见性
8. CAS 无锁并发
9. Unsafe，LongAdder 等底层手段，进行极端情况优化。
10. 哈希冲突的时候，优先以链表的形式扩展，在同一个位置的个数又达到了8个以上，如果数组的长度还小于64的时候，则会扩容数组。如果数组的长度大于等于64，则将该节点的链表转换成树。
11. 线程安全，数组中单个slot元素个数超过8个时会将链表结构转换成红黑树，注意树节点之间还是有next指针的；
12. 当元素个数超过N（`N = tab.length - tab.length>>>2，达到0.75阈值时`）个时触发rehash，成倍扩容；
13. 当线程扩容时，其他线程put数据时会加入帮助扩容，加快扩容速度；
14. put时对单个slot头节点元素进行synchronized加锁，ConcurrentHashMap中的加锁粒度是针对slot节点的，rehash过程中加锁粒度也是如此；
15. get时一般是不加锁。如果slot元素为链表，直接读取返回即可；如果slot元素为红黑树，并且此时该树在进行再平衡或者节点删除操作，读取操作会按照树节点的next指针进行读取，也是不加锁的（因为红黑树中节点也是有链表串起来的）；如果该树并没有进行平衡或者节点删除操作，那么会用CAS加读锁，防止读取过程中其他线程该树进行更新操作（主要是防止破坏红黑树节点之间的链表特性），破坏“读视图”。
16.

## 附录

为什么容量为2的 N 次方幂?

1、经计算槽位更加高效 hash/(cap-1)
2、扩容避免对新表加锁

负载因子为什么是 0.75

1、
