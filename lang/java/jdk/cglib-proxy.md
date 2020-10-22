cglib-proxy.md



### 生成的代理类



cglib 创建某个类A的动态代理类的模式是：

1、查找A上的所有非final 的public类型的方法定义；

2、将这些方法的定义转换成字节码；

3、将组成的字节码转换成相应的代理的class对象；

4、实现 MethodInterceptor接口，用来处理 对代理类上所有方法的请求（这个接口和JDK动态代理InvocationHandler的功能和角色是一样的）



ls /tmp/cglib/com/wexueliu/bizhidao/test

```java
HelloImplEnhancerByCGLIBc810a79cFastClassByCGLIBda241937.class
HelloImplFastClassByCGLIBee8ebf83.class
HelloImplEnhancerByCGLIBc810a79c.class
```

 ls /tmp/cglib/org/springframework/cglib/core

```java
MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a.class
```



ls /tmp/cglib/org/springframework/cglib/proxy

```java
Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f.class
```



HelloImpl$$EnhancerByCGLIB$$c810a79c.java

```java
import com.wexueliu.bizhidao.test.HelloImpl;
import java.lang.reflect.Method;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class HelloImpl$$EnhancerByCGLIB$$c810a79c extends HelloImpl implements Factory {

   private boolean CGLIB$BOUND;
   public static Object CGLIB$FACTORY_DATA;
   private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
   private static final Callback[] CGLIB$STATIC_CALLBACKS;
   private MethodInterceptor CGLIB$CALLBACK_0;
   private static Object CGLIB$CALLBACK_FILTER;
   private static final Method CGLIB$sayHello$0$Method;
   private static final MethodProxy CGLIB$sayHello$0$Proxy;
   private static final Object[] CGLIB$emptyArgs;
   private static final Method CGLIB$equals$1$Method;
   private static final MethodProxy CGLIB$equals$1$Proxy;
   private static final Method CGLIB$toString$2$Method;
   private static final MethodProxy CGLIB$toString$2$Proxy;
   private static final Method CGLIB$hashCode$3$Method;
   private static final MethodProxy CGLIB$hashCode$3$Proxy;
   private static final Method CGLIB$clone$4$Method;
   private static final MethodProxy CGLIB$clone$4$Proxy;


   static void CGLIB$STATICHOOK1() {
      CGLIB$THREAD_CALLBACKS = new ThreadLocal();
      CGLIB$emptyArgs = new Object[0];
      Class var0 = Class.forName("com.wexueliu.bizhidao.test.HelloImpl$$EnhancerByCGLIB$$c810a79c");
      Class var1;
      Method[] var10000 = ReflectUtils.findMethods(new String[]{"equals", "(Ljava/lang/Object;)Z", "toString", "()Ljava/lang/String;", "hashCode", "()I", "clone", "()Ljava/lang/Object;"}, (var1 = Class.forName("java.lang.Object")).getDeclaredMethods());
      CGLIB$equals$1$Method = var10000[0];
      CGLIB$equals$1$Proxy = MethodProxy.create(var1, var0, "(Ljava/lang/Object;)Z", "equals", "CGLIB$equals$1");
      CGLIB$toString$2$Method = var10000[1];
      CGLIB$toString$2$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/String;", "toString", "CGLIB$toString$2");
      CGLIB$hashCode$3$Method = var10000[2];
      CGLIB$hashCode$3$Proxy = MethodProxy.create(var1, var0, "()I", "hashCode", "CGLIB$hashCode$3");
      CGLIB$clone$4$Method = var10000[3];
      CGLIB$clone$4$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/Object;", "clone", "CGLIB$clone$4");
      CGLIB$sayHello$0$Method = ReflectUtils.findMethods(new String[]{"sayHello", "()V"}, (var1 = Class.forName("com.wexueliu.bizhidao.test.HelloImpl")).getDeclaredMethods())[0];
      CGLIB$sayHello$0$Proxy = MethodProxy.create(var1, var0, "()V", "sayHello", "CGLIB$sayHello$0");
   }

   final void CGLIB$sayHello$0() {
      super.sayHello();
   }

   public final void sayHello() {
      MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
      if(this.CGLIB$CALLBACK_0 == null) {
         CGLIB$BIND_CALLBACKS(this);
         var10000 = this.CGLIB$CALLBACK_0;
      }

      if(var10000 != null) {
         var10000.intercept(this, CGLIB$sayHello$0$Method, CGLIB$emptyArgs, CGLIB$sayHello$0$Proxy);
      } else {
         super.sayHello();
      }
   }

   final boolean CGLIB$equals$1(Object var1) {
      return super.equals(var1);
   }

   public final boolean equals(Object var1) {
      MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
      if(this.CGLIB$CALLBACK_0 == null) {
         CGLIB$BIND_CALLBACKS(this);
         var10000 = this.CGLIB$CALLBACK_0;
      }

      if(var10000 != null) {
         Object var2 = var10000.intercept(this, CGLIB$equals$1$Method, new Object[]{var1}, CGLIB$equals$1$Proxy);
         return var2 == null?false:((Boolean)var2).booleanValue();
      } else {
         return super.equals(var1);
      }
   }

   final String CGLIB$toString$2() {
      return super.toString();
   }

   public final String toString() {
      MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
      if(this.CGLIB$CALLBACK_0 == null) {
         CGLIB$BIND_CALLBACKS(this);
         var10000 = this.CGLIB$CALLBACK_0;
      }

      return var10000 != null?(String)var10000.intercept(this, CGLIB$toString$2$Method, CGLIB$emptyArgs, CGLIB$toString$2$Proxy):super.toString();
   }

   final int CGLIB$hashCode$3() {
      return super.hashCode();
   }

   public final int hashCode() {
      MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
      if(this.CGLIB$CALLBACK_0 == null) {
         CGLIB$BIND_CALLBACKS(this);
         var10000 = this.CGLIB$CALLBACK_0;
      }

      if(var10000 != null) {
         Object var1 = var10000.intercept(this, CGLIB$hashCode$3$Method, CGLIB$emptyArgs, CGLIB$hashCode$3$Proxy);
         return var1 == null?0:((Number)var1).intValue();
      } else {
         return super.hashCode();
      }
   }

   final Object CGLIB$clone$4() throws CloneNotSupportedException {
      return super.clone();
   }

   protected final Object clone() throws CloneNotSupportedException {
      MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
      if(this.CGLIB$CALLBACK_0 == null) {
         CGLIB$BIND_CALLBACKS(this);
         var10000 = this.CGLIB$CALLBACK_0;
      }

      return var10000 != null?var10000.intercept(this, CGLIB$clone$4$Method, CGLIB$emptyArgs, CGLIB$clone$4$Proxy):super.clone();
   }

   public static MethodProxy CGLIB$findMethodProxy(Signature var0) {
      String var10000 = var0.toString();
      switch(var10000.hashCode()) {
      case -508378822:
         if(var10000.equals("clone()Ljava/lang/Object;")) {
            return CGLIB$clone$4$Proxy;
         }
         break;
      case 1535311470:
         if(var10000.equals("sayHello()V")) {
            return CGLIB$sayHello$0$Proxy;
         }
         break;
      case 1826985398:
         if(var10000.equals("equals(Ljava/lang/Object;)Z")) {
            return CGLIB$equals$1$Proxy;
         }
         break;
      case 1913648695:
         if(var10000.equals("toString()Ljava/lang/String;")) {
            return CGLIB$toString$2$Proxy;
         }
         break;
      case 1984935277:
         if(var10000.equals("hashCode()I")) {
            return CGLIB$hashCode$3$Proxy;
         }
      }

      return null;
   }

   public HelloImpl$$EnhancerByCGLIB$$c810a79c() {
      CGLIB$BIND_CALLBACKS(this);
   }

   public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] var0) {
      CGLIB$THREAD_CALLBACKS.set(var0);
   }

   public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] var0) {
      CGLIB$STATIC_CALLBACKS = var0;
   }

   private static final void CGLIB$BIND_CALLBACKS(Object var0) {
      HelloImpl$$EnhancerByCGLIB$$c810a79c var1 = (HelloImpl$$EnhancerByCGLIB$$c810a79c)var0;
      if(!var1.CGLIB$BOUND) {
         var1.CGLIB$BOUND = true;
         Object var10000 = CGLIB$THREAD_CALLBACKS.get();
         if(var10000 == null) {
            var10000 = CGLIB$STATIC_CALLBACKS;
            if(CGLIB$STATIC_CALLBACKS == null) {
               return;
            }
         }

         var1.CGLIB$CALLBACK_0 = (MethodInterceptor)((Callback[])var10000)[0];
      }

   }

   public Object newInstance(Callback[] var1) {
      CGLIB$SET_THREAD_CALLBACKS(var1);
      HelloImpl$$EnhancerByCGLIB$$c810a79c var10000 = new HelloImpl$$EnhancerByCGLIB$$c810a79c();
      CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
      return var10000;
   }

   public Object newInstance(Callback var1) {
      CGLIB$SET_THREAD_CALLBACKS(new Callback[]{var1});
      HelloImpl$$EnhancerByCGLIB$$c810a79c var10000 = new HelloImpl$$EnhancerByCGLIB$$c810a79c();
      CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
      return var10000;
   }

   public Object newInstance(Class[] var1, Object[] var2, Callback[] var3) {
      CGLIB$SET_THREAD_CALLBACKS(var3);
      HelloImpl$$EnhancerByCGLIB$$c810a79c var10000 = new HelloImpl$$EnhancerByCGLIB$$c810a79c;
      switch(var1.length) {
      case 0:
         var10000.<init>();
         CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
         return var10000;
      default:
         throw new IllegalArgumentException("Constructor not found");
      }
   }

   public Callback getCallback(int var1) {
      CGLIB$BIND_CALLBACKS(this);
      MethodInterceptor var10000;
      switch(var1) {
      case 0:
         var10000 = this.CGLIB$CALLBACK_0;
         break;
      default:
         var10000 = null;
      }

      return var10000;
   }

   public void setCallback(int var1, Callback var2) {
      switch(var1) {
      case 0:
         this.CGLIB$CALLBACK_0 = (MethodInterceptor)var2;
      default:
      }
   }

   public Callback[] getCallbacks() {
      CGLIB$BIND_CALLBACKS(this);
      return new Callback[]{this.CGLIB$CALLBACK_0};
   }

   public void setCallbacks(Callback[] var1) {
      this.CGLIB$CALLBACK_0 = (MethodInterceptor)var1[0];
   }

   static {
      CGLIB$STATICHOOK1();
   }
}
```



