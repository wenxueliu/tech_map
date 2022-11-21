



HttpMessageConverter 是 Spring MVC 中将 Java 对象 和 HttpMessage 进行相互转换的转换器。



```java
public interface HttpMessageConverter<T> {
   boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

   boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

   List<MediaType> getSupportedMediaTypes();

   T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
         throws IOException, HttpMessageNotReadableException;

   void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
         throws IOException, HttpMessageNotWritableException;
}
```