HelloImpl$$EnhancerByCGLIB$$c810a79c$$FastClassByCGLIB$$da241937.java

```java
import com.wexueliu.bizhidao.test.HelloImpl..EnhancerByCGLIB..c810a79c;
import java.lang.reflect.InvocationTargetException;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.reflect.FastClass;

public class HelloImpl$$EnhancerByCGLIB$$c810a79c$$FastClassByCGLIB$$da241937 extends FastClass {

   public HelloImpl$$EnhancerByCGLIB$$c810a79c$$FastClassByCGLIB$$da241937(Class var1) {
      super(var1);
   }

   public int getIndex(Signature var1) {
      String var10000 = var1.toString();
      switch(var10000.hashCode()) {
      case -1882565338:
         if(var10000.equals("CGLIB$equals$1(Ljava/lang/Object;)Z")) {
            return 17;
         }
         break;
      case -1870561232:
         if(var10000.equals("CGLIB$findMethodProxy(Lorg/springframework/cglib/core/Signature;)Lorg/springframework/cglib/proxy/MethodProxy;")) {
            return 7;
         }
         break;
      case -1745842178:
         if(var10000.equals("setCallbacks([Lorg/springframework/cglib/proxy/Callback;)V")) {
            return 9;
         }
         break;
      case -1641413109:
         if(var10000.equals("newInstance([Lorg/springframework/cglib/proxy/Callback;)Ljava/lang/Object;")) {
            return 4;
         }
         break;
      case -1457535688:
         if(var10000.equals("CGLIB$STATICHOOK1()V")) {
            return 15;
         }
         break;
      case -1411842725:
         if(var10000.equals("CGLIB$hashCode$3()I")) {
            return 19;
         }
         break;
      case -1034266769:
         if(var10000.equals("CGLIB$SET_STATIC_CALLBACKS([Lorg/springframework/cglib/proxy/Callback;)V")) {
            return 10;
         }
         break;
      case -1025895669:
         if(var10000.equals("CGLIB$SET_THREAD_CALLBACKS([Lorg/springframework/cglib/proxy/Callback;)V")) {
            return 11;
         }
         break;
      case -988317324:
         if(var10000.equals("newInstance([Ljava/lang/Class;[Ljava/lang/Object;[Lorg/springframework/cglib/proxy/Callback;)Ljava/lang/Object;")) {
            return 6;
         }
         break;
      case -508378822:
         if(var10000.equals("clone()Ljava/lang/Object;")) {
            return 3;
         }
         break;
      case 291273791:
         if(var10000.equals("CGLIB$sayHello$0()V")) {
            return 16;
         }
         break;
      case 610042816:
         if(var10000.equals("newInstance(Lorg/springframework/cglib/proxy/Callback;)Ljava/lang/Object;")) {
            return 5;
         }
         break;
      case 1132856532:
         if(var10000.equals("getCallbacks()[Lorg/springframework/cglib/proxy/Callback;")) {
            return 13;
         }
         break;
      case 1246779367:
         if(var10000.equals("setCallback(ILorg/springframework/cglib/proxy/Callback;)V")) {
            return 14;
         }
         break;
      case 1306468936:
         if(var10000.equals("CGLIB$toString$2()Ljava/lang/String;")) {
            return 18;
         }
         break;
      case 1364367423:
         if(var10000.equals("getCallback(I)Lorg/springframework/cglib/proxy/Callback;")) {
            return 12;
         }
         break;
      case 1535311470:
         if(var10000.equals("sayHello()V")) {
            return 8;
         }
         break;
      case 1800494055:
         if(var10000.equals("CGLIB$clone$4()Ljava/lang/Object;")) {
            return 20;
         }
         break;
      case 1826985398:
         if(var10000.equals("equals(Ljava/lang/Object;)Z")) {
            return 0;
         }
         break;
      case 1913648695:
         if(var10000.equals("toString()Ljava/lang/String;")) {
            return 1;
         }
         break;
      case 1984935277:
         if(var10000.equals("hashCode()I")) {
            return 2;
         }
      }

      return -1;
   }

   public int getIndex(String var1, Class[] var2) {
      switch(var1.hashCode()) {
      case -2012993625:
         if(var1.equals("sayHello")) {
            switch(var2.length) {
            case 0:
               return 8;
            }
         }
         break;
      case -1983192202:
         if(var1.equals("CGLIB$sayHello$0")) {
            switch(var2.length) {
            case 0:
               return 16;
            }
         }
         break;
      case -1776922004:
         if(var1.equals("toString")) {
            switch(var2.length) {
            case 0:
               return 1;
            }
         }
         break;
      case -1295482945:
         if(var1.equals("equals")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("java.lang.Object")) {
                  return 0;
               }
            }
         }
         break;
      case -1053468136:
         if(var1.equals("getCallbacks")) {
            switch(var2.length) {
            case 0:
               return 13;
            }
         }
         break;
      case -124978609:
         if(var1.equals("CGLIB$equals$1")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("java.lang.Object")) {
                  return 17;
               }
            }
         }
         break;
      case -60403779:
         if(var1.equals("CGLIB$SET_STATIC_CALLBACKS")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("[Lorg.springframework.cglib.proxy.Callback;")) {
                  return 10;
               }
            }
         }
         break;
      case -29025555:
         if(var1.equals("CGLIB$hashCode$3")) {
            switch(var2.length) {
            case 0:
               return 19;
            }
         }
         break;
      case 85179481:
         if(var1.equals("CGLIB$SET_THREAD_CALLBACKS")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("[Lorg.springframework.cglib.proxy.Callback;")) {
                  return 11;
               }
            }
         }
         break;
      case 94756189:
         if(var1.equals("clone")) {
            switch(var2.length) {
            case 0:
               return 3;
            }
         }
         break;
      case 147696667:
         if(var1.equals("hashCode")) {
            switch(var2.length) {
            case 0:
               return 2;
            }
         }
         break;
      case 161998109:
         if(var1.equals("CGLIB$STATICHOOK1")) {
            switch(var2.length) {
            case 0:
               return 15;
            }
         }
         break;
      case 495524492:
         if(var1.equals("setCallbacks")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("[Lorg.springframework.cglib.proxy.Callback;")) {
                  return 9;
               }
            }
         }
         break;
      case 1154623345:
         if(var1.equals("CGLIB$findMethodProxy")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("org.springframework.cglib.core.Signature")) {
                  return 7;
               }
            }
         }
         break;
      case 1543336189:
         if(var1.equals("CGLIB$toString$2")) {
            switch(var2.length) {
            case 0:
               return 18;
            }
         }
         break;
      case 1811874389:
         if(var1.equals("newInstance")) {
            switch(var2.length) {
            case 1:
               String var10001 = var2[0].getName();
               switch(var10001.hashCode()) {
               case -1997738671:
                  if(var10001.equals("[Lorg.springframework.cglib.proxy.Callback;")) {
                     return 4;
                  }
                  break;
               case 1364160985:
                  if(var10001.equals("org.springframework.cglib.proxy.Callback")) {
                     return 5;
                  }
               }
            case 2:
            default:
               break;
            case 3:
               if(var2[0].getName().equals("[Ljava.lang.Class;") && var2[1].getName().equals("[Ljava.lang.Object;") && var2[2].getName().equals("[Lorg.springframework.cglib.proxy.Callback;")) {
                  return 6;
               }
            }
         }
         break;
      case 1817099975:
         if(var1.equals("setCallback")) {
            switch(var2.length) {
            case 2:
               if(var2[0].getName().equals("int") && var2[1].getName().equals("org.springframework.cglib.proxy.Callback")) {
                  return 14;
               }
            }
         }
         break;
      case 1905679803:
         if(var1.equals("getCallback")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("int")) {
                  return 12;
               }
            }
         }
         break;
      case 1951977610:
         if(var1.equals("CGLIB$clone$4")) {
            switch(var2.length) {
            case 0:
               return 20;
            }
         }
      }

      return -1;
   }

   public int getIndex(Class[] var1) {
      switch(var1.length) {
      case 0:
         return 0;
      default:
         return -1;
      }
   }

   public Object invoke(int var1, Object var2, Object[] var3) throws InvocationTargetException {
      c810a79c var10000 = (c810a79c)var2;
      int var10001 = var1;

      try {
         switch(var10001) {
         case 0:
            return new Boolean(var10000.equals(var3[0]));
         case 1:
            return var10000.toString();
         case 2:
            return new Integer(var10000.hashCode());
         case 3:
            return var10000.clone();
         case 4:
            return var10000.newInstance((Callback[])var3[0]);
         case 5:
            return var10000.newInstance((Callback)var3[0]);
         case 6:
            return var10000.newInstance((Class[])var3[0], (Object[])var3[1], (Callback[])var3[2]);
         case 7:
            return c810a79c.CGLIB$findMethodProxy((Signature)var3[0]);
         case 8:
            var10000.sayHello();
            return null;
         case 9:
            var10000.setCallbacks((Callback[])var3[0]);
            return null;
         case 10:
            c810a79c.CGLIB$SET_STATIC_CALLBACKS((Callback[])var3[0]);
            return null;
         case 11:
            c810a79c.CGLIB$SET_THREAD_CALLBACKS((Callback[])var3[0]);
            return null;
         case 12:
            return var10000.getCallback(((Number)var3[0]).intValue());
         case 13:
            return var10000.getCallbacks();
         case 14:
            var10000.setCallback(((Number)var3[0]).intValue(), (Callback)var3[1]);
            return null;
         case 15:
            c810a79c.CGLIB$STATICHOOK1();
            return null;
         case 16:
            var10000.CGLIB$sayHello$0();
            return null;
         case 17:
            return new Boolean(var10000.CGLIB$equals$1(var3[0]));
         case 18:
            return var10000.CGLIB$toString$2();
         case 19:
            return new Integer(var10000.CGLIB$hashCode$3());
         case 20:
            return var10000.CGLIB$clone$4();
         }
      } catch (Throwable var4) {
         throw new InvocationTargetException(var4);
      }

      throw new IllegalArgumentException("Cannot find matching method/constructor");
   }

   public Object newInstance(int var1, Object[] var2) throws InvocationTargetException {
      c810a79c var10000 = new c810a79c;
      c810a79c var10001 = var10000;
      int var10002 = var1;

      try {
         switch(var10002) {
         case 0:
            var10001.<init>();
            return var10000;
         }
      } catch (Throwable var3) {
         throw new InvocationTargetException(var3);
      }

      throw new IllegalArgumentException("Cannot find matching method/constructor");
   }

   public int getMaxIndex() {
      return 20;
   }
}
```



HelloImpl$$FastClassByCGLIB$$ee8ebf83.java

```java
import com.wexueliu.bizhidao.test.HelloImpl;
import java.lang.reflect.InvocationTargetException;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.reflect.FastClass;

public class HelloImpl$$FastClassByCGLIB$$ee8ebf83 extends FastClass {

   public HelloImpl$$FastClassByCGLIB$$ee8ebf83(Class var1) {
      super(var1);
   }

   public int getIndex(Signature var1) {
      String var10000 = var1.toString();
      switch(var10000.hashCode()) {
      case 1535311470:
         if(var10000.equals("sayHello()V")) {
            return 0;
         }
         break;
      case 1826985398:
         if(var10000.equals("equals(Ljava/lang/Object;)Z")) {
            return 1;
         }
         break;
      case 1913648695:
         if(var10000.equals("toString()Ljava/lang/String;")) {
            return 2;
         }
         break;
      case 1984935277:
         if(var10000.equals("hashCode()I")) {
            return 3;
         }
      }

      return -1;
   }

   public int getIndex(String var1, Class[] var2) {
      switch(var1.hashCode()) {
      case -2012993625:
         if(var1.equals("sayHello")) {
            switch(var2.length) {
            case 0:
               return 0;
            }
         }
         break;
      case -1776922004:
         if(var1.equals("toString")) {
            switch(var2.length) {
            case 0:
               return 2;
            }
         }
         break;
      case -1295482945:
         if(var1.equals("equals")) {
            switch(var2.length) {
            case 1:
               if(var2[0].getName().equals("java.lang.Object")) {
                  return 1;
               }
            }
         }
         break;
      case 147696667:
         if(var1.equals("hashCode")) {
            switch(var2.length) {
            case 0:
               return 3;
            }
         }
      }

      return -1;
   }

   public int getIndex(Class[] var1) {
      switch(var1.length) {
      case 0:
         return 0;
      default:
         return -1;
      }
   }

   public Object invoke(int var1, Object var2, Object[] var3) throws InvocationTargetException {
      HelloImpl var10000 = (HelloImpl)var2;
      int var10001 = var1;

      try {
         switch(var10001) {
         case 0:
            var10000.sayHello();
            return null;
         case 1:
            return new Boolean(var10000.equals(var3[0]));
         case 2:
            return var10000.toString();
         case 3:
            return new Integer(var10000.hashCode());
         }
      } catch (Throwable var4) {
         throw new InvocationTargetException(var4);
      }

      throw new IllegalArgumentException("Cannot find matching method/constructor");
   }

   public Object newInstance(int var1, Object[] var2) throws InvocationTargetException {
      HelloImpl var10000 = new HelloImpl;
      HelloImpl var10001 = var10000;
      int var10002 = var1;

      try {
         switch(var10002) {
         case 0:
            var10001.<init>();
            return var10000;
         }
      } catch (Throwable var3) {
         throw new InvocationTargetException(var3);
      }

      throw new IllegalArgumentException("Cannot find matching method/constructor");
   }

   public int getMaxIndex() {
      return 3;
   }
}
```



MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a

```java
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.MethodWrapper.MethodWrapperKey;

public class MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a extends KeyFactory implements MethodWrapperKey {

   private final String FIELD_0;
   private final String[] FIELD_1;
   private final String FIELD_2;


   public MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a() {
   }

   public Object newInstance(String var1, String[] var2, String var3) {
      return new MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a(var1, var2, var3);
   }

   public MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a(String var1, String[] var2, String var3) {
      this.FIELD_0 = var1;
      this.FIELD_1 = var2;
      this.FIELD_2 = var3;
   }

   public int hashCode() {
      int var10000 = 938313161 * 362693231 + (this.FIELD_0 != null?this.FIELD_0.hashCode():0);
      if(this.FIELD_1 != null) {
         String[] var1 = this.FIELD_1;

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var10000 = var10000 * 362693231 + (var1[var2] != null?var1[var2].hashCode():0);
         }
      }

      return var10000 * 362693231 + (this.FIELD_2 != null?this.FIELD_2.hashCode():0);
   }

   public boolean equals(Object var1) {
      if(var1 instanceof MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a) {
         if(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_0 == null) {
            if(this.FIELD_0 != null) {
               return false;
            }
         } else if(this.FIELD_0 == null || !this.FIELD_0.equals(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_0)) {
            return false;
         }

         if(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_1 == null) {
            if(this.FIELD_1 != null) {
               return false;
            }
         } else {
            label104: {
               if(this.FIELD_1 != null) {
                  if(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_1.length == this.FIELD_1.length) {
                     String[] var2 = ((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_1;
                     String[] var3 = this.FIELD_1;
                     int var4 = 0;

                     while(true) {
                        if(var4 >= var2.length) {
                           break label104;
                        }

                        String var10000 = var2[var4];
                        String var10001 = var3[var4];
                        if(var3[var4] == null) {
                           if(var10000 != null) {
                              return false;
                           }
                        } else if(var10000 == null || !var10000.equals(var10001)) {
                           return false;
                        }

                        ++var4;
                     }
                  }
               }

               return false;
            }
         }

         if(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_2 == null) {
            if(this.FIELD_2 != null) {
               return false;
            }
         } else if(this.FIELD_2 == null || !this.FIELD_2.equals(((MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB$$552be97a)var1).FIELD_2)) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuffer var10000 = new StringBuffer();
      var10000 = (this.FIELD_0 != null?var10000.append(this.FIELD_0.toString()):var10000.append("null")).append(", ");
      if(this.FIELD_1 != null) {
         var10000 = var10000.append("{");
         String[] var1 = this.FIELD_1;

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var10000 = (var1[var2] != null?var10000.append(var1[var2].toString()):var10000.append("null")).append(", ");
         }

         var10000.setLength(var10000.length() - 2);
         var10000 = var10000.append("}");
      } else {
         var10000 = var10000.append("null");
      }

      var10000 = var10000.append(", ");
      return (this.FIELD_2 != null?var10000.append(this.FIELD_2.toString()):var10000.append("null")).toString();
   }
}
```



Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f.java

```java
import org.springframework.asm.Type;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.WeakCacheKey;
import org.springframework.cglib.proxy.Enhancer.EnhancerKey;

public class Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f extends KeyFactory implements EnhancerKey {

   private final String FIELD_0;
   private final String[] FIELD_1;
   private final WeakCacheKey FIELD_2;
   private final Type[] FIELD_3;
   private final boolean FIELD_4;
   private final boolean FIELD_5;
   private final Long FIELD_6;


   public Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f() {
   }

   public Object newInstance(String var1, String[] var2, WeakCacheKey var3, Type[] var4, boolean var5, boolean var6, Long var7) {
      return new Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f(var1, var2, var3, var4, var5, var6, var7);
   }

   public Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f(String var1, String[] var2, WeakCacheKey var3, Type[] var4, boolean var5, boolean var6, Long var7) {
      this.FIELD_0 = var1;
      this.FIELD_1 = var2;
      this.FIELD_2 = var3;
      this.FIELD_3 = var4;
      this.FIELD_4 = var5;
      this.FIELD_5 = var6;
      this.FIELD_6 = var7;
   }

   public int hashCode() {
      int var10000 = 1213 * 1209107 + (this.FIELD_0 != null?this.FIELD_0.hashCode():0);
      if(this.FIELD_1 != null) {
         String[] var1 = this.FIELD_1;

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var10000 = var10000 * 1209107 + (var1[var2] != null?var1[var2].hashCode():0);
         }
      }

      var10000 = var10000 * 1209107 + (this.FIELD_2 != null?this.FIELD_2.hashCode():0);
      if(this.FIELD_3 != null) {
         Type[] var3 = this.FIELD_3;

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var10000 = var10000 * 1209107 + (var3[var4] != null?var3[var4].hashCode():0);
         }
      }

      return ((var10000 * 1209107 + (this.FIELD_4 ^ 1)) * 1209107 + (this.FIELD_5 ^ 1)) * 1209107 + (this.FIELD_6 != null?this.FIELD_6.hashCode():0);
   }

   public boolean equals(Object var1) {
      if(var1 instanceof Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f) {
         if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_0 == null) {
            if(this.FIELD_0 != null) {
               return false;
            }
         } else if(this.FIELD_0 == null || !this.FIELD_0.equals(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_0)) {
            return false;
         }

         if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_1 == null) {
            if(this.FIELD_1 != null) {
               return false;
            }
         } else {
            label171: {
               if(this.FIELD_1 != null) {
                  if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_1.length == this.FIELD_1.length) {
                     String[] var2 = ((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_1;
                     String[] var3 = this.FIELD_1;
                     int var4 = 0;

                     while(true) {
                        if(var4 >= var2.length) {
                           break label171;
                        }

                        String var10000 = var2[var4];
                        String var10001 = var3[var4];
                        if(var3[var4] == null) {
                           if(var10000 != null) {
                              return false;
                           }
                        } else if(var10000 == null || !var10000.equals(var10001)) {
                           return false;
                        }

                        ++var4;
                     }
                  }
               }

               return false;
            }
         }

         if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_2 == null) {
            if(this.FIELD_2 != null) {
               return false;
            }
         } else if(this.FIELD_2 == null || !this.FIELD_2.equals(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_2)) {
            return false;
         }

         if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_3 == null) {
            if(this.FIELD_3 != null) {
               return false;
            }
         } else {
            if(this.FIELD_3 == null) {
               return false;
            }

            if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_3.length != this.FIELD_3.length) {
               return false;
            }

            Type[] var5 = ((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_3;
            Type[] var6 = this.FIELD_3;

            for(int var7 = 0; var7 < var5.length; ++var7) {
               Type var8 = var5[var7];
               Type var10002 = var6[var7];
               if(var6[var7] == null) {
                  if(var8 == null) {
                     continue;
                  }
               } else if(var8 != null && var8.equals(var10002)) {
                  continue;
               }

               return false;
            }
         }

         if(this.FIELD_4 == ((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_4 && this.FIELD_5 == ((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_5) {
            if(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_6 == null) {
               if(this.FIELD_6 != null) {
                  return false;
               }
            } else if(this.FIELD_6 == null || !this.FIELD_6.equals(((Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$4ce19e8f)var1).FIELD_6)) {
               return false;
            }

            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuffer var10000 = new StringBuffer();
      var10000 = (this.FIELD_0 != null?var10000.append(this.FIELD_0.toString()):var10000.append("null")).append(", ");
      if(this.FIELD_1 != null) {
         var10000 = var10000.append("{");
         String[] var1 = this.FIELD_1;

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var10000 = (var1[var2] != null?var10000.append(var1[var2].toString()):var10000.append("null")).append(", ");
         }

         var10000.setLength(var10000.length() - 2);
         var10000 = var10000.append("}");
      } else {
         var10000 = var10000.append("null");
      }

      var10000 = var10000.append(", ");
      var10000 = (this.FIELD_2 != null?var10000.append(this.FIELD_2.toString()):var10000.append("null")).append(", ");
      if(this.FIELD_3 != null) {
         var10000 = var10000.append("{");
         Type[] var3 = this.FIELD_3;

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var10000 = (var3[var4] != null?var10000.append(var3[var4].toString()):var10000.append("null")).append(", ");
         }

         var10000.setLength(var10000.length() - 2);
         var10000 = var10000.append("}");
      } else {
         var10000 = var10000.append("null");
      }

      var10000 = var10000.append(", ").append(this.FIELD_4).append(", ").append(this.FIELD_5).append(", ");
      return (this.FIELD_6 != null?var10000.append(this.FIELD_6.toString()):var10000.append("null")).toString();
   }
}
```

